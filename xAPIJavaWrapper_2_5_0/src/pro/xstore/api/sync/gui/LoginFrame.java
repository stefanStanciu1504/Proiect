package pro.xstore.api.sync.gui;

import pro.xstore.api.sync.Credentials;
import pro.xstore.api.sync.Example;
import pro.xstore.api.sync.ServerData.ServerEnum;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import javax.swing.*;


public class LoginFrame extends JFrame implements ActionListener {
    static JTextField username;
    static JTextField password;
    static JFrame f;
    static JLabel notify;
    static JButton b;
    static Box box = new Box(BoxLayout.Y_AXIS);
    static JPanel p = new JPanel();
    static File credentials;
    static boolean saveCredentials = false;
    static ServerEnum accountType = ServerEnum.REAL;

    public LoginFrame()
    {
    }

    public void run() {
        f = new JFrame("Login");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        b = new JButton("Login");

        JToggleButton selectDemo = new JToggleButton("Demo account");
        JToggleButton selectReal = new JToggleButton("Real account");

        ItemListener itemListenerDemo = itemEvent -> {
            int state = itemEvent.getStateChange();
            if (state == ItemEvent.SELECTED) {
                accountType = ServerEnum.DEMO;
                selectReal.setSelected(false);
            } else {
                accountType = ServerEnum.REAL;
            }
        };
        selectDemo.addItemListener(itemListenerDemo);
        selectReal.setSelected(true);
        ItemListener itemListenerReal = itemEvent -> {
            int state = itemEvent.getStateChange();
            if (state == ItemEvent.SELECTED) {
                accountType = ServerEnum.REAL;
                selectDemo.setSelected(false);
            } else {
                accountType = ServerEnum.DEMO;
            }
        };
        selectReal.addItemListener(itemListenerReal);

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
        try {
            credentials = new File("./src/Saves/Creds/credentials.txt");
            if (!credentials.createNewFile()) {
                Scanner myReader = new Scanner(credentials);
                while (myReader.hasNextLine()) {
                    username.setText(myReader.nextLine());
                    password.setText(myReader.nextLine());
                }
                myReader.close();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        JCheckBox checkBox = new JCheckBox("Save credentials");
        checkBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkBox.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                saveCredentials = true;
            }
        });
        p.add(checkBox);
        p.add(b);

        JPanel accountPanel = new JPanel();
        accountPanel.setLayout(new FlowLayout());
        accountPanel.add(selectDemo);
        accountPanel.add(selectReal);
        p.add(accountPanel);

        p.setBorder(BorderFactory.createTitledBorder("Login"));

        box.add(Box.createVerticalGlue());
        box.add(p);
        box.add(Box.createVerticalGlue());

        f.add(box);
        f.setSize(480, 480);
        f.setLocationRelativeTo(null);
        f.setVisible(true);

    }

    // if the button is pressed
    public void actionPerformed(ActionEvent e) {
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
                    ex.runExample(accountType, credentials);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                if (ex.getLoginResponse() == null) {
                    notify.setText("Incorrect authentication!");
                } else {
                    if (saveCredentials) {
                        try {
                            FileWriter myFile = new FileWriter("./src/Saves/Creds/credentials.txt");
                            myFile.write(username.getText() + "\n");
                            myFile.write(password.getText() + "\n");
                            myFile.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
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