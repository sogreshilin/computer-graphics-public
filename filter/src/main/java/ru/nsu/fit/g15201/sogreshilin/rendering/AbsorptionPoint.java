package ru.nsu.fit.g15201.sogreshilin.rendering;

public class AbsorptionPoint {
    private int x;
    private double value;

    public AbsorptionPoint(int x, double value) {
        this.x = x;
        this.value = value;
    }

    public int getX() {
        return x;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("([%d], %.2f)", x, value);
    }
}
