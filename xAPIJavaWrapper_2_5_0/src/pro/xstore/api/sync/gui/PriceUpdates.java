package pro.xstore.api.sync.gui;

import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.records.STickRecord;
import pro.xstore.api.message.records.TickRecord;
import pro.xstore.api.message.response.TickPricesResponse;
import pro.xstore.api.sync.SyncAPIConnector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PriceUpdates implements Subject, Runnable {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private static OutputFrame outputFrame;
    private final SyncAPIConnector connector;
    private final String market;
    private final LinkedList<String> subscribedMarkets;
    private STickRecord record = null;
    private List<Observer> observers;
    private boolean changed;
    private final Object MUTEX = new Object();

    public PriceUpdates(SyncAPIConnector new_connector, String new_market, LinkedList<String> new_subscribedMarkets, OutputFrame new_outFrame) {
        connector = new_connector;
        market = new_market;
        subscribedMarkets = new_subscribedMarkets;
        outputFrame = new_outFrame;
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
        List<Observer> observersLocal = null;

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

    public void run() {
        running.set(true);
        TickPricesResponse resp = null;
        try {
            resp = APICommandFactory.executeTickPricesCommand(connector, 0L, subscribedMarkets, 0L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert resp != null;
        for (TickRecord tr : resp.getTicks()) {
            outputFrame.updateOutput("Market: " + tr.getSymbol());
        }

        try {
            connector.subscribePrice(market);
            connector.subscribeTrades();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        STickRecord prev = null;
        STickRecord curr = null;
        while (running.get()) {
            if (curr != null)
                prev = curr;
            curr = connector.getTickRecord();

            if (curr != null && prev != null && !curr.getAsk().equals(prev.getAsk()) && curr.getSymbol().equals(market)) {
                postPrice(curr);
                outputFrame.updateOutput(curr);
            }
        }
    }
}
