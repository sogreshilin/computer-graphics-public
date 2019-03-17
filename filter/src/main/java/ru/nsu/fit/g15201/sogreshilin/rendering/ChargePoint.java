package ru.nsu.fit.g15201.sogreshilin.rendering;

public class ChargePoint {
    private double x;
    private double y;
    private double z;
    private double value;

    public ChargePoint(double x, double y, double z, double value) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.value = value;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("([%.1f, %.1f, %.1f], %.2f)", x, y, z, value);
    }
}
