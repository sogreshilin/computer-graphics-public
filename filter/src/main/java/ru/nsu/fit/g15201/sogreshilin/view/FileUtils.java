package ru.nsu.fit.g15201.sogreshilin.view;

import java.util.List;
import ru.nsu.fit.g15201.sogreshilin.controller.ExtensionFileFilter;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Objects;

public class FileUtils {
    private static final String PATHNAME = "./FIT_15201_Sogreshilin_Filter_Data";
    private static File DATA_DIR = new File(PATHNAME);

    private static File getDataDirectory() {
        if(DATA_DIR == null || !DATA_DIR.exists()) {
            DATA_DIR = new File(PATHNAME);
        }
        for(File f: Objects.requireNonNull(DATA_DIR.listFiles())) {
            if(f.isDirectory() && f.getName().endsWith("_Data")) {
                DATA_DIR = f;
                break;
            }
        }
        return DATA_DIR;
    }

    public static File getSaveFileName(JFrame parent, List<String> extensions, String description) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        for (String extension: extensions) {
            FileFilter filter = new ExtensionFileFilter(extension, description);
            fileChooser.addChoosableFileFilter(filter);
        }

        fileChooser.setCurrentDirectory(getDataDirectory());
        if(fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            if(!f.getName().contains("."))
                f = new File(f.getParent(), f.getName() + "." + fileChooser.getFileFilter());
            return f;
        }
        return null;
    }

    public static File getOpenFileName(JFrame parent,  List<String> extensions, String description) {
        JFileChooser fileChooser = new JFileChooser();
        for (String extension: extensions) {
            FileFilter filter = new ExtensionFileFilter(extension, description);
            fileChooser.addChoosableFileFilter(filter);
        }

        fileChooser.setCurrentDirectory(getDataDirectory());
        if(fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            if(!f.getName().contains("."))
                f = new File(f.getParent(), f.getName() + "." + fileChooser.getFileFilter());
            return f;
        }
        return null;
    }

    public static String getExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }
}
