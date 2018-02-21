package ru.nsu.fit.g15201.sogreshilin.view.component;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
        slider.setSnapToTicks(true);
        slider.setFocusable(false);
        textField = new JTextField(5);
        add(this.label, constraints);
        add(slider, constraints);
        add(textField, constraints);

        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
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
