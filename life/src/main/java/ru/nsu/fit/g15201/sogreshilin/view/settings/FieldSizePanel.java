package ru.nsu.fit.g15201.sogreshilin.view.settings;

import ru.nsu.fit.g15201.sogreshilin.model.io.Config;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class FieldSizePanel extends JPanel {

    private Config config;

    public FieldSizePanel(Config config) {
        this.config = config;
        setBorder(new TitledBorder("Field size"));
        setLayout(new GridLayout(2, 1));

        JPanel panelWidth = new JPanel(new GridLayout(1, 2));
        panelWidth.add(new JLabel("Width"));
        JTextField widthTextField = new JTextField(String.valueOf(config.getFieldWidth()));
        panelWidth.add(widthTextField);
        widthTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (widthTextField.getText().isEmpty()) {
                    widthTextField.setForeground(Color.black);
                    return;
                }
                try {
                    widthTextField.setForeground(Color.BLACK);
                    int val = Integer.valueOf(widthTextField.getText());
                    if (val <= 0) {
                        throw new NumberFormatException();
                    }
                    config.setFieldWidth(val);
                } catch (NumberFormatException ex) {
                    widthTextField.setForeground(Color.RED);
                    config.setFieldWidth(-1);
                }
            }
        });


        JPanel panelHeight = new JPanel(new GridLayout(1, 2));
        panelHeight.add(new JLabel("Height"));
        JTextField heightTextField = new JTextField(String.valueOf(config.getFieldHeight()));
        panelHeight.add(heightTextField);

        heightTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (heightTextField.getText().isEmpty()) {
                    heightTextField.setForeground(Color.black);
                    return;
                }
                try {
                    heightTextField.setForeground(Color.BLACK);
                    int val = Integer.valueOf(heightTextField.getText());
                    if (val <= 0) {
                        throw new NumberFormatException();
                    }
                    config.setFieldHeight(val);
                } catch (NumberFormatException ex) {
                    heightTextField.setForeground(Color.RED);
                    config.setFieldHeight(-1);
                }
            }
        });

        add(panelWidth);
        add(panelHeight);
    }
}
