package ru.nsu.fit.g15201.sogreshilin.model

import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.sin

class Function(var domain : Domain) {
    var zRange = Pair(Double.MAX_VALUE, Double.MIN_VALUE)

    fun recalculateRange(step: Double) {
        var (zMin, zMax) = zRange

        val (xMin, xMax) = domain.xDomain
        val (yMin, yMax) = domain.yDomain
        var x = xMin
        while (x <= xMax) {
            var y = yMin
            while (y <= yMax) {
                val z = invoke(x, y)
                if (z < xMin) {
                    zMin = z
                }
                if (z > xMax) {
                    zMax = z
                }
                y += step
            }
            x += step
        }
    }

    operator fun invoke(points : List<Pair<Double, Double>>) : List<Double> {
        return points
                .map { (x, y) -> invoke(x, y) }
                .sorted()
    }

    operator fun invoke(x: Double, y: Double): Double {
        return -(y - 0.5) * abs(sin(3 * atan((y - 0.5) / (x - 0.5))))
    }


}