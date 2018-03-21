package ru.nsu.fit.g15201.sogreshilin.rendering;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import ru.nsu.fit.g15201.sogreshilin.controller.FilterAppliedObserver;
import ru.nsu.fit.g15201.sogreshilin.filter.Filter;
import static java.lang.Math.*;
import static ru.nsu.fit.g15201.sogreshilin.filter.ColorUtils.*;

public class VolumeRendering implements Filter {
    public static final int MIN_VOXEL_COUNT = 2;
    public static final int MAX_VOXEL_COUNT = 350;
    private final FilterAppliedObserver observer;
    private final Config config;
    private static final int COUNT = 100;
    private int[] emissionRed = new int[COUNT];
    private int[] emissionGreen = new int[COUNT];
    private int[] emissionBlue = new int[COUNT];
    private double[] absorption = new double[COUNT];
    private int xLayers = 200;
    private int yLayers = 200;
    private int zLayers = 200;

    private double maxScalarFunctionValue = -Double.MAX_VALUE;
    private double minScalarFunctionValue = Double.MAX_VALUE;
    private boolean emissionEnabled;
    private boolean absorptionEnabled;

    public VolumeRendering(FilterAppliedObserver observer, Config config) {
        this.observer = observer;
        this.config = config;
        fillEmissionPalette();
        fillAbsorptionPalette();
    }

    private void fillAbsorptionPalette() {
        int k = 0;
        int currentX = config.getAbsorption().get(k).getX();
        int previousX = 0;

        double currentY = config.getAbsorption().get(k).getValue();
        double previousY = 0;

        for (int i = 0; i < COUNT; ++i) {
            while (i == currentX) {
                ++k;
                previousX = currentX;
                currentX = config.getAbsorption().get(k).getX();
                previousY = currentY;
                currentY = config.getAbsorption().get(k).getValue();
                absorption[i] = config.getAbsorption().get(k).getValue();
            }
            if (i < currentX) {
                double y = (currentY - previousY) / (currentX - previousX) * (i - previousX) + previousY;
                absorption[i] = y;
            }
        }
    }

    private void fillEmissionPalette() {
        int k = 0;
        //todo k = 0 length = 0
        int currentX = config.getEmission().get(k).getX();
        int previousX = 0;

        int currentRed = config.getEmission().get(k).getRed();
        int previousRed = 0;

        int currentGreen = config.getEmission().get(k).getGreen();
        int previousGreen = 0;

        int currentBlue = config.getEmission().get(k).getBlue();
        int previousBlue = 0;

        for (int i = 0; i < COUNT; ++i) {
            while (i == currentX) {
                ++k;
                previousX = currentX;
                currentX = config.getEmission().get(k).getX();
                previousRed = currentRed;
                currentRed = config.getEmission().get(k).getRed();
                previousGreen = currentGreen;
                currentGreen = config.getEmission().get(k).getGreen();
                previousBlue = currentBlue;
                currentBlue = config.getEmission().get(k).getBlue();
                emissionRed[i] = config.getEmission().get(k).getRed();
                emissionBlue[i] = config.getEmission().get(k).getBlue();
                emissionGreen[i] = config.getEmission().get(k).getGreen();
            }
            if (i < currentX) {
                int red = (int) round((double) (currentRed - previousRed) / (currentX - previousX) * (i - previousX) + previousRed);
                int blue = (int) round((double) (currentBlue - previousBlue) / (currentX - previousX) * (i - previousX) + previousBlue);
                int green = (int) round((double) (currentGreen - previousGreen) / (currentX - previousX) * (i - previousX) + previousGreen);
                emissionRed[i] = red;
                emissionBlue[i] = blue;
                emissionGreen[i] = green;
            }
        }
    }

    public void setLayers(int xAxis, int yAxis, int zAxis) {
        xLayers = xAxis;
        yLayers = yAxis;
        zLayers = zAxis;
    }

    public void setXLayers(int count) {
        xLayers = count;
    }

