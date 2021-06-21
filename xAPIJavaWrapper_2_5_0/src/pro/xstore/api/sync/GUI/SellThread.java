package pro.xstore.api.sync.GUI;

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
    private final MainThread mainThread;
    private final HashMap<Double, Long> sellPrices = new HashMap<>();
    private final Set<Double> deleteSellPrices = new HashSet<>();
    private double time;
    private double diff;
    private double tradeVolume;
    private double stopLoss = Double.MIN_VALUE;
    private double takeProfit = Double.MIN_VALUE;
    private double delay = 0.25;
    private String market;
    private double maxTransactions = Double.MIN_VALUE;
    private STickRecord currentPrice = null;

    public SellThread(MainThread mainThread) {
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
                   double new_time, double new_diff, double new_tradeVolume, String market) {
        this.connector = new_connector;
        this.outputFrame = new_outFrame;
        this.updates = new_updates;
        this.time = new_time;
        this.diff = new_diff;
        this.tradeVolume = new_tradeVolume;
        this.market = market;
    }

    public void setOptionals(double new_stopLoss, double new_takeProfit, double new_delay, double maxTransactions) {
        this.stopLoss = new_stopLoss;
        this.takeProfit = new_takeProfit;
        this.delay = new_delay;
        this.maxTransactions = maxTransactions;
    }

    public void setOptionalToDefault() {
        this.stopLoss = Double.MIN_VALUE;
        this.takeProfit = Double.MIN_VALUE;
        this.delay = 0.25;
        this.maxTransactions = Double.MIN_VALUE;
    }

    public void start() {
        Thread worker = new Thread(this);
        worker.start();
    }

    public void stop() {
        running.set(false);
    }

    private TradeTransInfoRecord makeSellInfo(long value) {
        TradeTransInfoRecord info;
        if (!mainThread.bigMoneyTime.get()) {
            info = new TradeTransInfoRecord(TRADE_OPERATION_CODE.SELL, TRADE_TRANSACTION_TYPE.OPEN,
                    this.currentPrice.getBid(), 0.0, 0.0, this.currentPrice.getSymbol(), this.tradeVolume, (long)0.0, "", value);
        } else {
            if (this.stopLoss != Double.MIN_VALUE && this.takeProfit == Double.MIN_VALUE) {
                double sl = this.currentPrice.getBid() + this.stopLoss;
                info = new TradeTransInfoRecord(TRADE_OPERATION_CODE.SELL, TRADE_TRANSACTION_TYPE.OPEN,
                        this.currentPrice.getBid(), sl, 0.0, this.currentPrice.getSymbol(), this.tradeVolume, (long)0.0, "", value);
            } else if (this.stopLoss == Double.MIN_VALUE && this.takeProfit != Double.MIN_VALUE) {
                double tp = this.currentPrice.getBid() - this.takeProfit;
                info = new TradeTransInfoRecord(TRADE_OPERATION_CODE.SELL, TRADE_TRANSACTION_TYPE.OPEN,
                        this.currentPrice.getBid(), 0.0, tp, this.currentPrice.getSymbol(), this.tradeVolume, (long)0.0, "", value);
            } else if (this.stopLoss != Double.MIN_VALUE && this.takeProfit != Double.MIN_VALUE) {
                double sl = this.currentPrice.getBid() + this.stopLoss;
                double tp = this.currentPrice.getBid() - this.takeProfit;
                info = new TradeTransInfoRecord(TRADE_OPERATION_CODE.SELL, TRADE_TRANSACTION_TYPE.OPEN,
                        this.currentPrice.getBid(), sl, tp, this.currentPrice.getSymbol(), this.tradeVolume, (long)0.0, "", value);
            } else {
                info = new TradeTransInfoRecord(TRADE_OPERATION_CODE.SELL, TRADE_TRANSACTION_TYPE.OPEN,
                        this.currentPrice.getBid(), 0.0, 0.0, this.currentPrice.getSymbol(), this.tradeVolume, (long)0.0, "", value);
            }
        }
        return info;
    }

    private void checkTransactionsLimit() {
        if ((mainThread.currTransactions.get() >= this.maxTransactions) &&
                (this.maxTransactions != 0) && (this.maxTransactions != Double.MIN_VALUE)) {
            if ((mainThread.bigMoneyTime.get()) && (!mainThread.blockTransactions.get())) {
                mainThread.blockTransactions.set(true);
                if ((!mainThread.messagePrinted.get()) && (this.outputFrame != null)) {
                    mainThread.messagePrinted.set(true);
                    this.outputFrame.updateOutput("Maximum transactions reached!");
                }
            } else if ((!mainThread.bigMoneyTime.get()) && (mainThread.blockTransactions.get())) {
                mainThread.messagePrinted.set(false);
                mainThread.blockTransactions.set(false);
            }
        } else if ((mainThread.currTransactions.get() < this.maxTransactions) && (mainThread.blockTransactions.get())) {
            mainThread.messagePrinted.set(false);
            mainThread.blockTransactions.set(false);
        }
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
                    } else if (!mainThread.blockTransactions.get()) {
                        if (System.currentTimeMillis() >= mainThread.atomicDelay.get()) {
                            if (key - this.currentPrice.getBid() >= this.diff) {
                                boolean isLockAcquired = mainThread.lock.tryLock();
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
                                                        NoFundsFrame warning = new NoFundsFrame(this.market);
                                                        warning.run();
                                                        if (this.outputFrame != null) {
                                                            this.outputFrame.updateOutput("No funds left.");
                                                        }
                                                        mainThread.stopTransactions();
                                                        break;
                                                    }
                                                } else if (tradeStatus.getRequestStatus().equals(REQUEST_STATUS.ACCEPTED)) {
                                                    int temp = mainThread.currTransactions.get();
                                                    if (this.outputFrame != null) {
                                                        String transactionInfo = "A sell position was opened with the number " + tradeStatus.getOrder() + ".";
                                                        this.outputFrame.updateOutput(transactionInfo);
                                                    }
                                                    mainThread.currTransactions.set(temp + 1);
                                                    checkTransactionsLimit();
                                                } else if (tradeStatus.getRequestStatus().equals(REQUEST_STATUS.PENDING)) {
                                                    try {
                                                        tradeStatus = APICommandFactory.executeTradeTransactionStatusCommand(connector,
                                                                tradeResponse.getOrder());
                                                    } catch (Exception ignore) {
                                                    }
                                                    int temp = mainThread.currTransactions.get();
                                                    if (this.outputFrame != null) {
                                                        String transactionInfo = "A sell position was opened with the number " + tradeStatus.getOrder() + ".";
                                                        this.outputFrame.updateOutput(transactionInfo);
                                                    }
                                                    mainThread.currTransactions.set(temp + 1);
                                                    checkTransactionsLimit();
                                                }
                                            }
                                        }
                                    } finally {
                                        long curr_t = System.currentTimeMillis();
                                        mainThread.atomicDelay.set((long) (curr_t + (this.delay * 1000)));
                                        mainThread.lock.unlock();
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
