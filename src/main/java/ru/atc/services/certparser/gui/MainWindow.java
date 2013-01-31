package ru.atc.services.certparser.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.atc.services.certparser.config.Configuration;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Date: 05.01.13
 * Time: 0:08
 */
public class MainWindow extends JFrame {

    public ButtonsPanel buttonsPanel;
    public CertificatePanel certificatePanel;
    private static MainWindow windowInstance;
    public static Logger log = LoggerFactory.getLogger("certificateparser.gui");
    public static File certFile;

    private MainWindow(String s) {
        super(s);
    }

    public static MainWindow getInstance() {
        if(windowInstance == null) {
            windowInstance = new MainWindow("Парсер сертификатов");
        }
        return windowInstance;
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     * @param args
     */
    private static void createAndShowGUI(String[] args) {
        MainWindow frame = MainWindow.getInstance();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JPanel mainPanel = new JPanel(new FlowLayout());


        frame.certificatePanel = new CertificatePanel();
        mainPanel.add(frame.certificatePanel);

        frame.buttonsPanel = new ButtonsPanel();
        mainPanel.add(frame.buttonsPanel);
        frame.buttonsPanel.setTransferHandler(frame.certificatePanel.getTransferHandler());

        frame.add(mainPanel);
        frame.pack();
        frame.setVisible(true);

        //обработка параметров
        if (args.length != 0) {
            if (args.length > 1) {
                log.warn("Wrong usage! Launched with more than one parameter!");
                System.err.println("Wrong usage! You should run with one parameter - file name, or without parameters at all, i suppose...");
            } else {
                File file = new File(args[0]);
                if (!file.exists()) {
                    //todo это делать бы не здесь...
                    log.warn("(parameter) File doesn't exist: " + file.toString());
                    System.err.println("File doesn't exist! " + file.toString());
                } else {
                    try {
                        frame.certificatePanel.openCertificate(file);
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        }
    }

    public static void main(final String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(args);
            }
        });
    }
}
