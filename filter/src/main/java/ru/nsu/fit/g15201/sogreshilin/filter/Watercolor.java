package ru.nsu.fit.g15201.sogreshilin.filter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import ru.nsu.fit.g15201.sogreshilin.controller.FilterAppliedObserver;
import ru.nsu.fit.g15201.sogreshilin.filter.affine.Sharpening;

import static java.lang.Math.abs;

public class Watercolor implements Filter {
    private FilterAppliedObserver observer;
    private int radius = 2;

    public Watercolor(FilterAppliedObserver observer) {
        this.observer = observer;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public BufferedImage apply(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int count = (2 * radius + 1) * (2 * radius + 1);

        BufferedImage filteredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int[] reds = new int[count];
                int[] greens = new int[count];
                int[] blues = new int[count];

                for (int i = x - radius, k = 0; i <= x + radius; ++i) {
                    for (int j = y - radius; j <= y + radius; ++j, ++k) {
                        Color color = new Color(image.getRGB(abs(i) % width, abs(j) % height));
                        reds[k] = color.getRed();
                        greens[k] = color.getGreen();
                        blues[k] = color.getBlue();
                    }
                }

                Arrays.sort(reds);
                Arrays.sort(greens);
                Arrays.sort(blues);

                int middle = count / 2 + 1;
                Color color = new Color(reds[middle], greens[middle], blues[middle]);
                filteredImage.setRGB(x, y, color.getRGB());
            }
        }

        filteredImage = new Sharpening(null).apply(filteredImage);

        if (observer != null) {
            observer.onFilterApplied(filteredImage);
        }
        return filteredImage;
    }
}
