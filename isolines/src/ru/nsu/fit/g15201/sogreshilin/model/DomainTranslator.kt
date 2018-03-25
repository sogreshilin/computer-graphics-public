package ru.nsu.fit.g15201.sogreshilin.model

class Domain(val xDomain : Pair<Double, Double>, val yDomain : Pair<Double, Double>) {
    val xLength
        get() = xDomain.second - xDomain.first

    val yLength
        get() = yDomain.second - yDomain.first
}

class Translator(val fromDomain : Domain, val toDomain : Domain) {
    fun translateX(oldX : Double) : Double {
        return (oldX - fromDomain.xDomain.first) / fromDomain.xLength *
                toDomain.xLength + toDomain.xDomain.first
    }

    fun translateY(oldY : Double) : Double {
        return (oldY - fromDomain.yDomain.first) / fromDomain.yLength *
                toDomain.yLength + toDomain.yDomain.first
    }

    fun translate(point : Pair<Double, Double>) : Pair<Double, Double> {
        val (oldX, oldY) = point
        return Pair(translateX(oldX), translateY(oldY))
    }
}