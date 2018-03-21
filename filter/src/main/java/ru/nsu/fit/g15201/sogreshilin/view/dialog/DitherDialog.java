package ru.nsu.fit.g15201.sogreshilin.view.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import ru.nsu.fit.g15201.sogreshilin.controller.Controller;
import ru.nsu.fit.g15201.sogreshilin.filter.dither.Dithering;
import ru.nsu.fit.g15201.sogreshilin.filter.dither.OrderedDithering;
import ru.nsu.fit.g15201.sogreshilin.view.component.LabeledTextField;
import ru.nsu.fit.g15201.sogreshilin.view.component.OkCancelButtonPanel;

public class DitherDialog extends JDialog {
    private final Controller controller;
    private final Dithering filter;
    private JCheckBox online;
    private final LabeledTextField redLevels;
    private final LabeledTextField greenLevels;
    private final LabeledTextField blueLevels;

    public DitherDialog(Controller controller, Dithering filter) {
        this.controller = controller;
        this.filter = filter;
        setModal(true);
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel(new BorderLayout());

        JPanel paletteLevels = new JPanel(new GridLayout(1, 3));
        redLevels = new LabeledTextField("Red", 2);
        greenLevels = new LabeledTextField("Green", 2);
        blueLevels = new LabeledTextField("Blue", 2);
        setObservers();

        paletteLevels.add(redLevels);
        paletteLevels.add(greenLevels);
        paletteLevels.add(blueLevels);
        paletteLevels.setBorder(BorderFactory.createTitledBorder("Gradations"));
        controlPanel.add(paletteLevels, BorderLayout.CENTER);
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

    private void setObservers() {
        LabeledTextField.ValueChangedObserver listener =
                e -> onPaletteChanged(redLevels.getValue(),
                        greenLevels.getValue(),
                        blueLevels.getValue());

        redLevels.addValueChangedObserver(listener);
        greenLevels.addValueChangedObserver(listener);
        blueLevels.addValueChangedObserver(listener);
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
        System.out.println(String.format("call setLevels(%d, %d, %d)", redLevels, greenLevels, blueLevels));
        filter.setLevels(redLevels, greenLevels, blueLevels);
        controller.apply(filter);
    }

    private void setUpOrderedDitheringMatrixSizeChooser(JPanel controlPanel) {
        if (filter instanceof OrderedDithering) {
            String[] items = new String[10];
            for (int i = 0; i < 10; ++i) {
                items[i] = String.valueOf(1 << (i + 1));
            }
            JComboBox matrixSizeComboBox = new JComboBox(items);
            matrixSizeComboBox.setEditable(true);
            controlPanel.add(matrixSizeComboBox, BorderLayout.SOUTH);
            matrixSizeComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int i = matrixSizeComboBox.getSelectedIndex();
                    System.out.println("selected index = " + i);
                    ((OrderedDithering) filter).setMatrixSize(1 << (i + 1));
                    controller.apply(filter);
                }
            });

        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
