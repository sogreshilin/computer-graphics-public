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
    private int value;

    public LabeledTextField(String name, int value) {
        setLayout(new GridLayout(2, 1));
        JLabel label = new JLabel(name);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        textField = new JTextField(String.valueOf(value));
        add(label);
        add(textField);
        this.value = value;

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = textField.getText();
                if (text.isEmpty()) {
                    textField.setForeground(Color.black);
                    return;
                }
                try {
                    int val = Integer.parseInt(text);
                    if (2 <= val && val <= 256) {
                        notifyValueChanged(val);
                        textField.setForeground(Color.black);
                    } else {
                        textField.setForeground(Color.red);
//                        notifyValueChanged(-1);
                    }
                } catch (NumberFormatException ex) {
                    textField.setForeground(Color.red);
//                    notifyValueChanged(-1);
                }
            }
        });
    }

    public void addValueChangedObserver(ValueChangedObserver o) {
        observers.add(o);
    }

    private void notifyValueChanged(int value) {
        this.value = value;
        for (ValueChangedObserver o : observers) {
            if (o != null) {
                o.setValue(value);
            }
        }
    }

    public int getValue() {
        return value;
    }

    public interface ValueChangedObserver {
        void setValue(double value);
    }
}
