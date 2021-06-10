package pro.xstore.api.sync.gui;

import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.SymbolRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.AllSymbolsResponse;
import pro.xstore.api.sync.SyncAPIConnector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.metal.MetalToggleButtonUI;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class MainPanel {
    private JComboBox<String> comboBox;
    private final LinkedList<String> marketList = new LinkedList<>();
    private final LinkedList<String> subscribedMarkets = new LinkedList<>();
    private final JTextField priceDiff = new JTextField("", 5);
    private final JTextField timeInterval = new JTextField("", 5);
    private final JTextField tradeVolume = new JTextField("", 5);
    private final JTextField stopLoss = new JTextField("", 5);
    private final JTextField takeProfit = new JTextField("", 5);
    private final JTextField maxTransactions = new JTextField("", 5);
    private final JTextField trailingStop = new JTextField("", 5);
    private final JTextField timeTransaction = new JTextField("", 5);
    private final HashMap<String, String> marketMap = new HashMap<>();
    private JLabel market_label;
    private JLabel priceDiff_label;
    private JLabel timeInterval_label;
    private JLabel tradeVolume_label;
    private JLabel stopLoss_label;
    private JLabel takeProfit_label;
    private JLabel maxTransactions_label;
    private JLabel trailingStop_label;
    private JLabel timeTransaction_label;
    private final JPanel simpleOrderPanel = new JPanel();
    private final JPanel mainPanel = new JPanel();
    private final JPanel bigMoneyPanel = new JPanel();
    private final JPanel header = new JPanel();
    private final JPanel footer = new JPanel();
    private String market_value = "";
    private double priceDiff_value = Double.MIN_VALUE;
    private double timeInterval_value = Double.MIN_VALUE;
    private double tradeVolume_value = Double.MIN_VALUE;
    private double stopLoss_value = Double.MIN_VALUE;
    private double takeProfit_value = Double.MIN_VALUE;
    private double maxTransactions_value = Double.MIN_VALUE;
    private double trailingStop_value = Double.MIN_VALUE;
    private double timeTransaction_value = Double.MIN_VALUE;
    private final MainThread trader = new MainThread();
    private final SyncAPIConnector connector;
    private String aux = null;

    public MainPanel(SyncAPIConnector conn) {
        this.connector = conn;
    }

    public void setValues(JTextField text) {
        if(text.equals(priceDiff)) {
            try {
                priceDiff_value = Double.parseDouble(text.getText());
            } catch (Exception e) {
                priceDiff_value = Double.MIN_VALUE;
                priceDiff_label.setText("Insert a number");
                priceDiff_label.setForeground(Color.red);
            }
        } else if (text.equals(timeInterval)) {
            try {
                timeInterval_value = Double.parseDouble(text.getText());
            } catch (Exception e) {
                timeInterval_value = Double.MIN_VALUE;
                timeInterval_label.setText("Insert a number");
                timeInterval_label.setForeground(Color.red);
            }
        } else if (text.equals(tradeVolume)) {
            try {
                tradeVolume_value = Double.parseDouble(text.getText());
            } catch (Exception e) {
                tradeVolume_value = Double.MIN_VALUE;
                tradeVolume_label.setText("Insert a number");
                tradeVolume_label.setForeground(Color.red);
            }
        } else if (text.equals(stopLoss)) {
            try {
                stopLoss_value = Double.parseDouble(text.getText());
            } catch (Exception e) {
                stopLoss_value = Double.MIN_VALUE;
                stopLoss_label.setText("Insert a number");
                stopLoss_label.setForeground(Color.red);
            }
        } else if (text.equals(takeProfit)) {
            try {
                takeProfit_value = Double.parseDouble(text.getText());
            } catch (Exception e) {
                takeProfit_value = Double.MIN_VALUE;
                takeProfit_label.setText("Insert a number");
                takeProfit_label.setForeground(Color.red);
            }
        } else if (text.equals(maxTransactions)) {
            try {
                maxTransactions_value = Double.parseDouble(text.getText());
            } catch (Exception e) {
                maxTransactions_value = Double.MIN_VALUE;
                maxTransactions_label.setText("Insert a number");
                maxTransactions_label.setForeground(Color.red);
            }
        } else if (text.equals(trailingStop)) {
            try {
                trailingStop_value = Double.parseDouble(text.getText());
            } catch (Exception e) {
                trailingStop_value = Double.MIN_VALUE;
                trailingStop_label.setText("Insert a number");
                trailingStop_label.setForeground(Color.red);
            }
        } else if (text.equals(timeTransaction)) {
            try {
                timeTransaction_value = Double.parseDouble(text.getText());
            } catch (Exception e) {
                timeTransaction_value = Double.MIN_VALUE;
                timeTransaction_label.setText("Insert a number");
                timeTransaction_label.setForeground(Color.red);
            }
        }
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
        text.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
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

    public boolean getOptionalValues() {
        setValues(stopLoss);
        setValues(takeProfit);
        setValues(maxTransactions);
        setValues(trailingStop);
        setValues(timeTransaction);
        return stopLoss_value != Double.MIN_VALUE && takeProfit_value != Double.MIN_VALUE
                && maxTransactions_value != Double.MIN_VALUE && trailingStop_value != Double.MIN_VALUE
                && timeTransaction_value != Double.MIN_VALUE;
    }

    public boolean getNecessaryValues() {
        setValues(priceDiff);
        setValues(timeInterval);
        setValues(tradeVolume);
        return (!(market_value.equals("")) && (marketList.contains(market_value))) && priceDiff_value != Double.MIN_VALUE
                && timeInterval_value != Double.MIN_VALUE && tradeVolume_value != Double.MIN_VALUE;
    }

    public boolean getSavedOptionals() {
        boolean allGood = true;
        if (!stopLoss.getText().equals("")) {
            setValues(stopLoss);
            if (stopLoss_value == Double.MIN_VALUE) {
                allGood = false;
            }
        }
        if (!takeProfit.getText().equals("")) {
            setValues(takeProfit);
            if (takeProfit_value == Double.MIN_VALUE) {
                allGood = false;
            }
        }
        if (!maxTransactions.getText().equals("")) {
            setValues(maxTransactions);
            if (maxTransactions_value == Double.MIN_VALUE) {
                allGood = false;
            }
        }
        if (!trailingStop.getText().equals("")) {
            setValues(trailingStop);
            if (trailingStop_value == Double.MIN_VALUE) {
                allGood = false;
            }
        }
        if (!timeTransaction.getText().equals("")) {
            setValues(timeTransaction);
            if (timeTransaction_value == Double.MIN_VALUE) {
                allGood = false;
            }
        }
        return allGood;
    }

    public void resetOptions() {
        stopLoss.setText("");
        takeProfit.setText("");
        maxTransactions.setText("");
        trailingStop.setText("");
        timeTransaction.setText("");
        priceDiff.setText("");
        timeInterval.setText("");
        tradeVolume.setText("");
        comboBox.getEditor().setItem("");
    }

    public JPanel buildMainPanel() throws APIErrorResponse, APICommunicationException, APIReplyParseException, APICommandConstructionException {
        ImageIcon logo = new ImageIcon(new ImageIcon("./src/Media/logo.jpeg").getImage().getScaledInstance(100, 60, Image.SCALE_SMOOTH));
        ImageIcon info = new ImageIcon(new ImageIcon("./src/Media/info.png").getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH));
        JLabel label = new JLabel(logo);
        simpleOrderPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 80, 20));
        bigMoneyPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 55, 20));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(10, 10, 10, 10));
        header.setPreferredSize(new Dimension(1100, 100));
        header.setMaximumSize(new Dimension(1100, 100));


        footer.setLayout(new BorderLayout());
        footer.setBorder(new EmptyBorder(0, 10, 0, 0));
        footer.setPreferredSize(new Dimension(1100, 100));
        footer.setMaximumSize(new Dimension(1100, 100));

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

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
        comboBox.setFont(new Font("Comic Sans MS", Font.BOLD, 14));

        JComboBoxDecorator.decorate(comboBox, true, marketList);

        market_label = new JLabel("*Market", info, JLabel.CENTER);
        market_label.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        market_label.setHorizontalTextPosition(JLabel.LEFT);
        market_label.setToolTipText("Dropdown with all the markets from which to choose one market on which transactions will be made.");

        priceDiff_label = new JLabel("*Price Difference", info, JLabel.CENTER);
        priceDiff_label.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        priceDiff_label.setHorizontalTextPosition(JLabel.LEFT);
        priceDiff_label.setToolTipText("The price difference for which to look out for at each market's price change.");


        timeInterval_label = new JLabel("*Time Interval", info, JLabel.CENTER);
        timeInterval_label.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        timeInterval_label.setHorizontalTextPosition(JLabel.LEFT);
        timeInterval_label.setToolTipText("The interval of time in which to look out for the price difference at each market's price change.");


        tradeVolume_label = new JLabel("*Trade volume", info, JLabel.CENTER);
        tradeVolume_label.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        tradeVolume_label.setHorizontalTextPosition(JLabel.LEFT);
        tradeVolume_label.setToolTipText("The volume of currency that will be used for each transaction.");

        stopLoss_label = new JLabel("Stop Loss", info, JLabel.CENTER);
        stopLoss_label.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        stopLoss_label.setHorizontalTextPosition(JLabel.LEFT);
        stopLoss_label.setToolTipText("<html>A subtraction, to the price of each future transaction, that when it's reached<br/>" +
                                        "by the market's price, the transaction will automatically be closed.</html>");

        takeProfit_label = new JLabel("Take Profit", info, JLabel.CENTER);
        takeProfit_label.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        takeProfit_label.setHorizontalTextPosition(JLabel.LEFT);
        takeProfit_label.setToolTipText("<html>An addition, to the price of each future transaction, that when it's reached<br/>" +
                                        "by the market's price, the transaction will automatically be closed.</html>");

        maxTransactions_label = new JLabel("Max Transactions", info, JLabel.CENTER);
        maxTransactions_label.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        maxTransactions_label.setHorizontalTextPosition(JLabel.LEFT);
        maxTransactions_label.setToolTipText("The number of maximum transactions that will made.");


        trailingStop_label = new JLabel("Trailing Stop(%)", info, JLabel.CENTER);
        trailingStop_label.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        trailingStop_label.setHorizontalTextPosition(JLabel.LEFT);
        trailingStop_label.setToolTipText("<html>If the market's price moves towards the Take Profit that was set with the percentage provided<br/>" +
                                            "then the values Stop Loss and Take Profit are updated for the opened transactions.<html>");

        timeTransaction_label = new JLabel("Time/Transactions", info, JLabel.CENTER);
        timeTransaction_label.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        timeTransaction_label.setHorizontalTextPosition(JLabel.LEFT);
        timeTransaction_label.setToolTipText("The time between each future transactions.");

        JPanel market_panel = new JPanel();
        market_panel.setBackground(Color.white);
        JPanel priceDiff_panel = new JPanel();
        priceDiff_panel.setBackground(Color.white);
        JPanel timeInterval_panel = new JPanel();
        timeInterval_panel.setBackground(Color.white);
        JPanel tradeVolume_panel = new JPanel();
        tradeVolume_panel.setBackground(Color.white);
        JPanel stopLoss_panel = new JPanel();
        stopLoss_panel.setBackground(Color.white);
        JPanel takeProfit_panel = new JPanel();
        takeProfit_panel.setBackground(Color.white);
        JPanel maxTransactions_panel = new JPanel();
        maxTransactions_panel.setBackground(Color.white);
        JPanel trailingStop_panel = new JPanel();
        trailingStop_panel.setBackground(Color.white);
        JPanel timeTransaction_panel = new JPanel();
        timeTransaction_panel.setBackground(Color.white);

        market_panel.setLayout(new BoxLayout(market_panel, BoxLayout.PAGE_AXIS));
        market_panel.add(market_label);
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        market_panel.add(comboBox);
        simpleOrderPanel.add(market_panel);

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
        simpleActive.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));

        JButton resetButton = new JButton("Reset Options");
        resetButton.setFocusPainted(false);
        resetButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        resetButton.addActionListener(e -> resetOptions());

        JButton saveButton = new JButton("Save Options");
        saveButton.setFocusPainted(false);
        saveButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        saveButton.addActionListener(e -> {
            setMarket();
            SaveFrame saveOption;
            if (getNecessaryValues()) {
                checkValues();
                saveOption = new SaveFrame(market_value, priceDiff_value, timeInterval_value, tradeVolume_value);
                if (getSavedOptionals()) {
                    saveOption.saveBigDickOptions(stopLoss_value, takeProfit_value, maxTransactions_value, trailingStop_value, timeTransaction_value);
                    saveOption.run();
                }
            }
        });

        JToggleButton toggleButton = new JToggleButton("Activate additional fields");
        toggleButton.setFocusPainted(false);
        toggleButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        toggleButton.setToolTipText("While this is active, it adds the additional fields to all the next transactions.");
        ItemListener itemListener = itemEvent -> {
            int state = itemEvent.getStateChange();
            if (state == ItemEvent.SELECTED) {
                toggleButton.setText("OFF");
                if (getOptionalValues()) {
                    checkValues();
                    disableBigMoneyFields();
                    trader.setBigMoney(stopLoss_value, takeProfit_value, maxTransactions_value, trailingStop_value, timeTransaction_value);
                } else {
                    toggleButton.setSelected(false);
                }
            } else {
                enableBigMoneyFields();
                trader.setToDefault();
                toggleButton.setText("Activate additional fields");
            }
        };
        toggleButton.addItemListener(itemListener);

        JButton loadButton = new JButton("Load Options");
        loadButton.setFocusPainted(false);
        loadButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        loadButton.addActionListener(e -> {
            LoadFrame loadOption = new LoadFrame(comboBox, priceDiff, timeInterval, tradeVolume,
                    stopLoss, takeProfit, maxTransactions, trailingStop, timeTransaction);
            try {
                loadOption.run();
                toggleButton.setSelected(false);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        JToggleButton button = new JToggleButton("Place Order");
        button.setFocusPainted(false);
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        ItemListener itemListener2 = itemEvent -> {
            int state = itemEvent.getStateChange();
            trader.setConnector(connector);
            if (state == ItemEvent.SELECTED) {
                setMarket();
                if (getNecessaryValues()) {
                    checkValues();
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
                    loadButton.setEnabled(false);
                    resetButton.setEnabled(false);
                    trader.start();
                } else {
                    aux = null;
                    button.setSelected(false);
                }
            } else {
                button.setText("Place Order");
                enableSimpleFields();
                loadButton.setEnabled(true);
                resetButton.setEnabled(true);
                if (aux != null) {
                    trader.stop();
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

        simpleActive.add(Box.createRigidArea(new Dimension(10, 75)));
        saveButton.setBackground(new Color(0,104,55));
        saveButton.setForeground(Color.white);
        simpleActive.add(saveButton);

        loadButton.setBackground(new Color(0,104,55));
        loadButton.setForeground(Color.white);
        simpleActive.add(loadButton);

        resetButton.setBackground(new Color(0,104,55));
        resetButton.setForeground(Color.white);
        simpleActive.add(resetButton);

        simpleActive.setBackground(Color.white);
        header.add(label, BorderLayout.LINE_START);
        header.add(simpleActive, BorderLayout.LINE_END);
        header.setBackground(Color.white);

        mainPanel.add(header);

        toggleButton.setBackground(new Color(0,104,55));
        toggleButton.setForeground(Color.white);
        toggleButton.setUI(new MetalToggleButtonUI() {
            @Override
            protected Color getSelectColor() {
                return new Color(0,104,55).brighter();
            }
        });
        bigMoneyActive.add(toggleButton);

        bigMoneyActive.setBackground(Color.white);
        mainPanel.add(simpleOrderPanel);
        JPanel placeOrderPanel = new JPanel();
        button.setBackground(new Color(0,104,55));
        button.setForeground(Color.white);
        placeOrderPanel.add(button);
        placeOrderPanel.setBackground(Color.white);
        mainPanel.add(placeOrderPanel);
        simpleOrderPanel.setBackground(Color.white);
        bigMoneyPanel.setBackground(Color.white);

        JPanel information = new JPanel();
        JLabel importantFields = new JLabel("All the fields marked with * are mandatory!", info, JLabel.CENTER);
        importantFields.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        information.add(importantFields);
        information.setBackground(Color.white);

        mainPanel.add(new JSeparator(), "cell 1 0,growx");
        mainPanel.add(bigMoneyPanel);
        mainPanel.add(bigMoneyActive);
        mainPanel.add(information);
        mainPanel.setBackground(Color.white);

        JLabel copyright = new JLabel("Developed using xAPI from XTB Platform");
        copyright.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        copyright.setForeground(Color.white);
        footer.add(copyright, BorderLayout.LINE_START);
        footer.setBackground(Color.black);
        mainPanel.add(footer);

        return mainPanel;
    }
}
