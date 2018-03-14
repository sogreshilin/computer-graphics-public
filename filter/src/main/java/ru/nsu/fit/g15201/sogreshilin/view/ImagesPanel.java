
package ru.nsu.fit.g15201.sogreshilin.view;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import ru.nsu.fit.g15201.sogreshilin.controller.Controller;

public class ImagesPanel extends JPanel {
    private static final int ZONE_HEIGHT = 350;
    private static final int ZONE_WIDTH = 350;
    private static final int SPACE = 20;
    private static final int SIDE = 350;

    private static final int BORDER_THICKNESS = 1;
    private static final Point zoneA = new Point(SPACE, SPACE);
    private static final Point zoneB = new Point(2 * SPACE + ZONE_WIDTH, SPACE);
    private static final Point zoneC = new Point(3 * SPACE + 2 * ZONE_WIDTH, SPACE);

    private double heightOverWidth;
    private double scaleCoefficient;

    private final Dimension dimension = new Dimension(ZONE_WIDTH * 3 + SPACE * 4, ZONE_HEIGHT + 2 * SPACE);

    private BufferedImage sourceImageLayer;
    private int scaledImageWidth;
    private int scaledImageHeight;

    private BufferedImage selectionLayer;
    private int selectionSquareHalfSide;
    private boolean selectionEnabled = false;
    private Point selectedTopLeft;

    private BufferedImage filteredImage;

    private Controller controller;

    public ImagesPanel(Controller controller) {
        this.controller = controller;
        setSize(dimension);
    }

    public void setFilteredImage(BufferedImage image) {
        filteredImage = image;
        repaint();
    }

    public void setSelectionEnabled(boolean selectionEnabled) {
        this.selectionEnabled = selectionEnabled;
    }

    public void setImage(BufferedImage image) {
        sourceImageLayer = image;
        selectedTopLeft = null;
        controller.setSelectSelected(false);
        selectionEnabled = false;
        int initialWidth = image.getWidth();
        int initialHeight = image.getHeight();
        selectionLayer = new BufferedImage(initialWidth, initialHeight, BufferedImage.TYPE_INT_ARGB);
        heightOverWidth = ((double) initialHeight / initialWidth);
        if (initialHeight < ZONE_HEIGHT && initialWidth < ZONE_HEIGHT) {
            scaledImageWidth = initialWidth;
            scaledImageHeight = initialHeight;
            scaleCoefficient = 1;
            selectionSquareHalfSide = SIDE / 2;
            repaint();
            return;
        }
        if (initialHeight < initialWidth) {
            scaledImageWidth = ZONE_WIDTH;
            scaledImageHeight = (int) Math.round(ZONE_HEIGHT * heightOverWidth);
            scaleCoefficient = ((double) initialWidth) / ZONE_WIDTH;
        } else {
            scaledImageWidth = (int) Math.round(ZONE_WIDTH / heightOverWidth);
            scaledImageHeight = ZONE_HEIGHT;
            scaleCoefficient = ((double) initialHeight) / ZONE_HEIGHT;
        }
        this.selectionSquareHalfSide = (int) Math.round(SIDE / scaleCoefficient) / 2;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return dimension;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;

        drawBorders(graphics);

        if (sourceImageLayer == null) {
            return;
        }
        drawSourceImage(graphics);


        if (selectedTopLeft == null) {
            return;
        }
        drawSelectedImage(graphics);
        drawSelectionRectangle(graphics);

        if (filteredImage == null) {
            return;
        }
        drawFilteredImage(graphics);
    }

    private void drawFilteredImage(Graphics2D graphics) {
        graphics.drawImage(filteredImage,
                zoneC.getX(), zoneC.getY(),
                zoneC.getX() + SIDE, zoneC.getY() + SIDE,
                0, 0, filteredImage.getWidth(), filteredImage.getHeight(),
                this
        );
    }

    private void drawSelectionRectangle(Graphics2D graphics) {
        graphics.drawImage(selectionLayer, zoneA.getX(), zoneA.getY(), this);
    }

    private void drawBorders(Graphics2D graphics) {
        Stroke dashedStroke = new BasicStroke(BORDER_THICKNESS, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[]{ 10, 5 }, 0);
        graphics.setStroke(dashedStroke);
        graphics.setColor(Color.GRAY);

        graphics.drawRect(zoneA.getX(), zoneA.getY(), ZONE_WIDTH, ZONE_HEIGHT);
        graphics.drawRect(zoneB.getX(), zoneB.getY(), ZONE_WIDTH, ZONE_HEIGHT);
        graphics.drawRect(zoneC.getX(), zoneC.getY(), ZONE_WIDTH, ZONE_HEIGHT);
    }

