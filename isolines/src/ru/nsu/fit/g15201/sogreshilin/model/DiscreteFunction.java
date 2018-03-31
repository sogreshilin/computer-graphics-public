package ru.nsu.fit.g15201.sogreshilin.model;

import java.util.ArrayList;
import ru.nsu.fit.g15201.sogreshilin.controller.Config;

import static java.lang.Math.*;

public class DiscreteFunction {
    public static class ContinuousFunction {
        static double valueAt(double x, double y) {
            return -(y - 0.5) * abs(sin(3 * atan((y - 0.5) / (x - 0.5))));
        }
    }

    private static final double EPS = 0.01;

    private int gridX;
    private int gridY;
    private double[][] values;
    private Translator translatorX;
    private Translator translatorY;
    private Domain domain = Domain.DEFAULT_DOMAIN;

    private ArrayList<RangeChangedObserver> rangeChangedObservers = new ArrayList<>();

    public interface RangeChangedObserver {
        void onRangeChanged(double min, double max);
    }

    public void addRangeChangedObserver(RangeChangedObserver observer) {
        rangeChangedObservers.add(observer);
    }

    public void setConfig(Config config) {
        this.domain = config.getDomain();
        this.gridX = config.getGridX();
        this.gridY = config.getGridY();
        setTranslators();
        countGridValuesAndUpdateMinMax();
    }

    public double valueAt(double x, double y) {
        if (isInDomain(x, y)) {
            return ContinuousFunction.valueAt(x, y);
        } else {
            throw new IllegalArgumentException(
                    String.format("Point (%.02f, %.02f) is out of domain %s", x, y, domain));
        }
    }

    public ArrayList<Segment> getIsoline(double z) {
        ArrayList<Segment> segments = new ArrayList<>();

        for (int i = 0; i < gridX; ++i) {
            for (int j = 0; j < gridY; ++j) {
                ArrayList<Point> intersects = new ArrayList<>();
                double xLeft = translatorX.translate(i);
                double xRight = translatorX.translate(i + 1);
                double yBottom = translatorY.translate(j);
                double yTop = translatorY.translate(j + 1);
                double dx = xRight - xLeft;
                double dy = yTop - yBottom;

                double zLeftTop = values[j + 1][i];
                double zLeftBottom = values[j][i];
                double zRightTop = values[j + 1][i + 1];
                double zRightBottom = values[j][i + 1];

                if (doesIsolineCrossGrid(z, zLeftTop, zRightTop)) {
                    double x = dx * (z - zLeftTop) / (zRightTop - zLeftTop) + xLeft;
                    intersects.add(new Point(x, yTop));
                }

                if (doesIsolineCrossGrid(z, zLeftBottom, zRightBottom)) {
                    double x = dx * (z - zLeftBottom) / (zRightBottom - zLeftBottom) + xLeft;
                    intersects.add(new Point(x, yBottom));
                }

                if (doesIsolineCrossGrid(z, zLeftBottom, zLeftTop)) {
                    double y = dy * (z - zLeftBottom) / (zLeftTop - zLeftBottom) + yBottom;
                    intersects.add(new Point(xLeft, y));
                }

                if (doesIsolineCrossGrid(z, zRightBottom, zRightTop)) {
                    double y = dy * (z - zRightBottom) / (zRightTop - zRightBottom) + yBottom;
                    intersects.add(new Point(xRight, y));
                }

                switch (intersects.size()) {
                    case 0:
                        break;
                    case 2: {
                        segments.add(new Segment(intersects.get(0), intersects.get(1)));
                        break;
                    }
                    case 4: {
                        double signCenter = signum(zLeftTop + zLeftBottom + zRightTop + zRightBottom);
                        if (signCenter == signum(zLeftTop - z)) {
                            segments.add(new Segment(intersects.get(0), intersects.get(3)));
                            segments.add(new Segment(intersects.get(1), intersects.get(2)));
                        } else {
                            segments.add(new Segment(intersects.get(0), intersects.get(2)));
                            segments.add(new Segment(intersects.get(1), intersects.get(3)));
                        }
                        break;
                    }
                    default:
                        return getIsoline(z + EPS);
                }
            }
        }
        return segments;
    }

    private void notifyRangeChanged(double min, double max) {
        for (RangeChangedObserver observer : rangeChangedObservers) {
            observer.onRangeChanged(min, max);
        }
    }

    private void countGridValuesAndUpdateMinMax() {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        values = new double[gridY + 1][gridX + 1];
        for (int j = 0; j <= gridY; ++j) {
            for (int i = 0; i <= gridX; ++i) {
                double x = translatorX.translate(i);
                double y = translatorY.translate(j);
                values[j][i] = ContinuousFunction.valueAt(x, y);
                min = min(min, values[j][i]);
                max = max(max, values[j][i]);
            }
        }
        notifyRangeChanged(min, max);
    }

    private void setTranslators() {
        translatorX = new Translator(0, gridX, domain.getX1(), domain.getX2());
        translatorY = new Translator(0, gridY, domain.getY1(), domain.getY2());
    }

    private boolean isInDomain(double x, double y) {
        return domain.getX1() <= x && x <= domain.getX2() &&
               domain.getY1() <= y && y <= domain.getY2();
    }

    private boolean doesIsolineCrossGrid(double z, double zLow, double zHigh) {
        return zLow <= z && z <= zHigh || zHigh <= z && z <= zLow;
    }
}
