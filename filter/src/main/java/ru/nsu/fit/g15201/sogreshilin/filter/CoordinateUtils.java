package ru.nsu.fit.g15201.sogreshilin.filter;

import static java.lang.Math.*;

public class CoordinateUtils {
    public static int reflectIfOutside(int coordinate, int upper) {
        return abs(coordinate) % upper;
    }
}
