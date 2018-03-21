package ru.nsu.fit.g15201.sogreshilin.view.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import ru.nsu.fit.g15201.sogreshilin.controller.Controller;
import ru.nsu.fit.g15201.sogreshilin.filter.GammaCorrection;
import ru.nsu.fit.g15201.sogreshilin.view.component.LabeledSliderWithTextField;
import ru.nsu.fit.g15201.sogreshilin.view.component.OkCancelButtonPanel;
import static java.lang.Math.*;

public class GammaDialog extends JDialog {
    private static final int SPACING = 1;
    private final Controller controller;
    private LabeledSliderWithTextField gamma;
    private JCheckBox online;
    private JSlider slider;
    private JTextField textField;
    private int value;
    private final int amplitude;

    private GammaCorrection filter;

    public GammaDialog(Controller controller, GammaCorrection filter) {
        setModal(true);
        this.controller = controller;
        this.filter = filter;
        setLayout(new BorderLayout());

        this.value = GammaCorrection.DEFAULT_GAMMA;
        this.amplitude = (GammaCorrection.MAX_GAMMA - GammaCorrection.MIN_GAMMA);


        JPanel controlPanel = new JPanel(new GridLayout(2, 1));
        slider = new JSlider(
                GammaCorrection.MIN_GAMMA,
                GammaCorrection.MAX_GAMMA);
        slider.setValue(value);
        slider.setSnapToTicks(true);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.addChangeListener(e -> {
            value = slider.getValue();
            onGammaChanged((double) value / amplitude * 10);
            textField.setText(String.format("%.1f", (double) value / amplitude * 10));
        });

        textField = new JTextField(String.format("%.1f", (double) value / amplitude * 10));

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textField.setForeground(Color.black);
                String text = textField.getText();
                try {
                    double input = Double.parseDouble(text);
                    int inputValue = (int) ((((double) round(input * 2)) / 2) * 10);
                    if (inputValue > 100) {
                        inputValue = 100;
                    } else if (inputValue < 0) {
                        inputValue = 0;
                    }
                    value = inputValue;
                    textField.setText(String.format("%.1f", (double) value / amplitude * 10));
                    slider.setValue(value);
                } catch (NumberFormatException ex) {
                    textField.setForeground(Color.red);
                }
            }
        });

        controlPanel.add(slider);
        controlPanel.add(textField);

        add(controlPanel, BorderLayout.CENTER);

        JPanel checkBoxPanel = createCheckBoxPanel();
        JPanel buttonPanel = createButtonPanel();
        JPanel southPanel = new JPanel(new GridLayout(2, 1));
        southPanel.add(checkBoxPanel);
        southPanel.add(buttonPanel);
        add(southPanel, BorderLayout.SOUTH);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                controller.onCancel();
            }
        });
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(controller);
        this.setResizable(true);
    }

    private JPanel createCheckBoxPanel() {
        JPanel checkBoxPanel = new JPanel(new GridLayout(1, 1));
        online = new JCheckBox("Enable preview", true);

        ChangeListener listener = e -> onGammaChanged((double) value / amplitude * 10);
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
                apply((double) value / amplitude * 10);
                dispose();
            }
        });
        return buttonPanel;
    }

    private void onGammaChanged(double value) {
        if (online.isSelected()) {
            apply(value);
        }
    }

    private void apply(double value) {
        filter.setGamma(value);
        controller.apply(filter);
    }
}
