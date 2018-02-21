package ru.nsu.fit.g15201.sogreshilin.view.settings;

import ru.nsu.fit.g15201.sogreshilin.model.io.Config;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class GameModePanel extends JPanel {
    private JRadioButton replaceMode = new JRadioButton("Replace");
    private JRadioButton xorMode = new JRadioButton("XOR");

    public GameModePanel(Config config) {
        setBorder(new TitledBorder("Color mode"));
        setLayout(new GridLayout(2, 1));
        ButtonGroup group = new ButtonGroup();
        group.add(replaceMode);
        group.add(xorMode);

        replaceMode.setSelected(config.getMode() == Config.FillMode.REPLACE);
        xorMode.setSelected(config.getMode() == Config.FillMode.XOR);

        replaceMode.addActionListener(e -> {
            config.setMode(Config.FillMode.REPLACE);
        });

        xorMode.addActionListener(e -> {
            config.setMode(Config.FillMode.XOR);
        });

        add(replaceMode);
        add(xorMode);
    }
}
