package pro.xstore.api.sync.GUI;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.DefaultComboBoxModel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.SwingUtilities;

public class JComboBoxDecorator
{
    public static void decorate(final JComboBox jcb, boolean editable)
    {
        List<String> entries = new ArrayList<>();
        for (int i = 0; i < jcb.getItemCount(); i++)
        {
            entries.add((String)jcb.getItemAt(i));
        }

        decorate(jcb, editable, entries);
    }

    public static void decorate(final JComboBox jcb, boolean editable, final List<String> entries)
    {
        jcb.setEditable(editable);
        jcb.setModel(
                new DefaultComboBoxModel(
                        entries.toArray()));

        final JTextField textField = (JTextField) jcb.getEditor().getEditorComponent();

        textField.addKeyListener(
                new KeyAdapter()
                {
                    public void keyReleased(KeyEvent e)
                    {
                        SwingUtilities.invokeLater(
                                new Runnable()
                                {
                                    public void run()
                                    {
                                        comboFilter(textField.getText(), jcb, entries);
                                    }
                                }
                        );
                    }
                }
        );
    }

    private static void comboFilter(String enteredText, JComboBox jcb, List<String> entries)
    {
        List<String> entriesFiltered = new ArrayList<String>();

        for (String entry : entries)
        {
            if (entry.toLowerCase().contains(enteredText.toLowerCase()))
            {
                entriesFiltered.add(entry);
            }
        }

        if (entriesFiltered.size() > 0)
        {
            jcb.setModel(
                    new DefaultComboBoxModel(
                            entriesFiltered.toArray()));
            jcb.setSelectedItem(enteredText);
            jcb.showPopup();
        }
        else
        {
            jcb.hidePopup();
        }
    }

}
