package ru.atc.services.certparser.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import ru.atc.services.certparser.config.Configuration;
import ru.atc.services.certparser.service.SendCertActionListener;
import ru.atc.services.certparser.service.ServiceVerify;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * <p>Панелька с кнопочками, предположительно справа от поля с сертификатом</p>
 * <p>Тут должны быть:</p>
 * <ul>
 * <li>Кнопка "Послать сертификат, при нажатии на которую сертификат посылается на сервис проверки сертификатов"</li>
 * <li>??? "Открыть..." при нажатии на которую открывается explorer</li>
 * <li>На итерации 3: кнопка "Сгенерить скрипты"</li>
 * <li>На итерации 4: кнопка "Зарегать ИС"</li>
 * </ul>
 */
public class ButtonsPanel extends JPanel implements ActionListener{
    JButton verifyCertButton = new JButton("Послать сертификат");
    JButton testButton = new JButton("Test");
    JButton reloadConfigButton = new JButton("Reload config");


    ServiceVerify verifier;
    public static Logger log = LoggerFactory.getLogger("certificateparser.gui");

    public ButtonsPanel() {
        verifyCertButton.addActionListener(SendCertActionListener.getInstance());
        testButton.addActionListener(this);
        reloadConfigButton.addActionListener(Configuration.getInstance());
        this.add(verifyCertButton);
        this.add(testButton);
        this.add(reloadConfigButton);
//        this.setTransferHandler();
        //todo добавить transferhandler
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(this.testButton)) {
            this.getParent().getParent().getParent().getParent().getParent().getClass().getName();

            System.out.println(this.getParent().getClass().getName());
            System.out.println(this.getParent().getParent().getClass().getName());
            System.out.println(this.getParent().getParent().getParent().getClass().getName());
            System.out.println(this.getParent().getParent().getParent().getParent().getClass().getName());
            System.out.println(this.getParent().getParent().getParent().getParent().getParent().getClass().getName());
        }
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Итерация 2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new FlowLayout());
        CertificatePanel certPanel = new CertificatePanel();
        panel.add(certPanel);
        ButtonsPanel buttonsPanel = new ButtonsPanel();

        panel.add(buttonsPanel);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);

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
