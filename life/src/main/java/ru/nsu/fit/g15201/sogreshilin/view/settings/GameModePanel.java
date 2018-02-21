package ru.nsu.fit.g15201.sogreshilin.view.settings;

import ru.nsu.fit.g15201.sogreshilin.model.io.Config;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class GameModePanel extends JPanel {
    private Config config;
    private JRadioButton replaceMode;
    private JRadioButton xorMode;

    public GameModePanel(Config config) {
        setBorder(new TitledBorder("Color mode"));
        setLayout(new GridLayout(2, 1));
        ButtonGroup group = new ButtonGroup();
        replaceMode = new JRadioButton("Replace");
        group.add(replaceMode);
        xorMode = new JRadioButton("XOR");
        group.add(xorMode);

        replaceMode.setSelected(config.getMode() == Config.FillMode.REPLACE);
        xorMode.setSelected(config.getMode() == Config.FillMode.XOR);

        replaceMode.addActionListener(e -> {
            getConfig().setMode(Config.FillMode.REPLACE);
        });

        xorMode.addActionListener(e -> {
            getConfig().setMode(Config.FillMode.XOR);
        });

        add(replaceMode);
        add(xorMode);
    }

    public void setConfig(Config config) {
        this.config = config;
        replaceMode.setSelected(config.getMode() == Config.FillMode.REPLACE);
        xorMode.setSelected(config.getMode() == Config.FillMode.XOR);
    }

    public Config getConfig() {
        return config;
    }
}
