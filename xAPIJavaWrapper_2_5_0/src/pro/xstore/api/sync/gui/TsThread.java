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

public class TsThread implements Runnable {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private static OutputFrame outputFrame;
    private SyncAPIConnector connector;
    private static PriceUpdates updates;
    private double time;
    private static long checkDelay = 0;
    private static double maxTransactions = 0.0;
    private static double trailingStop = 0.0;
    private double tradeVolume;
    private double stopLoss;
    private double takeProfit;
    private static TradesResponse tds;

    public TsThread() {
    }

    public void setMandatoryValues(SyncAPIConnector new_connector, OutputFrame new_outFrame, PriceUpdates new_updates,
                                   double new_time, double new_tradeVolume) {
        connector = new_connector;
        outputFrame = new_outFrame;
        updates = new_updates;
        time = new_time;
        tradeVolume = new_tradeVolume;
    }

    public void setOptionals(double new_stopLoss, double new_takeProfit,
                             double new_maxTransactions, double new_trailingStop) {
        stopLoss = new_stopLoss;
        takeProfit = new_takeProfit;
        maxTransactions = new_maxTransactions;
        trailingStop = new_trailingStop;

    }

    public void setOptionalToDefault() {
        stopLoss = 0.0;
        takeProfit = 0.0;
        maxTransactions = 0.0;
        trailingStop = 0.0;
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
        long end = (long) (curr_t + (time * 1000));
        double sl = aux.getAsk() - stopLoss;
        double tp = aux.getAsk() + takeProfit;
        info = new TradeTransInfoRecord(TRADE_OPERATION_CODE.BUY, TRADE_TRANSACTION_TYPE.MODIFY,
                ask, sl, tp, aux.getSymbol(), tradeVolume, order, "", end);
        return info;
    }

    public TradeTransInfoRecord makeSellChange(STickRecord aux, long order, double bid) {
        TradeTransInfoRecord info;
        long curr_t = System.currentTimeMillis();
        long end = (long) (curr_t + (time * 1000));
        double sl = aux.getBid() + stopLoss;
        double tp = aux.getBid() - takeProfit;
        info = new TradeTransInfoRecord(TRADE_OPERATION_CODE.SELL, TRADE_TRANSACTION_TYPE.MODIFY,
                bid, sl, tp, aux.getSymbol(), tradeVolume, order, "", end);

        return info;
    }
                                                
    public void run() {
        running.set(true);
        while (running.get()) {
            STickRecord aux;
            if (System.currentTimeMillis() >= checkDelay) {
                if (connector != null) {
                    boolean isLockAcquired = MainThread.lock.tryLock();
                    if (isLockAcquired) {
                        try {
                            tds = APICommandFactory.executeTradesCommand(connector, true);
                        }
                        catch (Exception ex) {
                            System.out.println("ceva");
                        }
                        finally {
                            long curr_t1 = System.currentTimeMillis();
                            checkDelay = (long) (curr_t1 + (0.25 * 1000));
                            MainThread.lock.unlock();
                        }
                    }
                }
            }

            if (MainThread.currTransactions.get() >= maxTransactions && maxTransactions != 0) {
                if (MainThread.bigMoneyTime.get() && !MainThread.blockTransactions.get()) {
                    outputFrame.updateOutput("Maximum transactions reached");
                    MainThread.blockTransactions.set(true);
                } else if (!MainThread.bigMoneyTime.get() && MainThread.blockTransactions.get()) {
                    MainThread.blockTransactions.set(false);
                }
            } else if (MainThread.currTransactions.get() < maxTransactions && MainThread.blockTransactions.get()) {
                MainThread.blockTransactions.set(false);
            }

            if (tds != null) {
                if (tds.getTradeRecords() != null) {
                    MainThread.currTransactions.set(tds.getTradeRecords().size());
                    for (TradeRecord td : tds.getTradeRecords()) {
                        aux = updates.getRecord();
                        if (System.currentTimeMillis() >= checkDelay) {
                            if (aux != null && td != null) {
                                boolean isLockAcquired = MainThread.lock.tryLock();
                                if (isLockAcquired) {
                                    try {
                                        TradeTransInfoRecord info = null;
                                        if (td.getCmd() == 0 && MainThread.bigMoneyTime.get()) {
                                            if (aux.getAsk() >= (td.getOpen_price() + takeProfit * (trailingStop / 100.0f)) && MainThread.bigMoneyTime.get()) {
                                                info = makeBuyChange(aux, td.getPosition(), td.getOpen_price());
                                            }
                                        } else if (td.getCmd() == 1 && MainThread.bigMoneyTime.get()) {
                                            if (aux.getBid() <= (td.getOpen_price() - takeProfit * (trailingStop / 100.0f)) && MainThread.bigMoneyTime.get()) {
                                                info = makeSellChange(aux, td.getPosition(), td.getOpen_price());
                                            }
                                        }
                                        if (info != null) {
                                            TradeTransactionResponse tradeResponse = null;
                                            try {
                                                tradeResponse = APICommandFactory.executeTradeTransactionCommand(connector, info);
                                            } catch (Exception ignore) {
                                            }
                                            if (tradeResponse != null) {
                                                TradeTransactionStatusResponse tradeStatus = null;
                                                try {
                                                    tradeStatus = APICommandFactory.executeTradeTransactionStatusCommand(connector,
                                                            tradeResponse.getOrder());
                                                } catch (Exception ignore) {
                                                }
                                                if (tradeStatus != null) {
                                                    if (tradeStatus.getRequestStatus().equals(REQUEST_STATUS.REJECTED)) {
                                                        System.out.println("REJECTED : " + tradeStatus);
                                                    } else if (tradeStatus.getRequestStatus().equals(REQUEST_STATUS.ERROR)) {
                                                        System.out.println("ERROR : " + tradeStatus);
                                                    } else {
                                                        outputFrame.updateOutput("Order's (" + td.getPosition() + ") SL and TP were modified.");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    finally {
                                        long curr_t1 = System.currentTimeMillis();
                                        checkDelay = (long) (curr_t1 + (0.25 * 1000));
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
