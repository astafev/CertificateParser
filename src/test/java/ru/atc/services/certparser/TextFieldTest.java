package ru.atc.services.certparser;
import ru.atc.services.certparser.config.Configuration;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class TextFieldTest extends JFrame implements ActionListener {
    JPanel panel = new JPanel(new FlowLayout());
    JTextField textField = new JTextField(20);
    JButton set = new JButton("Set Text");
    JButton get = new JButton("Get Text");

    public TextFieldTest() {
        super();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(panel);
        panel.add(textField);
        set.addActionListener(this); //this tells the program that the button actually triggers an event
        panel.add(set);
        get.addActionListener(this);
        panel.add(get);
        pack();
        setVisible(true);



    }


    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == set) {
            textField.setText(JOptionPane.showInputDialog(null, "Enter a new word for the text field:"));
        } else {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            textField.setText("asdfasdfsdfwefq e123");
            System.out.println(textField.getText());
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });

    }

    private static void createAndShowGUI() {
        TextFieldTest  tt = new TextFieldTest ();
    }


    private class DragAndDropHandler extends TransferHandler{
        public CertFactory factory;

        DragAndDropHandler(Configuration config){
            factory = new CertFactory();
        }

        @Override
        public boolean canImport(TransferSupport support) {
            return support
                    .isDataFlavorSupported(DataFlavor.javaFileListFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }
            Transferable t = support.getTransferable();
            try {

                if(t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {

                    Object o = t.getTransferData(DataFlavor.javaFileListFlavor);
                    @SuppressWarnings("unchecked")
                    java.util.List<File> files = (java.util.List<File>) o;
                    for (final File file : files) {

                        //TODO !!!

                    }
                }
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }
}
