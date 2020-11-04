package pro.xstore.api.sync.gui;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import javax.swing.*;


public class SaveFrame extends JFrame {
    private static JFrame f;
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
        f = new JFrame("File Name");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel p = new JPanel();
        p.setLayout(new GridBagLayout());

        JButton close = new JButton("Close");
        JButton save = new JButton("Save");

        JLabel notify = new JLabel("Save file");

        JTextField fileName = new JTextField(20);

        close.addActionListener(e -> f.dispose());
        save.addActionListener(e -> {
            if (!fileName.getText().equals("")) {
                try {
                    String aux = "../../../src/Saves/" + fileName.getText() + ".txt";
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
            f.dispose();
        } else {
                notify.setText("Please enter a valid name");
            }
        });

        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.WEST;
        GridBagConstraints right = new GridBagConstraints();
        right.anchor = GridBagConstraints.EAST;
        right.weightx = 2.0;

        p.add(save, left);
        p.add(close, right);

        f.add(p, BorderLayout.PAGE_END);
        f.add(fileName, BorderLayout.CENTER);
        f.add(notify, BorderLayout.PAGE_START);
        f.setVisible(true);
        f.setSize(240, 90);
        f.setLocationRelativeTo(null);
    }
}