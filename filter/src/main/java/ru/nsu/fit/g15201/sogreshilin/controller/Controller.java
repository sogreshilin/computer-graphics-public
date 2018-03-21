package ru.nsu.fit.g15201.sogreshilin.controller;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import javax.swing.*;
import ru.nsu.fit.g15201.sogreshilin.filter.*;
import ru.nsu.fit.g15201.sogreshilin.filter.affine.*;
import ru.nsu.fit.g15201.sogreshilin.filter.dither.*;
import ru.nsu.fit.g15201.sogreshilin.filter.edge.*;
import ru.nsu.fit.g15201.sogreshilin.rendering.Config;
import ru.nsu.fit.g15201.sogreshilin.rendering.VolumeRendering;
import ru.nsu.fit.g15201.sogreshilin.view.*;
import ru.nsu.fit.g15201.sogreshilin.view.dialog.*;
import ru.nsu.fit.g15201.sogreshilin.view.toolbar.*;

public class Controller extends MainFrame implements FilterAppliedObserver {
    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 600;

    private static final int WIDTH = 1134;
    private static final int HEIGHT = 600;

    private static final String TITLE = "Filters";
    private static final java.util.List<String> EXTENSIONS = List.of("bmp", "png");
    private static final String DEFAULT_EXTENSION = "png";
    private static final String ABOUT = TITLE + ", version 1.0\n" +
            "Copyright 2018 Sogreshilin Alexander, FIT-15201";
    private final ImagesPanel imagesPanel;
    private final JScrollPane scrollPane;
    private MenuToolbarManager manager;
    private File currentFile = null;
    private boolean filtersEnabled = false;
    private boolean volumeEnabled = false;
    private boolean emissionEnabled = false;
    private boolean absorptionEnabled = false;

    private BufferedImage imageBeforeFilter;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Config config;

    private Controller() {
        super(WIDTH, HEIGHT, TITLE);
        imagesPanel = new ImagesPanel(this);
        scrollPane = new JScrollPane(imagesPanel);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setupImagesPanel();
        setupToolbarMenuManager();
        setupStatusBar();
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

    private void setupImagesPanel() {
        add(scrollPane);

        imagesPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                manager.setCopyToRightEnabled(true);
                imagesPanel.drawSelectionRectangle(e.getX(), e.getY());
            }
        });

        imagesPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                manager.setCopyToRightEnabled(true);
                imagesPanel.drawSelectionRectangle(e.getX(), e.getY());
            }
        });
    }

    private void setupToolbarMenuManager() {
        manager = new MenuToolbarManager(this);
        manager.setupMenu();
        manager.setupToolbar();
        manager.setSaveEnabled(false);
        manager.setSelectEnabled(false);
        manager.setCopyToLeftEnabled(false);
        manager.setCopyToRightEnabled(false);
        manager.setFiltersEnabled(false);
        manager.setVolumeEnabled(false);
    }

    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.setVisible(true);
    }

    public void onNew() {
        filtersEnabled = false;
        volumeEnabled = false;
        emissionEnabled = false;
        absorptionEnabled = false;
        imageBeforeFilter = null;
        manager.clear();
        imagesPanel.setFilteredImage(null);
        imagesPanel.clearAll();
    }

    public MenuToolbarManager getManager() {
        return manager;
    }

    public void onOpen() {
        File file = FileUtils.getOpenFileName(this, EXTENSIONS, "Image");
        setCurrentFile(file);
        if (file == null) {
            return;
        }

        try (FileInputStream in = new FileInputStream(currentFile)) {
            Image image = ImageIO.read(in);
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
        BufferedImage filteredImage = imagesPanel.getFilteredImage();
        if (filteredImage != null) {
            File file = FileUtils.getSaveFileName(this, EXTENSIONS, "Image file");
            if (file == null) {
                return;
            }
            String extension = FileUtils.getExtension(file);
            if (extension.isEmpty()) {
                extension = DEFAULT_EXTENSION;
            }
            try (FileOutputStream out = new FileOutputStream(file)) {
                ImageIO.write(filteredImage, extension, out);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "File cannot be saved",
                        "Saving error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void onConfigOpen() {
        File file = FileUtils.getOpenFileName(this, List.of("txt"), "Config");
        if (file == null) {
            return;
        }

        try (FileInputStream in = new FileInputStream(file)) {
            config = new Config();
            config.readConfigFromFile(in);
            imagesPanel.drawGraphs(config);
            volumeEnabled = true;
            manager.setVolumeEnabled(filtersEnabled);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid file format.\nFile cannot be read",
                    "Invalid file format",
                    JOptionPane.ERROR_MESSAGE);
        }
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
        imagesPanel.copySelectedToFiltered();
    }

    public void onCopyToRight() {
        imagesPanel.copyFilteredToSelected();
    }

    public void onGrayscale() {
        apply(new GrayscaleConversion(this));
    }

    public void onInvert() {
        apply(new NegativeConversion(this));
    }

    private void onDither(Dithering filter) {
        imageBeforeFilter = imagesPanel.getFilteredImage();
        new DitherDialog(this, filter).setVisible(true);
    }

    public void onFloydSteinbergDither() {
        FloydSteinbergDithering filter = new FloydSteinbergDithering(this);
        filter.setLevels(2, 2, 2);
        onDither(filter);
    }

    public void onOrderDither() {
        OrderedDithering filter = new OrderedDithering(this);
        filter.setLevels(2, 2, 2);
        filter.setMatrixSize(8);
        onDither(filter);
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

    private void save(BufferedImage image, String filename, String extension) throws IOException {
        ImageIO.write(image, extension, new File(filename));
    }

    @Override
    public void onFilterApplied(BufferedImage image) {
        SwingUtilities.invokeLater(() -> imagesPanel.setFilteredImage(image));
    }

    public void setFiltersEnabled(boolean value) {
        if (value != filtersEnabled) {
            filtersEnabled = value;
            manager.setFiltersEnabled(value);
            manager.setVolumeEnabled(volumeEnabled);
        }
    }

    public void onEmission() {
        emissionEnabled = !emissionEnabled;
    }

    public void onAbsorption() {
        absorptionEnabled = !absorptionEnabled;
    }

    public void onVolumeRender() {
        if (config != null) {
            imageBeforeFilter = imagesPanel.getFilteredImage();
            VolumeRendering filter = new VolumeRendering(this, config);
            filter.setAbsorptionEnabled(absorptionEnabled);
            filter.setEmissionEnabled(emissionEnabled);
            new VolumeRenderingDialog(this, filter).setVisible(true);
        }
    }

    public void onAbout() {
        JOptionPane.showMessageDialog(this,
                ABOUT,
                "About " + TITLE,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void setAbleToSave(boolean ableToSave) {
        manager.setSaveEnabled(ableToSave);
        manager.setCopyToLeftEnabled(ableToSave);
    }
}
