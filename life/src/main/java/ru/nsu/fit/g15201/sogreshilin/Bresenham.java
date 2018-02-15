package ru.nsu.fit.g15201.sogreshilin;

import ru.nsu.fit.g15201.sogreshilin.Point;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Bresenham {

    public static void drawLine(BufferedImage image, int x0, int y0, int x1, int y1, Color color) {
        for (Point pixel : getLinePixels(x0, y0, x1, y1)) {
            image.setRGB(pixel.getX(), pixel.getY(), color.getRGB());
        }
    }

    public static List<Point> getLinePixels(int x0, int y0, int x1, int y1) {
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
