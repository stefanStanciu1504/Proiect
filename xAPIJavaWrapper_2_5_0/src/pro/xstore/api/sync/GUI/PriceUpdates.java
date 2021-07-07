package pro.xstore.api.sync.GUI;

import pro.xstore.api.message.records.STickRecord;
import pro.xstore.api.sync.SyncAPIConnector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PriceUpdates implements Subject, Runnable {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private OutputFrame outputFrame;
    private SyncAPIConnector connector;
    private String market;
    private final MainThread mainThread;
    private STickRecord record = null;
    private List<Observer> observers;
    private boolean changed;
    private double maxTransactions;
    private final Object MUTEX = new Object();

    public PriceUpdates(MainThread mainThread) {
        this.mainThread = mainThread;
    }

    public void setMandatoryValues(SyncAPIConnector new_connector, String new_market, OutputFrame new_outFrame) {
        this.connector = new_connector;
        this.market = new_market;
        this.outputFrame = new_outFrame;
        this.observers = new ArrayList<>();
    }

    @Override
    public void register(Observer obj) {
        if (obj == null) throw new NullPointerException("Null Observer");
        if (!observers.contains(obj))
            observers.add(obj);
    }

    @Override
    public void unregister(Observer obj) {
        observers.remove(obj);
    }

    @Override
    public void notifyObservers() {
        List<Observer> observersLocal;

        synchronized (MUTEX) {
            if (!changed) return;
            observersLocal = new ArrayList<>(this.observers);
            this.changed = false;
        }
        for (Observer obj : observersLocal) {
            obj.update();
        }
    }

    @Override
    public Object getUpdate(Observer obj) {
        return record;
    }

    public void start() {
        Thread worker = new Thread(this);
        worker.start();
    }

    public void stop() {
        running.set(false);
    }

    public void postPrice(STickRecord newPrice) {
        record = newPrice;
        this.changed = true;
        notifyObservers();
    }

    public void setMaxTransactions(double maxTransactions) {
        this.maxTransactions = maxTransactions;
    }

    private void checkTransactionsLimit() {
        if ((mainThread.currTransactions.get() >= this.maxTransactions) &&
                (this.maxTransactions != 0) && (this.maxTransactions != Double.MIN_VALUE)) {
            if ((mainThread.bigMoneyTime.get()) && (!mainThread.blockTransactions.get())) {
                mainThread.blockTransactions.set(true);
            } else if ((mainThread.bigMoneyTime.get()) && (mainThread.blockTransactions.get())) {
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
        if (outputFrame != null) {
            outputFrame.updateOutput("Market: " + market);
        }

        try {
            connector.subscribePrice(market);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        STickRecord prev = null;
        STickRecord curr = null;
        while (running.get()) {
            checkTransactionsLimit();
            if (curr != null)
                prev = curr;
            curr = connector.getTickRecord();

            if (curr != null && prev != null && !curr.getAsk().equals(prev.getAsk()) && curr.getSymbol().equals(market)) {
                postPrice(curr);
                if (outputFrame != null) {
                    outputFrame.updateOutput("The market's ask price is " + curr.getAsk() + " and the bid price is: " + curr.getBid());
                }
            }
        }
    }
}
