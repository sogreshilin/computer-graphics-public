package ru.nsu.fit.g15201.sogreshilin.view

import kotlin.math.round

class Coordinates(var width : Double, var height : Double) {
    var gridXCount : Int = 0
    var gridYCount : Int = 0

    val padding = 10.0
    val minLegendColorMapWidth = 15.0

    private val graphicsWeight = 0.8
    private val legendWeight = 1 - graphicsWeight

    val graphicsX0 : Double
        get() = padding

    val graphicsX1 : Double
        get() = graphicsX0 + graphicsWidth

    val graphicsY0 : Double
        get() = padding

    val graphicsY1 : Double
        get() = graphicsY0 + graphicsHeight

    val graphicsWidth : Double
        get() = round(graphicsWeight * (width - 3 * padding))

    val graphicsHeight : Double
        get() = round(height - 2 * padding)

    val legendX0 : Double
        get() = round(width - legendWeight * (width - 3 * padding) - padding)

    val legendY0 : Double
        get() = graphicsY0

    val legendX1 : Double
        get() = legendX0 + legendWidth

    val legendY1 : Double
        get() = legendY0 + legendHeight

    val legendColorMapX0 : Double
        get() = legendX0 + padding

    val legendColorMapX1 : Double
        get() = legendColorMapX0 + legendColorMapWidth

    val legendColorMapY0 : Double
        get() = legendY0 + padding

    val legendColorMapY1 : Double
        get() = legendY1 - padding

    val legendColorMapHeight : Double
        get() = legendColorMapY1 - legendColorMapY0

    val legendColorMapWidth : Double
        get() = legendWidth - 2 * padding

    val legendWidth : Double
        get() = round(legendWeight * (width - 3 * padding))

    val legendHeight : Double
        get() = graphicsHeight

    val gridXUnitLength : Double
        get() = if (gridXCount > 0) graphicsWidth / gridXCount else 0.0

    val gridYUnitLength : Double
        get() = if (gridXCount > 0) graphicsHeight / gridYCount else 0.0
}