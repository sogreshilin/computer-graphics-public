package ru.nsu.fit.g15201.sogreshilin.filter.affine;

import ru.nsu.fit.g15201.sogreshilin.controller.FilterAppliedObserver;

public class AntiAliasing extends MatrixFilter {
    private static final double COEFFICIENT = 1.0 / 9.0;
    private static final Matrix MATRIX = Matrix.of(
            1, 1, 1,
            1, 1, 1,
            1, 1, 1
    );

    public AntiAliasing(FilterAppliedObserver observer) {
        super(observer);
        setCoefficient(COEFFICIENT);
        setFilterMatrix(MATRIX);
    }
}
