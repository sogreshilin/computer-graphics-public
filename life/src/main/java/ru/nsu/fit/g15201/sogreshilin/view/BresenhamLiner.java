package ru.nsu.fit.g15201.sogreshilin.view;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class BresenhamLiner {

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
        int deltax = x1 - x0;
        int deltay = y1 - y0;

        int dirx = Integer.signum(deltax);
        int diry = Integer.signum(deltay);

        deltax = Math.abs(deltax);
        deltay = Math.abs(deltay);

        boolean isAcuteAngle = deltax > deltay;

        int dx = isAcuteAngle ? dirx : 0;
        int dy = isAcuteAngle ? 0 : diry;
        int deltaError = isAcuteAngle ? deltay : deltax;
        int length = isAcuteAngle ? deltax : deltay;

        int x = x0;
        int y = y0;
        int error = 0;
        for (int i = 0; i <= length; ++i) {
            pixels.add(new Point(x, y));
            error += deltaError;
            if (2 * error >= length) {
                error -= length;
                y += diry;
                x += dirx;
            } else {
                x += dx;
                y += dy;
            }
        }
        return pixels;
    }
}
