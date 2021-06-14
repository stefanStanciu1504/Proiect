package pro.xstore.api.sync.gui;

import pro.xstore.api.message.codes.REQUEST_STATUS;
import pro.xstore.api.message.codes.TRADE_OPERATION_CODE;
import pro.xstore.api.message.codes.TRADE_TRANSACTION_TYPE;
import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.records.STickRecord;
import pro.xstore.api.message.records.TradeRecord;
import pro.xstore.api.message.records.TradeTransInfoRecord;
import pro.xstore.api.message.response.TradeTransactionResponse;
import pro.xstore.api.message.response.TradeTransactionStatusResponse;
import pro.xstore.api.message.response.TradesResponse;
import pro.xstore.api.sync.SyncAPIConnector;

import java.util.concurrent.atomic.AtomicBoolean;

public class TsThread implements Runnable, Observer {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private OutputFrame outputFrame;
    private SyncAPIConnector connector;
    private PriceUpdates updates;
    private double time;
    private long checkDelay = 0;
    private double trailingStop = 0.0;
    private double tradeVolume;
    private double stopLoss;
    private double takeProfit;
    private STickRecord currentPrice = null;

    public TsThread() {
    }

    @Override
    public void update() {
        STickRecord tempPrice = (STickRecord) updates.getUpdate(this);
        if (tempPrice != null) {
            this.currentPrice = tempPrice;
        }
    }

    @Override
    public void setSubject(Subject sub)  {
        this.updates = (PriceUpdates) sub;
    }

    public void setMandatoryValues(SyncAPIConnector new_connector, OutputFrame new_outFrame, PriceUpdates new_updates,
                                   double new_time, double new_tradeVolume) {
        this.connector = new_connector;
        this.outputFrame = new_outFrame;
        this.updates = new_updates;
        this.time = new_time;
        this.tradeVolume = new_tradeVolume;
    }

    public void setOptionals(double new_stopLoss, double new_takeProfit, double new_trailingStop) {
        this.stopLoss = new_stopLoss;
        this.takeProfit = new_takeProfit;
        this.trailingStop = new_trailingStop;
    }

    public void setOptionalToDefault() {
        this.stopLoss = Double.MIN_VALUE;
        this.takeProfit = Double.MIN_VALUE;
        this.trailingStop = Double.MIN_VALUE;
    }

    public void start() {
        Thread worker = new Thread(this);
        worker.start();
    }

    public void stop() {
        running.set(false);
    }

    public TradeTransInfoRecord makeBuyChange(STickRecord aux, long order, double ask) {
        TradeTransInfoRecord info;
        long curr_t = System.currentTimeMillis();
        long end = (long) (curr_t + (this.time * 1000));
        double sl = aux.getAsk() - this.stopLoss;
        double tp = aux.getAsk() + this.takeProfit;
        info = new TradeTransInfoRecord(TRADE_OPERATION_CODE.BUY, TRADE_TRANSACTION_TYPE.MODIFY,
                ask, sl, tp, aux.getSymbol(), this.tradeVolume, order, "", end);
        return info;
    }

    public TradeTransInfoRecord makeSellChange(STickRecord aux, long order, double bid) {
        TradeTransInfoRecord info;
        long curr_t = System.currentTimeMillis();
        long end = (long) (curr_t + (this.time * 1000));
        double sl = aux.getBid() + this.stopLoss;
        double tp = aux.getBid() - this.takeProfit;
        info = new TradeTransInfoRecord(TRADE_OPERATION_CODE.SELL, TRADE_TRANSACTION_TYPE.MODIFY,
                bid, sl, tp, aux.getSymbol(), this.tradeVolume, order, "", end);

        return info;
    }
                                                
