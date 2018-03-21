package ru.nsu.fit.g15201.sogreshilin.view.dialog;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import ru.nsu.fit.g15201.sogreshilin.controller.Controller;
import ru.nsu.fit.g15201.sogreshilin.filter.Watercolor;
import ru.nsu.fit.g15201.sogreshilin.filter.edge.EdgeDetection;
import ru.nsu.fit.g15201.sogreshilin.view.component.LabeledSliderWithTextField;
import ru.nsu.fit.g15201.sogreshilin.view.component.OkCancelButtonPanel;

public class EdgeThresholdDialog extends JDialog {
    private static final int SPACING = 1;
    private final Controller controller;
    private LabeledSliderWithTextField threshold;
    private JCheckBox online;
    private JCheckBox invert;

    private EdgeDetection filter;

    public EdgeThresholdDialog(Controller controller, EdgeDetection filter) {
        setModal(true);
        this.controller = controller;
        this.filter = filter;
        setLayout(new BorderLayout());

        threshold = new LabeledSliderWithTextField(
                "Edge threshold",
                EdgeDetection.MIN_THRESHOLD,
                EdgeDetection.MAX_THRESHOLD,
                SPACING);

        threshold.setValue(EdgeDetection.DEFAULT_THRESHOLD);
        threshold.addValueChangedObserver(this::onEdgeThresholdChanged);

        add(threshold, BorderLayout.CENTER);
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
        JPanel checkBoxPanel = new JPanel(new GridLayout(1, 2));
        online = new JCheckBox("Enable preview", true);
        invert = new JCheckBox("Invert colors", false);

        ChangeListener listener = e -> onEdgeThresholdChanged(threshold.getValue());
        online.addChangeListener(listener);
        invert.addChangeListener(listener);

        checkBoxPanel.add(online);
        checkBoxPanel.add(invert);
        return checkBoxPanel;
    }

    private JPanel createButtonPanel() {
        OkCancelButtonPanel buttonPanel = new OkCancelButtonPanel();
        buttonPanel.addOkButtonListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                apply(threshold.getValue());
                dispose();
            }
        });
        buttonPanel.addCancelButtonListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controller.onCancel();
                dispose();
            }
        });
        return buttonPanel;
    }

    private void onEdgeThresholdChanged(int value) {
        if (online.isSelected()) {
            apply(value);
        }
    }

    private void apply(int value) {
        filter.setInverted(invert.isSelected());
        filter.setThreshold(value);
        controller.apply(filter);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
