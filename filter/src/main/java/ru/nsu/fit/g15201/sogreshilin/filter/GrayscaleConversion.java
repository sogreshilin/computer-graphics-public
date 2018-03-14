package ru.nsu.fit.g15201.sogreshilin.filter;

import java.awt.*;
import java.awt.image.BufferedImage;
import ru.nsu.fit.g15201.sogreshilin.controller.FilterAppliedObserver;

public class GrayscaleConversion implements Filter {
    private static final double RED_WEIGHT = 0.299;
    private static final double GREEN_WEIGHT = 0.587;
    private static final double BLUE_WEIGHT = 0.114;
    private final FilterAppliedObserver observer;

    public GrayscaleConversion(FilterAppliedObserver observer) {
        this.observer = observer;
    }

    public BufferedImage apply(BufferedImage source) {
        BufferedImage destination = new BufferedImage(source.getWidth(), source.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < source.getWidth(); ++x) {
            for (int y = 0; y < source.getHeight(); ++y) {
                Color sourceColor = new Color(source.getRGB(x, y));
                int red = sourceColor.getRed();
                int green = sourceColor.getGreen();
                int blue = sourceColor.getBlue();

                int gray = (int) (RED_WEIGHT * red) +
                        (int) (GREEN_WEIGHT * green) +
                        (int) (BLUE_WEIGHT * blue);

                destination.setRGB(x, y, new Color(gray, gray, gray).getRGB());
            }
        }

        observer.onFilterApplied(destination);
        return destination;
    }
}
