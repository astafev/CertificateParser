package ru.atc.services.certparser.gui;

import ru.atc.services.certparser.config.Configuration;

import javax.swing.*;
import java.awt.*;

/**
 * Date: 05.01.13
 * Time: 0:08
 */
public class MainWindow extends JFrame {

    public ButtonsPanel buttonsPanel;
    public CertificatePanel certificatePanel;

    public MainWindow(String s) {
        super(s);
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        MainWindow frame = new MainWindow("Итерация 1");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JPanel mainPanel = new JPanel(new FlowLayout());

        CertificatePanel certPanel = new CertificatePanel();
        mainPanel.add(certPanel);

        ButtonsPanel buttonsPanel = new ButtonsPanel();
        mainPanel.add(buttonsPanel);

        frame.add(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
