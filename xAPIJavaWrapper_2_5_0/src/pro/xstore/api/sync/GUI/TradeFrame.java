package pro.xstore.api.sync.GUI;
import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.response.LogoutResponse;
import pro.xstore.api.sync.SyncAPIConnector;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TradeFrame extends JFrame {
    private final SyncAPIConnector connector;
    public AtomicLong atomicDelay = new AtomicLong(0);
    public ReentrantLock lock = new ReentrantLock();
    private final JTabbedPane tabs = new JTabbedPane();

    public TradeFrame(SyncAPIConnector aux_connector) {
        connector = aux_connector;
    }

    public void run() throws Exception {
        JFrame frame = new JFrame("MoneyTrading");

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

        Image icon = Toolkit.getDefaultToolkit().getImage("../../../src/Media/logo.png");
        frame.setIconImage(icon);

        UIManager.put("TabbedPane.contentAreaColor", Color.white);
        this.tabs.setUI(new CustomTabbedPaneUI());
        this.tabs.setFont(new Font("Comic Sans MS", Font.BOLD, 14));


        MainPanel defaultTab = new MainPanel(connector, this);
        this.tabs.addTab("Tab 0", null, defaultTab.buildMainPanel(), null);
        this.tabs.addTab(" + ", null, new JPanel(), null);

        this.tabs.addChangeListener(evt -> {
            JTabbedPane tabbedPane = (JTabbedPane)evt.getSource();

            if(tabbedPane.getSelectedIndex() == tabbedPane.indexOfTab(" + ")) {
                int index = tabbedPane.getSelectedIndex();
                try {
                    MainPanel newTab = new MainPanel(connector, this);
                    tabbedPane.remove(index);
                    tabbedPane.addTab("Tab " + index, null, newTab.buildMainPanel(), null);
                    tabbedPane.setSelectedIndex(index);
                    tabbedPane.addTab(" + ", null, new JPanel(), null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        frame.getContentPane().add(this.tabs);
        frame.setPreferredSize(new Dimension(1100, 700));
        frame.setResizable(false);
        frame.getContentPane().setBackground(Color.black);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}