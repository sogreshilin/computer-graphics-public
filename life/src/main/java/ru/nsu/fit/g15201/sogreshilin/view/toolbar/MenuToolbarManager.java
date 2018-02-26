package ru.nsu.fit.g15201.sogreshilin.view.toolbar;

import ru.nsu.fit.g15201.sogreshilin.controller.MainFrame;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class MenuToolbarManager {
    private final MainFrame frame;

    private final Map<String, JButton> buttons = new HashMap<>();
    private final Map<String, JMenuItem> menuItems = new HashMap<>();
    private boolean constructingModeEnabled;
    private boolean settingsEnabled;
    private JLabel statusLabelListeners;

    public MenuToolbarManager(MainFrame mainFrame) {
        frame = mainFrame;
        JMenuBar menuBar = mainFrame.getJMenuBar();
        JToolBar toolBar = mainFrame.getJToolBar();
    }

    public void setupMenu() {
        try {
            frame.addSubMenu("File", KeyEvent.VK_F);
            menuItems.put("File/New", frame.addMenuItem("File/New", "Start new game", KeyEvent.VK_N, "new.png", "onNew"));
            menuItems.put("File/Open", frame.addMenuItem("File/Open", "Open saved game", KeyEvent.VK_O, "open.png", "onOpen"));
            menuItems.put("File/Save", frame.addMenuItem("File/Save", "Save the game", KeyEvent.VK_S, "save.png", "onSave"));
            menuItems.put("File/Save as", frame.addMenuItem("File/Save as", "Save the game as", KeyEvent.VK_S, "saveas.png", "onSaveAs"));
            frame.addMenuSeparator("File");
            menuItems.put("File/Exit", frame.addMenuItem("File/Exit", "Exit application", KeyEvent.VK_X, "exit.png", "onExit"));

            frame.addSubMenu("Edit", KeyEvent.VK_H);
            menuItems.put("Edit/XOR", frame.addMenuItem("Edit/XOR", "XOR mode", KeyEvent.VK_X, "xor.png", "onXor"));
            menuItems.put("Edit/Replace", frame.addMenuItem("Edit/Replace", "Replace mode", KeyEvent.VK_R, "replace.png", "onReplace"));
            menuItems.put("Edit/Clear", frame.addMenuItem("Edit/Clear", "Clear the field", KeyEvent.VK_C, "clear.png", "onClear"));
            menuItems.put("Edit/Settings", frame.addMenuItem("Edit/Settings", "Open parameters", KeyEvent.VK_P, "settings.png", "onSettings"));

            frame.addSubMenu("View", KeyEvent.VK_H);
            menuItems.put("View/Impacts", frame.addMenuItem("View/Impacts", "Shows impact of each cell", KeyEvent.VK_C, "impact.png", "onShowImpact"));

            frame.addSubMenu("Simulation", KeyEvent.VK_H);
            menuItems.put("Simulation/Run", frame.addMenuItem("Simulation/Run", "Run the game", KeyEvent.VK_R, "run.png", "onRun"));
            menuItems.put("Simulation/Pause", frame.addMenuItem("Simulation/Pause", "Pause running game", KeyEvent.VK_S, "pause.png", "onPause"));
            menuItems.put("Simulation/Step", frame.addMenuItem("Simulation/Step", "Make one step", KeyEvent.VK_S, "step.png", "onStep"));

            frame.addSubMenu("Help", KeyEvent.VK_H);
            menuItems.put("Help/About", frame.addMenuItem("Help/About", "Show program version and copyright information", KeyEvent.VK_A, "about.png", "onAbout"));

        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setupToolbar() {
        try {
            buttons.put("File/New", frame.addToolBarButton("File/New"));
            buttons.put("File/Open", frame.addToolBarButton("File/Open"));
            buttons.put("File/Save", frame.addToolBarButton("File/Save"));
            buttons.put("File/Save as", frame.addToolBarButton("File/Save as"));
            buttons.put("File/Exit", frame.addToolBarButton("File/Exit"));
            frame.addToolBarSeparator();

            buttons.put("Edit/XOR", frame.addToolBarButton("Edit/XOR"));
            buttons.put("Edit/Replace", frame.addToolBarButton("Edit/Replace"));
            buttons.put("Edit/Clear", frame.addToolBarButton("Edit/Clear"));
            buttons.put("Edit/Settings", frame.addToolBarButton("Edit/Settings"));
            frame.addToolBarSeparator();

            buttons.put("View/Impacts", frame.addToolBarButton("View/Impacts"));
            frame.addToolBarSeparator();

            buttons.put("Simulation/Run", frame.addToolBarButton("Simulation/Run"));
            buttons.put("Simulation/Pause", frame.addToolBarButton("Simulation/Pause"));
            buttons.put("Simulation/Step", frame.addToolBarButton("Simulation/Step"));
            frame.addToolBarSeparator();

            buttons.put("Help/About", frame.addToolBarButton("Help/About"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setEnabled(String key, boolean b) {
        buttons.get(key).setEnabled(b);
        menuItems.get(key).setEnabled(b);
    }

    public void setReplaceEnabled(boolean b) {
        setEnabled("Edit/Replace", b);
    }

    public void setXOREnabled(boolean b) {
        setEnabled("Edit/XOR", b);
    }

    public void setRunEnabled(boolean b) {
        setEnabled("Simulation/Run", b);
    }

    public void setPauseEnabled(boolean b) {
        setEnabled("Simulation/Pause", b);
    }

    public void setConstructingModeEnabled(boolean b) {
        setEnabled("File/New", b);
        setEnabled("File/Open", b);
        setEnabled("File/Save", b);
        setEnabled("File/Save as", b);
        setEnabled("Edit/Clear", b);
        setEnabled("Edit/Settings", b);
        setEnabled("Simulation/Step", b);
    }

    public void setSettingsEnabled(boolean b) {
        setEnabled("Edit/Settings", b);
    }

    public void setStatusLabelListeners(JLabel statusLabel) {
        buttons.values().forEach(b -> b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(b.getToolTipText());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText("");
            }
        }));

    }
}
