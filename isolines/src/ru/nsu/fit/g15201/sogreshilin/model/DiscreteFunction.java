package ru.nsu.fit.g15201.sogreshilin.model;

import java.util.ArrayList;
import ru.nsu.fit.g15201.sogreshilin.controller.Config;

import static java.lang.Math.*;

public class DiscreteFunction {

    private static final double EPS = 0.01;

    public Domain getDomain() {
        return domain;
    }

    public void setConfig(Config config) {
        setGrid(config.getGridX(), config.getGridY());
        setGridZ(config.getKeyValueCount());
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    private static class ContinuousFunction {
        static double valueAt(double x, double y) {
//            return x * x + y * y;
//            return -(y - 0.5) * abs(sin(3 * atan((y - 0.5) / (x - 0.5))));
//            return x * x * x * x - x * x + y * y;
            return cos(x) * sin(y);
        }
    }

    private double[][] values;
    private int gridX;
    private int gridY;
    private int gridZ;
    private double min;
    private double max;

    private Translator translatorX;
    private Translator translatorY;
    private Domain domain = new Domain(-10, 10, -6, 6);
    private ArrayList<RangeChangedObserver> observers = new ArrayList<>();

    public interface RangeChangedObserver {
        void onRangeChanged(double min, double max);
    }

    public void addRangeChangedObserver(RangeChangedObserver observer) {
        observers.add(observer);
    }

    private void notifyRangeChanged(double min, double max) {
        for (RangeChangedObserver observer : observers) {
            observer.onRangeChanged(min, max);
        }
    }

    public DiscreteFunction() {
    }

    private void countGridValuesAndUpdateMinMax() {
        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;
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
        notifyRangeChanged(min, max + getDz());
    }

    public int getChunk(double z, int chunkCount) {
        if (z > max) {
            return chunkCount;
        }
        if (z < min) {
            return 0;
        }
        return (int) new Translator(min, max, 0, chunkCount).translate(z);
    }

    public int getChunk(double x, double y, int chunkCount) {
        double z = ContinuousFunction.valueAt(x, y);
        return getChunk(z, chunkCount);
    }

    public double getDz() {
        return (max - min) / gridZ;
    }

    private void setTranslators() {
        translatorX = new Translator(0, gridX, domain.getX1(), domain.getX2());
        translatorY = new Translator(0, gridY, domain.getY1(), domain.getY2());
    }

    public double valueAt(double x, double y) {
        if (isInDomain(x, y)) {
            return ContinuousFunction.valueAt(x, y);
        } else {
            throw new IllegalArgumentException(
                    String.format("Point (%.02f, %.02f) is out of domain %s", x, y, domain));
        }
    }

    private boolean isInDomain(double x, double y) {
        return domain.getX1() <= x && x <= domain.getX2() &&
               domain.getY1() <= y && y <= domain.getY2();
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
        setTranslators();
        countGridValuesAndUpdateMinMax();
    }

    public void setGrid(int x, int y) {
        this.gridX = x;
        this.gridY = y;
        setTranslators();
        countGridValuesAndUpdateMinMax();
    }

    public void setGridX(int gridX) {
        setGrid(gridX, gridY);
    }

    public void setGridY(int gridY) {
        setGrid(gridX, gridY);
    }

    public void setGridZ(int gridZ) {
        this.gridZ = gridZ;
        notifyRangeChanged(min, max + getDz());
    }

    public ArrayList<Segment> getIsoline(double z) {
        ArrayList<Segment> segments = new ArrayList<>();

        for (int i = 0; i < gridX; ++i) {
            for (int j = 0; j < gridY; ++j) {
                ArrayList<Point> intersections = new ArrayList<>();
                double xLeft = translatorX.translate(i);
                double xRight = translatorX.translate(i + 1);
                double yBottom = translatorX.translate(j);
                double yTop = translatorX.translate(j + 1);
                double dx = xRight - xLeft;
                double dy = yTop - yBottom;

                double zLeftTop = values[j + 1][i];
                double zLeftBottom = values[j][i];
                double zRightTop = values[j + 1][i + 1];
                double zRightBottom = values[j][i + 1];

                if (doesIsolineCrossGrid(z, zLeftTop, zRightTop)) {
                    double x = dx * (z - zLeftTop) / (zRightTop - zLeftTop) + xLeft;
                    intersections.add(new Point(x, yTop));
                }

                if (doesIsolineCrossGrid(z, zLeftBottom, zRightBottom)) {
                    double x = dx * (z - zLeftBottom) / (zRightBottom - zLeftBottom) + xLeft;
                    intersections.add(new Point(x, yBottom));
                }

                if (doesIsolineCrossGrid(z, zLeftBottom, zLeftTop)) {
                    double y = dy * (z - zLeftBottom) / (zLeftTop - zLeftBottom) + yBottom;
                    intersections.add(new Point(xLeft, y));
                }

                if (doesIsolineCrossGrid(z, zRightBottom, zRightTop)) {
                    double y = dy * (z - zRightBottom) / (zRightTop - zRightBottom) + yBottom;
                    intersections.add(new Point(xRight, y));
                }

                if (intersections.size() == 2) {
                    double x1 = intersections.get(0).getX();
                    double y1 = intersections.get(0).getY();
                    double x2 = intersections.get(1).getX();
                    double y2 = intersections.get(1).getY();
                    segments.add(new Segment(x1, y1, x2, y2));
                }

                if (intersections.size() == 3) {
                    return getIsoline(z + EPS);
                }

                if (intersections.size() == 4) {
                    double x1 = intersections.get(0).getX();
                    double y1 = intersections.get(0).getY();
                    double x2 = intersections.get(1).getX();
                    double y2 = intersections.get(1).getY();
                    double x3 = intersections.get(2).getX();
                    double y3 = intersections.get(2).getY();
                    double x4 = intersections.get(3).getX();
                    double y4 = intersections.get(3).getY();

                    double signLeftTop = signum(zLeftTop - z);
                    double signCenter = signum(valueAt((xRight - xLeft) / 2, (yTop - yBottom) / 2));

                    if (signCenter == signLeftTop) {
                        segments.add(new Segment(x1, y1, x4, y4));
                        segments.add(new Segment(x2, y2, x3, y3));
                    } else {
                        segments.add(new Segment(x1, y1, x3, y3));
                        segments.add(new Segment(x2, y2, x4, y4));
                    }
                }
            }
        }
        return segments;
    }

    private boolean doesIsolineCrossGrid(double z, double zLow, double zHigh) {
        return zLow <= z && z <= zHigh || zHigh <= z && z <= zLow;
    }
}
