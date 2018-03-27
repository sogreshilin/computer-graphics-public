package ru.nsu.fit.g15201.sogreshilin.model;

public class Translator {
    private double fromLeft;
    private double fromRight;
    private double toLeft;
    private double toRight;

    public Translator(double fromLeft, double fromRight, double toLeft, double toRight) {
        this.fromLeft = fromLeft;
        this.fromRight = fromRight;
        this.toLeft = toLeft;
        this.toRight = toRight;
    }

    public double translate(double x) {
        return (x - fromLeft) / (fromRight - fromLeft) * (toRight - toLeft) + toLeft;
    }

    public double inverseTranslate(double x) {
        return (x - toLeft) / (toRight - toLeft) * (fromRight - fromLeft) + fromLeft;
    }

    public void setFromLeft(double fromLeft) {
        this.fromLeft = fromLeft;
    }

    public void setFromRight(double fromRight) {
        this.fromRight = fromRight;
    }

    public void setToLeft(double toLeft) {
        this.toLeft = toLeft;
    }

    public void setToRight(double toRight) {
        this.toRight = toRight;
    }

    @Override
    public String toString() {
        return String.format("[%.2f, %.2f]*[%.2f, %.2f]", fromLeft, fromRight, toLeft, toRight);
    }
}
