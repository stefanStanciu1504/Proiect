package pro.xstore.api.sync.gui;
import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.records.SymbolRecord;
import pro.xstore.api.message.response.AllSymbolsResponse;
import pro.xstore.api.sync.SyncAPIConnector;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import javax.swing.*;


public class TradeFrame extends JFrame implements ActionListener {
    private final SyncAPIConnector connector;

    public TradeFrame(SyncAPIConnector aux_connector)
    {
        connector = aux_connector;
    }

    public void run() throws Exception {
        JFrame frame = new JFrame("TradeFrame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        AllSymbolsResponse allSymbols = APICommandFactory.executeAllSymbolsCommand(connector);
        LinkedList<String> list = new LinkedList<>();
        list.add("");

        for (SymbolRecord idx : allSymbols.getSymbolRecords()) {
            int aux = idx.getSymbol().indexOf("_");
            String symbol;
            if (aux != -1) {
                symbol = idx.getSymbol().substring(0, aux);
            } else {
                symbol = idx.getSymbol();
            }

            if (idx.getDescription().contains("CFD")) {
                String category = idx.getCategoryName().concat(" CFD");
                list.add(symbol + " " + category);
            } else {
                list.add(symbol + " " + idx.getCategoryName());
            }
        }

        JComboBox<String> comboBox = new JComboBox<>(list.toArray(new String[0]));
        comboBox.setMaximumRowCount(10);
        comboBox.setPreferredSize(comboBox.getPreferredSize());
        comboBox.setMaximumSize(comboBox.getPreferredSize());

        JComboBoxDecorator.decorate(comboBox, true, list);

        frame.setLayout(new FlowLayout(FlowLayout.LEFT));
        frame.getContentPane().add(comboBox);
        frame.setSize(480, 480);
        frame.setVisible(true);

    }

    // if the button is pressed
    public void actionPerformed(ActionEvent e)
    {
    }
}