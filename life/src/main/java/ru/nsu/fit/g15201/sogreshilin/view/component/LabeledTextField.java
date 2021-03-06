package ru.nsu.fit.g15201.sogreshilin.view.component;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class LabeledTextField extends JPanel {
    private final JTextField textField;
    private final List<ValueChangedObserver> observers = new ArrayList<>();

    public LabeledTextField(String name, double value) {
        setLayout( new GridLayout(2, 1));
        JLabel label = new JLabel(name);
        textField = new JTextField(String.valueOf(value));
        add(label);
        add(textField);

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = textField.getText();
                if (text.isEmpty()) {
                    textField.setForeground(Color.black);
                    return;
                }
                try {
                    double val = Double.parseDouble(text);
                    if (0 <= val) {
                        notifyValueChanged(val);
                        textField.setForeground(Color.black);
                    } else {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    textField.setForeground(Color.red);
                    notifyValueChanged(-1);
                }
            }
        });
    }

    public void addValueChangedObserver(ValueChangedObserver o) {
        observers.add(o);
    }

    private void notifyValueChanged(double value) {
        for (ValueChangedObserver o : observers) {
            if (o != null) {
                o.setValue(value);
            }
        }
    }

    public interface ValueChangedObserver {
        void setValue(double value);
    }
}
