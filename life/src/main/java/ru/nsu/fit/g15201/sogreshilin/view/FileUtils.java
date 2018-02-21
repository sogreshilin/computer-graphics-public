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
    private static File dataDirectory = new File("./FIT_15201_Sogreshilin_Life_Data");
    /**
     * Returns File pointing to Data directory of current project. If Data directory is not found, returns project directory.
     * @return File object.
     */
    private static File getDataDirectory() {
        if(dataDirectory == null) {
            try {
                String path = URLDecoder.decode(MainFrame.class.getProtectionDomain().getCodeSource().getLocation().getFile(), Charset.defaultCharset().toString());
                dataDirectory = new File(path).getParentFile();
            } catch (UnsupportedEncodingException e) {
                dataDirectory = new File(".");
            }
            if(dataDirectory == null || !dataDirectory.exists()) dataDirectory = new File(".");
            for(File f: Objects.requireNonNull(dataDirectory.listFiles())) {
                if(f.isDirectory() && f.getName().endsWith("_Data")) {
                    dataDirectory = f;
                    break;
                }
            }
        }
        return dataDirectory;
    }

    /**
     * Prompts user for file name to save and returns it
     * @param parent - parent frame for file selection dialog
     * @param extension - preferred file extension (example: "txt")
     * @param description - description of specified file type (example: "Text files")
     * @return File specified by user or null if user canceled operation
     */
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

    /**
     * Prompts user for file name to open and returns it
     * @param parent - parent frame for file selection dialog
     * @param extension - preferred file extension (example: "txt")
     * @param description - description of specified file type (example: "Text files")
     * @return File specified by user or null if user canceled operation
     */
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
