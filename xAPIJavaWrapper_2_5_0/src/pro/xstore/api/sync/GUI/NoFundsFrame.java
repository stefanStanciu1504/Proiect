package pro.xstore.api.sync.GUI;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;



public class NoFundsFrame extends Frame {
    private final String market;

    public NoFundsFrame(String market) {
        this.market = market;
    }

    public void run() {
        JFrame frame = new JFrame();
        Image icon = Toolkit.getDefaultToolkit().getImage("./src/Media/logo.png");
        frame.setIconImage(icon);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        try {
            File soundFile = new File("./src/Media/theetone.wav");

            AudioInputStream sampleStream = AudioSystem.getAudioInputStream(soundFile);

            AudioFormat formatAudio = sampleStream.getFormat();

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, formatAudio);

            SourceDataLine theAudioLine = (SourceDataLine) AudioSystem.getLine(info);

            theAudioLine.open(formatAudio);

            theAudioLine.start();

            int BUFFER_SIZE = 4096;
            byte[] bufferBytes = new byte[BUFFER_SIZE];
            int readBytes = -1;

            while ((readBytes = sampleStream.read(bufferBytes)) != -1) {
                theAudioLine.write(bufferBytes, 0, readBytes);
            }

            theAudioLine.drain();
            theAudioLine.close();
            sampleStream.close();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }

        Box box = new Box(BoxLayout.Y_AXIS);
        box.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        box.setBackground(Color.white);


        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        JLabel warning = new JLabel("No funds left on the market " + this.market + "!");
        warning.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        warning.setBackground(Color.white);
        warning.setForeground(Color.red);
        panel.add(warning);

        box.add(Box.createVerticalGlue());
        box.add(panel);
        box.add(Box.createVerticalGlue());

        frame.getContentPane().add(box);
        frame.getContentPane().setBackground(Color.white);
        frame.setSize(200, 120);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
    }
}
