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

public class BuyThread implements Observer, Runnable {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private static OutputFrame outputFrame;
    private SyncAPIConnector connector;
    private PriceUpdates updates;
    private static final HashMap<Double, Long> buyPrices = new HashMap<>();
    private static final Set<Double> deleteBuyPrices = new HashSet<>();
    private double time;
    private double diff;
    private double tradeVolume;
    private double stopLoss;
    private double takeProfit;
    private static double delay = 0.25;
    private STickRecord currentPrice = null;

    public BuyThread() {
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

    public TradeTransInfoRecord makeBuyInfo(long value) {
        TradeTransInfoRecord info;
        if (!MainThread.bigMoneyTime.get()) {
            info = new TradeTransInfoRecord(TRADE_OPERATION_CODE.BUY, TRADE_TRANSACTION_TYPE.OPEN,
                    this.currentPrice.getAsk(), 0.0, 0.0, this.currentPrice.getSymbol(), tradeVolume, (long)0.0, "", value);
        } else {
            double sl = this.currentPrice.getAsk() - stopLoss;
            double tp = this.currentPrice.getAsk() + takeProfit;
            info = new TradeTransInfoRecord(TRADE_OPERATION_CODE.BUY, TRADE_TRANSACTION_TYPE.OPEN,
                    this.currentPrice.getAsk(), sl, tp, this.currentPrice.getSymbol(), tradeVolume, (long)0.0, "", value);
        }
        return info;
    }

    public void run() {
        running.set(true);
        while (running.get()) {
            update();
            if (this.currentPrice != null) {
                if (!buyPrices.containsKey(this.currentPrice.getAsk())) {
                    long curr_t = System.currentTimeMillis();
                    long end = (long) (curr_t + (time * 1000));
                    buyPrices.put(this.currentPrice.getAsk(), end);
                }

                for (HashMap.Entry<Double, Long> entry : buyPrices.entrySet()) {
                    double key = entry.getKey();
                    long value = entry.getValue();
                    if (value <= System.currentTimeMillis()) {
                        deleteBuyPrices.add(key);
                    } else if (!MainThread.blockTransactions.get()) {
                        if (System.currentTimeMillis() >= MainThread.atomicDelay.get()) {
                            if (this.currentPrice.getAsk() - key >= diff) {
                                boolean isLockAcquired = MainThread.lock.tryLock();
                                if (isLockAcquired) {
                                    try {
                                        deleteBuyPrices.add(key);
                                        TradeTransInfoRecord info = makeBuyInfo(value);
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
                                                    if (tradeStatus.getMessage().equals("Not enough money")) {
                                                        outputFrame.updateOutput("No funds left.");
                                                        MainThread.stopTransactions();
                                                        break;
                                                    } else {
                                                        long curr_t = System.currentTimeMillis();
                                                        MainThread.atomicDelay.set((long) (curr_t + (delay * 1000)));
                                                    }
                                                } else if (tradeStatus.getRequestStatus().equals(REQUEST_STATUS.ACCEPTED)) {
                                                    int temp = MainThread.currTransactions.get();
                                                    MainThread.currTransactions.set(temp + 1);
                                                    String transactionInfo = (temp + 1) + ". A buy position was opened with the number " + tradeStatus.getOrder() + ".";
                                                    outputFrame.updateOutput(transactionInfo);
                                                    long curr_t = System.currentTimeMillis();
                                                    MainThread.atomicDelay.set((long) (curr_t + (delay * 1000)));
                                                } else if (tradeStatus.getRequestStatus().equals(REQUEST_STATUS.PENDING)) {
                                                    try {
                                                        tradeStatus = APICommandFactory.executeTradeTransactionStatusCommand(connector,
                                                                tradeResponse.getOrder());
                                                    } catch (Exception ignore) {
                                                    }

                                                    if (tradeStatus.getRequestStatus().equals(REQUEST_STATUS.ACCEPTED)) {
                                                        int temp = MainThread.currTransactions.get();
                                                        MainThread.currTransactions.set(temp + 1);
                                                        String transactionInfo = (temp + 1) + ". A buy position was opened with the number " + tradeStatus.getOrder() + ".";
                                                        outputFrame.updateOutput(transactionInfo);

                                                    }
                                                }
                                            }
                                        }
                                    } finally {
                                        long curr_t = System.currentTimeMillis();
                                        MainThread.atomicDelay.set((long) (curr_t + (delay * 1000)));
                                        MainThread.lock.unlock();
                                    }
                                }
                            }
                        }
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
