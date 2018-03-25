package ru.nsu.fit.g15201.sogreshilin.controller

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import ru.nsu.fit.g15201.sogreshilin.model.Domain
import ru.nsu.fit.g15201.sogreshilin.model.Function
import ru.nsu.fit.g15201.sogreshilin.model.Translator
import ru.nsu.fit.g15201.sogreshilin.view.MyCanvas


class Controller {
    @FXML private lateinit var root : VBox
    @FXML private lateinit var pane : BorderPane
    @FXML lateinit var canvas : MyCanvas

    var function = Function(Domain(Pair(-10.0, 10.0), Pair(-6.0, 6.0)))
    private lateinit var zLowerBounds : List<Double>
    private var config = Config()

    fun initializeCanvas() {
        canvas = MyCanvas(this)
        pane.center = canvas
        canvas.widthProperty().bind(root.widthProperty())
        canvas.heightProperty().bind(root.heightProperty())
        canvas.widthProperty().addListener({ _, _, newValue -> canvas.onWidthChanged(newValue as Double) })
        canvas.heightProperty().addListener({ _, _, newValue -> canvas.onHeightChanged(newValue as Double) })
        calculateFunctionAtGridNodes()
        redrawCanvas()
    }

    fun redrawCanvas() {
        canvas.redraw()
    }

    private fun calculateFunctionAtGridNodes() {
        val fromDomain = Domain(Pair(0.0, config.grid.first.toDouble()), Pair(0.0, config.grid.second.toDouble()))
        val toDomain = function.domain
        val translator = Translator(fromDomain, toDomain)

        val xs = canvas.coordinates.xGridCoordinates.map { x -> translator.translateX(x.toDouble()) }
        val ys = canvas.coordinates.yGridCoordinates.map { y -> translator.translateY(y.toDouble()) }
        val zs = function(xs * ys)
        zLowerBounds = zs.chunked(zs.size / config.keyValueCount).map { list -> list.min() ?: Double.NaN }
    }

    // todo: check this method
    private fun colorAt(x : Double, y : Double) : Color {
        val zValue = function(x, y)
        var i = zLowerBounds.indexOfFirst { element -> zValue < element }
        if (i >= config.colors.size || i < 0) {
            i = config.colors.size - 1
        }
        return config.colors[i]
    }

    fun colorAt(point : Pair<Double, Double>) : Color {
        return colorAt(point.first, point.second)
    }

    fun onOpen(actionEvent: ActionEvent) {

    }

    fun onParameters(actionEvent: ActionEvent) {

    }

    fun onInterpolation(actionEvent: ActionEvent) {

    }

    fun onGrid(actionEvent: ActionEvent) {

    }

    fun onColorMap(actionEvent: ActionEvent) {

    }

    fun onContourLine(actionEvent: ActionEvent) {

    }

    fun onControlPoints(actionEvent: ActionEvent) {

    }

    fun onDraw(actionEvent: ActionEvent) {

    }

    fun onErase(actionEvent: ActionEvent) {

    }

    fun onAbout(actionEvent: ActionEvent) {

    }

    fun onExit(actionEvent: ActionEvent) {

    }
}

operator fun <T> List<T>.times(other: List<T>): List<Pair<T, T>> {
    val prod = mutableListOf<Pair<T, T>>()
    for (thisElement in this) {
        other.mapTo(prod) { Pair(thisElement, it) }
    }
    return prod
}