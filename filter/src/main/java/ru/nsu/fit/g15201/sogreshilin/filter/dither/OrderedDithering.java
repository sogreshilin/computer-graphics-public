package ru.nsu.fit.g15201.sogreshilin.filter.dither;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.stream.Collectors;
import ru.nsu.fit.g15201.sogreshilin.controller.FilterAppliedObserver;
import ru.nsu.fit.g15201.sogreshilin.filter.Filter;

import static java.lang.Math.*;

public class OrderedDithering implements Dithering {
    private static final int DEFAULT_SIZE = 8;
    private static final int[][] PATTERN = { { 0, 2 }, { 3, 1 } };

    private final FilterAppliedObserver observer;
    private int[][] matrix = PATTERN;
    private int[] redPalette = new int[256];
    private int[] greenPalette = new int[256];
    private int[] bluePalette = new int[256];

    public OrderedDithering(FilterAppliedObserver observer) {
        this.observer = observer;
        setMatrixSize(DEFAULT_SIZE);
    }

    public int[] buildPalette(int grads) {
        int[] palette = new int[grads];

        double len = (double) 255 / (grads - 1);
        for (int i = 0; i < grads; ++i) {
            palette[i] = (int) round(i * len);
        }

        return palette;
    }

    public void setLevels(int redLevels, int greenLevels, int blueLevels) {
        redPalette = buildPalette(redLevels);
        greenPalette = buildPalette(greenLevels);
        bluePalette = buildPalette(blueLevels);
    }

    public void setMatrixSize(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Ordered dithering matrix size " +
                    "has to be a positive number");
        }

        if (Integer.bitCount(size) != 1) {
            throw new IllegalArgumentException("Ordered dithering matrix size " +
                    "has to be a power of two");
        }

        if (matrix.length > size) {
            matrix = PATTERN;
        }

        while (matrix.length != size) {
            matrix = doubleMatrix(matrix);
        }
    }

    private int[][] doubleMatrix(int[][] matrix) {
        int len = matrix.length;
        int[][] newMatrix = new int[2 * len][2 * len];

        int[] xs = new int[] { 0, len, 0, len };
        int[] ys = new int[] { 0, 0, len, len };
        int[] shifts = new int[] { 0, 2, 3, 1 };

        for (int quadrant = 0; quadrant < 4; ++quadrant) {
            for (int i = 0; i < len; ++i) {
                for (int j = 0; j < len; ++j) {
                    int x = xs[quadrant] + i;
                    int y = ys[quadrant] + j;
                    int shift = shifts[quadrant];
                    newMatrix[x][y] = 4 * matrix[i][j] + shift;
                }
            }
        }

        return newMatrix;
    }

    @Override
    public BufferedImage apply(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage filteredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int oldMax = 255;
        int newMax = matrix.length * matrix.length - 1;

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                Color color = new Color(image.getRGB(x, y));
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                int i = x % matrix.length;
                int j = y % matrix.length;

                int a = matrix[i][j];

                int newRed = newMax * red >= oldMax * a
                        ? nextColorFromPalette(red, redPalette)
                        : previousColorFromPalette(red, redPalette);

                int newGreen = newMax * green >= oldMax * a
                        ? nextColorFromPalette(green, greenPalette)
                        : previousColorFromPalette(green, greenPalette);

                int newBlue = newMax * blue >= oldMax * a
                        ? nextColorFromPalette(blue, bluePalette)
                        : previousColorFromPalette(blue, bluePalette);

                filteredImage.setRGB(x, y, new Color(newRed, newGreen, newBlue).getRGB());
            }
        }
        observer.onFilterApplied(filteredImage);
        return filteredImage;

    }

    private int previousColorFromPalette(int currentColor, int[] palette) {
        int k = 0;
        while (palette[k] < currentColor) {
            k++;
        }
        return k == 0 ? 0 : palette[k - 1];
    }

    private int nextColorFromPalette(int currentColor, int[] palette) {
        int k = 0;
        while (palette[k] < currentColor) {
            k++;
        }
        return palette[k];
    }
}
