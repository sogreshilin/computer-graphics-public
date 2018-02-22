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
import ru.nsu.fit.g15201.sogreshilin.view.toolbar.MenuToolbarManager;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

public class Controller extends MainFrame {
    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 600;
    private static final String EXTENSION = "life";
    private static final String FILE_DESCRIPTION = "Text file";
    private static final String TITLE = "Game of Life";
    private static final String ABOUT =
            TITLE + ", version 1.0\n" +
                    "Copyright 2018 Sogreshilin Alexander, FIT-15201";
    private static final int SAVED_SUCCESSFULLY = 0;
    private static final int SAVE_CANCELLED = 1;

    private Config config;
    private GameModel gameModel;
    private Canvas gameField;
    private JScrollPane scrollPane;
    private MenuToolbarManager manager;
    private ParametersForm parameters;

    public Controller(Config config) {
        super(MIN_WIDTH, MIN_HEIGHT, TITLE);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));

        this.config = config;
        this.gameModel = new GameModel(config);
        this.gameField = new Canvas(config);
        setupGameViewObservers();
        setupGameModelObservers();
        setCurrentFile(null);

        scrollPane = new JScrollPane(gameField);
        add(scrollPane, BorderLayout.CENTER);

        setupToolbarMenuManager();
        setupStatusBar();

        this.parameters = new ParametersForm(this, config);
        parameters.addConfigChangedObserver(this::setConfig);
    }

    private void setupToolbarMenuManager() {
        manager = new MenuToolbarManager(this);
        manager.setupMenu();
        manager.setupToolbar();
        manager.setReplaceEnabled(false);
        manager.setPauseEnabled(false);
    }

    private void setupStatusBar() {
        JPanel statusBar = new JPanel(new GridLayout());
        statusBar.setPreferredSize(new Dimension(this.getWidth(), 20));
        add(statusBar, BorderLayout.SOUTH);
        JLabel statusLabel = new JLabel(TITLE);
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        statusBar.add(statusLabel);
        manager.setStatusLabelListeners(statusLabel);
    }

    private void setupGameModelObservers() {
        gameModel.addCellStateObserver(new CellStateChangedObserver() {
            @Override
            public void onCellStateChanged(int i, int j, State newState) {
                gameField.fillHexagon(i, j, newState);
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
    }

    private void setupGameViewObservers() {
        gameField.addObserver((i, j) -> {
            switch (getMode()) {
                case REPLACE:
                    gameModel.setStateAt(i, j, State.ALIVE);
                    break;
                case XOR:
                    gameModel.switchStateAt(i, j);
                    break;
                default:
                    throw new RuntimeException("Unexpected case");
            }
        });
    }

    private Config.FillMode getMode() {
        return config.getMode();
    }

    public void setConfig(Config newConfig) {
        gameField.setConfig(newConfig);
        scrollPane.updateUI();
        gameModel.setConfig(newConfig);
        this.config = newConfig;
        manager.setReplaceEnabled(config.getMode() != Config.FillMode.REPLACE);
        manager.setXOREnabled(config.getMode() != Config.FillMode.XOR);
        if (gameField.showsImpacts()) {
            gameField.drawImpacts(gameModel.getImpacts());
        }
    }

    public void onAbout() {
        JOptionPane.showMessageDialog(this,
                ABOUT,
                "About " + TITLE,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void onExit() {
        int optionChosen = showSaveOptionDialog();
        switch (optionChosen) {
            case JOptionPane.YES_OPTION:
                int result = onSave();
                if (result == SAVE_CANCELLED) {
                    return;
                }
                break;
            case JOptionPane.NO_OPTION:
                break;
            case JOptionPane.CANCEL_OPTION:
            case JOptionPane.CLOSED_OPTION:
                return;
            default:
                throw new RuntimeException("Unexpected option chosen");
        }
        System.exit(0);
    }

    public void onNew() {
        int optionChosen = showSaveOptionDialog();
        switch (optionChosen) {
            case JOptionPane.YES_OPTION:
                int result = onSave();
                if (result == SAVE_CANCELLED) {
                    return;
                }
                break;
            case JOptionPane.NO_OPTION:
                break;
            case JOptionPane.CANCEL_OPTION:
            case JOptionPane.CLOSED_OPTION:
                return;
            default:
                throw new RuntimeException("Unexpected option chosen");
        }
        setCurrentFile(null);
        config = new Config();
        onClear();
        onSettings();
    }

    public void onOpen() {
        File file = FileUtils.getOpenFileName(this, EXTENSION, FILE_DESCRIPTION);
        setCurrentFile(file);
        if (file == null) {
            return;
        }

        int optionChosen = showSaveOptionDialog();
        switch (optionChosen) {
            case JOptionPane.YES_OPTION:
                int result = onSave();
                if (result == SAVE_CANCELLED) {
                    return;
                }
                break;
            case JOptionPane.NO_OPTION:
                break;
            case JOptionPane.CANCEL_OPTION:
            case JOptionPane.CLOSED_OPTION:
                return;
            default:
                throw new RuntimeException("Unexpected option chosen");
        }

        try (FileInputStream in = new FileInputStream(currentFile)) {
            Config newConfig = ConfigParser.deserialize(in);
            setConfig(newConfig);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid file format.\nFile cannot be read",
                    "Invalid file format",
                    JOptionPane.ERROR_MESSAGE);
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

    private File currentFile = null;

    public void setCurrentFile(File currentFile) {
        this.currentFile = currentFile;
        String filename = currentFile != null ? currentFile.getName() : "Unsaved";
        setTitle(filename + " - " + TITLE);
    }

    public int onSave() {
        if (currentFile == null) {
            File file = FileUtils.getSaveFileName(this, EXTENSION, FILE_DESCRIPTION);
            setCurrentFile(file);
        }
        if (currentFile != null) {
            try (FileOutputStream out = new FileOutputStream(currentFile)) {
                config.setAliveCells(gameModel.getAliveCells());
                ByteArrayOutputStream baos = ConfigParser.serialize(config);
                baos.writeTo(out);
                return SAVED_SUCCESSFULLY;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "File cannot be saved",
                        "Saving error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        return SAVE_CANCELLED;
    }

    public void onSaveAs() {
        File file = FileUtils.getSaveFileName(this, EXTENSION, FILE_DESCRIPTION);
        if (file != null) {
            setCurrentFile(file);
            onSave();
        }
    }

    public void onXor() {
        config.setMode(Config.FillMode.XOR);
        manager.setXOREnabled(false);
        manager.setReplaceEnabled(true);
    }

    public void onReplace() {
        config.setMode(Config.FillMode.REPLACE);
        manager.setXOREnabled(true);
        manager.setReplaceEnabled(false);
    }

    public void onClear() {
        gameModel.clearCells();
    }

    public void onSettings() {
        config.setAliveCells(gameModel.getAliveCells());
        parameters.setConfig(config);
        parameters.setVisible(true);
    }

    public void onRun() {
        gameField.setMouseListenersEnabled(false);
        gameModel.startTimer();
        manager.setRunEnabled(false);
        manager.setPauseEnabled(true);
        manager.setConstructingModeEnabled(false);
    }

    public void onPause() {
        gameField.setMouseListenersEnabled(true);
        gameModel.stopTimer();
        manager.setRunEnabled(true);
        manager.setPauseEnabled(false);
        manager.setConstructingModeEnabled(true);
    }

    public void onStep() {
        gameModel.nextGeneration();
    }

    public void onShowImpact() {
        gameField.switchShowImpacts();
        if (gameField.showsImpacts()) {
            gameField.drawImpacts(gameModel.getImpacts());
        } else {
            gameField.clearImpacts();
        }
    }

    public static void main(String[] args) {
        Config config = new Config();
        Controller controller = new Controller(config);
        controller.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        controller.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.onExit();
            }
        });
        controller.setVisible(true);
    }
}
