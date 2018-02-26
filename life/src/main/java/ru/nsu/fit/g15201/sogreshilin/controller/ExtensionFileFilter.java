package ru.nsu.fit.g15201.sogreshilin.controller;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ExtensionFileFilter extends FileFilter {
    private final String extension;
    private final String description;

    public ExtensionFileFilter(String extension, String description) {
        this.extension = extension;
        this.description = description;
    }

    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith("."+extension.toLowerCase());
    }

    @Override
    public String getDescription() {
        return description+" (*."+extension+")";
    }
}
