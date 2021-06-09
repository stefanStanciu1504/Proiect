package pro.xstore.api.sync.gui;
import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.SymbolRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.AllSymbolsResponse;
import pro.xstore.api.message.response.LogoutResponse;
import pro.xstore.api.sync.SyncAPIConnector;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
//    private static final JPanel mainPanel = new JPanel();
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
    private static final MainThread trader = new MainThread();
    private static String aux = null;
    private JTabbedPane tabs = new JTabbedPane();


    public TradeFrame(SyncAPIConnector aux_connector) {
        connector = aux_connector;
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

    static public Object deepCopy(Object oldObj) throws Exception {
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            ByteArrayOutputStream bos =
                    new ByteArrayOutputStream(); // A
            oos = new ObjectOutputStream(bos); // B
            oos.writeObject(oldObj);   // C
            oos.flush();               // D
            ByteArrayInputStream bin =
                    new ByteArrayInputStream(bos.toByteArray()); // E
            ois = new ObjectInputStream(bin);                  // F
            return ois.readObject(); // G
        } catch (Exception e) {
            System.out.println("Exception in ObjectCloner = " + e);
            throw (e);
        } finally {
            oos.close();
            ois.close();
        }
    }

    public JPanel buildMainPanel() throws APIErrorResponse, APICommunicationException, APIReplyParseException, APICommandConstructionException {
        JPanel mainPanel = new JPanel();
        ImageIcon logo = new ImageIcon(new ImageIcon("./src/Media/logo.jpeg").getImage().getScaledInstance(100, 60, Image.SCALE_SMOOTH));
        JLabel label = new JLabel(logo);
        simpleOrderPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        bigMoneyPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        balancePanel.setLayout(new BorderLayout());
        balancePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel accountBalance_label;
        if (connector.getBalanceRecord() == null) {
            accountBalance_label = new JLabel("Account Balance: " + "no server response");
        } else {
            double accountBalance_value = connector.getBalanceRecord().getBalance();
            accountBalance_label = new JLabel("Account Balance: " + accountBalance_value);
        }

        balancePanel.add(label, BorderLayout.LINE_START);
        balancePanel.add(accountBalance_label, BorderLayout.LINE_END);
        balancePanel.setBackground(Color.white);

        mainPanel.add(balancePanel);
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

        JButton resetButton = new JButton("Reset Options");
        resetButton.addActionListener(e -> resetOptions());

        JButton saveButton = new JButton("Save Options");
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

        JToggleButton toggleButton = new JToggleButton("ON");
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
                toggleButton.setText("ON");
            }
        };
        toggleButton.addItemListener(itemListener);

        JButton loadButton = new JButton("Load Options");
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

        simpleActive.add(button);
        simpleActive.add(saveButton);
        simpleActive.add(loadButton);
        simpleActive.add(resetButton);
        bigMoneyActive.add(toggleButton);

        mainPanel.add(simpleOrderPanel);
        mainPanel.add(simpleActive);
        mainPanel.add(bigMoneyPanel);
        mainPanel.add(bigMoneyActive);

        return mainPanel;
    }

    public void run() throws Exception {
        JFrame frame = new JFrame("TradeFrame");

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


        Image icon = Toolkit.getDefaultToolkit().getImage("./src/Media/log.jpeg");
        frame.setIconImage(icon);


        JPanel aux = buildMainPanel();
        this.tabs.addTab("Tab 0", null, aux, null);
        this.tabs.addTab(" + ", null, new JPanel(), null);


        this.tabs.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent evt)
            {
                JTabbedPane tabbedPane = (JTabbedPane)evt.getSource();

                if(tabbedPane.getSelectedIndex() == tabbedPane.indexOfTab(" + "))
                {
                    int index = tabbedPane.getSelectedIndex();
                    try {
                        JPanel newPanel = buildMainPanel();
                        tabbedPane.remove(index);
                        tabbedPane.addTab("Tab " + index, null, newPanel, null);
                        tabbedPane.setSelectedIndex(index);
                        tabbedPane.addTab(" + ", null, new JPanel(), null);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        frame.getContentPane().add(this.tabs);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}