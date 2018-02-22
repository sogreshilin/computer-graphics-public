package ru.nsu.fit.g15201.sogreshilin.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ru.nsu.fit.g15201.sogreshilin.view.Constants;

public class Cell {
    private double impact;
    private List<Cell> firstNeighbours = new ArrayList<>(Constants.FIRST_NEIGHBOURS_COUNT);
    private List<Cell> secondNeighbours = new ArrayList<>(Constants.SECOND_NEIGHBOURS_COUNT);
    private State state;

    public Cell(State state) {
        this.state = state;
    }

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

    public List<Cell> getFirstNeighbours() {
        return firstNeighbours;
    }

    public List<Cell> getSecondNeighbours() {
        return secondNeighbours;
    }

    public List<Cell> getAllNeighbours() {
        return Stream.concat(firstNeighbours.stream(), secondNeighbours.stream())
                .collect(Collectors.toList());
    }

//    public void nextGeneration() {
//        switch (state) {
//            case DEAD:
//                if (GameModel.BIRTH_BEGIN <= impact && impact <= GameModel.BIRTH_END) {
//                    setState(State.ALIVE);
//                }
//                break;
//            case ALIVE:
//                if (impact < GameModel.LIVE_BEGIN || impact > GameModel.LIVE_END) {
//                    setState(State.DEAD);
//                }
//                break;
//            default: throw new RuntimeException("Unexpected cell state");
//        }
//    }

    public double getImpact() {
        return impact;
    }

    public void clear() {
        state = State.DEAD;
        impact = 0;
    }

    public void setImpact(double impact) {
        this.impact = impact;
    }
}
