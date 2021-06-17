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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class TsThread implements Runnable, Observer {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private OutputFrame outputFrame;
    private SyncAPIConnector connector;
    private PriceUpdates updates;
    private final MainThread mainThread;
    private double time;
    private long checkDelay = 0;
    private double trailingStop = 0.0;
    private double tradeVolume;
    private double stopLoss;
    private String market;
    private double takeProfit;
    private STickRecord currentPrice = null;
    private boolean entered = false;
    private final List<Long> visited = new LinkedList<Long>();

    public TsThread(MainThread mainThread) {
        this.mainThread = mainThread;
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
                                   double new_time, double new_tradeVolume, String market) {
        this.connector = new_connector;
        this.outputFrame = new_outFrame;
        this.updates = new_updates;
        this.time = new_time;
        this.tradeVolume = new_tradeVolume;
        this.market = market;
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
                    boolean isLockAcquired = mainThread.lock.tryLock();
                    if (isLockAcquired) {
                        try {
                            tds = APICommandFactory.executeTradesCommand(this.connector, true);
                        }
                        catch (Exception ex) {
                            System.out.println("Exception at TsThread!");
                        }
                        finally {
                            if (!entered) {
                                long curr_t1 = System.currentTimeMillis();

                                this.checkDelay = (long) (curr_t1 + (2.0 * 1000));
                                this.entered = true;
                            }
                            mainThread.lock.unlock();
                        }
                    }
                }
            }

            if (tds != null) {
                if (tds.getTradeRecords() != null) {
                    List<TradeRecord> tempList = tds.getTradeRecords().stream().filter(p -> p.getSymbol().equals(this.market)).collect(Collectors.toList());
                    mainThread.currTransactions.set(tempList.size());
                    if ((this.stopLoss != Double.MIN_VALUE) && (this.takeProfit != Double.MIN_VALUE) && (this.trailingStop != Double.MIN_VALUE)) {
                        int count = 0;
                        boolean check = false;
                        for (TradeRecord td : tempList) {
                            update();
                            if (visited.contains(td.getPosition())) {
                                count++;
                                continue;
                            }
                            if (System.currentTimeMillis() >= this.checkDelay && this.entered) {
                                check = true;
                                if (this.currentPrice != null && td != null) {
                                    boolean isLockAcquired = mainThread.lock.tryLock();
                                    if (isLockAcquired) {
                                        TradeTransInfoRecord info = null;
                                        try {
                                            if ((td.getCmd() == 0) &&
                                                    (mainThread.bigMoneyTime.get())) {
                                                if ((this.currentPrice.getAsk() >= (td.getOpen_price() + this.takeProfit * (this.trailingStop / 100.0f))) &&
                                                        (mainThread.bigMoneyTime.get())) {
                                                    info = makeBuyChange(this.currentPrice, td.getPosition(), td.getOpen_price());
                                                }
                                            } else if ((td.getCmd() == 1) &&
                                                        (mainThread.bigMoneyTime.get())) {
                                                if ((this.currentPrice.getBid() <= (td.getOpen_price() - this.takeProfit * (this.trailingStop / 100.0f))) &&
                                                        (mainThread.bigMoneyTime.get())) {
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
                                            if (info != null) {
                                                if (!visited.contains(td)) {
                                                    visited.add(td.getPosition());
                                                }
                                                long curr_t1 = System.currentTimeMillis();
                                                this.checkDelay = (long) (curr_t1 + (0.25 * 1000));
                                            }
                                            mainThread.lock.unlock();
                                        }
                                    }
                                }
                            }
                        }
                        if (check) {
                            this.entered = false;
                        }
                        if (count == tempList.size()) {
                            System.out.println(visited);
                            visited.clear();
                        }
                    }
                }
            }
        }
    }
}
