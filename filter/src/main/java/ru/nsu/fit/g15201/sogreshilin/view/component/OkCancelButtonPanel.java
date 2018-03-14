package ru.nsu.fit.g15201.sogreshilin.view.component;

import java.awt.*;
import java.awt.event.MouseListener;
import javax.swing.*;

public class OkCancelButtonPanel extends JPanel {
    private JButton okButton = new JButton("OK");
    private JButton cancelButton = new JButton("Cancel");

    public OkCancelButtonPanel() {
        setLayout(new GridLayout(1, 2));
        add(okButton);
        add(cancelButton);
    }

    public void addOkButtonListener(MouseListener listener) {
        okButton.addMouseListener(listener);
    }

    public void addCancelButtonListener(MouseListener listener) {
        cancelButton.addMouseListener(listener);
    }
}
