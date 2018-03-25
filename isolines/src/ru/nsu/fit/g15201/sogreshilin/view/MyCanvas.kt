package ru.nsu.fit.g15201.sogreshilin.view

import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeLineJoin
import ru.nsu.fit.g15201.sogreshilin.controller.Controller
import ru.nsu.fit.g15201.sogreshilin.model.Translator
import ru.nsu.fit.g15201.sogreshilin.model.Domain


class MyCanvas(private val controller : Controller) : Canvas(800.0, 600.0 - 39.0) {
    var coordinates : Coordinates = Coordinates(width, height)

    fun onWidthChanged(value : Double) {
        coordinates.width = value
    }

    fun onHeightChanged(value : Double) {
        //todo: remove constant
        coordinates.height = value - 39.0
    }

    override fun isResizable(): Boolean {
        return true
    }

    fun redraw() {
        val context = graphicsContext2D
        context.clearRect(0.0, 0.0, width, height);
        drawColorMap(context)
        drawBorders(context)
        drawColorMapBorder(context)
        drawLegendBorder(context)
        drawGrid(context)
    }

    private fun drawColorMap(context: GraphicsContext?) {
        val fromDomain = Domain(Pair(0.0, coordinates.graphicsWidth), Pair(0.0, coordinates.graphicsHeight))
        val toDomain = controller.function.domain
        val translator = Translator(fromDomain, toDomain)

        for (i in 0 until coordinates.graphicsWidth.toInt()) {
            for (j in 0 until coordinates.graphicsHeight.toInt()) {
                val xPixel = i + coordinates.graphicsX0.toInt()
                val yPixel = j + coordinates.graphicsY0.toInt()
                val coordinates = translator.translate(Pair(i.toDouble(), j.toDouble()))
                val color = controller.colorAt(coordinates)
                context?.pixelWriter?.setColor(xPixel, yPixel, color)
            }
        }
    }

    private fun drawGrid(context: GraphicsContext?) {
        context?.stroke = Color.GRAY
        context?.lineWidth = 1.0
        context?.setLineDashes(5.0)
        context?.lineJoin = StrokeLineJoin.ROUND

        var x = coordinates.graphicsX0
        for (i in 1 until coordinates.gridXCount) {
            x += coordinates.gridXUnitLength
            context?.strokeLine(x, coordinates.graphicsY0, x, coordinates.graphicsY1)
        }

        var y = coordinates.graphicsY0
        for (i in 1 until coordinates.gridYCount) {
            y += coordinates.gridYUnitLength
            context?.strokeLine(coordinates.graphicsX0, y, coordinates.graphicsX1, y)
        }
    }

    private fun drawLegendBorder(context: GraphicsContext?) {
        context?.stroke = Color.BLACK
        context?.lineWidth = 1.0
        context?.setLineDashes()
        context?.strokeRect(
                coordinates.legendX0, coordinates.legendY0,
                coordinates.legendWidth, coordinates.legendHeight
        )
    }

    private fun drawColorMapBorder(context: GraphicsContext?) {
        context?.stroke = Color.BLACK
        context?.lineWidth = 1.0
        context?.setLineDashes()
        context?.strokeRect(
                coordinates.graphicsX0, coordinates.graphicsY0,
                coordinates.graphicsWidth, coordinates.graphicsHeight
        )
    }

    private fun drawBorders(context: GraphicsContext?) {
        context?.stroke = Color.BLACK
        context?.lineWidth = 1.0
        context?.setLineDashes()
        context?.strokeRect(0.5, 0.5, coordinates.width - 1, coordinates.height - 1)
    }
}