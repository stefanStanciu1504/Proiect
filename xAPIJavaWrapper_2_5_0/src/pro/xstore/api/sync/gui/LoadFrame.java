package pro.xstore.api.sync.gui;

import pro.xstore.api.message.records.TradeTransInfoRecord;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Scanner;
import javax.swing.*;


public class LoadFrame extends JFrame {
    private static JFrame f;
    private static JComboBox<String> comboBox;
    private static JComboBox<String> tradeBox;
    private static JTextField TD;
    private static JTextField tradeT;
    private static JTextField tradeV;
    private static JTextField SL;
    private static JTextField TP;
    private static JTextField maxT;
    private static JTextField TS;
    private static JTextField timeT;

    public LoadFrame(JComboBox<String> new_comboBox, JTextField new_diff, JTextField new_time,
                     JTextField new_volume, JTextField new_SL, JTextField new_TP, JTextField new_maxT, JTextField new_TS, JTextField new_timeT) {
        tradeBox = new_comboBox;
        TD = new_diff;
        tradeT = new_time;
        tradeV = new_volume;
        SL = new_SL;
        TP = new_TP;
        maxT = new_maxT;
        TS = new_TS;
        timeT = new_timeT;
    }

    public LinkedList<String> listFiles(String dir) throws IOException {
        LinkedList<String> fileList = new LinkedList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    String aux = path.getFileName().toString();
                    String kept = aux.substring(0, aux.indexOf(".txt"));
                    fileList.add(kept);
                }
            }
        }
        return fileList;
    }

    public void run() throws IOException {
        f = new JFrame("Load file");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel p = new JPanel();
        p.setLayout(new GridBagLayout());

        JButton close = new JButton("Close");
        JButton load = new JButton("Load");

        JLabel notify = new JLabel("Load file");

        close.addActionListener(e -> f.dispose());
        load.addActionListener(e -> {
            String aux = "./src/Saves/" + comboBox.getEditor().getItem() + ".txt";
            File file = new File(aux);
            Scanner myReader = null;
            try {
                myReader = new Scanner(file);
                while (myReader.hasNextLine()) {
                    tradeBox.getEditor().setItem(myReader.nextLine());
                    TD.setText(myReader.nextLine());
                    tradeT.setText(myReader.nextLine());
                    tradeV.setText(myReader.nextLine());
                    if (myReader.hasNextLine()) {
                        SL.setText(myReader.nextLine());
                        TP.setText(myReader.nextLine());
                        maxT.setText(myReader.nextLine());
                        TS.setText(myReader.nextLine());
                        timeT.setText(myReader.nextLine());
                    }
                }
                myReader.close();
                f.dispose();
            } catch (Exception ignore) {
                notify.setText("Please select a valid save");
            }

        });

        LinkedList<String> loadOptions = new LinkedList<>();
        loadOptions.add("");
        loadOptions.addAll(listFiles("./src/Saves"));
        comboBox = new JComboBox<>(loadOptions.toArray(new String[0]));
        comboBox.setMaximumRowCount(10);
        comboBox.setPreferredSize(comboBox.getPreferredSize());
        comboBox.setMaximumSize(comboBox.getPreferredSize());

        JComboBoxDecorator.decorate(comboBox, true, loadOptions);

        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.WEST;
        GridBagConstraints right = new GridBagConstraints();
        right.anchor = GridBagConstraints.EAST;
        right.weightx = 2.0;

        p.add(load, left);
        p.add(close, right);

        f.add(p, BorderLayout.PAGE_END);
        f.add(comboBox, BorderLayout.CENTER);
        f.add(notify, BorderLayout.PAGE_START);
        f.setVisible(true);
        f.setSize(240, 90);
        f.setLocationRelativeTo(null);
    }
}