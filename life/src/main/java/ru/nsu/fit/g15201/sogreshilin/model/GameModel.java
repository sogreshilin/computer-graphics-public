package ru.nsu.fit.g15201.sogreshilin.model;

import java.util.ArrayList;
import java.util.List;

public class GameModel {
    private double LIVE_BEGIN = 2.0;
    private double LIVE_END = 3.3;
    private double BIRTH_BEGIN = 2.3;
    private double BIRTH_END = 2.9;
    private double FST_IMPACT = 1.;
    private double SND_IMPACT = 0.;

    private Cell[][] cells;
    private int rowsCount;
    private int columnCount;

    public GameModel(int rowsCount, int columnsCount) {
        this.rowsCount = rowsCount;
        this.columnCount = columnsCount;
        this.cells = new Cell[rowsCount][columnsCount];
        for (int k = 0; k < rowsCount; ++k) {
            Cell[] line = new Cell[columnsCount];
            for (int i = 0; i < columnsCount; ++i) {
                line[i] = new Cell();
            }
            cells[k] = line;
        }
    }

    public void switchStateAt(int i, int j) {
        State oldState = cells[i][j].getState();
        State newState;
        switch (oldState) {
            case DEAD: newState = State.ALIVE; break;
            case ALIVE: newState = State.DEAD; break;
            default: throw new RuntimeException("Unexpected cell state");
        }
        cells[i][j].setState(newState);
    }

    public void setAliveAt(int i, int j) {
        cells[i][j].setState(State.ALIVE);
    }

    private boolean doesFieldContainCellWithCoordinates(int i, int j) {
        if (i < 0 || i >= rowsCount || j < 0) {
            return false;
        }

        if ((i & 1) == 0 && j >= columnCount ||
            (i & 1) != 0 && j >= columnCount - 1) {
            return false;
        }

        return true;
    }

    private void setFirstNeighboursTo(int i, int j) {
        Cell cell = cells[i][j];
        List<Cell> neighbours = new ArrayList<>();

        if (doesFieldContainCellWithCoordinates(i - 1, j - 1)) {
            neighbours.add(cells[i - 1][j - 1]);
        }
        if (doesFieldContainCellWithCoordinates(i - 1, j)) {
            neighbours.add(cells[i - 1][j]);
        }
        if (doesFieldContainCellWithCoordinates(i - 1, j + 1)) {
            neighbours.add(cells[i - 1][j + 1]);
        }
        if (doesFieldContainCellWithCoordinates(i, j - 1)) {
            neighbours.add(cells[i][j - 1]);
        }
        if (doesFieldContainCellWithCoordinates(i, j + 1)) {
            neighbours.add(cells[i][j + 1]);
        }
        if (doesFieldContainCellWithCoordinates(i + 1, j)) {
            neighbours.add(cells[i + 1][j]);
        }

        cells[i][j].setFirstNeighbours(neighbours);
    }

    private void setSecondNeighboursTo(int i, int j) {
        Cell cell = cells[i][j];
        List<Cell> neighbours = new ArrayList<>();

        if (doesFieldContainCellWithCoordinates(i - 1, j - 1)) {
            neighbours.add(cells[i - 1][j - 1]);
        }
        if (doesFieldContainCellWithCoordinates(i - 1, j)) {
            neighbours.add(cells[i - 1][j]);
        }
        if (doesFieldContainCellWithCoordinates(i - 1, j + 1)) {
            neighbours.add(cells[i - 1][j + 1]);
        }
        if (doesFieldContainCellWithCoordinates(i, j - 1)) {
            neighbours.add(cells[i][j - 1]);
        }
        if (doesFieldContainCellWithCoordinates(i, j + 1)) {
            neighbours.add(cells[i][j + 1]);
        }
        if (doesFieldContainCellWithCoordinates(i + 1, j)) {
            neighbours.add(cells[i + 1][j]);
        }

        cells[i][j].setSecondNeighbours(neighbours);
    }

}
