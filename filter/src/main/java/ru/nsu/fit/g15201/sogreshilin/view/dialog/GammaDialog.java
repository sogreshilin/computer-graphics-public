package ru.nsu.fit.g15201.sogreshilin.view.dialog;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import ru.nsu.fit.g15201.sogreshilin.controller.Controller;
import ru.nsu.fit.g15201.sogreshilin.filter.GammaCorrection;
import ru.nsu.fit.g15201.sogreshilin.view.component.LabeledSliderWithTextField;
import ru.nsu.fit.g15201.sogreshilin.view.component.OkCancelButtonPanel;

// todo : gamma coefficient should be double, not int
public class GammaDialog extends JFrame {
    private static final int SPACING = 1;
    private final Controller controller;
    private LabeledSliderWithTextField gamma;
    private JCheckBox online;

    private GammaCorrection filter;

    public GammaDialog(Controller controller, GammaCorrection filter) {
        super("Gamma");
        this.controller = controller;
        this.filter = filter;
        setLayout(new BorderLayout());

        gamma = new LabeledSliderWithTextField(
                "Gamma",
                GammaCorrection.MIN_GAMMA,
                GammaCorrection.MAX_GAMMA,
                SPACING);

        gamma.setValue(GammaCorrection.DEFAULT_GAMMA);
        gamma.addValueChangedObserver(this::onGammaChanged);

        add(gamma, BorderLayout.CENTER);
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

        ChangeListener listener = e -> onGammaChanged(gamma.getValue());
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
                apply(gamma.getValue());
                dispose();
            }
        });
        return buttonPanel;
    }

    private void onGammaChanged(int value) {
        if (online.isSelected()) {
            apply(value);
        }
    }

    private void apply(int value) {
        filter.setGamma(value);
        controller.apply(filter);
    }
}
