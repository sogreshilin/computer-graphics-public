package ru.nsu.fit.g15201.sogreshilin.filter;

import java.awt.*;
import java.awt.image.BufferedImage;
import ru.nsu.fit.g15201.sogreshilin.controller.FilterAppliedObserver;

public class NegativeConversion implements Filter {
    private final FilterAppliedObserver observer;

    public NegativeConversion(FilterAppliedObserver observer) {
        this.observer = observer;
    }

    private int negatePixelAt(BufferedImage image, int x, int y) {
        Color source = new Color(image.getRGB(x, y));
        return new Color(0xFFFFFF - source.getRGB()).getRGB();
    }

    public BufferedImage apply(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage destination = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                destination.setRGB(i, j, negatePixelAt(source, i, j));
            }
        }

        observer.onFilterApplied(destination);
        return destination;
    }
}
