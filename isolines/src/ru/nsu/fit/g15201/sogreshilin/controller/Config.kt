package ru.nsu.fit.g15201.sogreshilin.controller

import javafx.scene.paint.Color
import ru.nsu.fit.g15201.sogreshilin.model.Domain
import java.io.IOException
import java.io.InputStream

class Config {
    var grid = Pair(30, 30)
    var colors : List<Color> = listOf(
            Color.rgb(255,0,0),
            Color.rgb(255,127,0),
            Color.rgb(255, 255, 0),
            Color.rgb(0, 255, 0),
            Color.rgb(0, 0, 255),
            Color.rgb(75, 0, 130),
            Color.rgb(143, 0, 255))
    var keyValueCount : Int = colors.size - 1
    var contourColor = Color.BLACK!!

    var gridX : Int
        get() = grid.first
        set(value) {
            grid = Pair(value, grid.second)
        }

    var gridY : Int
        get() = grid.second
        set(value) {
            grid = Pair(grid.first, value)
        }

    var domain = Domain(-1.0, 1.0, -1.0, 1.0)

    @Throws(IOException::class)
    fun readConfigFromFile(input: InputStream) {
        val lines = removeComments(input.bufferedReader().readLines())
        grid = readGrid(lines[0])
        keyValueCount = readKeyValueCount(lines[1])
        colors = readColors(lines.subList(2, 2 + keyValueCount + 1))
        contourColor = readColor(lines.last())
    }

    private fun readColor(line : String): Color {
        val strings  = line.split(" ")
        try {
            val red = Integer.parseInt(strings[0])
            val green = Integer.parseInt(strings[1])
            val blue = Integer.parseInt(strings[2])
            return Color.rgb(red, green, blue)
        } catch (exception : NumberFormatException) {
            throw IOException(exception)
        }
    }

    private fun readColors(lines : List<String>) : List<Color> {
        return lines.map { readColor(it) }
    }

    private fun readKeyValueCount(line : String) : Int {
        try {
            return Integer.parseInt(line)
        } catch (exception : NumberFormatException) {
            throw IOException(exception)
        }
    }

    private fun readGrid(line : String) : Pair<Int, Int> {
        val strings = line.split(" ")
        try {
            val xGridSize = Integer.parseInt(strings[0])
            val yGridSize = Integer.parseInt(strings[1])
            return Pair(xGridSize, yGridSize)
        } catch (exception : NumberFormatException) {
            throw IOException(exception)
        }
    }

    private fun removeComments(lines: List<String>): List<String> {
        val list = mutableListOf<String>()
        for (line in lines) {
            var mutableLine = line
            val offset = mutableLine.indexOf("//")
            if (offset >= 0) {
                mutableLine = mutableLine.substring(0, offset)
            }
            mutableLine.trim().replace("\\s+".toRegex(), " ")
            if (!mutableLine.isEmpty()) {
                list.add(mutableLine)
            }
        }
        return list
    }
}
