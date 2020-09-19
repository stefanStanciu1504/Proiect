package pro.xstore.api.sync.gui;
import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.records.SymbolRecord;
import pro.xstore.api.message.response.AllSymbolsResponse;
import pro.xstore.api.message.response.LogoutResponse;
import pro.xstore.api.sync.SyncAPIConnector;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class TradeFrame extends JFrame {
    private final SyncAPIConnector connector;
    private static JComboBox<String> comboBox;
    private static final LinkedList<String> marketList = new LinkedList<>();
    private static final LinkedList<String> subscribedMarkets = new LinkedList<>();
    private static final JTextField priceDiff = new JTextField("", 5);
    private static final JTextField timeInterval = new JTextField("", 5);
    private static final JTextField tradeVolume = new JTextField("", 5);
    private static final JTextField stopLoss = new JTextField("", 5);
    private static final JTextField takeProfit = new JTextField("", 5);
    private static final JTextField maxTransactions = new JTextField("", 5);
    private static final JTextField trailingStop = new JTextField("", 5);
    private static final JTextField timeTransaction = new JTextField("", 5);
    private static final HashMap<String, String> marketMap = new HashMap<>();
    private static JLabel market_label;
    private static JLabel priceDiff_label;
    private static JLabel timeInterval_label;
    private static JLabel tradeVolume_label;
    private static JLabel stopLoss_label;
    private static JLabel takeProfit_label;
    private static JLabel maxTransactions_label;
    private static JLabel trailingStop_label;
    private static JLabel timeTransaction_label;
    private static final JPanel simpleOrderPanel = new JPanel();
    private static final JPanel mainPanel = new JPanel();
    private static final JPanel bigMoneyPanel = new JPanel();
    private static final JPanel balancePanel = new JPanel();
    private static String market_value = "";
    private double priceDiff_value = Double.MIN_VALUE;
    private double timeInterval_value = Double.MIN_VALUE;
    private double tradeVolume_value = Double.MIN_VALUE;
    private double stopLoss_value = Double.MIN_VALUE;
    private double takeProfit_value = Double.MIN_VALUE;
    private double maxTransactions_value = Double.MIN_VALUE;
    private double trailingStop_value = Double.MIN_VALUE;
    private double timeTransaction_value = Double.MIN_VALUE;
    private static final TradeThread trader = new TradeThread();
    private static String aux = null;

    public TradeFrame(SyncAPIConnector aux_connector) {
        connector = aux_connector;
    }

    public void setValues(JTextField text) {
        if(text.equals(priceDiff)) {
            priceDiff_value = Double.parseDouble(text.getText());
        } else if (text.equals(timeInterval)) {
            timeInterval_value = Double.parseDouble(text.getText());
        } else if (text.equals(tradeVolume)) {
            tradeVolume_value = Double.parseDouble(text.getText());
        } else if (text.equals(stopLoss)) {
            stopLoss_value = Double.parseDouble(text.getText());
        } else if (text.equals(takeProfit)) {
            takeProfit_value = Double.parseDouble(text.getText());
        } else if (text.equals(maxTransactions)) {
            maxTransactions_value = Double.parseDouble(text.getText());
        } else if (text.equals(trailingStop)) {
            trailingStop_value = Double.parseDouble(text.getText());
        } else if (text.equals(timeTransaction)) {
            timeTransaction_value = Double.parseDouble(text.getText());
        }
    }

    public boolean checkBigDick() {
        return stopLoss_value != Double.MIN_VALUE && takeProfit_value != Double.MIN_VALUE &&
                maxTransactions_value != Double.MIN_VALUE && trailingStop_value != Double.MIN_VALUE &&
                timeTransaction_value != Double.MIN_VALUE;
    }

    public void setMarket() {
        market_value = String.valueOf(comboBox.getEditor().getItem());
        if ((market_value.equals("")) || (!marketList.contains(market_value))) {
            market_label.setForeground(Color.red);
            market_label.setText("Choose a valid market!");
        }
    }

    public void addPanel(JPanel panel, JTextField text, JLabel label, JPanel masterPanel) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(label);
        panel.add(text);
        masterPanel.add(panel);
    }

    public void checkValues() {
        if (market_label.getText().equals("Choose a valid market!")) {
            market_label.setForeground(Color.darkGray);
            market_label.setText("Market");
        }
        if ((priceDiff_value == Double.MIN_VALUE) ||
                (priceDiff_label.getText().equals("Insert a number"))) {
            priceDiff_label.setForeground(Color.darkGray);
            priceDiff_label.setText("Price Difference");
        }
        if ((timeInterval_value == Double.MIN_VALUE) ||
                (timeInterval_label.getText().equals("Insert a number"))) {
            timeInterval_label.setForeground(Color.darkGray);
            timeInterval_label.setText("Time Interval");
        }
        if ((tradeVolume_value == Double.MIN_VALUE) ||
                (tradeVolume_label.getText().equals("Insert a number"))) {
            tradeVolume_label.setForeground(Color.darkGray);
            tradeVolume_label.setText("Trade volume");
        }
        if ((stopLoss_value == Double.MIN_VALUE) ||
                (stopLoss_label.getText().equals("Insert a number"))) {
            stopLoss_label.setForeground(Color.darkGray);
            stopLoss_label.setText("Stop Loss");
        }
        if ((takeProfit_value == Double.MIN_VALUE) ||
                (takeProfit_label.getText().equals("Insert a number"))) {
            takeProfit_label.setForeground(Color.darkGray);
            takeProfit_label.setText("Take Profit");
        }
        if ((maxTransactions_value == Double.MIN_VALUE) ||
                (maxTransactions_label.getText().equals("Insert a number"))) {
            maxTransactions_label.setForeground(Color.darkGray);
            maxTransactions_label.setText("Max Transactions");
        }
        if ((trailingStop_value == Double.MIN_VALUE) ||
                (trailingStop_label.getText().equals("Insert a number"))) {
            trailingStop_label.setForeground(Color.darkGray);
            trailingStop_label.setText("Trailing Stop(%)");
        }
        if ((timeTransaction_value == Double.MIN_VALUE) ||
                (timeTransaction_label.getText().equals("Insert a number"))) {
            timeTransaction_label.setForeground(Color.darkGray);
            timeTransaction_label.setText("Time/Transactions");
        }
    }

    public void disableSimpleFields() {
        priceDiff.setEditable(false);
        timeInterval.setEditable(false);
        tradeVolume.setEditable(false);
    }

    public void enableSimpleFields() {
        priceDiff.setEditable(true);
        timeInterval.setEditable(true);
        tradeVolume.setEditable(true);
    }

    public void disableBigMoneyFields() {
        stopLoss.setEditable(false);
        takeProfit.setEditable(false);
        maxTransactions.setEditable(false);
        trailingStop.setEditable(false);
        timeTransaction.setEditable(false);
    }

    public void enableBigMoneyFields() {
        stopLoss.setEditable(true);
        takeProfit.setEditable(true);
        maxTransactions.setEditable(true);
        trailingStop.setEditable(true);
        timeTransaction.setEditable(true);
    }


    public void addListener(JTextField field, JLabel label) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }
            public void removeUpdate(DocumentEvent e) {
                warn();
            }
            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                if (field.equals(tradeVolume)) {
                    takeTransaction(field, label);
                }
                else {
                    takeValue(field, label);
                }
            }
        });
    }

    public void takeTransaction(JTextField text, JLabel label) {
        try {
            setValues(text);
//            if (tradeVolume_value > accountBalance_value) {
//                label.setText("Not Enough Funds");
//                label.setForeground(Color.red);
//            } else {
//                tradeVolume_label.setForeground(Color.darkGray);
//                tradeVolume_label.setText("Trade volume");
//            }
            checkValues();
        } catch (Exception ex) {
            label.setText("Insert a number");
            label.setForeground(Color.red);
        }
    }

    public void takeValue(JTextField text, JLabel label) {
        try {
            setValues(text);
            checkValues();
        } catch (Exception ex) {
            label.setText("Insert a number");
            label.setForeground(Color.red);
        }
    }

    public boolean checkNecessaryValues() {
        return (!(market_value.equals("")) || (marketList.contains(market_value))) && priceDiff_value != Double.MIN_VALUE
                && timeInterval_value != Double.MIN_VALUE && tradeVolume_value != Double.MIN_VALUE;
    }

    public void run() throws Exception {
        JFrame frame = new JFrame("TradeFrame");
        simpleOrderPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        bigMoneyPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        balancePanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 20));

        JLabel accountBalance_label;
        if (connector.getBalanceRecord() == null) {
            accountBalance_label = new JLabel("Account Balance: " + "fucking wanker server");
        } else {
            double accountBalance_value = connector.getBalanceRecord().getBalance();
            accountBalance_label = new JLabel("Account Balance: " + accountBalance_value);
        }
        balancePanel.setPreferredSize(accountBalance_label.getPreferredSize());
        balancePanel.add(accountBalance_label, BorderLayout.LINE_END);
        mainPanel.add(balancePanel);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    LogoutResponse logout = APICommandFactory.executeLogoutCommand(connector);
                    if (logout.getStatus()) {
                        frame.dispose();
                        connector.close();
                        System.exit(0);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        AllSymbolsResponse allSymbols = APICommandFactory.executeAllSymbolsCommand(connector);
        marketList.add("");

        for (SymbolRecord idx : allSymbols.getSymbolRecords()) {
            int aux = idx.getSymbol().indexOf("_");
            String symbol;
            if (aux != -1) {
                symbol = idx.getSymbol().substring(0, aux);
            } else {
                symbol = idx.getSymbol();
            }
            marketMap.put(symbol, idx.getSymbol());

            if (idx.getDescription().contains("CFD")) {
                String category = idx.getCategoryName().concat(" CFD");
                marketList.add(symbol + " " + category);
            } else {
                marketList.add(symbol + " " + idx.getCategoryName());
            }
        }

        comboBox = new JComboBox<>(marketList.toArray(new String[0]));
        comboBox.setMaximumRowCount(10);
        comboBox.setPreferredSize(comboBox.getPreferredSize());
        comboBox.setMaximumSize(comboBox.getPreferredSize());

        JComboBoxDecorator.decorate(comboBox, true, marketList);

        market_label = new JLabel("Market");
        priceDiff_label = new JLabel("Price Difference");
        timeInterval_label = new JLabel("Time Interval");
        tradeVolume_label = new JLabel("Trade volume");
        stopLoss_label = new JLabel("Stop Loss");
        takeProfit_label = new JLabel("Take Profit");
        maxTransactions_label = new JLabel("Max Transactions");
        trailingStop_label = new JLabel("Trailing Stop(%)");
        timeTransaction_label = new JLabel("Time/Transactions");

        JPanel market_panel = new JPanel();
        JPanel priceDiff_panel = new JPanel();
        JPanel timeInterval_panel = new JPanel();
        JPanel tradeVolume_panel = new JPanel();
        JPanel stopLoss_panel = new JPanel();
        JPanel takeProfit_panel = new JPanel();
        JPanel maxTransactions_panel = new JPanel();
        JPanel trailingStop_panel = new JPanel();
        JPanel timeTransaction_panel = new JPanel();

        market_panel.setLayout(new BoxLayout(market_panel, BoxLayout.PAGE_AXIS));
        market_panel.add(market_label);
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        market_panel.add(comboBox);
        simpleOrderPanel.add(market_panel);

        addListener(priceDiff, priceDiff_label);
        addListener(timeInterval, timeInterval_label);
        addListener(tradeVolume, tradeVolume_label);

        addListener(stopLoss, stopLoss_label);
        addListener(takeProfit, takeProfit_label);
        addListener(maxTransactions, maxTransactions_label);
        addListener(trailingStop, trailingStop_label);
        addListener(timeTransaction, timeTransaction_label);
        

        addPanel(priceDiff_panel, priceDiff, priceDiff_label, simpleOrderPanel);
        addPanel(timeInterval_panel, timeInterval, timeInterval_label, simpleOrderPanel);
        addPanel(tradeVolume_panel, tradeVolume, tradeVolume_label, simpleOrderPanel);

        addPanel(stopLoss_panel, stopLoss, stopLoss_label, bigMoneyPanel);
        addPanel(takeProfit_panel, takeProfit, takeProfit_label, bigMoneyPanel);
        addPanel(maxTransactions_panel, maxTransactions, maxTransactions_label, bigMoneyPanel);
        addPanel(trailingStop_panel, trailingStop, trailingStop_label, bigMoneyPanel);
        addPanel(timeTransaction_panel, timeTransaction, timeTransaction_label, bigMoneyPanel);


        JPanel bigMoneyActive = new JPanel();
        JPanel simpleActive = new JPanel();

        JToggleButton toggleButton = new JToggleButton("ON");
        ItemListener itemListener = itemEvent -> {
            int state = itemEvent.getStateChange();
            if (state == ItemEvent.SELECTED) {
                toggleButton.setText("OFF");
                disableBigMoneyFields();
                trader.setBigMoney(stopLoss_value, takeProfit_value, maxTransactions_value, trailingStop_value, timeTransaction_value);
            } else {
                enableBigMoneyFields();
                trader.setToDefault();
                toggleButton.setText("ON");
            }
        };
        toggleButton.addItemListener(itemListener);

        JToggleButton button = new JToggleButton("Place Order");
        ItemListener itemListener2 = itemEvent -> {
            int state = itemEvent.getStateChange();
            trader.setConnector(connector);
            if (state == ItemEvent.SELECTED) {
                setMarket();
                if (checkNecessaryValues()) {
                    aux = marketMap.get(market_value.split(" ")[0]);
                    if (!subscribedMarkets.contains(aux)) {
                        subscribedMarkets.add(aux);
                    }
                    OutputFrame outFrame = new OutputFrame();
                    outFrame.reset();
                    outFrame.run();
                    trader.setEssentials(aux, subscribedMarkets, timeInterval_value, priceDiff_value, tradeVolume_value, outFrame);
                    button.setText("Stop");
                    disableSimpleFields();
                    trader.start();
                } else {
                    aux = null;
                }
            } else {
                button.setText("Place Order");
                enableSimpleFields();
                if (aux != null) {
                    trader.stop();
                    trader.clearAll();
                    try {
                        connector.unsubscribePrice(aux);
                        connector.unsubscribeTrades();
                    } catch (APICommunicationException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        button.addItemListener(itemListener2);

        simpleActive.add(button);
        JButton saveOptions = new JButton("Save Options");
        saveOptions.addActionListener(e -> {
            setMarket();
            SaveFrame saveOption = new SaveFrame(market_value, priceDiff_value, timeInterval_value, tradeVolume_value);
            if (checkBigDick()) {
                saveOption.saveBigDickOptions(stopLoss_value, takeProfit_value, maxTransactions_value, trailingStop_value, timeTransaction_value);
            }
            saveOption.run();
        });
        JButton loadOptions = new JButton("Load Options");
        loadOptions.addActionListener(e -> {
            LoadFrame loadOption = new LoadFrame(comboBox, priceDiff, timeInterval, tradeVolume,
                            stopLoss, takeProfit, maxTransactions, trailingStop, timeTransaction);
            try {
                loadOption.run();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        simpleActive.add(saveOptions);
        simpleActive.add(loadOptions);
        bigMoneyActive.add(toggleButton);

        mainPanel.add(simpleOrderPanel);
        mainPanel.add(simpleActive);
        mainPanel.add(bigMoneyPanel);
        mainPanel.add(bigMoneyActive);

        frame.getContentPane().add(mainPanel);
        frame.setSize(780, 480);
        frame.setVisible(true);

    }
}