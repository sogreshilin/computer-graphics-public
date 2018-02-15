package ru.nsu.fit.g15201.sogreshilin.model;

import java.util.ArrayList;
import java.util.List;
import ru.nsu.fit.g15201.sogreshilin.Constants;

public class Cell {
    private double impact;
    private List<Cell> firstNeighbours = new ArrayList<>(Constants.FIRST_NEIGHBOURS_COUNT);
    private List<Cell> secondNeighbours = new ArrayList<>(Constants.SECOND_NEIGHBOURS_COUNT);
    private State state;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setFirstNeighbours(List<Cell> firstNeighbours) {
        this.firstNeighbours = firstNeighbours;
    }

    public void setSecondNeighbours(List<Cell> secondNeighbours) {
        this.secondNeighbours = secondNeighbours;
    }
}
