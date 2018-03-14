package ru.nsu.fit.g15201.sogreshilin.controller;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import javax.swing.*;
import ru.nsu.fit.g15201.sogreshilin.filter.*;
import ru.nsu.fit.g15201.sogreshilin.filter.affine.*;
import ru.nsu.fit.g15201.sogreshilin.filter.dither.*;
import ru.nsu.fit.g15201.sogreshilin.filter.edge.*;
import ru.nsu.fit.g15201.sogreshilin.view.*;
import ru.nsu.fit.g15201.sogreshilin.view.dialog.*;
import ru.nsu.fit.g15201.sogreshilin.view.toolbar.*;

public class Controller extends MainFrame implements FilterAppliedObserver {
    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 600;

    private static final int WIDTH = 1134;
    private static final int HEIGHT = 600;

    private static final String TITLE = "Filters";
    private static final java.util.List<String> EXTENSIONS = Arrays.asList("bmp", "png");
    private final ImagesPanel imagesPanel;
    private final JScrollPane scrollPane;
    private MenuToolbarManager manager;
    private File currentFile = null;

    private BufferedImage imageBeforeFilter;
    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private Controller() {
        super(WIDTH, HEIGHT, TITLE);
        imagesPanel = new ImagesPanel(this);
        scrollPane = new JScrollPane(imagesPanel);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setupImagesPanel();
        setupToolbarMenuManager();
    }

    private void setupImagesPanel() {
        add(scrollPane);

        imagesPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                imagesPanel.drawSelectionRectangle(e.getX(), e.getY());
            }
        });

        imagesPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                imagesPanel.drawSelectionRectangle(e.getX(), e.getY());
            }
        });
    }

    private void setupToolbarMenuManager() {
        manager = new MenuToolbarManager(this);
        manager.setupMenu();
        manager.setupToolbar();
        manager.setSelectEnabled(false);
    }

    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.setVisible(true);
    }

    public void onNew() {
        File file = FileUtils.getOpenFileName(this, EXTENSIONS, "Image");
        setCurrentFile(file);
        if (file == null) {
            return;
        }

        try (FileInputStream in = new FileInputStream(currentFile)) {
            BufferedImage image = ImageIO.read(in);
            if (image != null) {
                imagesPanel.setImage(image);
            } else {
                throw new IOException("Image file cannot be read");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid file format.\nFile cannot be read",
                    "Invalid file format",
                    JOptionPane.ERROR_MESSAGE);
        }

        manager.setSelectEnabled(true);
        manager.setSelectSelected(false);

    }

    public void onOpen() {

    }

    private void setCurrentFile(File currentFile) {
        this.currentFile = currentFile;
        String filename = "Untitled";
        if (currentFile != null) {
            manager.setSelectEnabled(true);
            filename = currentFile.getName();
        }
        setTitle(filename + " - " + TITLE);
    }

    public void onSave() {
        if (currentFile == null) {
            File file = FileUtils.getSaveFileName(this, EXTENSIONS, "Image file");
            setCurrentFile(file);
        }
        if (currentFile != null) {
            try (FileOutputStream out = new FileOutputStream(currentFile)) {
//            todo : add file saving
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "File cannot be saved",
                        "Saving error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void onExit() {

    }

    public void onSelect() {
        imagesPanel.setSelectionEnabled(!imagesPanel.isSelectionEnabled());
        if (!imagesPanel.isSelectionEnabled()) {
            imagesPanel.clearSelection();
        }
    }

    public void setSelectSelected(boolean value) {
        manager.setSelectSelected(value);
    }

    public void onCopyToLeft() {
//        todo : copy to left
    }

    public void onCopyToRight() {
//        todo : copy to right
    }

    public void onGrayscale() {
        apply(new GrayscaleConversion(this));
    }

    public void onInvert() {
        apply(new NegativeConversion(this));
    }

    public void onFloydSteinbergDither() {
        FloydSteinbergDithering filter = new FloydSteinbergDithering(this);
        filter.setLevels(4, 4, 4);
        apply(filter);
    }

    public void onOrderDither() {
        OrderedDithering filter = new OrderedDithering(this);
        filter.setLevels(16, 16, 16);
//        todo : add this dialog menu
//        new DitherDialog(this, filter).setVisible(true);
        apply(filter);
    }

    public void onDoubleMagnification() {
        apply(new DoubleMagnification(this));
    }

    public void onRobertsEdgeDetection() {
        onEdgeDetection(new RobertsEdgeDetection(this));
    }

    public void onSobelEdgeDetection() {
        onEdgeDetection(new SobelEdgeDetection(this));
    }

    private void onEdgeDetection(EdgeDetection filter) {
        imageBeforeFilter = imagesPanel.getFilteredImage();
        new EdgeThresholdDialog(this, filter).setVisible(true);
    }

    public void onCancel() {
        imagesPanel.setFilteredImage(imageBeforeFilter);
    }

    public void onAntiAliasing() {
        apply(new AntiAliasing(this));
    }

    public void onSharpening() {
        apply(new Sharpening(this));
    }

    public void onEmbossing() {
        apply(new Embossing(this));
    }

    public void onWatercolor() {
        apply(new Watercolor(this));
    }

    public void onRotation() {
        imageBeforeFilter = imagesPanel.getFilteredImage();
        new AngleDialog(this, new Rotation(this)).setVisible(true);
    }

    public void onGammaCorrection() {
        imageBeforeFilter = imagesPanel.getFilteredImage();
        new GammaDialog(this, new GammaCorrection(this)).setVisible(true);
    }

    public void apply(Filter filter) {
        BufferedImage selectedImage = imagesPanel.getSelectedImage();
        executor.submit(() -> filter.apply(selectedImage));
    }

    @Override
    public void onFilterApplied(BufferedImage image) {
//        todo: ask which way to do this
//        imagesPanel.setFilteredImage(image);
        SwingUtilities.invokeLater(() -> imagesPanel.setFilteredImage(image));
    }
}
