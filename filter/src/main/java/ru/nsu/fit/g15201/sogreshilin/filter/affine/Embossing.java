package ru.nsu.fit.g15201.sogreshilin.filter.affine;

import ru.nsu.fit.g15201.sogreshilin.controller.FilterAppliedObserver;

public class Embossing extends MatrixFilter {
    private static final double COEFFICIENT = 1.0;
    private static final Matrix MATRIX = Matrix.of(
            -1, -1,  0,
            -1,  0,  1,
             0,  1,  1
    );
    private static final int BIAS = 128;

    public Embossing(FilterAppliedObserver observer) {
        super(observer);
        setCoefficient(COEFFICIENT);
        setFilterMatrix(MATRIX);
        setBias(BIAS);
    }
}
