package pro.xstore.api.sync.GUI;

import pro.xstore.api.sync.SyncAPIConnector;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MainThread implements Runnable {
    private final AtomicBoolean running = new AtomicBoolean(false);
    public AtomicBoolean bigMoneyTime = new AtomicBoolean(false);
    public AtomicBoolean blockTransactions = new AtomicBoolean(false);
    public AtomicInteger currTransactions = new AtomicInteger(0);
    private OutputFrame outputFrame;
    private final PriceUpdates updates = new PriceUpdates(this);
    private final BuyThread buyThread = new BuyThread(this);
    private final SellThread sellThread = new SellThread(this);
    private final TsThread tsThread = new TsThread(this);
    public AtomicBoolean messagePrinted =  new AtomicBoolean(false);
    private final SyncAPIConnector connector;
    private String market;
    private double time;
    private double diff;
    private double tradeVolume;
    private final TradeFrame tradeFrame;
    private JToggleButton button;

    public MainThread(SyncAPIConnector connector, TradeFrame tradeFrame) {
        this.connector = connector;
        this.tradeFrame = tradeFrame;
    }

    public void setEssentials(String new_market, double new_time,
                              double new_diff, double new_tradeVolume, OutputFrame new_outFrame) {
        this.market = new_market;
        this.time = new_time;
        this.diff = new_diff;
        this.tradeVolume = new_tradeVolume;
        this.outputFrame = new_outFrame;
    }

    public void start() {
        Thread worker = new Thread(this);
        worker.start();
    }

    public void setButton(JToggleButton button) {
        this.button = button;
    }

    public void stop() {
        buyThread.stop();
        sellThread.stop();
        tsThread.stop();
        updates.stop();
        if (outputFrame != null) {
            outputFrame.closeFrame();
        }
        running.set(false);
    }

    public void setBigMoney(double stopLoss, double takeProfit,
                            double maxTransactions, double trailingStop, double delay) {

        updates.setMaxTransactions(maxTransactions);
        buyThread.setOptionals(stopLoss, takeProfit, delay, maxTransactions);
        sellThread.setOptionals(stopLoss, takeProfit, delay, maxTransactions);
        tsThread.setOptionals(stopLoss, takeProfit, trailingStop);
        bigMoneyTime.set(true);
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
        updates.setMandatoryValues(connector, market, outputFrame);
        updates.register(buyThread);
        updates.register(sellThread);
        updates.register(tsThread);

        buyThread.setSubject(updates);
        sellThread.setSubject(updates);
        tsThread.setSubject(updates);

        updates.start();
        buyThread.setMandatoryValues(connector, outputFrame, updates, time, diff, tradeVolume, market, tradeFrame);
        buyThread.setButton(this.button);
        sellThread.setMandatoryValues(connector, outputFrame, updates, time, diff, tradeVolume, market, tradeFrame);
        sellThread.setButton(this.button);
        tsThread.setMandatoryValues(connector, outputFrame, updates, time, tradeVolume, market, tradeFrame);

        buyThread.start();
        sellThread.start();
        if (bigMoneyTime.get()) {
            tsThread.start();
        }
    }
}