package ru.nsu.fit.g15201.sogreshilin.controller

import javafx.scene.paint.Color
import ru.nsu.fit.g15201.sogreshilin.model.Domain
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

class Config {
    var grid = Pair(10, 10)
    var colors : List<Color> = listOf(
            Color.rgb( 87, 120, 177),
            Color.rgb( 93, 130, 182),
            Color.rgb(100, 139, 187),
            Color.rgb(106, 149, 193),
            Color.rgb(113, 159, 198),
            Color.rgb(119, 169, 203),
            Color.rgb(126, 179, 208),
            Color.rgb(132, 188, 213),
            Color.rgb(138, 198, 218),
            Color.rgb(145, 208, 224),
            Color.rgb(151, 218, 229),
            Color.rgb(158, 227, 234),
            Color.rgb(164, 237, 239))
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

    var domain = Domain(-3.0, 3.0, -3.0, 3.0)

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
        val red   = Integer.parseInt(strings[0])
        val green = Integer.parseInt(strings[1])
        val blue  = Integer.parseInt(strings[2])
        return Color.rgb(red, green, blue)
    }

    private fun readColors(lines : List<String>) : List<Color> {
        return lines.map { readColor(it) }
    }

    private fun readKeyValueCount(line : String) : Int {
        return Integer.parseInt(line)
    }

    private fun readGrid(line : String) : Pair<Int, Int> {
        val strings = line.split(" ")
        val xGridSize = Integer.parseInt(strings[0])
        val yGridSize = Integer.parseInt(strings[1])
        return Pair(xGridSize, yGridSize)
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
