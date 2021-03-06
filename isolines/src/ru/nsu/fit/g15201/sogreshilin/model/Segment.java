package ru.nsu.fit.g15201.sogreshilin.model;

public class Segment {
    private double x1;
    private double y1;
    private double x2;
    private double y2;

    public Segment(Point point1, Point point2) {
        this.x1 = point1.getX();
        this.y1 = point1.getY();
        this.x2 = point2.getX();
        this.y2 = point2.getY();
    }

    public Segment(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public double getX1() {
        return x1;
    }

    public double getY1() {
        return y1;
    }

    public double getX2() {
        return x2;
    }

    public double getY2() {
        return y2;
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)->(%.2f, %.2f)", x1, y1, x2, y2);
    }
}
