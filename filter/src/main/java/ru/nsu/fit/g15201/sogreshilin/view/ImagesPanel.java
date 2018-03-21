
package ru.nsu.fit.g15201.sogreshilin.view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;
import ru.nsu.fit.g15201.sogreshilin.controller.Controller;
import ru.nsu.fit.g15201.sogreshilin.rendering.AbsorptionPoint;
import ru.nsu.fit.g15201.sogreshilin.rendering.Config;
import ru.nsu.fit.g15201.sogreshilin.rendering.EmissionPoint;

import static java.lang.Math.*;

public class ImagesPanel extends JPanel {
    private static final int IMAGE_HEIGHT = 350;
    private static final int IMAGE_WIDTH = 350;
    private static final int SELECTING_MAX_SIDE = 350;
    private static final int BORDER_THICKNESS = 1;
    private static final int SPACE = 20;

    private static final int GRAPH_HEIGHT = 150;
    private static final int GRAPH_WIDTH;

    private static final int CANVAS_WIDTH;
    private static final int CANVAS_HEIGHT;

    private Dimension dimension;

    private Image source;
    private BufferedImage selectedImage;
    private BufferedImage filteredImage;

    private BufferedImage imagesCanvas = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    private BufferedImage selectCanvas = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_ARGB);

    private double scaleCoefficient;

    private int initialImageWidth;
    private int initialImageHeight;
    private int scaledImageWidth;
    private int scaledImageHeight;

    private int selectWidth;
    private int selectHeight;
    private boolean selectionEnabled = false;

    private Controller controller;

    private static final int ZONE_HEIGHT;
    private static final int ZONE_WIDTH;
    private static final Point ZONE_A;
    private static final Point ZONE_B;
    private static final Point ZONE_C;
    private static final Point ABSORPTION;
    private static final Point EMISSION;


    private static final Stroke STROKE;

    static {
        ZONE_HEIGHT = IMAGE_HEIGHT + 1;
        ZONE_WIDTH = IMAGE_HEIGHT + 1;
        ZONE_A = new Point(SPACE, SPACE);
        ZONE_B = new Point(2 * SPACE + IMAGE_WIDTH, SPACE);
        ZONE_C = new Point(3 * SPACE + 2 * IMAGE_WIDTH, SPACE);
        CANVAS_WIDTH = ZONE_WIDTH * 3 + SPACE * 4;
        CANVAS_HEIGHT = ZONE_HEIGHT + 3 * SPACE + GRAPH_HEIGHT;
        GRAPH_WIDTH = (CANVAS_WIDTH - 3 * SPACE) / 2;
        ABSORPTION = new Point(SPACE, 2 * SPACE + IMAGE_HEIGHT);
        EMISSION = new Point(2 * SPACE + GRAPH_WIDTH, 2 * SPACE + IMAGE_HEIGHT);
        STROKE = new BasicStroke(
                BORDER_THICKNESS,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL,
                0,
                new float[]{ 3 },
                0);
    }

    public ImagesPanel(Controller controller) {
        drawBorders((Graphics2D) imagesCanvas.getGraphics());
        this.controller = controller;
        dimension = new Dimension(IMAGE_WIDTH * 3 + SPACE * 4, IMAGE_HEIGHT + 2 * SPACE);
        setSize(dimension);
        repaint();
    }

    public void setFilteredImage(BufferedImage image) {
        filteredImage = image;
        if (image != null) {
            drawFilteredImage();
            controller.setAbleToSave(true);
        } else {
            clearFilteredImage();
            controller.setAbleToSave(false);
        }
        repaint();
    }

    public void setSelectionEnabled(boolean selectionEnabled) {
        this.selectionEnabled = selectionEnabled;
    }

    public void setImage(Image image) {
        controller.setSelectSelected(false);
        source = image;
        selectionEnabled = false;
        initialImageWidth = image.getWidth(null);
        initialImageHeight = image.getHeight(null);
        double heightOverWidth = ((double) initialImageHeight / initialImageWidth);
        if (initialImageHeight < IMAGE_HEIGHT && initialImageWidth < IMAGE_WIDTH) {
            scaledImageWidth = initialImageWidth;
            scaledImageHeight = initialImageHeight;
            selectWidth = initialImageWidth;
            selectHeight = initialImageHeight;
            scaleCoefficient = 1;
        } else if (initialImageHeight < initialImageWidth) {
            scaledImageWidth = IMAGE_WIDTH;
            scaledImageHeight = (int) Math.round(IMAGE_HEIGHT * heightOverWidth);
            scaleCoefficient = ((double) initialImageWidth) / IMAGE_WIDTH;
            selectWidth = (int) (SELECTING_MAX_SIDE / scaleCoefficient);
            selectHeight = (int) (SELECTING_MAX_SIDE / scaleCoefficient);
        } else {
            scaledImageWidth = (int) Math.round(IMAGE_WIDTH / heightOverWidth);
            scaledImageHeight = IMAGE_HEIGHT;
            scaleCoefficient = ((double) initialImageHeight) / IMAGE_HEIGHT;
            selectWidth = (int) (SELECTING_MAX_SIDE / scaleCoefficient);
            selectHeight = (int) (SELECTING_MAX_SIDE / scaleCoefficient);
        }
        Image sourceImageLayer = image.getScaledInstance(scaledImageWidth, scaledImageHeight, Image.SCALE_DEFAULT);
        Graphics2D graphics = (Graphics2D) imagesCanvas.getGraphics();
        clearZoneA();
        graphics.drawImage(sourceImageLayer, ZONE_A.getX() + 1, ZONE_A.getY() + 1, null);
        repaint();
    }

    private void clearZoneA() {
        int x = ZONE_A.getX() + 1;
        int y = ZONE_A.getY() + 1;
        clear((Graphics2D) imagesCanvas.getGraphics(), x, y, IMAGE_WIDTH, IMAGE_HEIGHT);
        clear((Graphics2D) selectCanvas.getGraphics(), x, y, IMAGE_WIDTH, IMAGE_HEIGHT);
    }

    @Override
    public Dimension getPreferredSize() {
        return dimension;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;

        graphics.drawImage(imagesCanvas, null, null);
        graphics.drawImage(selectCanvas, null, null);
    }

    private Image scaleToFit(BufferedImage image) {
        int oldWidth = image.getWidth();
        int oldHeight = image.getHeight();

        int newWidth;
        int newHeight;

        if (oldWidth <= IMAGE_WIDTH && oldHeight <= IMAGE_HEIGHT) {
            return image;
        }

        if (oldHeight < oldWidth) {
            newWidth = IMAGE_WIDTH;
            newHeight = (int) Math.round((double) newWidth / oldWidth * oldHeight);
        } else {
            newHeight = IMAGE_HEIGHT;
            newWidth = (int) Math.round((double) newHeight / oldHeight * oldWidth);
        }

        return image.getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT);
    }

    private void drawFilteredImage() {
        Graphics2D graphics = imagesCanvas.createGraphics();
        clear(graphics, ZONE_C.getX() + 1, ZONE_C.getY() + 1, IMAGE_WIDTH, IMAGE_HEIGHT);

        graphics.drawImage(scaleToFit(filteredImage), ZONE_C.getX() + 1, ZONE_C.getY() + 1, null);

        graphics.dispose();
        repaint();
    }

    private void clearFilteredImage() {
        Graphics2D graphics = imagesCanvas.createGraphics();
        clear(graphics, ZONE_C.getX() + 1, ZONE_C.getY() + 1, IMAGE_WIDTH, IMAGE_HEIGHT);
    }

    private void drawGraphsAxises() {
        Graphics2D graphics = (Graphics2D) imagesCanvas.getGraphics();
        dimension = new Dimension(IMAGE_WIDTH * 3 + SPACE * 4, IMAGE_HEIGHT + 3 * SPACE + GRAPH_HEIGHT);
        setSize(dimension);

        graphics.setStroke(STROKE);
        graphics.setColor(Color.BLACK);

        graphics.drawLine(ABSORPTION.getX(), ABSORPTION.getY(),
                ABSORPTION.getX(), ABSORPTION.getY() + GRAPH_HEIGHT);

        graphics.drawLine(ABSORPTION.getX(), ABSORPTION.getY() + GRAPH_HEIGHT,
                ABSORPTION.getX() + GRAPH_WIDTH, ABSORPTION.getY() + GRAPH_HEIGHT);

        graphics.drawLine(EMISSION.getX(), EMISSION.getY(),
                EMISSION.getX(), EMISSION.getY() + GRAPH_HEIGHT);

        graphics.drawLine(EMISSION.getX(), EMISSION.getY() + GRAPH_HEIGHT,
                EMISSION.getX() + GRAPH_WIDTH, EMISSION.getY() + GRAPH_HEIGHT);
    }

    private void drawGraph(Graphics2D graphics,
                           int originX, int originY, int initialWidht, int initialHeight,
                           int[] xCoordinates, double[] yCoordinates) {
        double unitX = (double) GRAPH_WIDTH / initialWidht;
        double unitY = (double) GRAPH_HEIGHT / initialHeight;
//        graphics.setColor(Color.PINK);
//        System.out.println("Origin: " + originX + ", " + originY);
//        System.out.println("UnitX : " + unitX);
//        System.out.println("UnitY : " + unitY);
//        System.out.println(ABSORPTION.getX());
//        System.out.println(GRAPH_WIDTH);
        for (int i = 0; i < xCoordinates.length - 1; ++i) {
//            System.out.println(String.format("[%d %.1f] [%d %.1f]", xCoordinates[i], yCoordinates[i], xCoordinates[i + 1], yCoordinates[i + 1]));
            int x1 = originX + (int) round(xCoordinates[i] * unitX);
            int y1 = originY - (int) round(yCoordinates[i] * unitY);
            int x2 = originX + (int) round(xCoordinates[i + 1] * unitX);
            int y2 = originY - (int) round(yCoordinates[i + 1] * unitY);
//            System.out.println(String.format("[%d %d] [%d %d]", x1, y1, x2, y2));
            graphics.drawLine(x1, y1, x2, y2);
        }
    }

    public void drawGraphs(Config config) {
        clear((Graphics2D) imagesCanvas.getGraphics(),
                ABSORPTION.getX(), ABSORPTION.getY(), GRAPH_WIDTH + 1, GRAPH_HEIGHT + 1);
        clear((Graphics2D) imagesCanvas.getGraphics(),
                EMISSION.getX(), EMISSION.getY(), GRAPH_WIDTH + 1, GRAPH_HEIGHT + 1);
        drawGraphsAxises();
        drawAbsorptionGraph(config.getAbsorption());
        drawEmissionGraphs(config.getEmission());
    }

    private void drawEmissionGraphs(ArrayList<EmissionPoint> emission) {
        int[] xs = new int[emission.size()];
        double[] reds = new double[emission.size()];
        double[] greens = new double[emission.size()];
        double[] blues = new double[emission.size()];
        int k = 0;
        for (EmissionPoint point: emission) {
            xs[k] = point.getX();
            reds[k] = point.getRed();
            greens[k] = point.getGreen();
            blues[k] = point.getBlue();
            ++k;
        }
        Graphics2D graphics = (Graphics2D) imagesCanvas.getGraphics();
        graphics.setColor(Color.RED);
        drawGraph(graphics, EMISSION.getX(), EMISSION.getY() + GRAPH_HEIGHT,
                100, 255, xs, reds);
        graphics.setColor(Color.GREEN);
        drawGraph(graphics, EMISSION.getX() + 1, EMISSION.getY() + GRAPH_HEIGHT - 1,
                100, 255, xs, greens);
        graphics.setColor(Color.BLUE);
        drawGraph(graphics, EMISSION.getX() + 2, EMISSION.getY() + GRAPH_HEIGHT - 2,
                100, 255, xs, blues);
    }

    private void drawAbsorptionGraph(ArrayList<AbsorptionPoint> absorption) {
        int[] xs = new int[absorption.size()];
        double[] ys = new double[absorption.size()];
        int k = 0;
        for (AbsorptionPoint point: absorption) {
            xs[k] = point.getX();
            ys[k] = point.getValue();
            ++k;
        }
        Graphics2D graphics = (Graphics2D) imagesCanvas.getGraphics();
        graphics.setColor(Color.BLACK);
        drawGraph(graphics, ABSORPTION.getX(), ABSORPTION.getY() + GRAPH_HEIGHT,
                100, 1, xs, ys);
    }

    private void drawSelectionRectangle(Graphics2D graphics) {
        graphics.drawImage(selectCanvas, ZONE_A.getX(), ZONE_A.getY(), this);
    }

    private void drawBorders(Graphics2D graphics) {
        graphics.setStroke(STROKE);
        graphics.setColor(Color.BLACK);

        graphics.drawRect(ZONE_A.getX(), ZONE_A.getY(), ZONE_WIDTH, ZONE_HEIGHT);
        graphics.drawRect(ZONE_B.getX(), ZONE_B.getY(), ZONE_WIDTH, ZONE_HEIGHT);
        graphics.drawRect(ZONE_C.getX(), ZONE_C.getY(), ZONE_WIDTH, ZONE_HEIGHT);
    }

    public boolean isSelectionEnabled() {
        return selectionEnabled;
    }

    private int getLeftRectangle(int x) {
        if (scaleCoefficient == 1) {
            return SPACE + 1;
        }
        int halfWidth = selectWidth / 2 - (selectWidth + 1) % 2;

        if (x - halfWidth <= SPACE) {
            return SPACE + 1;
        }


        int width = scaledImageWidth;

        if (x + halfWidth >= width + SPACE + 1) {
            return width - 2 * halfWidth + SPACE;
        }

        return x - halfWidth;
    }

    private int getTopRectangle(int y) {
        if (scaleCoefficient == 1) {
            return SPACE + 1;
        }
        int halfHeight = selectHeight / 2 - (selectHeight + 1) % 2;

        if (y - halfHeight <= SPACE) {
            return SPACE + 1;
        }

        int height = scaledImageHeight;

        if (y + halfHeight >= height + SPACE + 1) {
            return height - 2 * halfHeight + SPACE;
        }

        return y - halfHeight;

    }

    private void drawRectangle(int x, int y, int width, int height) {
        drawHorizontalLine(x, width, y);
        drawHorizontalLine(x, width, y + height - 1);
        drawVerticalLine(y, height, x);
        drawVerticalLine(y, height, x + width - 1);
    }

    private void drawVerticalLine(int fromCoordinate, int height, int fixedCoordinate) {
        for (int i = fromCoordinate; i < fromCoordinate + height; ++i) {
            if (i % 6 >= 3) {
                negatePixelAt(fixedCoordinate, i);
            }
        }
    }

    private void drawHorizontalLine(int fromCoordinate, int length, int fixedCoordinate) {
        for (int i = fromCoordinate; i < fromCoordinate + length; ++i) {
            if (i % 6 < 3) {
                negatePixelAt(i, fixedCoordinate);
            }
        }
    }

    private void negatePixelAt(int x, int y) {
        Color source = new Color(imagesCanvas.getRGB(x, y));
        Color destination = new Color(0xFFFFFF - source.getRGB());
        selectCanvas.setRGB(x, y, destination.getRGB());
    }

    public void drawSelectionRectangle(int x, int y) {
        if (selectionEnabled) {

            clearSelection();

            int xRectangle = getLeftRectangle(x);
            int yRectangle = getTopRectangle(y);

            int height = scaleCoefficient == 1 ? scaledImageHeight : (selectHeight / 2 - (selectHeight + 1) % 2) * 2 + 1;
            int width = scaleCoefficient == 1 ? scaledImageWidth: (selectWidth / 2 - (selectWidth + 1) % 2) * 2 + 1;

            drawRectangle(xRectangle, yRectangle, width, height);

            controller.setFiltersEnabled(true);
            selectedImage = deepCopySelectedImage(source, (int) ((xRectangle - SPACE - 1) * scaleCoefficient),
                    (int) ((yRectangle - SPACE - 1) * scaleCoefficient),
                    Math.min(IMAGE_WIDTH, initialImageWidth),
                    Math.min(IMAGE_HEIGHT, initialImageHeight));
            drawSelectedImage();
            repaint();
        }
    }

    private BufferedImage deepCopySelectedImage(Image source, int x, int y, int width, int height) {
        BufferedImage destination = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        if (x + width > source.getWidth(null)) {
            x = source.getWidth(null) - width;
        }
        if (y + height > source.getHeight(null)) {
            y = source.getHeight(null) - height;
        }
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                destination.setRGB(i, j, ((BufferedImage) source).getRGB(x + i, y + j));
            }
        }
        return destination;
    }

    private void drawSelectedImage() {
        Graphics2D graphics = imagesCanvas.createGraphics();
        clear(graphics, ZONE_B.getX() + 1, ZONE_B.getY() + 1, IMAGE_WIDTH, IMAGE_HEIGHT);

        graphics.drawImage(scaleToFit(selectedImage), ZONE_B.getX() + 1, ZONE_B.getY() + 1, null);

        graphics.dispose();
        repaint();
    }

    private void clear(Graphics2D graphics, int x, int y, int width, int height) {
        graphics.setComposite(AlphaComposite.Clear);
        graphics.fillRect(x, y, width, height);
        graphics.setComposite(AlphaComposite.SrcOver);
        repaint();
    }

    public void clearSelection() {
        clear(selectCanvas.createGraphics(), 0, 0,
                selectCanvas.getWidth(), selectCanvas.getHeight());
    }

    public BufferedImage getSelectedImage() {
        return deepCopySelectedImage(selectedImage, 0, 0, selectedImage.getWidth(), selectedImage.getHeight());
    }

    public BufferedImage getFilteredImage() {
        return filteredImage;
    }

    public void copyFilteredToSelected() {
        selectedImage = deepCopySelectedImage(filteredImage, 0, 0,
                filteredImage.getWidth(), filteredImage.getHeight());
        drawSelectedImage();
    }

    public void copySelectedToFiltered() {
        setFilteredImage(deepCopySelectedImage(selectedImage, 0, 0,
                selectedImage.getWidth(), selectedImage.getHeight()));
    }

    public void clearAll() {
        setSelectionEnabled(false);
        clearSelection();
        clear((Graphics2D) imagesCanvas.getGraphics(), 0, 0, imagesCanvas.getWidth(), imagesCanvas.getHeight());
        drawBorders((Graphics2D) imagesCanvas.getGraphics());
        this.controller = controller;
        dimension = new Dimension(IMAGE_WIDTH * 3 + SPACE * 4, IMAGE_HEIGHT + 2 * SPACE);
        setSize(dimension);
        repaint();
    }
}
