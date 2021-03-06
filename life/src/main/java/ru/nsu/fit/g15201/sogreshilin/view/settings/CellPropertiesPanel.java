package ru.nsu.fit.g15201.sogreshilin.view.settings;

import ru.nsu.fit.g15201.sogreshilin.model.io.Config;
import ru.nsu.fit.g15201.sogreshilin.view.component.LabeledSliderWithTextField;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

class CellPropertiesPanel extends JPanel {
    private static final int SPACING = 1;
    private final LabeledSliderWithTextField lineThickness;
    private final LabeledSliderWithTextField cellSize;
    private Config config;

    public CellPropertiesPanel(Config config) {
        this.config = config;
        setBorder(new TitledBorder("Cell properties"));
        setLayout(new GridLayout(2, 1));

        lineThickness = new LabeledSliderWithTextField(
                "Line thickness",
                Config.MIN_THICKNESS,
                Config.MAX_THICKNESS,
                SPACING);
        cellSize = new LabeledSliderWithTextField(
                "Cell size",
                Config.MIN_SIZE,
                Config.MAX_SIZE,
                SPACING);

        lineThickness.setValue(config.getLineThickness());
        cellSize.setValue(config.getCellSize());

        lineThickness.addValueChangedObserver(this::setLineThickness);
        cellSize.addValueChangedObserver(this::setCellSize);

        add(lineThickness);
        add(cellSize);
    }

    public void setConfig(Config config) {
        this.config = config;
        lineThickness.setValue(config.getLineThickness());
        cellSize.setValue(config.getCellSize());
    }

    private void setLineThickness(int thickness) {
        config.setLineThickness(thickness);
    }

    private void setCellSize(int cellSize) {
        config.setCellSize(cellSize);
    }
}
