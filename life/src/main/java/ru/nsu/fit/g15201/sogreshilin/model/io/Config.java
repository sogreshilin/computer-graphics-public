package ru.nsu.fit.g15201.sogreshilin.model.io;

import java.util.ArrayList;
import ru.nsu.fit.g15201.sogreshilin.view.Point;

public class Config {
    public static final int MIN_THICKNESS = 1;
    public static final int MAX_THICKNESS = 10;
    public static final int MIN_SIZE  = 1;
    public static final int MAX_SIZE  = 40;

    private double liveBegin = 2.0;
    private double liveEnd = 3.3;
    private double birthBegin = 2.3;
    private double birthEnd = 2.9;
    private double firstImpact = 1.0;
    private double secondImpact = 0.3;

    private int fieldWidth = 40;
    private int fieldHeight = 60;

    private int lineThickness = 1;
    private int cellSize = 10;
    private ArrayList<Point> aliveCells = new ArrayList<>();
    private FillMode mode = FillMode.REPLACE;

    public enum FillMode { XOR, REPLACE }

    public Config() {
    }

    public Config(Config other) {
        this.liveBegin = other.liveBegin;
        this.liveEnd = other.liveEnd;
        this.birthBegin = other.birthBegin;
        this.birthEnd = other.birthEnd;
        this.firstImpact = other.firstImpact;
        this.secondImpact = other.secondImpact;
        this.fieldWidth = other.fieldWidth;
        this.fieldHeight = other.fieldHeight;
        this.lineThickness = other.lineThickness;
        this.cellSize = other.cellSize;
        this.mode = other.mode;
        this.aliveCells = other.aliveCells;
    }

    public int getFieldWidth() {
        return fieldWidth;
    }

    public void setFieldWidth(int fieldWidth) {
        this.fieldWidth = fieldWidth;
    }

    public int getFieldHeight() {
        return fieldHeight;
    }

    public void setFieldHeight(int fieldHeight) {
        this.fieldHeight = fieldHeight;
    }

    public int getLineThickness() {
        return lineThickness;
    }

    public void setLineThickness(int lineThickness) {
        this.lineThickness = lineThickness;
    }

    public int getCellSize() {
        return cellSize;
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }

    public ArrayList<Point> getAliveCells() {
        return aliveCells;
    }

    public void setAliveCells(ArrayList<Point> aliveCells) {
        this.aliveCells = aliveCells;
    }

    public double getLiveBegin() {
        return liveBegin;
    }

    public void setLiveBegin(double liveBegin) {
        this.liveBegin = liveBegin;
    }

    public double getLiveEnd() {
        return liveEnd;
    }

    public void setLiveEnd(double liveEnd) {
        this.liveEnd = liveEnd;
    }

    public double getBirthBegin() {
        return birthBegin;
    }

    public void setBirthBegin(double birthBegin) {
        this.birthBegin = birthBegin;
    }

    public double getBirthEnd() {
        return birthEnd;
    }

    public void setBirthEnd(double birthEnd) {
        this.birthEnd = birthEnd;
    }

    public double getFirstImpact() {
        return firstImpact;
    }

    public void setFirstImpact(double firstImpact) {
        this.firstImpact = firstImpact;
    }

    public double getSecondImpact() {
        return secondImpact;
    }

    public void setSecondImpact(double secondImpact) {
        this.secondImpact = secondImpact;
    }

    public FillMode getMode() {
        return mode;
    }

    public void setMode(FillMode mode) {
        this.mode = mode;
    }
}
