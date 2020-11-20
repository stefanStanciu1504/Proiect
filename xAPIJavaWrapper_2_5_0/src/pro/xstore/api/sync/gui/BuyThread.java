package pro.xstore.api.sync.gui;

import pro.xstore.api.message.codes.REQUEST_STATUS;
import pro.xstore.api.message.codes.TRADE_OPERATION_CODE;
import pro.xstore.api.message.codes.TRADE_TRANSACTION_TYPE;
import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.records.STickRecord;
import pro.xstore.api.message.records.TradeTransInfoRecord;
import pro.xstore.api.message.response.TradeTransactionResponse;
import pro.xstore.api.message.response.TradeTransactionStatusResponse;
import pro.xstore.api.sync.SyncAPIConnector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class BuyThread implements Runnable {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private static OutputFrame outputFrame;
    private SyncAPIConnector connector;
    private static PriceUpdates updates;
    private static final HashMap<Double, Long> buyPrices = new HashMap<>();
    private static final Set<Double> deleteBuyPrices = new HashSet<>();
    private double time;
    private double diff;
    private double tradeVolume;
    private double stopLoss;
    private double takeProfit;
    private static double delay = 0.25;

    public BuyThread() {
    }

    public void setMandatoryValues(SyncAPIConnector new_connector, OutputFrame new_outFrame, PriceUpdates new_updates,
                   double new_time, double new_diff, double new_tradeVolume) {
        connector = new_connector;
        outputFrame = new_outFrame;
        updates = new_updates;
        time = new_time;
        diff = new_diff;
        tradeVolume = new_tradeVolume;
    }

    public void setOptionals(double new_stopLoss, double new_takeProfit, double new_delay) {
        stopLoss = new_stopLoss;
        takeProfit = new_takeProfit;
        delay = new_delay;
    }

    public void setOptionalToDefault() {
        stopLoss = 0.0;
        takeProfit = 0.0;
        delay = 0.25;
    }

    public void start() {
        Thread worker = new Thread(this);
        worker.start();
    }

    public void stop() {
        running.set(false);
    }

    public TradeTransInfoRecord makeBuyInfo(STickRecord aux, long value) {
        TradeTransInfoRecord info;
        if (!MainThread.bigMoneyTime.get()) {
            info = new TradeTransInfoRecord(TRADE_OPERATION_CODE.BUY, TRADE_TRANSACTION_TYPE.OPEN,
                    aux.getAsk(), 0.0, 0.0, aux.getSymbol(), tradeVolume, (long)0.0, "", value);
        } else {
            double sl = aux.getAsk() - stopLoss;
            double tp = aux.getAsk() + takeProfit;
            info = new TradeTransInfoRecord(TRADE_OPERATION_CODE.BUY, TRADE_TRANSACTION_TYPE.OPEN,
                    aux.getAsk(), sl, tp, aux.getSymbol(), tradeVolume, (long)0.0, "", value);
        }
        return info;
    }

    public void run() {
        running.set(true);
        while (running.get()) {
            STickRecord aux = updates.getRecord();
            if (aux != null) {
                if (!buyPrices.containsKey(aux.getAsk())) {
                    long curr_t = System.currentTimeMillis();
                    long end = (long) (curr_t + (time * 1000));
                    buyPrices.put(aux.getAsk(), end);
                }

                for (HashMap.Entry<Double, Long> entry : buyPrices.entrySet()) {
                    double key = entry.getKey();
                    long value = entry.getValue();
                    if (!MainThread.blockTransactions.get()) {
                        if (System.currentTimeMillis() >= MainThread.atomicDelay.get()) {
                            if (aux.getAsk() - key >= diff) {
                                boolean isLockAcquired = MainThread.lock.tryLock();
                                if (isLockAcquired) {
                                    try {
                                        TradeTransInfoRecord info = makeBuyInfo(aux, value);
                                        TradeTransactionResponse tradeResponse = null;
                                        try {
                                            tradeResponse = APICommandFactory.executeTradeTransactionCommand(connector, info);
                                        } catch (Exception ignore) {
                                        }
                                        deleteBuyPrices.add(key);
                                        if (tradeResponse != null) {
                                            TradeTransactionStatusResponse tradeStatus = null;
                                            try {
                                                tradeStatus = APICommandFactory.executeTradeTransactionStatusCommand(connector,
                                                        tradeResponse.getOrder());
                                            } catch (Exception ignore) {
                                            }
                                            if (tradeStatus != null) {
                                                if (tradeStatus.getRequestStatus().equals(REQUEST_STATUS.REJECTED)) {
                                                    if (tradeStatus.getMessage().equals("Not enough money")) {
                                                        outputFrame.updateOutput("No funds left.");
                                                        MainThread.stopTransactions();
                                                        break;
                                                    }
                                                } else if (tradeStatus.getRequestStatus().equals(REQUEST_STATUS.ACCEPTED)) {
                                                    int temp = MainThread.currTransactions.get();
                                                    MainThread.currTransactions.set(temp + 1);
                                                    outputFrame.updateOutput(tradeStatus);
                                                    long curr_t = System.currentTimeMillis();
                                                    MainThread.atomicDelay.set((long) (curr_t + (delay * 1000)));
                                                }
                                            }
                                        }
                                    } finally {
                                        MainThread.lock.unlock();
                                    }
                                }
                            }
                        }
                    }
                    if (value <= System.currentTimeMillis()) {
                        deleteBuyPrices.add(key);
                    }
                }
                if (!deleteBuyPrices.isEmpty()) {
                    buyPrices.keySet().removeAll(deleteBuyPrices);
                    deleteBuyPrices.clear();
                }
            }
        }
    }
}
