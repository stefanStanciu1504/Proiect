package pro.xstore.api.sync.gui;

import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.records.STickRecord;
import pro.xstore.api.message.records.TickRecord;
import pro.xstore.api.message.response.TickPricesResponse;
import pro.xstore.api.sync.SyncAPIConnector;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class PriceUpdates implements Runnable {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private static OutputFrame outputFrame;
    private final SyncAPIConnector connector;
    private final String market;
    private final LinkedList<String> subscribedMarkets;
    private static STickRecord record = null;

    public PriceUpdates(SyncAPIConnector new_connector, String new_market, LinkedList<String> new_subscribedMarkets, OutputFrame new_outFrame) {
        connector = new_connector;
        market = new_market;
        subscribedMarkets = new_subscribedMarkets;
        outputFrame = new_outFrame;
    }

    public void start() {
        Thread worker = new Thread(this);
        worker.start();
    }

    public void stop() {
        running.set(false);
    }

    public STickRecord getRecord() {
        return record;
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
            outputFrame.updateOutput("TickPrices result: " + tr.getSymbol() + " - ask: " + tr.getAsk() + "\n");
        }

        try {
            connector.subscribePrice(market);
            connector.subscribeTrades();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        STickRecord prev = null;
        while (running.get()) {
            if (record != null)
                prev = record;
            record = connector.getTickRecord();

            if (record != null && prev != null && !record.getAsk().equals(prev.getAsk())) {
                outputFrame.updateOutput(record);
            }
        }
    }
}
