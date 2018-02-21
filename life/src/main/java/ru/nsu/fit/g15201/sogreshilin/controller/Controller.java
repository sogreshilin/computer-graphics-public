package ru.nsu.fit.g15201.sogreshilin.controller;

import ru.nsu.fit.g15201.sogreshilin.model.CellStateChangedObserver;
import ru.nsu.fit.g15201.sogreshilin.model.CellsImpactChangedObserver;
import ru.nsu.fit.g15201.sogreshilin.model.State;
import ru.nsu.fit.g15201.sogreshilin.model.io.Config;
import ru.nsu.fit.g15201.sogreshilin.model.GameModel;
import ru.nsu.fit.g15201.sogreshilin.model.io.ConfigParser;
import ru.nsu.fit.g15201.sogreshilin.view.Canvas;
import ru.nsu.fit.g15201.sogreshilin.view.FileUtils;
import ru.nsu.fit.g15201.sogreshilin.view.settings.ParametersForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

public class Controller extends MainFrame {
    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 600;
    private static final String EXTENSION = "life";
    private static final String FILE_DESCRIPTION = "Text file";

    private static final String TITLE = "Game of Life";

    private static final String aboutTxt =
            TITLE + ", version 1.0\n" +
            "Copyright 2018 Sogreshilin Alexander, FIT-15201";

    private Config config;
    private GameModel gameModel;
    private Canvas gameField;
    private JScrollPane scrollPane;
    private JButton replaceButton;
    private JButton xorButton;
    private JButton runButton;
    private JButton pauseButton;

