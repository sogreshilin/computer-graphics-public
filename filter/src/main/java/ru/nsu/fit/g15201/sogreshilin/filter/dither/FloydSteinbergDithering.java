package ru.nsu.fit.g15201.sogreshilin.filter.dither;

import java.awt.*;
import java.awt.image.BufferedImage;
import ru.nsu.fit.g15201.sogreshilin.controller.FilterAppliedObserver;
import ru.nsu.fit.g15201.sogreshilin.filter.ColorUtils;

import static java.lang.Math.round;


public class FloydSteinbergDithering implements Dithering {

    private final FilterAppliedObserver observer;

    private int[] redPalette = new int[256];
    private int[] greenPalette = new int[256];
    private int[] bluePalette = new int[256];

    public FloydSteinbergDithering(FilterAppliedObserver observer) {
        this.observer = observer;
    }

    private int[] buildPalette(int grads) {
        int[] palette = new int[256];

        double len = (double) 255 / (grads - 1);
        int halfLen = (int) round(len / 2);

        int k = 0;
        int color = (int) round(k * len);

        for (int i = 0; i < 256; ++i) {
            if (i >= color + halfLen) {
                k++;
                color = (int) round(k * len);
            }
            palette[i] = color;
        }

        return palette;
    }

    public void setLevels(int redLevels, int greenLevels, int blueLevels) {
        redPalette = buildPalette(redLevels);
        greenPalette = buildPalette(greenLevels);
        bluePalette = buildPalette(blueLevels);
    }

    @Override
    public BufferedImage apply(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[][][] errors = new int[width][height][3];

        BufferedImage filteredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                int rgb = image.getRGB(i, j);
                int r = ((rgb >> 16) & 0xFF) + errors[i][j][2];
                int g = ((rgb >> 8) & 0xFF)  + errors[i][j][1];
                int b = (rgb & 0xFF) + errors[i][j][0];

                int rNew = redPalette[ColorUtils.truncate(r)];
                int gNew = greenPalette[ColorUtils.truncate(g)];
                int bNew = bluePalette[ColorUtils.truncate(b)];

                Color destination = new Color(rNew, gNew, bNew);
                filteredImage.setRGB(i, j, destination.getRGB());

                int rError = r - rNew;
                int gError = g - gNew;
                int bError = b - bNew;

                spreadError(errors, i, j, rError, gError, bError);
            }
        }
        observer.onFilterApplied(filteredImage);
        return filteredImage;
    }

    private void spreadError(int[][][] errors, int x, int y, int rError, int gError, int bError) {
        int height = errors[0].length;
        int width = errors.length;

        int xLeft = x == 0 ? x + 1 : x - 1;
        int xRight = x == width - 1 ? width - 2 : x + 1;
        int yBelow = y == height - 1 ? height - 2 : y + 1;

        int[] xs = new int[] { xRight, xLeft, x, xRight };
        int[] ys = new int[] { y, yBelow, yBelow, yBelow };
        int[] weights = new int[] { 7, 3, 5, 1 };
        int coefficient = 16;

        for (int i = 0; i < 4; ++i) {
            errors[xs[i]][ys[i]][2] += rError * weights[i] / coefficient;
            errors[xs[i]][ys[i]][1] += gError * weights[i] / coefficient;
            errors[xs[i]][ys[i]][0] += bError * weights[i] / coefficient;
        }

    }
}