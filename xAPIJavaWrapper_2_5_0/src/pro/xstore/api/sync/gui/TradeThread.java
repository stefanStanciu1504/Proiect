package pro.xstore.api.sync.gui;

import pro.xstore.api.message.codes.REQUEST_STATUS;
import pro.xstore.api.message.codes.TRADE_OPERATION_CODE;
import pro.xstore.api.message.codes.TRADE_TRANSACTION_TYPE;
import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.records.STickRecord;
import pro.xstore.api.message.records.TradeRecord;
import pro.xstore.api.message.records.TradeTransInfoRecord;
import pro.xstore.api.message.response.TradeTransactionResponse;
import pro.xstore.api.message.response.TradeTransactionStatusResponse;
import pro.xstore.api.message.response.TradesResponse;
import pro.xstore.api.sync.SyncAPIConnector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

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
    private static BuyAlgh buyAlgh;
    private static final HashMap<Double, Long> buyPrices = new HashMap<>();
    private static final Set<Double> deleteBuyPrices = new HashSet<>();
    private static final HashMap<Double, Long> sellPrices = new HashMap<>();
    private static final Set<Double> deleteSellPrices = new HashSet<>();
    private static double stopLoss = 0.0;
    private static double takeProfit = 0.0;
    private static double maxTransactions = 0.0;
    private static double trailingStop = 0.0;
    private static boolean bigMoneyTime = false;
    private static double delay = 0.25;
    private static long transactionDelay = 0;
    private static long checkDelay = 0;
    private static boolean blockTransactions = false;
    private static TradesResponse tds;
    public static AtomicLong atomicDelay = new AtomicLong(0);


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
        bigMoneyTime = false;
        stopLoss = 0.0;
        takeProfit = 0.0;
        maxTransactions = 0.0;
        trailingStop = 0.0;
        delay = 0.25;
    }

    public TradeTransInfoRecord makeBuyInfo(STickRecord aux, long value) {
        TradeTransInfoRecord info;
        if (!bigMoneyTime) {
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


    public TradeTransInfoRecord makeSellInfo(STickRecord aux, long value) {
        TradeTransInfoRecord info;
        if (!bigMoneyTime) {
            info = new TradeTransInfoRecord(TRADE_OPERATION_CODE.SELL, TRADE_TRANSACTION_TYPE.OPEN,
                    aux.getBid(), 0.0, 0.0, aux.getSymbol(), tradeVolume, (long)0.0, "", value);
        } else {
            info = new TradeTransInfoRecord(TRADE_OPERATION_CODE.SELL, TRADE_TRANSACTION_TYPE.OPEN,
                    aux.getBid(), aux.getBid() + stopLoss, aux.getBid() - takeProfit, aux.getSymbol(), tradeVolume, (long)0.0, "", value);
        }
        return info;
    }

    public TradeTransInfoRecord makeBuyChange(STickRecord aux, long order, double ask) {
        TradeTransInfoRecord info;
        long curr_t = System.currentTimeMillis();
        long end = (long) (curr_t + (time * 1000));
        double sl = aux.getAsk() - stopLoss;
        double tp = aux.getAsk() + takeProfit;
        info = new TradeTransInfoRecord(TRADE_OPERATION_CODE.BUY, TRADE_TRANSACTION_TYPE.MODIFY,
                ask, sl, tp, aux.getSymbol(), tradeVolume, order, "", end);
        return info;
    }

    public TradeTransInfoRecord makeSellChange(STickRecord aux, long order, double bid) {
        TradeTransInfoRecord info;
        long curr_t = System.currentTimeMillis();
        long end = (long) (curr_t + (time * 1000));
        info = new TradeTransInfoRecord(TRADE_OPERATION_CODE.SELL, TRADE_TRANSACTION_TYPE.MODIFY,
                bid, aux.getBid() + stopLoss, aux.getBid() - takeProfit, aux.getSymbol(), tradeVolume, order, "", end);

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

            for (HashMap.Entry<Double, Long> entry : buyPrices.entrySet()) {
                double key = entry.getKey();
                long value = entry.getValue();
                if (System.currentTimeMillis() >= transactionDelay) {
                    if (aux.getAsk() - key >= diff) {
                        TradeTransInfoRecord info = makeBuyInfo(aux, value);
                        TradeTransactionResponse tradeResponse = null;
                        try {
                            tradeResponse = APICommandFactory.executeTradeTransactionCommand(connector, info);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (tradeResponse != null) {
                            TradeTransactionStatusResponse tradeStatus = APICommandFactory.executeTradeTransactionStatusCommand(connector,
                                        tradeResponse.getOrder());
                            if (tradeStatus.getRequestStatus().equals(REQUEST_STATUS.REJECTED)) {
                                if (tradeStatus.getMessage().equals("Not enough money")) {
                                    outputFrame.updateOutput("No funds left.");
                                    clearAll();
                                    this.stop();
                                    break;
                                } else {
                                    deleteBuyPrices.add(key);
                                    continue;
                                }
                            } else if (tradeStatus.getRequestStatus().equals(REQUEST_STATUS.ACCEPTED)) {
                                outputFrame.updateOutput(tradeStatus);
                            }

                            deleteBuyPrices.add(key);

                            long curr_t = System.currentTimeMillis();
                            transactionDelay = (long) (curr_t + (delay * 1000));
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

    public void sellAlgorithm() throws Exception {
        STickRecord aux = updates.getRecord();
        if (aux != null) {
            if (!sellPrices.containsKey(aux.getBid())) {
                long curr_t = System.currentTimeMillis();
                long end = (long) (curr_t + (time * 1000));
                sellPrices.put(aux.getBid(), end);
            }

            for (HashMap.Entry<Double, Long> entry : sellPrices.entrySet()) {
                double key = entry.getKey();
                long value = entry.getValue();
                if (System.currentTimeMillis() >= transactionDelay) {
                    if (key - aux.getBid() >= diff) {
                        /* sell order */
                        TradeTransInfoRecord info = makeSellInfo(aux, value);
                        TradeTransactionResponse tradeResponse = APICommandFactory.executeTradeTransactionCommand(connector, info);
                        TradeTransactionStatusResponse tradeStatus = APICommandFactory.executeTradeTransactionStatusCommand(connector, tradeResponse.getOrder());
                        if (tradeStatus.getRequestStatus().equals(REQUEST_STATUS.REJECTED)) {
                            outputFrame.updateOutput("No funds left.");
                            clearAll();
                            this.stop();
                            break;
                        } else if (tradeStatus.getRequestStatus().equals(REQUEST_STATUS.ACCEPTED)) {
                            outputFrame.updateOutput(tradeStatus);
                        }

                        deleteSellPrices.add(key);

                        long curr_t = System.currentTimeMillis();
                        transactionDelay = (long) (curr_t + (delay * 1000));
                    }
                    if (value <= System.currentTimeMillis()) {
                        deleteSellPrices.add(key);
                    }
                }
            }
            if (!deleteSellPrices.isEmpty()) {
                sellPrices.keySet().removeAll(deleteSellPrices);
                deleteSellPrices.clear();
            }
        }
    }

    public void checkTransactions() throws Exception {
        STickRecord aux;
        if (System.currentTimeMillis() >= checkDelay) {
            if (connector != null) {
                try {
                    tds = APICommandFactory.executeTradesCommand(connector, true);
                    long curr_t1 = System.currentTimeMillis();
                    checkDelay = (long) (curr_t1 + (5.00 * 1000));
                }
                catch (Exception e) {
                    System.out.println("balva");
                }
            }
        }

        if (tds != null) {
            if (tds.getTradeRecords() != null) {
                int currTransactions = tds.getTradeRecords().size();
                if (currTransactions >= maxTransactions) {
                    if (bigMoneyTime && !blockTransactions) {
                        outputFrame.updateOutput("Maximum transactions reached");
                        blockTransactions = true;
                    } else if (blockTransactions && !bigMoneyTime) {
                        blockTransactions = false;
                    }
                } else if (currTransactions < maxTransactions && blockTransactions) {
                    blockTransactions = false;
                }
                for (TradeRecord td : tds.getTradeRecords()) {
                    aux = updates.getRecord();
                    if (aux != null && td != null) {
                        System.out.println(td.getPosition());
                        TradeTransInfoRecord info = null;
                        if (td.getCmd() == 0 && bigMoneyTime) {
                            if (aux.getAsk() >= (td.getOpen_price() + takeProfit * (trailingStop / 100.0f)) && bigMoneyTime) {
                                info = makeBuyChange(aux, td.getPosition(), td.getOpen_price());

                            }
                        } else if (td.getCmd() == 1 && bigMoneyTime) {
                            if (aux.getBid() <= (td.getOpen_price() - takeProfit * (trailingStop / 100.0f)) && bigMoneyTime) {
                                info = makeSellChange(aux, td.getPosition(), td.getOpen_price());
                            }
                        }
                        if (info != null) {
                            TradeTransactionResponse tradeResponse = null;
                            try {
                                tradeResponse = APICommandFactory.executeTradeTransactionCommand(connector, info);
                            } catch (Exception ignore) {
                            }
                            if (tradeResponse != null) {
                                TradeTransactionStatusResponse tradeStatus = APICommandFactory.executeTradeTransactionStatusCommand(connector,
                                        tradeResponse.getOrder());
                                if (tradeStatus.getRequestStatus().equals(REQUEST_STATUS.REJECTED)) {
                                    System.out.println("REJECTED : " + tradeStatus);
                                } else if (tradeStatus.getRequestStatus().equals(REQUEST_STATUS.ERROR)) {
                                    System.out.println("ERROR : " + tradeStatus);
                                } else {
                                    outputFrame.updateOutput("Order's (" + td.getPosition() + ") SL and TP were modified.");
                                }
                            }
                        }
                    }
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
                if (!blockTransactions) {
                    buyAlgorithm();
                    sellAlgorithm();
                }
                if (bigMoneyTime) {
                    checkTransactions();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}