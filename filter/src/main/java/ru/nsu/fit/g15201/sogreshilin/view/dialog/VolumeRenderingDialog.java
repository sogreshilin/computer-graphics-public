package ru.nsu.fit.g15201.sogreshilin.view.dialog;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.*;
import ru.nsu.fit.g15201.sogreshilin.controller.Controller;
import ru.nsu.fit.g15201.sogreshilin.rendering.VolumeRendering;
import ru.nsu.fit.g15201.sogreshilin.view.component.OkCancelButtonPanel;

public class VolumeRenderingDialog extends JDialog {
    private final Controller controller;
    private final VolumeRendering filter;
    private final TextField xLayers;
    private final TextField yLayers;
    private final TextField zLayers;

    public VolumeRenderingDialog(Controller controller, VolumeRendering filter) {
        setModal(true);
        this.controller = controller;
        this.filter = filter;

        String defaultValue = String.valueOf(VolumeRendering.MAX_VOXEL_COUNT);
        xLayers = new TextField(defaultValue);
        yLayers = new TextField(defaultValue);
        zLayers = new TextField(defaultValue);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5 , 5, 5));

        JPanel controlPanel = new JPanel(new GridLayout(3, 2));
        controlPanel.add(new JLabel("X layers"));
        controlPanel.add(xLayers);
        controlPanel.add(new JLabel("Y layers"));
        controlPanel.add(yLayers);
        controlPanel.add(new JLabel("Z layers"));
        controlPanel.add(zLayers);
        mainPanel.add(controlPanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
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
                try {
                    filter.setXLayers(getValueFromTextField(xLayers));
                    filter.setYLayers(getValueFromTextField(yLayers));
                    filter.setZLayers(getValueFromTextField(zLayers));
                    dispose();
                    apply();
                } catch (IOException exception) {
                    String message = String.format("Values must be in %d..%d, ",
                            VolumeRendering.MIN_VOXEL_COUNT,
                            VolumeRendering.MAX_VOXEL_COUNT);
                    JOptionPane.showMessageDialog(VolumeRenderingDialog.this,
                            message,
                            "Invalid values",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        return buttonPanel;
    }

    private int getValueFromTextField(TextField textField) throws IOException {
        textField.setForeground(Color.black);
        String text = textField.getText();
        try {
            int input = Integer.parseInt(text);
            if (VolumeRendering.MIN_VOXEL_COUNT <= input &&
                    input <= VolumeRendering.MAX_VOXEL_COUNT) {
                return input;
            } else {
                textField.setText("");
                throw new IOException();
            }
        } catch (NumberFormatException ex) {
            textField.setText("");
            throw new IOException();
        }
    }

    private void apply() {
        controller.apply(filter);
    }
}
