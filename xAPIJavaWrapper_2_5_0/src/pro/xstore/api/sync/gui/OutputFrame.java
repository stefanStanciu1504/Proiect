package pro.xstore.api.sync.gui;

import com.sun.tools.javac.Main;
import pro.xstore.api.message.records.STickRecord;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public class OutputFrame extends JFrame {
    private JTextArea chatBox;
    private JFrame frame;
    private boolean pauseUpdate = false;
    private boolean filterUpdates = true;

    public OutputFrame() {
    }

    public <T> void updateOutput(T obj) {
        if (!pauseUpdate) {
            if (filterUpdates) {
                if (!(obj instanceof STickRecord)) {
                    chatBox.append(obj.toString() + "\n");
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
        frame = new JFrame("Output");
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new GridBagLayout());

        JButton closeFrame = new JButton("Close");
        JToggleButton pause = new JToggleButton("Pause Output");
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
        ItemListener itemListener2 = itemEvent -> {
            int state = itemEvent.getStateChange();
            if (state == ItemEvent.SELECTED) {
                filter.setText("Hide Price Updates");
            } else {
                filter.setText("Show Price Updates");
            }
            filterUpdates = !filterUpdates;
        };
        filter.addItemListener(itemListener2);

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

        frame.add(southPanel, BorderLayout.PAGE_END);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
        frame.setSize(560, 560);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Point mid = new Point(screenSize.width / 2, screenSize.height / 2);
        Point newLocation = new Point(mid.x + (frame.getWidth() / 2),
                mid.y - (frame.getHeight() / 2));
        frame.setLocation(newLocation);
    }

}