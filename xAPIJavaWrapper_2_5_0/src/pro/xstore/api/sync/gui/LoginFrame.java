package pro.xstore.api.sync.gui;

import pro.xstore.api.sync.Credentials;
import pro.xstore.api.sync.Example;
import pro.xstore.api.sync.ServerData.ServerEnum;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.jdesktop.swingx.border.DropShadowBorder;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.metal.MetalToggleButtonUI;


public class LoginFrame extends JFrame implements ActionListener {
    static JTextField username;
    static JTextField password;
    static JFrame frame;
    static JLabel notify;
    static JButton button;
    static Box box = new Box(BoxLayout.Y_AXIS);
    static JPanel panel = new JPanel();
    static File credentials;
    static boolean saveCredentials = false;
    static ServerEnum accountType = ServerEnum.REAL;

    public LoginFrame() {
    }

    public void run() {
        frame = new JFrame("Login");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        button = new JButton("Login");
        ImageIcon lower_left = new ImageIcon(new ImageIcon("./src/Media/circlesDown.png").getImage().getScaledInstance(63, 60, Image.SCALE_SMOOTH));
        ImageIcon upper_right = new ImageIcon(new ImageIcon("./src/Media/circlesUp.png").getImage().getScaledInstance(63, 60, Image.SCALE_SMOOTH));
        JLabel left = new JLabel(lower_left);
        JLabel right = new JLabel(upper_right);

        JToggleButton selectDemo = new JToggleButton("Demo account");
        selectDemo.setFont(new Font("Papyrus", Font.BOLD, 14));
        selectDemo.setBackground(new Color(0,104,55));
        selectDemo.setUI(new MetalToggleButtonUI() {
            @Override
            protected Color getSelectColor() {
                return new Color(0,104,55).brighter();
            }
        });

        selectDemo.setForeground(Color.white);
        JToggleButton selectReal = new JToggleButton("Real account");
        selectReal.setFont(new Font("Papyrus", Font.BOLD, 14));
        selectReal.setBackground(new Color(0,104,55));
        selectReal.setUI(new MetalToggleButtonUI() {
            @Override
            protected Color getSelectColor() {
                return new Color(0,104,55).brighter();
            }
        });
        selectReal.setForeground(Color.white);

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

        button.addActionListener(this);
        button.setBackground(new Color(0,104,55).darker());
        button.setForeground(Color.white);
        button.setFont(new Font("Papyrus", Font.BOLD, 14));

        notify = new JLabel("Welcome to MoneyTrading!");
        notify.setFont(new Font("Papyrus", Font.BOLD, 18));

        username = new JTextField(20);
        password = new JPasswordField(16);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.setPreferredSize(new Dimension(360, 300));
        panel.setMaximumSize(new Dimension(360, 300));

        panel.add(Box.createRigidArea(new Dimension(5, 16)));
        panel.add(notify);
        notify.setAlignmentX(Component.CENTER_ALIGNMENT);
        username.setMaximumSize(username.getPreferredSize());
        panel.add(Box.createRigidArea(new Dimension(5, 30)));
        panel.add(username);
        password.setMaximumSize(password.getPreferredSize());
        panel.add(Box.createRigidArea(new Dimension(5, 6)));
        panel.add(password);

        button.setAlignmentX(Component.CENTER_ALIGNMENT);
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
        checkBox.setFont(new Font("Papyrus", Font.BOLD, 14));
        checkBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkBox.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                saveCredentials = true;
            }
        });

        checkBox.setBackground(Color.white);
        panel.add(Box.createRigidArea(new Dimension(5, 6)));
        panel.add(checkBox);
        panel.add(Box.createRigidArea(new Dimension(5, 46)));
        panel.add(button);

        JPanel accountPanel = new JPanel();
        accountPanel.setLayout(new FlowLayout());
        accountPanel.add(selectDemo);
        accountPanel.add(selectReal);
        accountPanel.setBackground(Color.white);
        panel.add(Box.createRigidArea(new Dimension(5, 6)));
        panel.add(accountPanel);


        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
        rightPanel.add(right);
        rightPanel.setBackground(Color.white);
        box.add(rightPanel);
        box.add(Box.createVerticalGlue());
        box.add(panel);
        box.add(Box.createVerticalGlue());

        DropShadowBorder shadow = new DropShadowBorder();
        shadow.setShadowColor(Color.BLACK);
        shadow.setShowLeftShadow(true);
        shadow.setShowRightShadow(true);
        shadow.setShowBottomShadow(true);
        shadow.setShowTopShadow(true);
        panel.setBorder(shadow);

        panel.setBackground(Color.white);
        box.setBackground(Color.white);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        leftPanel.add(left);
        leftPanel.setBackground(Color.white);
        box.add(leftPanel);

        frame.getContentPane().setBackground(Color.white);
        frame.getContentPane().add(box);

        frame.setSize(480, 480);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
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
                    frame.dispose();
                }
            }
            username.setText("");
            password.setText("");
        }
    }
}