package ru.atc.services.certparser.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.atc.services.certparser.CertFactory;
import ru.atc.services.certparser.config.Configuration;
import ru.atc.services.certparser.config.Property;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.*;


public class CertificatePanel extends JPanel{
    //todo можно сделать нормальнее, добавив названия к компонентам как св-ва
    Map<Property, JTextComponent> propFields = new HashMap<Property, JTextComponent>();
    JTextArea certificateArea;
    public static Logger log = LoggerFactory.getLogger("certificateparser.gui");
    DragAndDropHandler transferHandler;

    Configuration config = Configuration.getInstance();
    //костыль
    static public File certFile = null;

    CertificatePanel(){
        super(new GridBagLayout());
        this.putClientProperty("Name", "CertificatePanel");
        this.setBackground(Color.GRAY);
        setBorder(BorderFactory.createEtchedBorder());
        addPropFields();
        transferHandler = new DragAndDropHandler();
        setTransferHandler(transferHandler);
    }



    public void addPropFields()  {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        int yi = 0;
        for(Property prop:config.propSet) {
            //prop[0] - паттерн
            //prop[1] - имя
            //prop[2] - где искать
            /*if(prop.getName().equals("Серийный номер")){
                //серийный номер обязательно должен быть, в конфиге не должно
                JLabel label = new JLabel("Серийный номер");
                c.gridy = yi;
                c.gridx = 0;
                c.gridwidth = 1;
                this.add(label, c);

                JTextField serialNumberField = new JTextField();
                c.gridy = yi;
                c.gridx = 1;
                this.add(serialNumberField, c);
                yi++;
//        serialNumberField.setEditable(false);
                propFields.put("Серийный номер", serialNumberField);
                continue;
            }*/
            if(!prop.getName().equals("Сертификат")){
                JLabel label = new JLabel(prop.getName());
                c.gridy = yi;
                c.gridx = 0;
                c.ipadx=10;
                c.gridwidth = 1;
                this.add(label, c);

                JTextField textField = new JTextField(2);
                c.gridy = yi;
                c.ipadx=200;
                c.gridx = 1;
                c.gridwidth = 2;
                this.add(textField, c);
                yi++;
                propFields.put(prop, textField);
            } else {
                JLabel certLabel = new JLabel("Сертификат:");
                c.fill = GridBagConstraints.HORIZONTAL;
                c.gridx = 0;
                c.gridy = yi++;
                c.gridwidth = 2;
                this.add(certLabel, c);

                certificateArea = new JTextArea();
                c.gridx = 0;
                c.gridy = yi++;
                c.anchor = GridBagConstraints.PAGE_END;
                c.weighty = 10;
                c.fill = GridBagConstraints.BOTH;
                c.gridwidth = 3;
                c.ipady = 50;
                this.add(certificateArea, c);
                propFields.put(prop, certificateArea);

                JScrollPane scrollPane = new JScrollPane(certificateArea);
                scrollPane.setPreferredSize(new Dimension(300, 250));
                this.add(scrollPane,c);
                continue;
            }
        }
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Итерация 1");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Configuration config = new Configuration();
        CertificatePanel panel = new CertificatePanel();
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }





    private class DragAndDropHandler extends TransferHandler{
        public CertFactory factory;

        DragAndDropHandler(){
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
//            Transferable t = support.getTransferable();
            try {
                @SuppressWarnings("unchecked")
                java.util.List<File> files = (java.util.List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                log.debug("dropped " + files.size() + " files");
                CertificatePanel.certFile = files.get(0);
                if(files.size()>1) {
                    log.error("You added more than one file");
                }

                Map<Property, String> certificateMap = factory.parseCertificate(files.get(0));
                for(Property prop:certificateMap.keySet())
                {
                    propFields.get(prop).setText(certificateMap.get(prop));
//                    propFields.get(prop.getName()).moveCaretPosition(0);
                }
                if(config.TO_SEND_AUTHOMATICALLY) {
                    //todo
                }
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}