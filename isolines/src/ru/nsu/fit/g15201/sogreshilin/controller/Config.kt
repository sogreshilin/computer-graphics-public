package ru.nsu.fit.g15201.sogreshilin.controller

import javafx.scene.paint.Color
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream

class Config {
    var grid = Pair(10, 10)
    var keyValueCount : Int = 3
    var colors : List<Color> = listOf(Color.RED, Color.GREEN, Color.BLUE, Color.WHITE)
    var contourColor = Color.BLACK!!

    lateinit var xDomain : Pair<Double, Double>
    lateinit var yDomain : Pair<Double, Double>

    @Throws(IOException::class)
    fun readConfigFromFile(input: InputStream) {
        val rawStringValue = ""
        try {
            input.bufferedReader().use { reader ->
                grid = readGrid(reader)
                keyValueCount = readKeyValueCount(reader)
                colors = readColors(reader)
                contourColor = readColor(reader)
            }
        } catch (e: IOException) {
            throw IOException("Invalid config file format. Unable to deserialize")
        } catch (e: NumberFormatException) {
            throw IOException("Invalid config file format. Unable to deserialize")
        }
    }

    private fun readColor(reader: BufferedReader): Color {
        val rawString = readLineAndRemoveComments(reader)
        val strings  = rawString.split(" ")
        val red   = Integer.parseInt(strings[0])
        val green = Integer.parseInt(strings[1])
        val blue  = Integer.parseInt(strings[2])
        return Color.rgb(red, green, blue)
    }

    private fun readColors(reader: BufferedReader) : List<Color> {
        val size = keyValueCount + 1
        val list = mutableListOf<Color>()
        for (i in 0 until size) {
            list.add(readColor(reader))
        }
        return list
    }

    private fun readKeyValueCount(reader: BufferedReader) : Int {
        val rawString = readLineAndRemoveComments(reader)
        return Integer.parseInt(rawString)
    }

    private fun readGrid(reader: BufferedReader) : Pair<Int, Int> {
        val rawString = readLineAndRemoveComments(reader)
        val strings = rawString.split(" ")
        val xGridSize = Integer.parseInt(strings[0])
        val yGridSize = Integer.parseInt(strings[1])
        return Pair(xGridSize, yGridSize)
    }

    private fun removeCommentsIn(string: String): String {
        val offset = string.indexOf("//")
        if (offset >= 0) {
            return string.substring(0, offset)
        }
        return string
    }

    @Throws(IOException::class)
    private fun readLineAndRemoveComments(reader: BufferedReader): String {
        var rawString: String
        do {
            rawString = removeCommentsIn(reader.readLine())
        } while (rawString.isEmpty())

        return rawString.trim().replace("\\s+".toRegex(), " ")
    }
}
