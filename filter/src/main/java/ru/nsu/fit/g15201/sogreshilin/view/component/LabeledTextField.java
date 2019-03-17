package ru.nsu.fit.g15201.sogreshilin.view.component;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class LabeledTextField extends JPanel {
    private int value;
    private String label;
    private List<ValueChangedObserver> observers = new ArrayList<>();

    public LabeledTextField(String label, int value) {
        this.label = label;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void addValueChangedObserver(ValueChangedObserver listener) {
        observers.add(listener);
    }

    public interface ValueChangedObserver {
        void onValueChanged();
    }
}
