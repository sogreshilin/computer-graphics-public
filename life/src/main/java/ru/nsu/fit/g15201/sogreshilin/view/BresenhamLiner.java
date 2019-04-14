package ru.nsu.fit.g15201.sogreshilin.view;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

class BresenhamLiner {

    private Color color = Color.BLACK;
    private int lineThickness = 1;

    public BresenhamLiner() {
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getLineThickness() {
        return lineThickness;
    }

    public void setLineThickness(int lineThickness) {
        this.lineThickness = lineThickness;
    }

    public void drawLine(BufferedImage image, int x0, int y0, int x1, int y1) {
        if (x1 - x0 < 0) {
            int tmp;
            tmp = x0; x0 = x1; x1 = tmp;
            tmp = y0; y0 = y1; y1 = tmp;
        }
        for (Point pixel : getLinePixels(x0, y0, x1, y1)) {
            image.setRGB(pixel.getX(), pixel.getY(), color.getRGB());
        }
    }

    private List<Point> getLinePixels(int x0, int y0, int x1, int y1) {
        List<Point> pixels = new ArrayList<>();
        int deltaX = x1 - x0;
        int deltaY = y1 - y0;

        int directionX = Integer.signum(deltaX);
        int directionY = Integer.signum(deltaY);

        deltaX = Math.abs(deltaX);
        deltaY = Math.abs(deltaY);

        boolean isAcuteAngle = deltaX > deltaY;

        int dx = isAcuteAngle ? directionX : 0;
        int dy = isAcuteAngle ? 0 : directionY;
        int deltaError = isAcuteAngle ? deltaY : deltaX;
        int length = isAcuteAngle ? deltaX : deltaY;

        int x = x0;
        int y = y0;
        int error = 0;
        for (int i = 0; i <= length; ++i) {
            pixels.add(new Point(x, y));
            error += deltaError;
            if (2 * error >= length) {
                error -= length;
                y += directionY;
                x += directionX;
            } else {
                x += dx;
                y += dy;
            }
        }
        return pixels;
    }
}