    public void setYLayers(int count) {
        yLayers = count;
    }

    public void setZLayers(int count) {
        zLayers = count;
    }

    private double countScalarFunctionAt(int voxelX, int voxelY, int voxelZ) {
        double sum = 0;
        for (ChargePoint point: config.getCharge()) {
            double chargeX = point.getX();
            double chargeY = point.getY();
            double chargeZ = point.getZ();
            double x = ((double) voxelX + 0.5) / (xLayers);
            double y = ((double) voxelY + 0.5) / (yLayers);
            double z = ((double) voxelZ + 0.5) / (zLayers);

            double value = point.getValue();
            double distance = countDistance(x, y, z, chargeX, chargeY, chargeZ);
            sum += value / distance;
        }
        return sum;
    }

    public void findMinAndMaxValues() {
        for (int x = 0; x < xLayers; ++x) {
            for (int y = 0; y < yLayers; ++y) {
                for (int z = 0; z < zLayers; ++z) {
                    double value = countScalarFunctionAt(x, y, z);
                    if (value > maxScalarFunctionValue) {
                        maxScalarFunctionValue = value;
                    }
                    if (value < minScalarFunctionValue) {
                        minScalarFunctionValue = value;
                    }
                }
            }
        }
    }

    private double countDistance(double... args) {
        if ((args.length & 1) == 1) {
            throw new IllegalArgumentException("Coordinates number has to be even");
        }

        double sum = 0;
        for (int i = 0; i < args.length / 2; ++i) {
            sum += (args[i] - args[args.length / 2 + i]) * (args[i] - args[args.length / 2 + i]);
        }

        return sum < 0.01 ? 0.1 : sqrt(sum);
    }

    @Override
    public BufferedImage apply(BufferedImage image) {
        if (!absorptionEnabled && !emissionEnabled) {
            if (observer != null) {
                observer.onFilterApplied(image);
            }
            return image;
        }
        findMinAndMaxValues();

        int width = image.getWidth();
        int height = image.getHeight();
        double xVoxelLength = (double) width / xLayers;
        double yVoxelLength = (double) height / yLayers;
        double dz = max(width, height) / min(xVoxelLength, yVoxelLength);
        double scalarFunctionRangeLength = maxScalarFunctionValue - minScalarFunctionValue;
        BufferedImage filteredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                Color color = new Color(image.getRGB(x, y));
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                int voxelX = (int) (x / xVoxelLength);
                int voxelY = (int) (y / yVoxelLength);

                for (int voxelZ = 0; voxelZ < zLayers; ++voxelZ) {
                    double valueAtVoxel = countScalarFunctionAt(voxelX, voxelY, voxelZ);
                    int paletteX = (int) round((valueAtVoxel - minScalarFunctionValue) / scalarFunctionRangeLength * (COUNT - 1));
                    double absorptionValue = exp(-absorption[paletteX] / dz);
                    double newRed = red;
                    double newGreen = green;
                    double newBlue = blue;
                    if (absorptionEnabled) {
                        newRed *= absorptionValue;
                        newGreen *= absorptionValue;
                        newBlue *= absorptionValue;
                    }
                    if (emissionEnabled) {
                        newRed += (double) emissionRed[paletteX] / dz;
                        newGreen += (double) emissionGreen[paletteX] / dz;
                        newBlue += (double) emissionBlue[paletteX] / dz;
                    }

                    red   = (int) round(newRed);
                    green = (int) round(newGreen);
                    blue  = (int) round(newBlue);
                }
                filteredImage.setRGB(x, y, new Color(truncate(red), truncate(green), truncate(blue)).getRGB());
            }
        }

        if (observer != null) {
            observer.onFilterApplied(filteredImage);
        }
        return filteredImage;
    }

    public void setEmissionEnabled(boolean emissionEnabled) {
        this.emissionEnabled = emissionEnabled;
    }

    public void setAbsorptionEnabled(boolean absorptionEnabled) {
        this.absorptionEnabled = absorptionEnabled;
    }
}
