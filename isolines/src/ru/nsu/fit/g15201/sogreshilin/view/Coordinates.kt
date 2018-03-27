package ru.nsu.fit.g15201.sogreshilin.view

import kotlin.math.round

class Coordinates(var width : Double, var height : Double) {
    companion object {
        const val INITIAL_WIDTH = 800.0
        //todo: remove constant
        const val INITIAL_HEIGHT = 300.0 - 39.0
    }

    var gridXCount : Int = 0
    var gridYCount : Int = 0

    private val padding = 10.0
    private val graphicsWeight = 0.8
    private val legendWeight = 1 - graphicsWeight

    val graphicsX0 : Double
        get() {
            return padding
        }

    val graphicsX1 : Double
        get() {
            return graphicsX0 + graphicsWidth
        }

    val graphicsY0 : Double
        get() {
            return padding
        }


    val graphicsY1 : Double
        get() {
            return graphicsY0 + graphicsHeight
        }

    val graphicsWidth : Double
        get() {
            return round(graphicsWeight * (width - 3 * padding))
        }

    val graphicsHeight : Double
        get() {
            return round(height - 2 * padding)
        }

    val legendX0 : Double
        get() {
            return round(width - legendWeight * (width - 3 * padding) - padding)
        }

    val legendY0 : Double
        get() {
            return graphicsY0
        }

    val legendX1 : Double
        get() {
            return legendX0 + legendWidth
        }

    val legendY1 : Double
        get() {
            return legendY0 + legendHeight
        }

    val legendWidth : Double
        get() {
            return round(legendWeight * (width - 3 * padding))
        }

    val legendHeight : Double
        get() {
            return graphicsHeight
        }

    val xGridCoordinates : List<Int>
        get() {
            return IntRange(0, gridXCount - 1).toList()
        }

    val yGridCoordinates : List<Int>
        get() {
            return IntRange(0, gridYCount - 1).toList()
        }

    val gridXUnitLength : Double

        get() {
            return if (gridXCount > 0) graphicsWidth / gridXCount else 0.0
        }

    val gridYUnitLength : Double

        get() {
            return if (gridXCount > 0) graphicsHeight / gridYCount else 0.0
        }
}