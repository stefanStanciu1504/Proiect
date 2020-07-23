package pro.xstore.api.sync.gui;

import pro.xstore.api.message.response.LoginResponse;
import pro.xstore.api.sync.Credentials;
import pro.xstore.api.sync.Example;
import pro.xstore.api.sync.ServerData.ServerEnum;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.print.DocFlavor;
import javax.swing.*;


public class LoginFrame extends JFrame implements ActionListener {
    static JTextField username;
    static JTextField password;
    static JFrame f;
    static JLabel notify;
    static JButton b;
    static Box box = new Box(BoxLayout.Y_AXIS);
    static JPanel p = new JPanel();

    public LoginFrame()
    {
    }

    public void run() {
        f = new JFrame("Login");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        b = new JButton("Login");

        LoginFrame te = new LoginFrame();

        b.addActionListener(te);

        notify = new JLabel("Welcome!");

        username = new JTextField(20);
        password = new JPasswordField(16);

        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.setPreferredSize(new Dimension(400, 200));
        p.setMaximumSize(new Dimension(400, 200));

        p.add(notify);
        notify.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(Box.createRigidArea(new Dimension(0, 5)));

        username.setMaximumSize(username.getPreferredSize());
        p.add(username);
        p.add(Box.createRigidArea(new Dimension(0, 5)));
        password.setMaximumSize(password.getPreferredSize());
        p.add(password);

        p.add(Box.createRigidArea(new Dimension(0, 10)));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(b);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.setBorder(BorderFactory.createTitledBorder("Login"));

        box.add(Box.createVerticalGlue());
        box.add(p);
        box.add(Box.createVerticalGlue());

        f.add(box);
        f.setSize(480, 480);

        f.show();

    }

    // if the button is pressed
    public void actionPerformed(ActionEvent e)
    {
        String s = e.getActionCommand();
        if (s.equals("Login")) {
            if (username.getText().equals("")) {
                notify.setText("No username was provided!");
            } else if (password.getText().equals("")) {
                notify.setText("No password was provided!");
            } else {
                Credentials credentials = new Credentials(username.getText(), password.getText(), null, null);
                Example ex = new Example();
                try {
                    ex.runExample(ServerEnum.DEMO, credentials);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                if (ex.getLoginResponse() == null) {
                    notify.setText("Incorrect authentication!");
                } else {
                    TradeFrame tradeFrame = new TradeFrame(ex.getConnector());
                    try {
                        tradeFrame.run();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    f.dispose();
                }
            }
            username.setText("");
            password.setText("");
        }
    }
}