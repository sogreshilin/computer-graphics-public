package ru.nsu.fit.g15201.sogreshilin.view.toolbar;

import java.awt.event.*;
import ru.nsu.fit.g15201.sogreshilin.controller.Controller;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class MenuToolbarManager {
    private final Controller controller;

    private final Map<String, AbstractButton> buttons = new HashMap<>();
    private final Map<String, JMenuItem> menuItems = new HashMap<>();

    public MenuToolbarManager(Controller controller) {
        this.controller = controller;
    }

    public void setupMenu() {
        try {
            controller.addSubMenu("File", KeyEvent.VK_F);
            menuItems.put("File/New", controller.addMenuItem("File/New", "New", KeyEvent.VK_N, "new.png", controller::onNew));
            menuItems.put("File/Open", controller.addMenuItem("File/Open", "Open", KeyEvent.VK_O, "open.png", controller::onOpen));
            menuItems.put("File/Save", controller.addMenuItem("File/Save", "Save", KeyEvent.VK_S, "save.png", controller::onSave));
//            menuItems.put("File/Save as", controller.addMenuItem("File/Save as", "Save the game as", KeyEvent.VK_S, "saveas.png", "onSaveAs"));
            menuItems.put("File/Exit", controller.addMenuItem("File/Exit", "Exit", KeyEvent.VK_X, "exit.png", controller::onExit));
            controller.addMenuSeparator("File");

            controller.addSubMenu("Edit", KeyEvent.VK_H);
            menuItems.put("Edit/Select", controller.addMenuItem("Edit/Select", "Select", KeyEvent.VK_S, "select.png", controller::onSelect));
            menuItems.put("Edit/Copy B to C", controller.addMenuItem("Edit/Copy B to C", "Copy B to C", KeyEvent.VK_B, "to_right.png", controller::onCopyToLeft));
            menuItems.put("Edit/Copy C to B", controller.addMenuItem("Edit/Copy C to B", "Copy C to B", KeyEvent.VK_C, "to_left.png", controller::onCopyToRight));
//            menuItems.put("Edit/Replace", controller.addMenuItem("Edit/Replace", "Replace mode", KeyEvent.VK_R, "replace.png", "onReplace"));
//            menuItems.put("Edit/Clear", controller.addMenuItem("Edit/Clear", "Clear the field", KeyEvent.VK_C, "clear.png", "onClear"));
//            menuItems.put("Edit/Settings", controller.addMenuItem("Edit/Settings", "Open parameters", KeyEvent.VK_P, "settings.png", "onSettings"));
//
            controller.addSubMenu("Filter", KeyEvent.VK_H);
//            menuItems.put("Filter/Grayscale", controller.addMenuItem("Filter/Grayscale", "Grayscale", KeyEvent.VK_G, "grayscale.png", controller::onGrayscale));
            menuItems.put("Filter/Grayscale", controller.addMenuItem("Filter/Grayscale", "Grayscale", KeyEvent.VK_I, "grayscale.png", controller::onGrayscale));
            menuItems.put("Filter/Invert", controller.addMenuItem("Filter/Invert", "Invert", KeyEvent.VK_I, "invert.png", controller::onInvert));
            menuItems.put("Filter/Floyd-Steinberg Dither", controller.addMenuItem("Filter/Floyd-Steinberg Dither", "Floyd-Steinberg Dither", KeyEvent.VK_I, "fs_dither.png", controller::onFloydSteinbergDither));
            menuItems.put("Filter/Ordered Dither", controller.addMenuItem("Filter/Ordered Dither", "Ordered Dither", KeyEvent.VK_I, "o_dither.png", controller::onOrderDither));
            menuItems.put("Filter/Double", controller.addMenuItem("Filter/Double", "Double", KeyEvent.VK_I, "double.png", controller::onDoubleMagnification));
            menuItems.put("Filter/Roberts Edge Detection", controller.addMenuItem("Filter/Roberts Edge Detection", "Roberts Edge Detection", KeyEvent.VK_I, "r_edges.png", controller::onRobertsEdgeDetection));
            menuItems.put("Filter/Sobel Edge Detection", controller.addMenuItem("Filter/Sobel Edge Detection", "Sobel Edge Detection", KeyEvent.VK_I, "s_edges.png", controller::onSobelEdgeDetection));
            menuItems.put("Filter/Anti-Aliasing", controller.addMenuItem("Filter/Anti-Aliasing", "Anti-Aliasing", KeyEvent.VK_I, "blur.png", controller::onAntiAliasing));
            menuItems.put("Filter/Sharpening", controller.addMenuItem("Filter/Sharpening", "Sharpening", KeyEvent.VK_I, "sharpen.png", controller::onSharpening));
            menuItems.put("Filter/Embossing", controller.addMenuItem("Filter/Embossing", "Embossing", KeyEvent.VK_I, "emboss.png", controller::onEmbossing));
            menuItems.put("Filter/Watercolor", controller.addMenuItem("Filter/Watercolor", "Watercolor", KeyEvent.VK_I, "watercolor.png", controller::onWatercolor));
            menuItems.put("Filter/Rotation", controller.addMenuItem("Filter/Rotation", "Rotation", KeyEvent.VK_I, "rotate.png", controller::onRotation));
            menuItems.put("Filter/Gamma Correction", controller.addMenuItem("Filter/Gamma Correction", "Gamma Correction", KeyEvent.VK_I, "gamma.png", controller::onGammaCorrection));

            controller.addSubMenu("Volume", KeyEvent.VK_H);
            menuItems.put("Volume/Open", controller.addMenuItem("Volume/Open", "Open config file", KeyEvent.VK_O, "open.png", controller::onConfigOpen));
            menuItems.put("Volume/Absorption", controller.addMenuItem("Volume/Absorption", "Absorption", KeyEvent.VK_A, "absorption.png", controller::onAbsorption));
            menuItems.put("Volume/Emission", controller.addMenuItem("Volume/Emission", "Emission", KeyEvent.VK_E, "emission.png", controller::onEmission));
            menuItems.put("Volume/Render", controller.addMenuItem("Volume/Render", "Render volume", KeyEvent.VK_R, "volume.png", controller::onVolumeRender));

        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setupToolbar() {
        try {
            buttons.put("File/New", controller.addToolBarRegularButton("File/New"));
            buttons.put("File/Open", controller.addToolBarRegularButton("File/Open"));
            buttons.put("File/Save", controller.addToolBarRegularButton("File/Save"));
//            buttons.put("File/Save as", controller.addToolBarRegularButton("File/Save as"));

            controller.addToolBarSeparator();
//
            buttons.put("Edit/Select", controller.addToolBarToggleButton("Edit/Select"));
//            System.out.println(buttons);
//            menuItems.get("Edit/Select").addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    buttons.get("Edit/Select").doClick();
//                }
//            });
            buttons.put("Edit/Copy B to C", controller.addToolBarRegularButton("Edit/Copy B to C"));
            buttons.put("Edit/Copy C to B", controller.addToolBarRegularButton("Edit/Copy C to B"));
            buttons.put("Filter/Double", controller.addToolBarRegularButton("Filter/Double"));
            buttons.put("Filter/Rotation", controller.addToolBarRegularButton("Filter/Rotation"));

            controller.addToolBarSeparator();
            buttons.put("Filter/Grayscale", controller.addToolBarRegularButton("Filter/Grayscale"));
            buttons.put("Filter/Invert", controller.addToolBarRegularButton("Filter/Invert"));
            buttons.put("Filter/Watercolor", controller.addToolBarRegularButton("Filter/Watercolor"));
            buttons.put("Filter/Gamma Correction", controller.addToolBarRegularButton("Filter/Gamma Correction"));
            controller.addToolBarSeparator();

            buttons.put("Filter/Floyd-Steinberg Dither", controller.addToolBarRegularButton("Filter/Floyd-Steinberg Dither"));
            buttons.put("Filter/Ordered Dither", controller.addToolBarRegularButton("Filter/Ordered Dither"));
            buttons.put("Filter/Roberts Edge Detection", controller.addToolBarRegularButton("Filter/Roberts Edge Detection"));
            buttons.put("Filter/Sobel Edge Detection", controller.addToolBarRegularButton("Filter/Sobel Edge Detection"));

            controller.addToolBarSeparator();
            buttons.put("Filter/Anti-Aliasing", controller.addToolBarRegularButton("Filter/Anti-Aliasing"));
            buttons.put("Filter/Sharpening", controller.addToolBarRegularButton("Filter/Sharpening"));
            buttons.put("Filter/Embossing", controller.addToolBarRegularButton("Filter/Embossing"));

            controller.addToolBarSeparator();
            buttons.put("Volume/Open", controller.addToolBarRegularButton("Volume/Open"));
            buttons.put("Volume/Absorption", controller.addToolBarToggleButton("Volume/Absorption"));
            buttons.put("Volume/Emission", controller.addToolBarToggleButton("Volume/Emission"));
            buttons.put("Volume/Render", controller.addToolBarRegularButton("Volume/Render"));

//            buttons.put("Edit/Settings", controller.addToolBarRegularButton("Edit/Settings"));
//            controller.addToolBarSeparator();
//
//            buttons.put("View/Impacts", controller.addToolBarRegularButton("View/Impacts"));
//            controller.addToolBarSeparator();
//
//            buttons.put("Simulation/Run", controller.addToolBarRegularButton("Simulation/Run"));
//            buttons.put("Simulation/Pause", controller.addToolBarRegularButton("Simulation/Pause"));
//            buttons.put("Simulation/Step", controller.addToolBarRegularButton("Simulation/Step"));
//            controller.addToolBarSeparator();
//
//            buttons.put("Help/About", controller.addToolBarRegularButton("Help/About"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setEnabled(String key, boolean value) {
        buttons.get(key).setEnabled(value);
        menuItems.get(key).setEnabled(value);
    }

    private void setSelected(String key, boolean value) {
        buttons.get(key).setSelected(value);
        menuItems.get(key).setSelected(value);
    }

    public void setFiltersEnabled(boolean value) {
        setEnabled("Filter/Double", value);
        setEnabled("Filter/Rotation", value);
        setEnabled("Filter/Grayscale", value);
        setEnabled("Filter/Invert", value);
        setEnabled("Filter/Watercolor", value);
        setEnabled("Filter/Gamma Correction", value);
        setEnabled("Filter/Floyd-Steinberg Dither", value);
        setEnabled("Filter/Ordered Dither", value);
        setEnabled("Filter/Roberts Edge Detection", value);
        setEnabled("Filter/Sobel Edge Detection", value);
        setEnabled("Filter/Anti-Aliasing", value);
        setEnabled("Filter/Sharpening", value);
        setEnabled("Filter/Embossing", value);
    }

    public void setVolumeEnabled(boolean value) {
        setEnabled("Volume/Absorption", value);
        setEnabled("Volume/Emission", value);
        setEnabled("Volume/Render", value);
    }

    public void setSelectEnabled(boolean value) {
        setEnabled("Edit/Select", value);
    }

    public void setCopyToRightEnabled(boolean value) {
        setEnabled("Edit/Copy B to C", value);
    }

    public void setCopyToLeftEnabled(boolean value) {
        setEnabled("Edit/Copy C to B", value);
    }

    public void setStatusLabelListeners(JLabel statusLabel){
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

    public void setSelectSelected(boolean value) {
        setSelected("Edit/Select", value);
    }


    public void setSaveEnabled(boolean value) {
        setEnabled("File/Save", value);
    }

    public void clear() {
        setSelectEnabled(false);
        setSelectSelected(false);
        setCopyToLeftEnabled(false);
        setCopyToRightEnabled(false);
        setFiltersEnabled(false);
        setVolumeEnabled(false);
        if (buttons.get("Volume/Absorption").isSelected()) {
            for (ActionListener actionListener : buttons.get("Volume/Absorption").getActionListeners()) {
                actionListener.actionPerformed(null);
            }
        }
        if (buttons.get("Volume/Emission").isSelected()) {
            for (ActionListener actionListener : buttons.get("Volume/Emission").getActionListeners()) {
                actionListener.actionPerformed(null);
            }
        }
    }
}
