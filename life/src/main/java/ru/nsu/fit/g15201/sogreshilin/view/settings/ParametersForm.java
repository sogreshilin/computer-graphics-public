package ru.nsu.fit.g15201.sogreshilin.view.settings;

import ru.nsu.fit.g15201.sogreshilin.model.io.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ParametersForm extends JFrame {
    private static final String RULES_USAGE = "Values has to be ordered as\n" +
            "LIVE_BEGIN ≤ BIRTH_BEGIN ≤ BIRTH_END ≤ LIVE_END";

    private Config oldConfig;
    private Config newConfig;
    private GameModePanel gameModePanel;
    private FieldSizePanel fieldSizePanel;
    private CellPropertiesPanel cellPropertiesPanel;
    private GameRulesPanel gameRulesPanel;

    private ArrayList<ConfigChangedObserver> observers = new ArrayList<>();
    private Config config;

    public ParametersForm(JFrame parent, Config config) throws HeadlessException {
        super("Parameters");

        oldConfig = config;
        newConfig = new Config(config);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel southPanel = new JPanel(new BorderLayout());
        JPanel northPanel = new JPanel(new GridLayout(1, 2));

        gameModePanel = new GameModePanel(newConfig);
        fieldSizePanel = new FieldSizePanel(newConfig);
        cellPropertiesPanel = new CellPropertiesPanel(newConfig);
        gameRulesPanel = new GameRulesPanel(newConfig);

        northPanel.add(gameModePanel);
        northPanel.add(fieldSizePanel);
        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(cellPropertiesPanel, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        southPanel.add(gameRulesPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        okButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean gameRulesCorrect = areGameRulesCorrect();
                boolean valuesCorrect = areAllValuesCorrect();
                if (gameRulesCorrect && valuesCorrect) {
                    notifyConfigChanged(newConfig);
                    dispose();
                    return;
                }
                if (!valuesCorrect) {
                    JOptionPane.showMessageDialog(ParametersForm.this,
                            "Input values are invalid.\nPlease change them",
                            "Invalid input values",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!gameRulesCorrect) {
                    JOptionPane.showMessageDialog(ParametersForm.this,
                            RULES_USAGE,
                            "Invalid game rules",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        });

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setContentPane(mainPanel);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(parent);
        this.setResizable(true);
    }

    private boolean areAllValuesCorrect() {
        return 0 < newConfig.getFieldWidth() &&
                0 < newConfig.getFieldHeight() &&
                Config.MIN_THICKNESS <= newConfig.getLineThickness()  &&
                newConfig.getLineThickness() <= Config.MAX_THICKNESS &&
                Config.MIN_SIZE <= newConfig.getCellSize() &&
                newConfig.getCellSize() <= Config.MAX_SIZE &&
                0 < newConfig.getFirstImpact() &&
                0 < newConfig.getSecondImpact();
    }

    private boolean areGameRulesCorrect() {
        return 0 <= newConfig.getLiveBegin() &&
                newConfig.getLiveBegin() <= newConfig.getBirthBegin() &&
                newConfig.getBirthBegin() <= newConfig.getBirthEnd() &&
                newConfig.getBirthEnd() <= newConfig.getLiveEnd();
    }

    public void dispose() {
        super.dispose();
    }

    public void setConfig(Config config) {
        this.config = config;
        this.newConfig = new Config(config);
        gameModePanel.setConfig(newConfig);
        fieldSizePanel.setConfig(newConfig);
        cellPropertiesPanel.setConfig(newConfig);
        gameRulesPanel.setConfig(newConfig);
    }

    public interface ConfigChangedObserver {
        void setConfig(Config config);
    }

    public void addConfigChangedObserver(ConfigChangedObserver observer) {
        observers.add(observer);
    }

    public void notifyConfigChanged(Config newConfig) {
        for (ConfigChangedObserver observer: observers) {
            observer.setConfig(newConfig);
        }
    }
}
