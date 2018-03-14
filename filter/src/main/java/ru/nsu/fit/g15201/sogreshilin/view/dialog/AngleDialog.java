package ru.nsu.fit.g15201.sogreshilin.view.dialog;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import ru.nsu.fit.g15201.sogreshilin.controller.Controller;
import ru.nsu.fit.g15201.sogreshilin.filter.Rotation;
import ru.nsu.fit.g15201.sogreshilin.view.component.LabeledSliderWithTextField;
import ru.nsu.fit.g15201.sogreshilin.view.component.OkCancelButtonPanel;

public class AngleDialog extends JFrame {
    private static final int SPACING = 1;
    private final Controller controller;
    private LabeledSliderWithTextField angle;
    private JCheckBox online;

    private Rotation filter;

    public AngleDialog(Controller controller, Rotation filter) {
        super("Edge Threshold");
        this.controller = controller;
        this.filter = filter;
        setLayout(new BorderLayout());

        angle = new LabeledSliderWithTextField(
                "Edge angle",
                Rotation.MIN_ANGLE,
                Rotation.MAX_ANGLE,
                SPACING);

        angle.setValue(0);

        angle.addValueChangedObserver(this::onAngleChanged);

        add(angle, BorderLayout.CENTER);
        JPanel checkBoxPanel = createCheckBoxPanel();
        JPanel buttonPanel = createButtonPanel();
        JPanel southPanel = new JPanel(new GridLayout(2, 1));
        southPanel.add(checkBoxPanel);
        southPanel.add(buttonPanel);
        add(southPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(controller);
        this.setResizable(true);
    }

    private JPanel createCheckBoxPanel() {
        JPanel checkBoxPanel = new JPanel(new GridLayout(1, 1));
        online = new JCheckBox("Enable preview", true);

        ChangeListener listener = e -> onAngleChanged(angle.getValue());
        online.addChangeListener(listener);

        checkBoxPanel.add(online);
        return checkBoxPanel;
    }

    private JPanel createButtonPanel() {
        OkCancelButtonPanel buttonPanel = new OkCancelButtonPanel();
        buttonPanel.addCancelButtonListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controller.onCancel();
                dispose();
            }
        });
        buttonPanel.addOkButtonListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                apply(angle.getValue());
                dispose();
            }
        });
        return buttonPanel;
    }

    private void onAngleChanged(int value) {
        if (online.isSelected()) {
            apply(value);
        }
    }

    private void apply(int value) {
        filter.setAngle(value);
        controller.apply(filter);
    }
}