    public void run() {
        running.set(true);
        while (running.get()) {
            TradesResponse tds = null;
            if (System.currentTimeMillis() >= this.checkDelay) {
                if (this.connector != null) {
                    boolean isLockAcquired = MainThread.lock.tryLock();
                    if (isLockAcquired) {
                        try {
                            tds = APICommandFactory.executeTradesCommand(this.connector, true);
                        }
                        catch (Exception ex) {
                            System.out.println("Exception at TsThread!");
                        }
                        finally {
                            long curr_t1 = System.currentTimeMillis();
                            this.checkDelay = (long) (curr_t1 + (2.0 * 1000));
                            MainThread.lock.unlock();
                        }
                    }
                }
            }

            if (tds != null) {
                if (tds.getTradeRecords() != null) {
                    MainThread.currTransactions.set(tds.getTradeRecords().size());
                    if ((this.stopLoss != Double.MIN_VALUE) && (this.takeProfit != Double.MIN_VALUE) && (this.trailingStop != Double.MIN_VALUE)) {
                        for (TradeRecord td : tds.getTradeRecords()) {
                            update();
                            if (System.currentTimeMillis() >= this.checkDelay) {
                                if (this.currentPrice != null && td != null) {
                                    boolean isLockAcquired = MainThread.lock.tryLock();
                                    if (isLockAcquired) {
                                        try {
                                            TradeTransInfoRecord info = null;
                                            if ((td.getCmd() == 0) &&
                                                    (MainThread.bigMoneyTime.get())) {
                                                if ((this.currentPrice.getAsk() >= (td.getOpen_price() + this.takeProfit * (this.trailingStop / 100.0f))) &&
                                                        (MainThread.bigMoneyTime.get())) {
                                                    info = makeBuyChange(this.currentPrice, td.getPosition(), td.getOpen_price());
                                                }
                                            } else if ((td.getCmd() == 1) &&
                                                        (MainThread.bigMoneyTime.get())) {
                                                if ((this.currentPrice.getBid() <= (td.getOpen_price() - this.takeProfit * (this.trailingStop / 100.0f))) &&
                                                        (MainThread.bigMoneyTime.get())) {
                                                    info = makeSellChange(this.currentPrice, td.getPosition(), td.getOpen_price());
                                                }
                                            }
                                            if (info != null) {
                                                TradeTransactionResponse tradeResponse = null;
                                                try {
                                                    tradeResponse = APICommandFactory.executeTradeTransactionCommand(this.connector, info);
                                                } catch (Exception ignore) {
                                                }
                                                if (tradeResponse != null) {
                                                    TradeTransactionStatusResponse tradeStatus = null;
                                                    try {
                                                        tradeStatus = APICommandFactory.executeTradeTransactionStatusCommand(this.connector,
                                                                tradeResponse.getOrder());
                                                    } catch (Exception ignore) {
                                                    }
                                                    if (tradeStatus != null) {
                                                        if (tradeStatus.getRequestStatus().equals(REQUEST_STATUS.REJECTED)) {
                                                            System.out.println("REJECTED : " + tradeStatus);
                                                        } else if (tradeStatus.getRequestStatus().equals(REQUEST_STATUS.ERROR)) {
                                                            System.out.println("ERROR : " + tradeStatus);
                                                        } else if ((tradeStatus.getRequestStatus().equals(REQUEST_STATUS.ACCEPTED)) && (this.outputFrame != null)) {
                                                            this.outputFrame.updateOutput("Order's (" + td.getOrder() + ") SL and TP were modified.");
                                                        } else {
                                                            try {
                                                                tradeStatus = APICommandFactory.executeTradeTransactionStatusCommand(this.connector,
                                                                        tradeResponse.getOrder());
                                                            } catch (Exception ignore) {
                                                            }
                                                            if ((tradeStatus.getRequestStatus().equals(REQUEST_STATUS.ACCEPTED)) && (this.outputFrame != null)) {
                                                                this.outputFrame.updateOutput("Order's (" + td.getOrder() + ") SL and TP were modified.");
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        finally {
                                            long curr_t1 = System.currentTimeMillis();
                                            this.checkDelay = (long) (curr_t1 + (0.25 * 1000));
                                            MainThread.lock.unlock();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
