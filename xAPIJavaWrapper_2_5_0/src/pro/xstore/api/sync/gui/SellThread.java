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

public class SellThread implements Runnable, Observer {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private OutputFrame outputFrame;
    private SyncAPIConnector connector;
    private PriceUpdates updates;
    private final HashMap<Double, Long> sellPrices = new HashMap<>();
    private final Set<Double> deleteSellPrices = new HashSet<>();
    private double time;
    private double diff;
    private double tradeVolume;
    private double stopLoss;
    private double takeProfit;
    private double delay = 0.25;
    private double maxTransactions;
    private STickRecord currentPrice = null;

    public SellThread() {
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
        this.connector = new_connector;
        this.outputFrame = new_outFrame;
        this.updates = new_updates;
        this.time = new_time;
        this.diff = new_diff;
        this.tradeVolume = new_tradeVolume;
    }

    public void setOptionals(double new_stopLoss, double new_takeProfit, double new_delay, double new_maxTransactions) {
        this.stopLoss = new_stopLoss;
        this.takeProfit = new_takeProfit;
        this.maxTransactions = new_maxTransactions;
        this.delay = new_delay;
    }

    public void setOptionalToDefault() {
        this.stopLoss = 0.0;
        this.takeProfit = 0.0;
        this.maxTransactions = Double.MAX_VALUE;
        this.delay = 0.25;
    }

    public void start() {
        Thread worker = new Thread(this);
        worker.start();
    }

    public void stop() {
        running.set(false);
    }

    public TradeTransInfoRecord makeSellInfo(long value) {
        TradeTransInfoRecord info;
        if (!MainThread.bigMoneyTime.get()) {
            info = new TradeTransInfoRecord(TRADE_OPERATION_CODE.SELL, TRADE_TRANSACTION_TYPE.OPEN,
                    this.currentPrice.getBid(), 0.0, 0.0, this.currentPrice.getSymbol(), this.tradeVolume, (long)0.0, "", value);
        } else {
            double sl = this.currentPrice.getBid() + this.stopLoss;
            double tp = this.currentPrice.getBid() - this.takeProfit;
            info = new TradeTransInfoRecord(TRADE_OPERATION_CODE.SELL, TRADE_TRANSACTION_TYPE.OPEN,
                    this.currentPrice.getBid(), sl, tp, this.currentPrice.getSymbol(), this.tradeVolume, (long)0.0, "", value);
        }
        return info;
    }

    public void run() {
        running.set(true);
        while (running.get()) {
            update();
            if (this.currentPrice != null) {
                if (!this.sellPrices.containsKey(this.currentPrice.getBid())) {
                    long curr_t = System.currentTimeMillis();
                    long end = (long) (curr_t + (this.time * 1000));
                    this.sellPrices.put(this.currentPrice.getBid(), end);
                }

                for (HashMap.Entry<Double, Long> entry : this.sellPrices.entrySet()) {
                    double key = entry.getKey();
                    long value = entry.getValue();
                    if (value <= System.currentTimeMillis()) {
                        this.deleteSellPrices.add(key);
                    } else if (!MainThread.blockTransactions.get()) {
                        if (System.currentTimeMillis() >= MainThread.atomicDelay.get()) {
                            if (key - this.currentPrice.getBid() >= this.diff) {
                                boolean isLockAcquired = MainThread.lock.tryLock();
                                if (isLockAcquired) {
                                    try {
                                        this.deleteSellPrices.add(key);
                                        TradeTransInfoRecord info = makeSellInfo(value);
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
                                                    if (tradeStatus.getMessage().equals("Not enough money")) {
                                                        this.outputFrame.updateOutput("No funds left.");
                                                        MainThread.stopTransactions();
                                                        break;
                                                    } else {
                                                        long curr_t = System.currentTimeMillis();
                                                        MainThread.atomicDelay.set((long) (curr_t + (this.delay * 1000)));
                                                    }
                                                } else if (tradeStatus.getRequestStatus().equals(REQUEST_STATUS.ACCEPTED)) {
                                                    int temp = MainThread.currTransactions.get();
                                                    MainThread.currTransactions.set(temp + 1);
                                                    String transactionInfo = (temp + 1) + ". A sell position was opened with the number " + tradeStatus.getOrder();
                                                    this.outputFrame.updateOutput(transactionInfo);
                                                    long curr_t = System.currentTimeMillis();
                                                    MainThread.atomicDelay.set((long) (curr_t + (this.delay * 1000)));
                                                } else if (tradeStatus.getRequestStatus().equals(REQUEST_STATUS.PENDING)) {
                                                    try {
                                                        tradeStatus = APICommandFactory.executeTradeTransactionStatusCommand(connector,
                                                                tradeResponse.getOrder());
                                                    } catch (Exception ignore) {
                                                    }
                                                    int temp = MainThread.currTransactions.get();
                                                    MainThread.currTransactions.set(temp + 1);
                                                    String transactionInfo = (temp + 1) + ". A sell position was opened with the number " + tradeStatus.getOrder();
                                                    this.outputFrame.updateOutput(transactionInfo);

                                                }
                                            }
                                        }
                                    } finally {
                                        long curr_t = System.currentTimeMillis();
                                        MainThread.atomicDelay.set((long) (curr_t + (this.delay * 1000)));
                                        MainThread.lock.unlock();
                                    }
                                }
                            }
                        }
                    }
                }
                if (!this.deleteSellPrices.isEmpty()) {
                    this.sellPrices.keySet().removeAll(this.deleteSellPrices);
                    this.deleteSellPrices.clear();
                }
            }
        }
    }
}
