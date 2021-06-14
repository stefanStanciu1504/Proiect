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
    private JLabel subtitle;
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
    private boolean showOutput = false;
    private String aux = null;

    public MainPanel(SyncAPIConnector conn) {
        this.connector = conn;
    }

    /* sets the values from the textfields */
    public void setValues(JTextField text) {
        if(text.equals(priceDiff)) {
            try {
                priceDiff_value = Double.parseDouble(text.getText());
                if (priceDiff_value == 0.0) {
                    priceDiff_value = Double.MIN_VALUE;
                    priceDiff_label.setText("Can't be 0");
                    priceDiff_label.setForeground(Color.red);
                }
            } catch (Exception e) {
                priceDiff_value = Double.MIN_VALUE;
                priceDiff_label.setText("Insert a number");
                priceDiff_label.setForeground(Color.red);
            }
        } else if (text.equals(timeInterval)) {
            try {
                timeInterval_value = Double.parseDouble(text.getText());
                if (timeInterval_value == 0.0) {
                    timeInterval_value = Double.MIN_VALUE;
                    timeInterval_label.setText("Can't be 0");
                    timeInterval_label.setForeground(Color.red);
                }
            } catch (Exception e) {
                timeInterval_value = Double.MIN_VALUE;
                timeInterval_label.setText("Insert a number");
                timeInterval_label.setForeground(Color.red);
            }
        } else if (text.equals(tradeVolume)) {
            try {
                tradeVolume_value = Double.parseDouble(text.getText());
                if (tradeVolume_value == 0.0) {
                    tradeVolume_value = Double.MIN_VALUE;
                    tradeVolume_label.setText("Can't be 0");
                    tradeVolume_label.setForeground(Color.red);
                }
            } catch (Exception e) {
                tradeVolume_value = Double.MIN_VALUE;
                tradeVolume_label.setText("Insert a number");
                tradeVolume_label.setForeground(Color.red);
            }
        } else if (text.equals(stopLoss)) {
            if (!text.getText().equals("")) {
                try {
                    stopLoss_value = Double.parseDouble(text.getText());
                    if (stopLoss_value == 0.0) {
                        stopLoss_value = Double.MIN_VALUE;
                        stopLoss_label.setText("Can't be 0");
                        stopLoss_label.setForeground(Color.red);
                    }
                } catch (Exception e) {
                    stopLoss_value = Double.MIN_VALUE;
                    stopLoss_label.setText("Insert a number");
                    stopLoss_label.setForeground(Color.red);
                }
            } else {
                stopLoss_value = Double.MIN_VALUE;
            }
        } else if (text.equals(takeProfit)) {
            if (!text.getText().equals("")) {
                try {
                    takeProfit_value = Double.parseDouble(text.getText());
                    if (takeProfit_value == 0.0) {
                        takeProfit_value = Double.MIN_VALUE;
                        takeProfit_label.setText("Can't be 0");
                        takeProfit_label.setForeground(Color.red);
                    }
                } catch (Exception e) {
                    takeProfit_value = Double.MIN_VALUE;
                    takeProfit_label.setText("Insert a number");
                    takeProfit_label.setForeground(Color.red);
                }
            } else {
                takeProfit_value = Double.MIN_VALUE;
            }
        } else if (text.equals(maxTransactions)) {
            if (!text.getText().equals("")) {
                try {
                    maxTransactions_value = Double.parseDouble(text.getText());
                    if (maxTransactions_value == 0.0) {
                        maxTransactions_value = Double.MIN_VALUE;
                        maxTransactions_label.setText("Can't be 0");
                        maxTransactions_label.setForeground(Color.red);
                    }
                } catch (Exception e) {
                    maxTransactions_value = Double.MIN_VALUE;
                    maxTransactions_label.setText("Insert a number");
                    maxTransactions_label.setForeground(Color.red);
                }
            } else {
                maxTransactions_value = Double.MIN_VALUE;
            }
        } else if (text.equals(trailingStop)) {
            if (!text.getText().equals("")) {
                try {
                    trailingStop_value = Double.parseDouble(text.getText());
                    if (trailingStop_value == 0.0) {
                        trailingStop_value = Double.MIN_VALUE;
                        trailingStop_label.setText("Can't be 0");
                        trailingStop_label.setForeground(Color.red);
                    }
                } catch (Exception e) {
                    trailingStop_value = Double.MIN_VALUE;
                    trailingStop_label.setText("Insert a number");
                    trailingStop_label.setForeground(Color.red);
                }
            } else {
                trailingStop_value = Double.MIN_VALUE;
            }
        } else if (text.equals(timeTransaction)) {
            if (!text.getText().equals("")) {
                try {
                    timeTransaction_value = Double.parseDouble(text.getText());
                    if (timeTransaction_value == 0.0) {
                        trailingStop_value = Double.MIN_VALUE;
                        timeTransaction_label.setText("Can't be 0");
                        timeTransaction_label.setForeground(Color.red);
                    }
                } catch (Exception e) {
                    timeTransaction_value = Double.MIN_VALUE;
                    timeTransaction_label.setText("Insert a number");
                    timeTransaction_label.setForeground(Color.red);
                }
            } else {
                timeTransaction_value = Double.MIN_VALUE;
            }
        }
    }

    /* gets the value from the markets dropdown */
    public void setMarket() {
        market_value = String.valueOf(comboBox.getEditor().getItem());
        if ((market_value.equals("")) || (!marketList.contains(market_value))) {
            market_label.setForeground(Color.red);
            market_label.setText("Choose a valid market!");
        }
    }

    /* adds to a panel a textfield and label */
    public void addPanel(JPanel panel, JTextField text, JLabel label, JPanel masterPanel) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(label);
        text.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        text.setColumns(4);
        panel.add(text);
        masterPanel.add(panel);
    }

    /* resets to default the mandatory labels */
    public void checkMandatoryValues() {
        if (market_label.getText().equals("Choose a valid market!") &&
                !(market_value.equals("")) && (marketList.contains(market_value))) {
            market_label.setForeground(Color.black);
            market_label.setText("Market");
        }
        if (((priceDiff_label.getText().equals("Insert a number")) ||
                (priceDiff_label.getText().equals("Can't be 0"))) && (priceDiff_value != Double.MIN_VALUE)) {
            priceDiff_label.setForeground(Color.black);
            priceDiff_label.setText("Price Difference");
        }
        if (((timeInterval_label.getText().equals("Insert a number")) ||
                (timeInterval_label.getText().equals("Can't be 0")))  && (timeInterval_value != Double.MIN_VALUE)) {
            timeInterval_label.setForeground(Color.black);
            timeInterval_label.setText("Time Interval");
        }
        if (((tradeVolume_label.getText().equals("Insert a number")) ||
                (tradeVolume_label.getText().equals("Can't be 0"))) && (tradeVolume_value != Double.MIN_VALUE)) {
            tradeVolume_label.setForeground(Color.black);
            tradeVolume_label.setText("Trade volume");
        }
    }

    /* resets to default the optional labels */
    private void defaultOptionalLabels() {
        if (stopLoss_label.getText().equals("Insert a number") ||
            (stopLoss_label.getText().equals("Can't be 0"))) {
            stopLoss_label.setForeground(Color.black);
            stopLoss_label.setText("Stop Loss");
        }
        if (takeProfit_label.getText().equals("Insert a number") ||
            (takeProfit_label.getText().equals("Can't be 0"))) {
            takeProfit_label.setForeground(Color.black);
            takeProfit_label.setText("Take Profit");
        }
        if (maxTransactions_label.getText().equals("Insert a number") ||
            (maxTransactions_label.getText().equals("Can't be 0"))) {
            maxTransactions_label.setForeground(Color.black);
            maxTransactions_label.setText("Max Transactions");
        }
        if (trailingStop_label.getText().equals("Insert a number") ||
            (trailingStop_label.getText().equals("Can't be 0"))) {
            trailingStop_label.setForeground(Color.black);
            trailingStop_label.setText("Trailing Stop(%)");
        }
        if (timeTransaction_label.getText().equals("Insert a number") ||
            (timeTransaction_label.getText().equals("Can't be 0"))) {
            timeTransaction_label.setForeground(Color.black);
            timeTransaction_label.setText("Time/Transactions");
        }
    }

    /* checks if the optional values have been changed */
    private boolean checkOptionalValues() {
        return (stopLoss_value != Double.MIN_VALUE) ||
                (takeProfit_value != Double.MIN_VALUE) ||
                (maxTransactions_value != Double.MIN_VALUE) ||
                (trailingStop_value != Double.MIN_VALUE) ||
                (timeTransaction_value != Double.MIN_VALUE);
    }

    /* checks if the optional labels have been changed */
    private boolean checkOptionalLabels() {
        return !stopLoss_label.getText().equals("Insert a number") && !stopLoss_label.getText().equals("Can't be 0") &&
                !takeProfit_label.getText().equals("Insert a number") && !takeProfit_label.getText().equals("Can't be 0") &&
                !maxTransactions_label.getText().equals("Insert a number") && !maxTransactions_label.getText().equals("Can't be 0") &&
                !trailingStop_label.getText().equals("Insert a number") && !trailingStop_label.getText().equals("Can't be 0") &&
                !timeTransaction_label.getText().equals("Insert a number") && !timeTransaction_label.getText().equals("Can't be 0");
    }

    /* disables the mandatory fields, makes the uneditable */
    public void disableSimpleFields() {
        comboBox.setEnabled(false);
        priceDiff.setEditable(false);
        timeInterval.setEditable(false);
        tradeVolume.setEditable(false);
    }

    /* enables the mandatory fields, makes them editable */
    public void enableSimpleFields() {
        comboBox.setEnabled(true);
        priceDiff.setEditable(true);
        timeInterval.setEditable(true);
        tradeVolume.setEditable(true);
    }

    /* disables the optional fields, makes the uneditable */
    public void disableBigMoneyFields() {
        stopLoss.setEditable(false);
        takeProfit.setEditable(false);
        maxTransactions.setEditable(false);
        trailingStop.setEditable(false);
        timeTransaction.setEditable(false);
    }

    /* enables the optional fields, makes the editable */
    public void enableBigMoneyFields() {
        stopLoss.setEditable(true);
        takeProfit.setEditable(true);
        maxTransactions.setEditable(true);
        trailingStop.setEditable(true);
        timeTransaction.setEditable(true);
    }

    /* gets the optional values and returns true or false based on the trailing stop value
    * false if the trailing stop has a valid value, but the stop loss and take profit values are not valid
    * true otherwise */
    public boolean getOptionalValues() {
        setValues(stopLoss);
        setValues(takeProfit);
        setValues(maxTransactions);
        setValues(trailingStop);
        setValues(timeTransaction);
        if (trailingStop_value != Double.MIN_VALUE) {
            return stopLoss_value != Double.MIN_VALUE || takeProfit_value != Double.MIN_VALUE;
        }
        return true;
    }

    /* gets the mandatory values and returns true or false based on the fields values
    * false if the values are invalid, meaning that some values were either strings or equal to 0
    * true otherwise */
    public boolean getNecessaryValues() {
        setValues(priceDiff);
        setValues(timeInterval);
        setValues(tradeVolume);
        return (!(market_value.equals("")) && (marketList.contains(market_value))) && priceDiff_value != Double.MIN_VALUE
                && timeInterval_value != Double.MIN_VALUE && tradeVolume_value != Double.MIN_VALUE;
    }

    /* check if there is an invalid field when saving */
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
        ImageIcon logo = new ImageIcon(new ImageIcon("./src/Media/logo.png").getImage().getScaledInstance(100, 60, Image.SCALE_SMOOTH));
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

        /* markets list */
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
        comboBox.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        JComboBoxDecorator.decorate(comboBox, true, marketList);


        /* Labels and tooltips */
        market_label = new JLabel("*Market", info, JLabel.CENTER);
        market_label.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        market_label.setForeground(Color.BLACK);
        market_label.setHorizontalTextPosition(JLabel.LEFT);
        market_label.setToolTipText("<html><p>Market</p>Dropdown with all the markets from which to choose one market " +
                                    "on which transactions will be made.</html>");

        priceDiff_label = new JLabel("*Price Difference", info, JLabel.CENTER);
        priceDiff_label.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        priceDiff_label.setForeground(Color.BLACK);
        priceDiff_label.setHorizontalTextPosition(JLabel.LEFT);
        priceDiff_label.setToolTipText("<html><p>Price Difference</p>The price difference for which to look out for at " +
                                        "each market's price change.</html>");

        timeInterval_label = new JLabel("*Time Interval", info, JLabel.CENTER);
        timeInterval_label.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        timeInterval_label.setForeground(Color.BLACK);
        timeInterval_label.setHorizontalTextPosition(JLabel.LEFT);
        timeInterval_label.setToolTipText("<html><p>Time Interval</p>The interval of time in which to look out for " +
                                            "the price difference at each market's price change.</html>");

        tradeVolume_label = new JLabel("*Trade volume", info, JLabel.CENTER);
        tradeVolume_label.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        tradeVolume_label.setForeground(Color.BLACK);
        tradeVolume_label.setHorizontalTextPosition(JLabel.LEFT);
        tradeVolume_label.setToolTipText("<html><p>Trade Volume</p>The volume of currency that will be used for each transaction.</html>");

        stopLoss_label = new JLabel("Stop Loss", info, JLabel.CENTER);
        stopLoss_label.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        stopLoss_label.setForeground(Color.BLACK);
        stopLoss_label.setHorizontalTextPosition(JLabel.LEFT);
        stopLoss_label.setToolTipText("<html><p>Stop Loss</p>A subtraction, to the price of each future transaction, that when it's reached<br/>" +
                                        "by the market's price, the transaction will automatically be closed.</html>");

        takeProfit_label = new JLabel("Take Profit", info, JLabel.CENTER);
        takeProfit_label.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        takeProfit_label.setForeground(Color.BLACK);
        takeProfit_label.setHorizontalTextPosition(JLabel.LEFT);
        takeProfit_label.setToolTipText("<html><p>Take Profit</p>An addition, to the price of each future transaction, that when it's reached<br/>" +
                                        "by the market's price, the transaction will automatically be closed.</html>");

        maxTransactions_label = new JLabel("Max Transactions", info, JLabel.CENTER);
        maxTransactions_label.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        maxTransactions_label.setForeground(Color.BLACK);
        maxTransactions_label.setHorizontalTextPosition(JLabel.LEFT);
        maxTransactions_label.setToolTipText("<html><p>Max Transactions</p>The number of maximum transactions that will made.</html>");


        trailingStop_label = new JLabel("Trailing Stop(%)", info, JLabel.CENTER);
        trailingStop_label.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        trailingStop_label.setForeground(Color.BLACK);
        trailingStop_label.setHorizontalTextPosition(JLabel.LEFT);
        trailingStop_label.setToolTipText("<html><p>Trailing Stop(%)</p>If the market's price moves towards the Take Profit that was set with the percentage provided<br/>" +
                                            "then the values Stop Loss and Take Profit are updated for the opened transactions.</html>");

        timeTransaction_label = new JLabel("Time/Transactions", info, JLabel.CENTER);
        timeTransaction_label.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        timeTransaction_label.setForeground(Color.BLACK);
        timeTransaction_label.setHorizontalTextPosition(JLabel.LEFT);
        timeTransaction_label.setToolTipText("<html><p>Time/Transactions</p>The time between each future transactions.</html>");

        /* panels for each label and textfield */
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

        /* the panels for the mandatory values */
        market_panel.setLayout(new BoxLayout(market_panel, BoxLayout.PAGE_AXIS));
        market_panel.add(market_label);
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboBox.setPreferredSize(new Dimension(170, 25));
        comboBox.setSize(new Dimension(170, 25));
        comboBox.setMaximumSize(new Dimension(170, 25));
        market_panel.add(comboBox);
        simpleOrderPanel.add(market_panel);
        addPanel(priceDiff_panel, priceDiff, priceDiff_label, simpleOrderPanel);
        addPanel(timeInterval_panel, timeInterval, timeInterval_label, simpleOrderPanel);
        addPanel(tradeVolume_panel, tradeVolume, tradeVolume_label, simpleOrderPanel);

        /* the panels for the optional values */
        addPanel(stopLoss_panel, stopLoss, stopLoss_label, bigMoneyPanel);
        addPanel(takeProfit_panel, takeProfit, takeProfit_label, bigMoneyPanel);
        addPanel(maxTransactions_panel, maxTransactions, maxTransactions_label, bigMoneyPanel);
        addPanel(trailingStop_panel, trailingStop, trailingStop_label, bigMoneyPanel);
        addPanel(timeTransaction_panel, timeTransaction, timeTransaction_label, bigMoneyPanel);

        JPanel bigMoneyActive = new JPanel();
        JPanel simpleActive = new JPanel();
        simpleActive.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));

        /* button for resetting the textfield's values */
        JButton resetButton = new JButton("Reset Options");
        resetButton.setFocusPainted(false);
        resetButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        resetButton.addActionListener(e -> resetOptions());

        /* button for saving the textfields's values */
        JButton saveButton = new JButton("Save Options");
        saveButton.setFocusPainted(false);
        saveButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        saveButton.addActionListener(e -> {
            setMarket();
            SaveFrame saveOption;
            if (getNecessaryValues()) {
                checkMandatoryValues();
                saveOption = new SaveFrame(market_value, priceDiff_value, timeInterval_value, tradeVolume_value);
                if (getSavedOptionals()) {
                    saveOption.saveOptions(stopLoss_value, takeProfit_value, maxTransactions_value, trailingStop_value, timeTransaction_value);
                    saveOption.run();
                }
            }
        });


        /* button for activating the optional fields */
        JToggleButton toggleButton = new JToggleButton("Activate additional fields");
        toggleButton.setFocusPainted(false);
        toggleButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        toggleButton.setToolTipText("While this is active, it adds the additional fields to all future transactions.");
        ItemListener itemListener = itemEvent -> {
            int state = itemEvent.getStateChange();
            if (state == ItemEvent.SELECTED) {
                toggleButton.setText("Deactivate additional fields");
                if (getOptionalValues()) {
                    if (checkOptionalValues()) {
                        if (!subtitle.getText().equals("Optional Values")) {
                            subtitle.setText("Optional Values");
                            subtitle.setForeground(Color.black);
                        }
                        defaultOptionalLabels();
                        disableBigMoneyFields();
                        trader.setBigMoney(stopLoss_value, takeProfit_value, maxTransactions_value, trailingStop_value, timeTransaction_value);
                    } else {
                        if (checkOptionalLabels()) {
                            subtitle.setText("No fields were filled out!");
                            subtitle.setForeground(Color.red);
                        }
                        toggleButton.setSelected(false);
                    }
                } else {
                    subtitle.setText("A Stop Loss and a Take Profit are needed for the Trailing Stop field!");
                    subtitle.setForeground(Color.red);
                    toggleButton.setSelected(false);
                }
            } else {
                enableBigMoneyFields();
                trader.setToDefault();
                toggleButton.setText("Activate additional fields");
            }
        };
        toggleButton.addItemListener(itemListener);

        /* button for loading previously saved values */
        JButton loadButton = new JButton("Load Options");
        loadButton.setToolTipText("Loads saved options.");
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

        JCheckBox checkBox = new JCheckBox("Show output");
        checkBox.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        checkBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkBox.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                showOutput = true;
            }
        });
        checkBox.setBackground(Color.white);

        /* button for starting doing transactions */
        JToggleButton button = new JToggleButton("Place Order");
        button.setToolTipText("Starts making transactions with the chosen options.");
        button.setFocusPainted(false);
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        ItemListener itemListener2 = itemEvent -> {
            int state = itemEvent.getStateChange();
            trader.setConnector(connector);
            if (state == ItemEvent.SELECTED) {
                setMarket();
                if (getNecessaryValues()) {
                    checkMandatoryValues();
                    aux = marketMap.get(market_value.split(" ")[0]);
                    if (!subscribedMarkets.contains(aux)) {
                        subscribedMarkets.add(aux);
                    }
                    OutputFrame outFrame;
                    if (showOutput) {
                        outFrame = new OutputFrame();
                        outFrame.reset();
                        outFrame.run();
                    } else {
                        outFrame = null;
                    }
                    checkBox.setEnabled(false);
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
                if (aux != null) {
                    trader.stop();
                    try {
                        connector.unsubscribePrice(aux);
                        connector.unsubscribeTrades();
                    } catch (APICommunicationException e) {
                        e.printStackTrace();
                    }
                }
                checkBox.setEnabled(true);
                checkMandatoryValues();
                button.setText("Place Order");
                enableSimpleFields();
                loadButton.setEnabled(true);
                resetButton.setEnabled(true);
                toggleButton.setSelected(false);
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
        JPanel information = new JPanel();
        JLabel importantFields = new JLabel("Mandatory values");
        importantFields.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        importantFields.setForeground(Color.BLACK);
        information.add(importantFields);
        information.setBackground(Color.white);
        mainPanel.add(information);

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

        /* place order panel */
        JPanel placeOrderPanel = new JPanel();
        placeOrderPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        button.setBackground(new Color(0,104,55));
        button.setForeground(Color.white);
        placeOrderPanel.add(button);
        placeOrderPanel.setBackground(Color.white);
        checkBox.setForeground(Color.black);
        placeOrderPanel.add(checkBox);
        mainPanel.add(placeOrderPanel);

        simpleOrderPanel.setBackground(Color.white);
        bigMoneyPanel.setBackground(Color.white);


        mainPanel.add(new JSeparator(), "cell 1 0,growx");
        JPanel subtitlePanel = new JPanel();
        subtitlePanel.setBackground(Color.white);
        subtitle = new JLabel("Optional values");
        subtitle.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        subtitle.setForeground(Color.BLACK);
        subtitlePanel.add(subtitle);
        mainPanel.add(subtitlePanel);
        mainPanel.add(bigMoneyPanel);
        mainPanel.add(bigMoneyActive);
        mainPanel.setBackground(Color.white);

        JLabel copyright = new JLabel("Developed using xAPI from XTB Platform");
        copyright.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        footer.add(copyright, BorderLayout.CENTER);
        footer.setBackground(Color.black);
        mainPanel.add(footer);

        return mainPanel;
    }
}
