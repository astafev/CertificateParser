package ru.atc.services.certparser.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.atc.services.certparser.config.Configuration;
import ru.atc.services.certparser.db.ScriptGeneratorListener;
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
    JButton verifyCertButton = new JButton("Послать сертификат"); {
        verifyCertButton.setToolTipText("Послать сертификат на сервис проверки сертификатов по тому адресу, который указан в конфиге");
    }
    JButton testButton = new JButton("Test");
    JButton reloadConfigButton = new JButton("Reload config");
    JButton generateScript = new JButton("Generate scripts");

    public static Logger log = LoggerFactory.getLogger("certificateparser.gui");

    public ButtonsPanel() {
        super(new GridLayout(4, 1, 10, 25));
        try {
            generateScript.addActionListener(ScriptGeneratorListener.getInstance());
        } catch (IOException e) {
            //todo
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        };


        verifyCertButton.setPreferredSize(new Dimension(250, 50));
//        verifyCertButton.setPreferredSize(new Dimension(150, 50));


        verifyCertButton.addActionListener(SendCertActionListener.getInstance());
        testButton.addActionListener(this);
        reloadConfigButton.addActionListener(Configuration.getInstance());
        this.add(testButton);
        this.add(reloadConfigButton);
        this.add(verifyCertButton);
        this.add(generateScript);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(this.testButton)) {

            System.out.println(this.getParent().getClass().getName());
            System.out.println(this.getParent().getParent().getClass().getName());
        }
    }
}