    private void drawSelectedImage(Graphics2D graphics) {
        graphics.drawImage(sourceImageLayer,
                zoneB.getX(), zoneB.getY(),
                zoneB.getX() + SIDE, zoneB.getY() + SIDE,
                selectedTopLeft.getX(), selectedTopLeft.getY(),
                selectedTopLeft.getX() + SIDE, selectedTopLeft.getY() + SIDE,
                this);
    }

    private void drawSourceImage(Graphics2D graphics) {
        graphics.drawImage(sourceImageLayer,
                zoneA.getX(), zoneA.getY(),
                zoneA.getX() + scaledImageWidth, zoneA.getY() + scaledImageHeight,
                0, 0, sourceImageLayer.getWidth(), sourceImageLayer.getHeight(),
                this);
    }

    public boolean isSelectionEnabled() {
        return selectionEnabled;
    }

    private int getUpperLeftX(int x) {
        if (x - selectionSquareHalfSide < 0) {
            return 0;
        }

        boolean isLandscape = heightOverWidth < 1;

        int width = isLandscape? ZONE_WIDTH : (int) Math.round(ZONE_WIDTH / heightOverWidth);
        if (x + selectionSquareHalfSide >= width) {
            return width - 2 * selectionSquareHalfSide;
        }

        return x - selectionSquareHalfSide;
    }

    private int getUpperLeftY(int y) {
        if (y - selectionSquareHalfSide < 0) {
            return 0;
        }

        boolean isPortrait = heightOverWidth > 1;
        int height = isPortrait? ZONE_HEIGHT : (int) Math.round(ZONE_HEIGHT * heightOverWidth);
        if (y + selectionSquareHalfSide >= height) {
            return height - 2 * selectionSquareHalfSide;
        }

        return y - selectionSquareHalfSide;
    }

    private void drawRectangle(int x, int y, int width, int height) {
        drawHorizontalLine(x, width, y);
        drawHorizontalLine(x, width, y + height - 1);
        drawVerticalLine(y, height, x);
        drawVerticalLine(y, height, x + width - 1);
    }

    private void drawVerticalLine(int fromCoordinate, int height, int fixedCoordinate) {
        for (int i = fromCoordinate; i < fromCoordinate + height; ++i) {
            negatePixelAt(fixedCoordinate, i);
        }
    }

    private void drawHorizontalLine(int fromCoordinate, int length, int fixedCoordinate) {
        for (int i = fromCoordinate; i < fromCoordinate + length; ++i) {
            negatePixelAt(i, fixedCoordinate);
        }
    }

    private void negatePixelAt(int x, int y) {
        Color source = new Color(sourceImageLayer.getRGB(x, y));
        Color destination = new Color(0xFFFFFF - source.getRGB());
        selectionLayer.setRGB(x, y, destination.getRGB());
    }

    public void drawSelectionRectangle(int x, int y) {
        if (selectionEnabled) {
            x -= zoneA.getX();
            y -= zoneA.getY();

            clearSelection();

            int xRectangle = getUpperLeftX(x);
            int yRectangle = getUpperLeftY(y);

            int size = (int) Math.round(350.0 / scaleCoefficient);

            int xAtImage = (int) Math.round(xRectangle * scaleCoefficient);
            int yAtImage = (int) Math.round(yRectangle * scaleCoefficient);
            selectedTopLeft = new Point(xAtImage, yAtImage);

            drawRectangle(xRectangle, yRectangle, size, size);
            repaint();
        }
    }

    public void clearSelection() {
        Graphics2D graphics = selectionLayer.createGraphics();
        graphics.setComposite(AlphaComposite.Clear);
        graphics.fillRect(0, 0, selectionLayer.getWidth(), selectionLayer.getHeight());
        graphics.setComposite(AlphaComposite.SrcOver);
        repaint();
    }

    public BufferedImage getSelectedImage() {
        return sourceImageLayer.getSubimage(selectedTopLeft.getX(), selectedTopLeft.getY(), ZONE_WIDTH, ZONE_HEIGHT);
    }

    public BufferedImage getFilteredImage() {
        return filteredImage;
    }
}
