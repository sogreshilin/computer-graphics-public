package ru.nsu.fit.g15201.sogreshilin.view.dialog;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import ru.nsu.fit.g15201.sogreshilin.controller.Controller;
import ru.nsu.fit.g15201.sogreshilin.filter.dither.Dithering;
import ru.nsu.fit.g15201.sogreshilin.filter.edge.EdgeDetection;
import ru.nsu.fit.g15201.sogreshilin.view.component.LabeledSliderWithTextField;
import ru.nsu.fit.g15201.sogreshilin.view.component.LabeledTextField;
import ru.nsu.fit.g15201.sogreshilin.view.component.OkCancelButtonPanel;

public class DitherDialog extends JFrame {
    private final Controller controller;
    private final Dithering filter;
    private JCheckBox online;
    private final LabeledTextField redLevels;
    private final LabeledTextField greenLevels;
    private final LabeledTextField blueLevels;

    public DitherDialog(Controller controller, Dithering filter) {
        super("Dither parameters");
        this.controller = controller;
        this.filter = filter;
        setLayout(new GridLayout(2, 1));

        JPanel paletteLevels = new JPanel(new GridLayout(1, 3));
        redLevels = new LabeledTextField("Red levels", 2);
        greenLevels = new LabeledTextField("Green levels", 2);
        blueLevels = new LabeledTextField("Blue levels", 2);
        paletteLevels.add(redLevels);
        paletteLevels.add(greenLevels);
        paletteLevels.add(blueLevels);
        add(paletteLevels);

        JPanel checkBoxPanel = createCheckBoxPanel();
        JPanel buttonPanel = createButtonPanel();
        JPanel southPanel = new JPanel(new GridLayout(2, 1));
        southPanel.add(checkBoxPanel);
        southPanel.add(buttonPanel);
        add(southPanel);

        add(buttonPanel);


        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(controller);
        this.setResizable(true);
    }

    private JPanel createCheckBoxPanel() {
        JPanel checkBoxPanel = new JPanel(new GridLayout(1, 1));
        online = new JCheckBox("Enable preview", true);

        ChangeListener listener =
                e -> onPaletteChanged(redLevels.getValue(),
                        greenLevels.getValue(),
                        blueLevels.getValue());
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
                apply(redLevels.getValue(),
                        greenLevels.getValue(),
                        blueLevels.getValue());
                dispose();
            }
        });
        return buttonPanel;

    }

    private void onPaletteChanged(int redLevels, int greenLevels, int blueLevels) {
        if (online.isSelected()) {
            apply(redLevels, greenLevels, blueLevels);
        }
    }

    private void apply(int redLevels, int greenLevels, int blueLevels) {
        filter.setLevels(redLevels, greenLevels, blueLevels);
        controller.apply(filter);
    }
}
