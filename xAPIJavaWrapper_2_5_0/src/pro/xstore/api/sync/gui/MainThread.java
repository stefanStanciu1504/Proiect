package pro.xstore.api.sync.gui;

import pro.xstore.api.sync.SyncAPIConnector;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class MainThread implements Runnable {
    public static ReentrantLock lock = new ReentrantLock();
    private final AtomicBoolean running = new AtomicBoolean(false);
    public static AtomicBoolean bigMoneyTime = new AtomicBoolean(false);
    public static AtomicLong atomicDelay = new AtomicLong(0);
    public static AtomicBoolean blockTransactions = new AtomicBoolean(false);
    public static AtomicInteger currTransactions = new AtomicInteger();
    private static OutputFrame outputFrame;
    private static final PriceUpdates updates = new PriceUpdates();
    private static final BuyThread buyThread = new BuyThread();
    private static final SellThread sellThread = new SellThread();
    private static final TsThread tsThread = new TsThread();
    private SyncAPIConnector connector;
    private String market;
    private LinkedList<String> subscribedMarkets;
    private double time;
    private double diff;
    private double tradeVolume;

    public MainThread() {
    }

    public void setConnector(SyncAPIConnector connector) {
        this.connector = connector;
    }

    public void setEssentials(String new_market,
                              LinkedList<String> new_subscribedMarkets, double new_time,
                              double new_diff, double new_tradeVolume, OutputFrame new_outFrame) {
        market = new_market;
        subscribedMarkets = new_subscribedMarkets;
        time = new_time;
        diff = new_diff;
        tradeVolume = new_tradeVolume;
        outputFrame = new_outFrame;
    }

    public void start() {
        Thread worker = new Thread(this);
        worker.start();
    }

    public void stop() {
        updates.stop();
        buyThread.stop();
        sellThread.stop();
        tsThread.stop();
        currTransactions.set(0);
        blockTransactions.set(false);
        atomicDelay.set(0);
        outputFrame.closeFrame();
        running.set(false);
    }

    public static void stopTransactions() {
        buyThread.stop();
        sellThread.stop();
    }

    public void setBigMoney(double stopLoss, double takeProfit,
                            double maxTransactions, double trailingStop, double delay) {
        bigMoneyTime.set(true);
        updates.setMaxTransactions(maxTransactions);
        buyThread.setOptionals(stopLoss, takeProfit, delay, maxTransactions);
        sellThread.setOptionals(stopLoss, takeProfit, delay, maxTransactions);
        tsThread.setOptionals(stopLoss, takeProfit, trailingStop);
        if (running.get()) {
            tsThread.start();
        }
    }

    public void setToDefault() {
        bigMoneyTime.set(false);
        tsThread.stop();
        buyThread.setOptionalToDefault();
        sellThread.setOptionalToDefault();
        tsThread.setOptionalToDefault();
        if (blockTransactions.get()) {
            blockTransactions.set(false);
        }
    }


    public void run() {
        running.set(true);
        updates.setMandatoryValues(connector, market, subscribedMarkets, outputFrame);
        updates.register(buyThread);
        updates.register(sellThread);
        updates.register(tsThread);

        buyThread.setSubject(updates);
        sellThread.setSubject(updates);
        tsThread.setSubject(updates);

        updates.start();
        buyThread.setMandatoryValues(connector, outputFrame, updates, time, diff, tradeVolume);
//        sellThread.setMandatoryValues(connector, outputFrame, updates, time, diff, tradeVolume, market);
        tsThread.setMandatoryValues(connector, outputFrame, updates, time, tradeVolume);

        buyThread.start();
//        sellThread.start();
        if (bigMoneyTime.get()) {
            tsThread.start();
        }
    }
}