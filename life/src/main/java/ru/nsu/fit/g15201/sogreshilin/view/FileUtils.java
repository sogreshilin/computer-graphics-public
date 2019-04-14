package ru.nsu.fit.g15201.sogreshilin.view;

import ru.nsu.fit.g15201.sogreshilin.controller.ExtensionFileFilter;
import ru.nsu.fit.g15201.sogreshilin.controller.MainFrame;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Objects;

public class FileUtils {
    private static File DATA_DIR = new File("./FIT_15201_Sogreshilin_Life_Data");

    private static File getDataDirectory() {
        try {
            String path = URLDecoder.decode(MainFrame.class.getProtectionDomain().getCodeSource().getLocation().getFile(),
                    Charset.defaultCharset().toString());
            DATA_DIR = new File(path).getParentFile();

        } catch (UnsupportedEncodingException e) {
            DATA_DIR = new File(".");
        }
        if(DATA_DIR == null || !DATA_DIR.exists()) DATA_DIR = new File(".");
        for(File f: Objects.requireNonNull(DATA_DIR.listFiles())) {
            if(f.isDirectory() && f.getName().endsWith("_Data")) {
                DATA_DIR = f;
                break;
            }
        }
        return DATA_DIR;
    }

    public static File getSaveFileName(JFrame parent, String extension, String description) {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new ExtensionFileFilter(extension, description);
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setCurrentDirectory(getDataDirectory());
        if(fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            if(!f.getName().contains("."))
                f = new File(f.getParent(), f.getName()+"."+extension);
            return f;
        }
        return null;
    }

    public static File getOpenFileName(JFrame parent, String extension, String description) {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new ExtensionFileFilter(extension, description);
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setCurrentDirectory(getDataDirectory());
        if(fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            if(!f.getName().contains("."))
                f = new File(f.getParent(), f.getName()+"."+extension);
            return f;
        }
        return null;
    }
}
