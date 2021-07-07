package pro.xstore.api.sync.GUI;

import javax.swing.*;
import javax.swing.plaf.metal.MetalToggleButtonUI;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public class OutputFrame extends JFrame {
    private JTextArea chatBox;
    private JFrame frame;
    private boolean pauseUpdate = false;
    private boolean filterUpdates = true;
    private final String market;

    public OutputFrame(String market) {
        this.market = market;
    }

    public <T> void updateOutput(T obj) {
        if (!pauseUpdate) {
            if (filterUpdates) {
                if (!obj.toString().startsWith("The market's ask")) {
                    chatBox.append(obj + "\n");
                    chatBox.setCaretPosition(chatBox.getDocument().getLength() - 1);
                }
            } else {
                chatBox.append(obj.toString() + "\n");
                chatBox.setCaretPosition(chatBox.getDocument().getLength() - 1);
            }
        }
    }

    public void reset() {
        pauseUpdate = false;
        filterUpdates = true;
        if (chatBox != null)
            chatBox.setText("");
    }

    public void closeFrame() {
        frame.dispose();
    }

    public void run() {
        frame = new JFrame("Output for " + this.market);
        Image icon = Toolkit.getDefaultToolkit().getImage("../../../src/Media/logo.png");
        frame.setIconImage(icon);
        JPanel panel = new JPanel();
        panel.setBackground(Color.white);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel optionsPanel = new JPanel();
        optionsPanel.setBackground(Color.white);
        optionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 61, 0));

        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        closeButton.setBackground(new Color(0,104,55));
        closeButton.setForeground(Color.white);
        optionsPanel.add(closeButton);

        JToggleButton pause = new JToggleButton("Pause Output");
        pause.setForeground(Color.white);
        pause.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        pause.setBackground(new Color(0,104,55));
        pause.setUI(new MetalToggleButtonUI() {
            @Override
            protected Color getSelectColor() {
                return new Color(0,104,55).brighter();
            }
        });
        optionsPanel.add(pause);

        ItemListener itemListener = itemEvent -> {
            int state = itemEvent.getStateChange();
            if (state == ItemEvent.SELECTED) {
                pause.setText("Resume Output");
            } else {
                pause.setText("Pause Output");
            }
            pauseUpdate = !pauseUpdate;
        };
        pause.addItemListener(itemListener);

        JToggleButton filter = new JToggleButton("Show Price Updates");
        filter.setForeground(Color.white);
        filter.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        filter.setBackground(new Color(0,104,55));
        filter.setUI(new MetalToggleButtonUI() {
            @Override
            protected Color getSelectColor() {
                return new Color(0,104,55).brighter();
            }
        });
        optionsPanel.add(filter);

        ItemListener filterButtonItemListener = itemEvent -> {
            int state = itemEvent.getStateChange();
            if (state == ItemEvent.SELECTED) {
                filter.setText("Hide Price Updates");
            } else {
                filter.setText("Show Price Updates");
            }
            filterUpdates = !filterUpdates;
        };
        filter.addItemListener(filterButtonItemListener);

        closeButton.addActionListener(e -> frame.dispose());

        chatBox = new JTextArea();
        chatBox.setEditable(false);
        chatBox.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        chatBox.setLineWrap(true);
        JScrollPane notificationsArea = new JScrollPane(chatBox);
        notificationsArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        notificationsArea.setPreferredSize(new Dimension(560, 490));
        notificationsArea.setSize(new Dimension(560, 490));
        notificationsArea.setMaximumSize(new Dimension(560, 490));

        panel.add(notificationsArea);
        panel.add(optionsPanel);

        frame.getContentPane().add(panel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setSize(560, 560);
    }

}