package ru.nsu.fit.g15201.sogreshilin.rendering;

public class EmissionPoint {
    private int x;
    private int red;
    private int green;
    private int blue;

    public EmissionPoint(int x, int red, int green, int blue) {
        this.x = x;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public int getX() {
        return x;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    @Override
    public String toString() {
        return String.format("([%d], %3d %3d %3d)", x, red, green, blue);
    }
}
