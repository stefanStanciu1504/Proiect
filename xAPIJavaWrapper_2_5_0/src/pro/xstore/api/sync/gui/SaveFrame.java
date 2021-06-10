package pro.xstore.api.sync.gui;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import javax.swing.*;


public class SaveFrame extends JFrame {
    private static JFrame frame;
    private static File file;
    private final String market;
    private final double diff;
    private final double time;
    private final double volume;
    private boolean optionalToggle = false;
    private double SL = Double.MIN_VALUE;
    private double TP = Double.MIN_VALUE;
    private double maxT = Double.MIN_VALUE;
    private double TS = Double.MIN_VALUE;
    private double timeT = Double.MIN_VALUE;

    public SaveFrame(String new_market, double new_diff, double new_time, double new_volume) {
        market = new_market;
        diff = new_diff;
        time = new_time;
        volume = new_volume;
    }

    public void saveBigDickOptions(double new_SL, double new_TP, double new_maxT, double new_TS, double new_timeT) {
        optionalToggle = !optionalToggle;
        SL = new_SL;
        TP = new_TP;
        maxT = new_maxT;
        TS =  new_TS;
        timeT = new_timeT;
    }

    public void writeOptionals(FileWriter myFile, DecimalFormat df) throws IOException {
        if (optionalToggle) {
            if (SL != Double.MIN_VALUE) {
                myFile.write(df.format(SL) + "\n");
            } else {
                myFile.write("\n");
            }
            if (TP != Double.MIN_VALUE) {
                myFile.write(df.format(TP) + "\n");
            } else {
                myFile.write("\n");
            }
            if (maxT != Double.MIN_VALUE) {
                myFile.write(df.format(maxT) + "\n");
            } else {
                myFile.write("\n");
            }
            if (TS != Double.MIN_VALUE) {
                myFile.write(df.format(TS) + "\n");
            } else {
                myFile.write("\n");
            }
            if (timeT != Double.MIN_VALUE) {
                myFile.write(df.format(timeT) + "\n");
            } else {
                myFile.write("\n");
            }
        }
    }

    public void run() {
        frame = new JFrame("Save file");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Image icon = Toolkit.getDefaultToolkit().getImage("./src/Media/logo.png");
        frame.setIconImage(icon);
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel buttons = new JPanel();
        buttons.setBackground(Color.white);
        buttons.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 0));

        JButton close = new JButton("Close");
        close.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        close.setBackground(new Color(0,104,55));
        close.setForeground(Color.white);
        buttons.add(close);

        JButton save = new JButton("Save");
        save.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        save.setBackground(new Color(0,104,55));
        save.setForeground(Color.white);
        buttons.add(save);

        JLabel notify = new JLabel("Save file");
        notify.setFont(new Font("Comic Sans MS", Font.BOLD, 14));

        JTextField fileName = new JTextField(15);
        fileName.setFont(new Font("Comic Sans MS", Font.BOLD, 14));

        close.addActionListener(e -> frame.dispose());
        save.addActionListener(e -> {
            if (!fileName.getText().equals("")) {
                try {
                    String aux = "./src/Saves/" + fileName.getText() + ".txt";
                    file = new File(aux);
                    file.createNewFile();
                    FileWriter myFile = new FileWriter(file);
                    myFile.write(market + "\n");
                    DecimalFormat df = new DecimalFormat("#.#");
                    df.setMaximumFractionDigits(8);
                    myFile.write(df.format(diff) + "\n");
                    myFile.write(df.format(time) + "\n");
                    myFile.write(df.format(volume) + "\n");
                    writeOptionals(myFile, df);
                    myFile.close();
            } catch(IOException ignored){
            }
            frame.dispose();
        } else {
                notify.setText("Please enter a valid name");
            }
        });

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        JPanel title = new JPanel();
        title.add(notify);
        title.setBackground(Color.white);
        panel.add(title);

        JPanel name = new JPanel();
        name.add(fileName);
        name.setBackground(Color.white);
        panel.add(name);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttons);

        frame.getContentPane().add(panel);
        frame.setVisible(true);
        frame.setSize(240, 200);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
    }
}