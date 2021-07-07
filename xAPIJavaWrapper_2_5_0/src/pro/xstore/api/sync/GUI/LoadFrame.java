package pro.xstore.api.sync.GUI;


import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Scanner;
import javax.swing.*;


public class LoadFrame extends JFrame {
    private static JFrame frame;
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
        frame = new JFrame("Load file");
        Image icon = Toolkit.getDefaultToolkit().getImage("../../../src/Media/logo.png");
        frame.setIconImage(icon);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel buttons = new JPanel();
        buttons.setBackground(Color.white);
        buttons.setLayout(new FlowLayout(FlowLayout.CENTER, 45, 0));
        
        JButton close = new JButton("Close");
        close.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        close.setBackground(new Color(0,104,55));
        close.setForeground(Color.white);
        buttons.add(close);

        JButton load = new JButton("Load");
        load.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        load.setBackground(new Color(0,104,55));
        load.setForeground(Color.white);
        buttons.add(load);

        JLabel notify = new JLabel("Load file");
        notify.setFont(new Font("Comic Sans MS", Font.BOLD, 14));

        close.addActionListener(e -> frame.dispose());
        load.addActionListener(e -> {
            String aux = "../../../src/Saves/" + comboBox.getEditor().getItem() + ".txt";
            File file = new File(aux);
            Scanner myReader;
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
                frame.dispose();
            } catch (Exception ignore) {
                notify.setText("Please select a valid save!");
                notify.setForeground(Color.red);
            }

        });

        LinkedList<String> loadOptions = new LinkedList<>();
        loadOptions.add("");
        loadOptions.addAll(listFiles("../../../src/Saves"));
        comboBox = new JComboBox<>(loadOptions.toArray(new String[0]));
        comboBox.setMaximumRowCount(10);
        comboBox.setPreferredSize(new Dimension(180, 25));
        comboBox.setSize(new Dimension(180, 25));
        comboBox.setMaximumSize(new Dimension(180, 25));
        JComboBoxDecorator.decorate(comboBox, true, loadOptions);
        comboBox.setFont(new Font("Comic Sans MS", Font.BOLD, 14));

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        JPanel title = new JPanel();
        title.add(notify);
        title.setBackground(Color.white);
        panel.add(title);

        JPanel dropdown = new JPanel();
        dropdown.add(comboBox);
        dropdown.setBackground(Color.white);
        panel.add(dropdown);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttons);

        frame.getContentPane().add(panel);
        frame.setVisible(true);
        frame.setSize(240, 200);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
    }
}