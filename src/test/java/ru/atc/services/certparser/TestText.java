package ru.atc.services.certparser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestText {
    private JTextField textField;
    private JButton button;
    private JPanel frame;
    JFrame jFrame;
    public void setText(String text) {
        textField.setText(text);
    }
    public String getText() {
        return textField.getText();
    }
    public TestText() {
        this.textField.setText("98.6");
        this.jFrame = new JFrame("ru.atc.services.certparser.TestText");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setText("new (button)");
            }
        });
    }
    public void setData(TestText data) {
        data.setText("new (setData)");
    }
    public void getData(TestText data) {
    }
    public boolean isModified(TestText data) {
        return false;
    }
    public void createGui(String[] args) {
        jFrame.setContentPane(new TestText().frame);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);
    }

    public static void main(String[] args) {

        TestText gui = new TestText();
        gui.createGui(null);

        System.out.println(gui.getText());
        gui.setData(gui);
        System.out.println(gui.getText());
        gui.setText("new (MainClass)");
        System.out.println(gui.getText());
    }
}