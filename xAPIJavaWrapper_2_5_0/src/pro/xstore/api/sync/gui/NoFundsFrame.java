package pro.xstore.api.sync.gui;

import javax.swing.*;
import java.awt.*;

public class NoFundsFrame extends Frame {

    public NoFundsFrame() {
    }

    public void run() {
        JFrame frame = new JFrame();
        Image icon = Toolkit.getDefaultToolkit().getImage("./src/Media/logo.png");
        frame.setIconImage(icon);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        JLabel warning = new JLabel("No funds left!");
        warning.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        warning.setBackground(Color.white);
        warning.setForeground(Color.red);
        panel.add(warning);

        frame.getContentPane().add(panel);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setSize(120, 100);
        frame.setLocationRelativeTo(null);
    }
}
