package pro.xstore.api.sync.gui;

import pro.xstore.api.message.records.STickRecord;

import javax.swing.*;
import java.awt.*;


public class OutputFrame extends JFrame {
    private static JTextArea chatBox;
    private static JFrame frame;
    private static boolean pauseUpdate = false;
    private static boolean filterUpdates = false;

    public OutputFrame() {
    }

    public <T> void updateOutput(T obj) {
        if (!pauseUpdate) {
            if (filterUpdates) {
                if (!(obj instanceof STickRecord)) {
                    chatBox.append(obj.toString());
                    chatBox.setCaretPosition(chatBox.getDocument().getLength() - 1);
                }
            } else {
                chatBox.append(obj.toString());
                chatBox.setCaretPosition(chatBox.getDocument().getLength() - 1);
            }

        }
    }

    public void run() {
        frame = new JFrame("Output");
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new GridBagLayout());

        JButton closeFrame = new JButton("Close");
        JButton pause = new JButton("Pause");
        JButton filter = new JButton("Filter Updates");
        chatBox = new JTextArea();
        chatBox.setEditable(false);
        JScrollPane scroll = new JScrollPane(chatBox);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        frame.getContentPane().add(BorderLayout.CENTER, scroll);

        chatBox.setLineWrap(true);

        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.WEST;
        left.weightx = 2.0;
        GridBagConstraints middle = new GridBagConstraints();
        middle.anchor = GridBagConstraints.CENTER;
        GridBagConstraints right = new GridBagConstraints();
        right.anchor = GridBagConstraints.EAST;
        right.weightx = 2.0;

        southPanel.add(filter, left);
        southPanel.add(pause, middle);
        southPanel.add(closeFrame, right);
        
        closeFrame.addActionListener(e -> frame.dispose());
        filter.addActionListener(e -> filterUpdates = !filterUpdates);
        pause.addActionListener(e -> pauseUpdate = !pauseUpdate);

        frame.add(southPanel, BorderLayout.PAGE_END);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
        frame.setSize(1320, 560);
    }

}