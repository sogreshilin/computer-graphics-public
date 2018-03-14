package ru.nsu.fit.g15201.sogreshilin.filter.affine;

public class Matrix {

    private double[][] matrix;
    private int radius;

    private Matrix(double[][] matrix) {
        this.matrix = matrix;
        this.radius = matrix.length / 2;
    }

    public static Matrix of(double... args) {
        double rawSize = Math.sqrt(args.length);
        int size = (int) rawSize;
        boolean isInteger = rawSize - size == 0;
        if (!isInteger) {
            throw new IllegalArgumentException("Square matrix cannot be built from " +
                    args.length + " elements");
        }

        boolean isOdd = (size & 1) == 1;
        if (!isOdd) {
            throw new IllegalArgumentException("Invalid kernel size: " +
                    args.length + ". Has to be odd number");
        }

        double matrix[][] = new double[size][size];
        for (int i = 0; i < args.length; ++i) {
            matrix[i / size][i % size] = args[i];
        }
        return new Matrix(matrix);
    }

    public int getRadius() {
        return radius;
    }

    public double at(int i, int j) {
        return matrix[radius + i][radius + j];
    }
}
