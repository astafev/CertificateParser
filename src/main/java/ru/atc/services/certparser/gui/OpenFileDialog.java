package ru.atc.services.certparser.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.atc.services.certparser.service.SendCertActionListener;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Date: 22.01.13
 * Time: 10:29
 */
class OpenFileDialog extends TransferHandler implements ActionListener {
    CertificatePanel cp = MainWindow.getInstance().certificatePanel;
    static File currentDir;

    JFileChooser fc = new JFileChooser(currentDir); {
        fc.setTransferHandler(cp.getTransferHandler());
    }

    public static Logger log = MainWindow.log;

    @Override
    public void actionPerformed(ActionEvent e) {
        int val = fc.showOpenDialog(MainWindow.getInstance());
        if(val == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            currentDir = fc.getCurrentDirectory();
            try {
                cp.openCertificate(file);
            } catch (IOException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }


    @Override
    public boolean canImport(TransferSupport support) {
        return support
                .isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }

    @Override
    public boolean importData(TransferSupport support) {
        fc.cancelSelection();
        fc.setVisible(false);
        return cp.getTransferHandler().importData(support);
    }


}
