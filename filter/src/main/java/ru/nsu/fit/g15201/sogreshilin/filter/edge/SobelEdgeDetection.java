package ru.nsu.fit.g15201.sogreshilin.filter.edge;

import java.awt.*;
import java.awt.image.BufferedImage;
import ru.nsu.fit.g15201.sogreshilin.controller.FilterAppliedObserver;
import ru.nsu.fit.g15201.sogreshilin.filter.ColorUtils;
import ru.nsu.fit.g15201.sogreshilin.filter.affine.Matrix;

public class SobelEdgeDetection implements EdgeDetection {
    private static final Matrix verticalFilterMatrix = Matrix.of(
            -1, 0, 1,
            -2, 0, 2,
            -1, 0, 1
    );

    private static final Matrix horizontalFilterMatrix = Matrix.of(
            1,  2,  1,
            0,  0,  0,
            -1, -2, -1
    );

    private static final double coefficient = 0.25;

    private static final int MAX_SUM = 3 * 255;
    private final FilterAppliedObserver observer;

    private int threshold = DEFAULT_THRESHOLD;
    private boolean inverted = false;

    public SobelEdgeDetection(FilterAppliedObserver observer) {
        this.observer = observer;
    }

    public BufferedImage apply(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage destination = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int black = inverted ? Color.WHITE.getRGB() : Color.BLACK.getRGB();
        int white = inverted ? Color.BLACK.getRGB() : Color.WHITE.getRGB();
        int currentThreshold = MAX_SUM * threshold / THRESHOLD_LENGTH;

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int left = calculateLeft(image, x, y);
                int right = calculateRight(image, x, y);

                int rgb = left + right;


                int sum = ((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF);
                int destinationColor = sum >= currentThreshold ? white : black;
                destination.setRGB(x, y, destinationColor);
            }
        }
        observer.onFilterApplied(destination);
        return destination;
    }

    private int calculateRight(BufferedImage image, int x, int y) {
        int mostRight = image.getWidth() - 1;
        int mostHigh = image.getHeight() - 1;
        int xLeft = (x == 0) ? x : x - 1;
        int xRight = (x == mostRight) ? x : x + 1;
        int yBelow = (y == 0) ? y : y - 1;
        int yAbove = (y == mostHigh) ? y : y + 1;
        int[] weights = new int[] { 1, 2, 1, -1, -2, -1 };
        int[] xs = new int[] { xLeft, x, xRight, xLeft, x, xRight };
        int[] ys = new int[] { yAbove, yAbove, yAbove, yBelow, yBelow, yBelow};
        return calculateSum(image, xs, ys, weights);
    }

    private int calculateLeft(BufferedImage image, int x, int y) {
        int mostRight = image.getWidth() - 1;
        int mostHigh = image.getHeight() - 1;
        int xLeft = (x == 0) ? x + 1 : x - 1;
        int xRight = (x == mostRight) ? mostRight - 1 : x + 1;
        int yBelow = (y == 0) ? y + 1 : y - 1;
        int yAbove = (y == mostHigh) ? mostHigh - 1 : y + 1;
        int[] weights = new int[] { -1, 1, -2, 2, -1, 1 };
        int[] xs = new int[] { xLeft, xRight, xLeft, xRight, xLeft, xRight };
        int[] ys = new int[] { yAbove, yAbove, y, y, yBelow, yBelow};
        return calculateSum(image, xs, ys, weights);
    }

    private int calculateSum(BufferedImage image, int[] xs, int[] ys, int[] weights) {
        int[] sum = new int[] {0, 0, 0};
        for (int k = 0; k < 3; ++k) {
            for (int i = 0; i < 6; ++i) {
                sum[k] += weights[i] * ColorUtils.getColorComponentAt(k, image, xs[i], ys[i]);
            }
        }
        for (int k = 0; k < 3; ++k) {
            sum[k] /= 4;
            sum[k] = ColorUtils.cut(sum[k]);
        }
        return new Color(sum[2], sum[1], sum[0]).getRGB();
    }


    @Override
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public void setInverted(boolean value) {
        this.inverted = value;
    }
}
