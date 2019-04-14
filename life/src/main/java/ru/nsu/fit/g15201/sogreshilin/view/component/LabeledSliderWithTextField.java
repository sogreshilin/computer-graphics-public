package ru.nsu.fit.g15201.sogreshilin.view.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class LabeledSliderWithTextField extends JPanel {
    private final JSlider slider;
    private final JTextField textField;

    private final List<ValueChangedObserver> observers = new ArrayList<>();

    public LabeledSliderWithTextField(String labelText, int min, int max, int spacing) {
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(200, 20));
        slider = new JSlider(min, max);
        slider.setSnapToTicks(true);
        slider.setFocusable(false);
        textField = new JTextField(5);
        add(label, constraints);
        add(slider, constraints);
        add(textField, constraints);

        slider.addChangeListener(e -> {
            int value = slider.getValue();
            notifyValueChanged(value);
            textField.setText(String.valueOf(value));
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
                         textField.setForeground(Color.red);
                         notifyValueChanged(min - 1);
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
