package ru.nsu.fit.g15201.sogreshilin.view.settings;

import ru.nsu.fit.g15201.sogreshilin.model.io.Config;
import ru.nsu.fit.g15201.sogreshilin.view.component.LabeledSliderWithTextField;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class CellPropertiesPanel extends JPanel {
    private static final int SPACING = 1;
    private LabeledSliderWithTextField lineThickness;
    private LabeledSliderWithTextField cellSize;

    public CellPropertiesPanel(Config config) {
        setBorder(new TitledBorder("Cell properties"));
        setLayout(new GridLayout(2, 1));

        lineThickness = new LabeledSliderWithTextField("Line thickness", config.MIN_THICKNESS, config.MAX_THICKNESS, SPACING);
        cellSize = new LabeledSliderWithTextField("Cell size", config.MIN_SIZE, config.MAX_SIZE, SPACING);

        lineThickness.setValue(config.getLineThickness());
        cellSize.setValue(config.getCellSize());

        lineThickness.addValueChangedObserver(config::setLineThickness);
        cellSize.addValueChangedObserver(config::setCellSize);

        add(lineThickness);
        add(cellSize);
    }
}
