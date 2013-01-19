package ru.atc.services.certparser.service;

import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import ru.atc.services.certparser.config.Configuration;
import ru.atc.services.certparser.gui.CertificatePanel;
import ru.atc.services.certparser.gui.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Date: 02.01.13
 * Time: 21:14
 */
public class SendCertActionListener implements ActionListener {
//    ServiceVerify verifier;
    public static Logger log = LoggerFactory.getLogger("certificateparser.service");
//    Executor exec = new ThreadPoolExecutor();
    private SendCertActionListener() {
    }

    public static SendCertActionListener sendCertActionListener;

    public static SendCertActionListener getInstance() {
        if(sendCertActionListener == null) {
            sendCertActionListener = new SendCertActionListener();
        }
        return sendCertActionListener;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //to_think
        Thread thread = new Thread( new Runnable() {
            @Override
            public void run() {
                try {

                    String response = new ServiceVerify().validateCertificate(MainWindow.certFile);
                    log.debug("Response: " + response);
                    if(response.equals("0"))
                        JOptionPane.showMessageDialog(null, "Сертификат прошел проверку", "Все ОК!", JOptionPane.YES_NO_CANCEL_OPTION);
                    else
                        JOptionPane.showMessageDialog(null, response, "Все плохо!", JOptionPane.ERROR_MESSAGE);
                }  catch (HttpHostConnectException e1) {
                    log.error("can't connect to service", e1);
                    JOptionPane.showMessageDialog(null, e1.getLocalizedMessage(), "Не могу достучаться до сервиса!", JOptionPane.ERROR_MESSAGE);
                } catch (SAXException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (NullPointerException e1) {
                    JOptionPane.showMessageDialog(null, "Fuck you!\n" + e1.getMessage(), "NullPointerException", JOptionPane.ERROR_MESSAGE);
                } catch (IOException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
        thread.start();
    }

}

