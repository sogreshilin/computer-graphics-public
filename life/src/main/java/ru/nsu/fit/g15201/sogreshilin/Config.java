package ru.nsu.fit.g15201.sogreshilin;

import java.util.ArrayList;

public class Config {
    private int fieldWidth;
    private int fieldHeight;
    private int borderWidth;
    private int cellRadius;
    private ArrayList<Point> aliveCells = new ArrayList<Point>();

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

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

    public int getCellRadius() {
        return cellRadius;
    }

    public void setCellRadius(int cellRadius) {
        this.cellRadius = cellRadius;
    }

    public ArrayList<Point> getAliveCells() {
        return aliveCells;
    }

    public void setAliveCells(ArrayList<Point> aliveCells) {
        this.aliveCells = aliveCells;
    }
}
