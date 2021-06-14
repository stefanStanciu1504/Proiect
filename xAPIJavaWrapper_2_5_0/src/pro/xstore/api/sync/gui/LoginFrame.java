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
import javax.swing.border.Border;
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
        Image icon = Toolkit.getDefaultToolkit().getImage("./src/Media/logo.png");
        frame.setIconImage(icon);
        button = new JButton("Login");
        ImageIcon lower_left = new ImageIcon(new ImageIcon("./src/Media/circlesDown.png").getImage().getScaledInstance(63, 60, Image.SCALE_SMOOTH));
        ImageIcon upper_right = new ImageIcon(new ImageIcon("./src/Media/circlesUp.png").getImage().getScaledInstance(63, 60, Image.SCALE_SMOOTH));
        JLabel left = new JLabel(lower_left);
        JLabel right = new JLabel(upper_right);

        JToggleButton selectDemo = new JToggleButton("Demo account");
        selectDemo.setFocusPainted(false);
        selectDemo.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        selectDemo.setBackground(new Color(0,104,55));
        selectDemo.setUI(new MetalToggleButtonUI() {
            @Override
            protected Color getSelectColor() {
                return new Color(0,104,55).brighter();
            }
        });

        selectDemo.setForeground(Color.white);
        JToggleButton selectReal = new JToggleButton("Real account");
        selectReal.setFocusPainted(false);
        selectReal.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
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
        button.setFocusPainted(false);
        button.setBackground(new Color(0,104,55).darker());
        button.setForeground(Color.white);
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 14));

        notify = new JLabel("Welcome to MoneyTrading!");
        notify.setFont(new Font("Comic Sans MS", Font.BOLD, 18));

        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        usernamePanel.setBackground(Color.white);

        username = new JTextField(16);
        username.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        JLabel accountInfo = new JLabel("Account Number:");
        accountInfo.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        usernamePanel.add(accountInfo);
        usernamePanel.add(username);

        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        passwordPanel.setBackground(Color.white);

        password = new JPasswordField(16);
        password.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        JLabel passwordInfo = new JLabel("Password:");
        passwordInfo.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        passwordPanel.add(Box.createRigidArea(new Dimension(37, 0)));
        passwordPanel.add(passwordInfo);
        passwordPanel.add(password);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.setPreferredSize(new Dimension(350, 360));
        panel.setMaximumSize(new Dimension(350, 360));

        panel.add(Box.createRigidArea(new Dimension(5, 16)));
        JPanel notifyPanel = new JPanel();
        notifyPanel.setBackground(Color.white);
        notifyPanel.add(notify);
        panel.add(notifyPanel);
        username.setMaximumSize(username.getPreferredSize());
        panel.add(Box.createRigidArea(new Dimension(5, 30)));
        panel.add(usernamePanel);
        password.setMaximumSize(password.getPreferredSize());
        panel.add(Box.createRigidArea(new Dimension(5, 6)));
        panel.add(passwordPanel);

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

        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setBackground(Color.white);
        JCheckBox checkBox = new JCheckBox("Save credentials");
        checkBox.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        checkBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkBox.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                saveCredentials = true;
            }
        });
        checkBox.setBackground(Color.white);
        checkBoxPanel.add(checkBox);

        panel.add(Box.createRigidArea(new Dimension(5, 6)));
        panel.add(checkBoxPanel);
        panel.add(Box.createRigidArea(new Dimension(5, 30)));
        panel.add(button);

        JPanel accountPanel = new JPanel();
        accountPanel.setLayout(new FlowLayout());
        accountPanel.add(selectDemo);
        accountPanel.add(selectReal);
        accountPanel.setBackground(Color.white);
        panel.add(Box.createRigidArea(new Dimension(5, 6)));
        panel.add(accountPanel);
        panel.add(Box.createRigidArea(new Dimension(5, 30)));

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
        rightPanel.add(right);
        rightPanel.setBackground(Color.white);
        box.add(rightPanel);
        box.add(Box.createVerticalGlue());
        box.add(panel);
        box.add(Box.createVerticalGlue());



        DropShadowBorder shadow = new DropShadowBorder();
        Border border = BorderFactory.createMatteBorder(
                1, 1, 1, 1, Color.LIGHT_GRAY);

        shadow.setShadowColor(Color.BLACK);
        shadow.setShowLeftShadow(false);
        shadow.setShowRightShadow(true);
        shadow.setShowBottomShadow(true);
        shadow.setShowTopShadow(false);
        shadow.setShadowSize(10);
        shadow.setShadowOpacity(0.3f);

        Border compound = BorderFactory.createCompoundBorder(shadow, border);
        panel.setBorder(compound);
        panel.setBackground(Color.white);
        box.setBackground(Color.white);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        leftPanel.add(left);
        leftPanel.setBackground(Color.white);
        box.add(leftPanel);

        frame.getContentPane().setBackground(Color.white);
        frame.getContentPane().add(box);

        frame.setSize(480, 520);
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