    public Controller(Config config) {
        super(MIN_WIDTH, MIN_HEIGHT, TITLE);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));

        this.config = config;
        this.gameModel = new GameModel(config);
        this.gameField = new Canvas(config);

        this.scrollPane = new JScrollPane(gameField);
        add(scrollPane);

        gameField.addObserver((i, j) -> {
            switch (getMode()) {
                case REPLACE: gameModel.setStateAt(i, j, State.ALIVE); break;
                case XOR: gameModel.switchStateAt(i, j); break;
                default: throw new RuntimeException("Unexpected case");
            }
        });

        gameModel.addCellStateObserver(new CellStateChangedObserver() {
            @Override
            public void onCellStateChanged(int i, int j, State newState) {
                gameField.fillHexagon(i ,j ,newState);
            }

            @Override
            public void onClear() {
                gameField.clearField();
            }
        });
        gameModel.addCellsImpactObserver(new CellsImpactChangedObserver() {
            @Override
            public void onImpactChanged(double[] impacts) {
                gameField.drawImpacts(impacts);
            }

            @Override
            public void onClear() {
                gameField.clearImpacts();
                gameField.drawImpacts(null);
            }
        });


        setUpMenu();
        setUpToolbar();
    }

    private Config.FillMode getMode() {
        return config.getMode();
    }

    private void setUpToolbar() {
        try {
            addToolBarButton("File/New");
            addToolBarButton("File/Open");
            addToolBarButton("File/Save");
            addToolBarButton("File/Save as");
            addToolBarButton("File/Exit");
            addToolBarSeparator();
            xorButton = addToolBarButton("Edit/XOR");
            replaceButton = addToolBarButton("Edit/Replace");
            xorButton.setEnabled(config.getMode() != Config.FillMode.XOR);
            replaceButton.setEnabled(config.getMode() != Config.FillMode.REPLACE);
            addToolBarButton("Edit/Clear");
            addToolBarButton("Edit/Settings");
            addToolBarSeparator();
            addToolBarButton("View/Impacts");
            addToolBarSeparator();
            runButton = addToolBarButton("Simulation/Run");
            pauseButton = addToolBarButton("Simulation/Pause");
            pauseButton.setEnabled(false);
            addToolBarButton("Simulation/Step");
            addToolBarSeparator();
            addToolBarButton("Help/About...");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setUpMenu() {
        try {
            addSubMenu("File", KeyEvent.VK_F);
            addMenuItem("File/New", "Start new game", KeyEvent.VK_N, "new.png", "onNew");
            addMenuItem("File/Open", "Open saved game", KeyEvent.VK_O, "open.png", "onOpen");
            addMenuItem("File/Save", "Save the game", KeyEvent.VK_S, "save.png", "onSave");
            addMenuItem("File/Save as", "Save the game as", KeyEvent.VK_S, "saveas.png", "onSave");
            addMenuItem("File/Exit", "Exit application", KeyEvent.VK_X, "exit.png", "onExit");

            addSubMenu("Edit", KeyEvent.VK_H);
            addMenuItem("Edit/XOR", "XOR mode", KeyEvent.VK_X, "xor.png", "onXor");
            addMenuItem("Edit/Replace", "Replace mode", KeyEvent.VK_R, "replace.png", "onReplace");
            addMenuItem("Edit/Clear", "Clear the field", KeyEvent.VK_C, "clear.png", "onClear");
            addMenuItem("Edit/Settings", "Open parameters", KeyEvent.VK_P, "settings.png", "onSettings");

            addSubMenu("View", KeyEvent.VK_H);
            addMenuItem("View/Impacts", "Shows impact of each cell", KeyEvent.VK_C, "impact.png", "onShowImpact");

            addSubMenu("Simulation", KeyEvent.VK_H);
            addMenuItem("Simulation/Run", "Run the game", KeyEvent.VK_R, "run.png", "onRun");
            addMenuItem("Simulation/Pause", "Make one step", KeyEvent.VK_S, "pause.png", "onPause");
            addMenuItem("Simulation/Step", "Make one step", KeyEvent.VK_S, "step.png", "onStep");

            addSubMenu("Help", KeyEvent.VK_H);
            addMenuItem("Help/About...", "Show program version and copyright information", KeyEvent.VK_A, "about.png", "onAbout");
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setConfig(Config newConfig) {
        /* Field needs to be redrawn */
        if (config.getFieldWidth() != newConfig.getFieldWidth() ||
                config.getFieldHeight() != newConfig.getFieldHeight() ||
                config.getLineThickness() != newConfig.getLineThickness() ||
                config.getCellSize() != newConfig.getCellSize()) {
            gameField.setConfig(newConfig);
            scrollPane.updateUI();
        }

        /* Just a change in a model */
        if (config.getFieldWidth() != newConfig.getFieldWidth() ||
                config.getFieldHeight() != newConfig.getFieldHeight() ||
                GameModel.rulesChanged(config, newConfig)) {
            gameModel.setConfig(newConfig);
        }

        /* Change toolbar */
        if (config.getMode() != newConfig.getMode()) {
            xorButton.setEnabled(newConfig.getMode() != Config.FillMode.XOR);
            replaceButton.setEnabled(newConfig.getMode() != Config.FillMode.REPLACE);
        }

        this.config = newConfig;
    }

    public void onAbout() {
        JOptionPane.showMessageDialog(this,
                aboutTxt,
                "About " + TITLE,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void onExit() {
        int optionChosen = showSaveOptionDialog();
        switch (optionChosen) {
            case JOptionPane.YES_OPTION: onSave(); break;
            case JOptionPane.NO_OPTION: break;
            case JOptionPane.CANCEL_OPTION:
            case JOptionPane.CLOSED_OPTION: return;
            default: throw new RuntimeException("Unexpected option chosen");
        }
        System.exit(0);
    }

    public void onNew() {
        int optionChosen = showSaveOptionDialog();
        switch (optionChosen) {
            case JOptionPane.YES_OPTION: onSave(); break;
            case JOptionPane.NO_OPTION: break;
            case JOptionPane.CANCEL_OPTION:
            case JOptionPane.CLOSED_OPTION: return;
            default: throw new RuntimeException("Unexpected option chosen");
        }
        onSettings();
    }

    public void onOpen() {
        File file = FileUtils.getOpenFileName(this, EXTENSION, FILE_DESCRIPTION);
        if (file != null) {
            try (FileInputStream in = new FileInputStream(file)){
                Config newConfig = ConfigParser.deserialize(in);
                setConfig(newConfig);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Invalid file format.\nFile cannot be read",
                        "Invalid file format",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public int showSaveOptionDialog() {
        return JOptionPane.showOptionDialog(this,
                "Do you want to save current game state?",
                "Save",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                "Yes"
                );
    }

    public void onSave() {
        File file = FileUtils.getSaveFileName(this, EXTENSION, FILE_DESCRIPTION);
        if (file != null) {
            try (FileOutputStream out = new FileOutputStream(file)){
                ByteArrayOutputStream baos = ConfigParser.serialize(config);
                baos.writeTo(out);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "File cannot be saved",
                        "Saving error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void onXor() {
        config.setMode(Config.FillMode.XOR);
        xorButton.setEnabled(false);
        replaceButton.setEnabled(true);
    }

    public void onReplace() {
        config.setMode(Config.FillMode.REPLACE);
        replaceButton.setEnabled(false);
        xorButton.setEnabled(true);
    }

    public void onClear() {
        gameModel.clearCells();
    }


    public void onSettings() {
        ParametersForm parameters = new ParametersForm(this, config);
        parameters.addConfigChangedObserver(newConfig -> setConfig(newConfig));
        parameters.setVisible(true);
    }

    public void onRun() {
        gameModel.startTimer();
        runButton.setEnabled(false);
        pauseButton.setEnabled(true);
    }

    public void onPause() {
        gameModel.stopTimer();
        runButton.setEnabled(true);
        pauseButton.setEnabled(false);
    }

    public void onStep() {
        gameModel.nextGeneration();
    }

    public void onShowImpact() {
        gameField.switchShowImpacts();
        gameField.clearImpacts();
        if (gameField.showsImpacts()) {
            gameField.drawImpacts(gameModel.getImpacts());
        }
    }

    public static void main(String[] args) {
        Config config = new Config();
        Controller controller = new Controller(config);
        controller.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        controller.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                controller.onExit();
            }
        });
        controller.setVisible(true);
    }
}
