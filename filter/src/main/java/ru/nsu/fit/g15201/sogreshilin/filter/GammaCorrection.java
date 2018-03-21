package ru.nsu.fit.g15201.sogreshilin.filter;

import java.awt.*;
import java.awt.image.BufferedImage;
import ru.nsu.fit.g15201.sogreshilin.controller.FilterAppliedObserver;
import static java.lang.Math.*;

public class GammaCorrection implements Filter {
    public static final int MIN_GAMMA = 0;
    public static final int MAX_GAMMA = 100;
    public static final int DEFAULT_GAMMA = 25;

    private final FilterAppliedObserver observer;
    private double inverseGamma = 1.0 / 1.5;
    private double coefficient = pow(255, 1 - inverseGamma);
    private int[] palette = new int[256];

    public GammaCorrection(FilterAppliedObserver observer) {
        this.observer = observer;
    }

    @Override
    public BufferedImage apply(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage filteredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                Color color = new Color(image.getRGB(x, y));
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();
                filteredImage.setRGB(x, y, new Color(palette[red], palette[green], palette[blue]).getRGB());
            }
        }

        if (observer != null) {
            observer.onFilterApplied(filteredImage);
        }
        return filteredImage;
    }

    private int correct(int color) {
        return ColorUtils.truncate((int) round(coefficient * pow(color, inverseGamma)));
    }

    public void setGamma(double gamma) {
        this.inverseGamma = 1.0 / gamma;
        this.coefficient = pow(255, 1 - inverseGamma);
        for (int i = 0; i < 256; ++i) {
            palette[i] = correct(i);
        }
    }

    public void setGamma(int gamma) {
        setGamma(gamma / 10.0);
    }
}

