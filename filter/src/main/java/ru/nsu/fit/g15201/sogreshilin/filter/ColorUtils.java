package ru.nsu.fit.g15201.sogreshilin.filter;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorUtils {
    public static int getColorComponentAt(int componentIndex, BufferedImage image, int x, int y) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (x >= width) {
            x = 2 * width - x - 1;
        } else if (x < 0) {
            x = -x;
        }

        if (y >= height) {
            y = 2 * height - y - 1;
        } else if (y < 0) {
            y = -y;
        }

        return (image.getRGB(x, y) >> (componentIndex * 8)) & 0xFF;
    }

    public static int cut(int value) {
        if (value < 0) {
            return 0;
        }
        if (value > 255) {
            return 255;
        }
        return value;
    }

    public static Color negateColor(Color color) {
        return new Color(0xFFFFFF - color.getRGB());
    }
}
