package ru.nsu.fit.g15201.sogreshilin.view.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class LabeledSliderWithTextField extends JPanel {
    private JLabel label;
    private JSlider slider;
    private JTextField textField;

    private List<ValueChangedObserver> observers = new ArrayList<>();

    public LabeledSliderWithTextField(String label, int min, int max, int spacing) {
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        this.label = new JLabel(label);
        this.label.setPreferredSize(new Dimension(200, 20));
        slider = new JSlider(min, max);
//        slider.setMajorTickSpacing(spacing);
//        slider.setPaintTicks(true);
        slider.setSnapToTicks(true);
        slider.setFocusable(false);
        textField = new JTextField(5);

//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        constraints.gridx = 0;
//        constraints.gridy = 0;
//        constraints.weightx = 0.25;
        add(this.label, constraints);
//        constraints.gridx = 1;
//        constraints.gridwidth = 3;
//        constraints.weightx = 0.75;
        add(slider, constraints);
//        constraints.gridx = 4;
//        constraints.weightx = 0.25;
        add(textField, constraints);

//        setBorder(new EtchedBorder());

        slider.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                int value = slider.getValue();
                notifyValueChanged(value);
                textField.setText(String.valueOf(value));
            }
        });

        textField.addKeyListener(new KeyAdapter() {
             @Override
             public void keyReleased(KeyEvent e) {
                 String text = textField.getText();
                 if (text.isEmpty()) {
                     textField.setForeground(Color.black);
                     return;
                 }
                 try {
                     int value = Integer.parseInt(text);
                     if (min <= value && value <= max) {
                         notifyValueChanged(value);
                         textField.setText(String.valueOf(value));
                         textField.setForeground(Color.black);
                         slider.setValue(value);
                     } else {
                         throw new NumberFormatException();
                     }
                 } catch (NumberFormatException ex) {
                     textField.setForeground(Color.red);
                     notifyValueChanged(min - 1);
                 }
             }
         });
    }

    public void addValueChangedObserver(ValueChangedObserver o) {
        observers.add(o);
    }

    private void notifyValueChanged(int value) {
        for (ValueChangedObserver o : observers) {
            if (o != null) {
                o.setValue(value);
            }
        }
    }

    public void setValue(int value) {
        textField.setText(String.valueOf(value));
        slider.setValue(value);
    }

    public interface ValueChangedObserver {
        void setValue(int value);
    }
}
