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
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class TradeThread implements Runnable {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private static OutputFrame outputFrame;
    private SyncAPIConnector connector;
    private String market;
    private LinkedList<String> subscribedMarkets;
    private double time;
    private double diff;
    private double tradeVolume;
    private static PriceUpdates updates;
    private static final HashMap<Double, Long> buyPrices = new HashMap<>();
    private static final Set<Double> deleteBuyPrices = new HashSet<>();
    private static final HashMap<Double, Long> sellPrices = new HashMap<>();
    private static final Set<Double> deleteSellPrices = new HashSet<>();
    private static double stopLoss = 0.0;
    private static double takeProfit = 0.0;
    private static double maxTransactions = 0.0;
    private static double trailingStop = 0.0;
    private static boolean bigMoneyTime = false;
    private static double lastBuyPrice = 0.0;
    private static double delay = 0.25;
    private static long transactionDelay = 0;

    public TradeThread() {
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
        running.set(false);
    }

    public void setBigMoney(double new_stopLoss, double new_takeProfit,
                            double new_maxTransactions, double new_trailingStop, double new_delay) {
        stopLoss = new_stopLoss;
        takeProfit = new_takeProfit;
        maxTransactions = new_maxTransactions;
        trailingStop = new_trailingStop;
        delay = new_delay;
        bigMoneyTime = true;
    }

    public void setToDefault() {
        stopLoss = 0.0;
        takeProfit = 0.0;
        maxTransactions = 0.0;
        trailingStop = 0.0;
        delay = 0.25;
        bigMoneyTime = false;
    }

    public TradeTransInfoRecord makeBuyInfo(STickRecord aux, long value) {
        TradeTransInfoRecord info;
        if ((stopLoss == 0) && (takeProfit == 0) &&
                (maxTransactions == 0) && (trailingStop == 0)) {
            info = new TradeTransInfoRecord(TRADE_OPERATION_CODE.BUY, TRADE_TRANSACTION_TYPE.OPEN,
                    aux.getAsk(), 0.0, 0.0, aux.getSymbol(), tradeVolume, (long)0.0, "", value);
        } else {
            info = new TradeTransInfoRecord(TRADE_OPERATION_CODE.BUY, TRADE_TRANSACTION_TYPE.OPEN,
                    aux.getAsk(), aux.getAsk() - stopLoss, aux.getAsk() + takeProfit, aux.getSymbol(), tradeVolume, (long)0.0, "", value);
        }
        return info;
    }


    public TradeTransInfoRecord makeSellInfo(STickRecord aux, long value) {
        TradeTransInfoRecord info;
        if ((stopLoss == 0) && (takeProfit == 0) &&
                (maxTransactions == 0) && (trailingStop == 0)) {
            info = new TradeTransInfoRecord(TRADE_OPERATION_CODE.SELL, TRADE_TRANSACTION_TYPE.OPEN,
                    aux.getBid(), 0.0, 0.0, aux.getSymbol(), tradeVolume, (long)0.0, "", value);
        } else {
            info = new TradeTransInfoRecord(TRADE_OPERATION_CODE.SELL, TRADE_TRANSACTION_TYPE.OPEN,
                    aux.getBid(), aux.getBid() + stopLoss, aux.getBid() - takeProfit, aux.getSymbol(), tradeVolume, (long)0.0, "", value);
        }
        return info;
    }

    public void clearAll() {
        deleteBuyPrices.clear();
        deleteSellPrices.clear();
        buyPrices.clear();
        sellPrices.clear();
    }

    public void buyAlgorithm() throws Exception {
        STickRecord aux = updates.getRecord();
        if (aux != null) {
            if (!buyPrices.containsKey(aux.getAsk())) {
                long curr_t = System.currentTimeMillis();
                long end = (long) (curr_t + (time * 1000));
                buyPrices.put(aux.getAsk(), end);
            }

            if (System.currentTimeMillis() >= transactionDelay) {
                for (HashMap.Entry<Double, Long> entry : buyPrices.entrySet()) {
                    double key = entry.getKey();
                    long value = entry.getValue();
                    if (aux.getAsk() - key >= diff) {
                        /* buy order */
                        double d = aux.getAsk() - key;
                        System.out.println("it's a buy -> curr: " + aux.getAsk() + " past: " + key);
                        System.out.printf("diff = %.5f\n", d);
                        TradeTransInfoRecord info = makeBuyInfo(aux, value);
                        TradeTransactionResponse tradeResponse = APICommandFactory.executeTradeTransactionCommand(connector, info);
                        TradeTransactionStatusResponse tradeStatus = APICommandFactory.executeTradeTransactionStatusCommand(connector, tradeResponse.getOrder());
                        if (tradeStatus.getRequestStatus().equals(REQUEST_STATUS.REJECTED)) {
                            System.out.println("bitch you better stop cuz you ain't got no money");
                            this.stop();
                            clearAll();
                            break;
                        }
                        lastBuyPrice = aux.getAsk();
                        System.out.println(tradeStatus);
                        deleteBuyPrices.add(key);
                        if (maxTransactions != 0 && bigMoneyTime) {
                            maxTransactions--;
                        } else if (maxTransactions == 0 && bigMoneyTime) {
                            System.out.println("max transactions reached");
                            clearAll();
                            this.stop();
                            break;
                        }
                        long curr_t = System.currentTimeMillis();
                        transactionDelay = (long) (curr_t + (delay * 1000));
                        System.out.println("curr time : " + curr_t + " delay: " + transactionDelay);
                    } else if (value <= System.currentTimeMillis()) {
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

    public void sellAlgorithm() throws Exception {
        STickRecord aux = updates.getRecord();
        if (aux != null) {
            if (!sellPrices.containsKey(aux.getBid())) {
                long curr_t = System.currentTimeMillis();
                long end = (long) (curr_t + (time * 1000));
                sellPrices.put(aux.getBid(), end);
            }

            if (System.currentTimeMillis() >= transactionDelay) {
                for (HashMap.Entry<Double, Long> entry : sellPrices.entrySet()) {
                    double key = entry.getKey();
                    long value = entry.getValue();
                    if (key - aux.getBid() >= diff) {
                        /* sell order */
                        double d = key - aux.getBid();
                        System.out.println("it's a sell -> curr: " + aux.getBid() + " past: " + key);
                        System.out.printf("diff = %.5f\n", d);
                        TradeTransInfoRecord info = makeSellInfo(aux, value);
                        TradeTransactionResponse tradeResponse = APICommandFactory.executeTradeTransactionCommand(connector, info);
                        TradeTransactionStatusResponse tradeStatus = APICommandFactory.executeTradeTransactionStatusCommand(connector, tradeResponse.getOrder());
                        if (tradeStatus.getRequestStatus().equals(REQUEST_STATUS.REJECTED)) {
                            System.out.println("bitch you better stop cuz you ain't got no money");
                            this.stop();
                            clearAll();
                            break;
                        }
                        System.out.println(tradeStatus);
                        deleteSellPrices.add(key);
                        if (maxTransactions != 0 && bigMoneyTime) {
                            maxTransactions--;
                        } else if (maxTransactions == 0 && bigMoneyTime) {
                            System.out.println("max transactions reached");
                            this.stop();
                            clearAll();
                            break;
                        }
                        long curr_t = System.currentTimeMillis();
                        transactionDelay = (long) (curr_t + (delay * 1000));
                        System.out.println("curr time : " + curr_t + " delay: " + transactionDelay);
                    } else if (value <= System.currentTimeMillis()) {
                        deleteSellPrices.add(key);
                    }
                }
                if (!deleteSellPrices.isEmpty()) {
                    sellPrices.keySet().removeAll(deleteSellPrices);
                    deleteSellPrices.clear();
                }
            }
        }
    }

    public void run() {
        running.set(true);
        updates = new PriceUpdates(connector, market, subscribedMarkets, outputFrame);
        updates.start();

        while (running.get()) {
            try {
                buyAlgorithm();
                sellAlgorithm();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}