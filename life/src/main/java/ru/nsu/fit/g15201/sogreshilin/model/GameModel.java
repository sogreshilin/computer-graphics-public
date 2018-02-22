package ru.nsu.fit.g15201.sogreshilin.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ru.nsu.fit.g15201.sogreshilin.model.io.Config;
import ru.nsu.fit.g15201.sogreshilin.view.Point;

public class GameModel {
    private static final int TIME_TICK = 50;

    private Cell[][] cells;
    private int rowsCount;
    private int columnsCount;
    private Timer timer;
    private Config config;

    private GameModel(int rowsCount, int columnsCount) {
        this.rowsCount = rowsCount;
        this.columnsCount = columnsCount;
        initializeMatrix(rowsCount, columnsCount);
    }

    public GameModel(Config config) {
        this(config.getFieldHeight(), config.getFieldWidth());
        this.config = config;
        for (Point alive : config.getAliveCells()) {
            if (doesFieldContainCellWithCoordinates(alive.getX(), alive.getY())) {
                setStateAt(alive.getY(), alive.getX(), State.ALIVE);
            }
        }
    }

    public void clearCells() {
        for (int i = 0; i < rowsCount; ++i) {
            for (int j = 0; j < columnsCount - (i & 1); ++j) {
                cells[i][j].clear();
            }
        }
        cellsImpactChangedObservers.forEach(CellsImpactChangedObserver::onClear);
        cellStateChangedObservers.forEach(CellStateChangedObserver::onClear);
    }

    private void setNeighboursToAll() {
        for (int i = 0; i < rowsCount; ++i) {
            for (int j = 0; j < columnsCount - (i & 1); ++j) {
                cells[i][j].setFirstNeighbours(getFirstNeighboursOf(i, j));
                cells[i][j].setSecondNeighbours(getSecondNeighboursOf(i, j));
            }
        }
    }

    public void switchStateAt(int i, int j) {
        if (!doesFieldContainCellWithCoordinates(j, i)) {
            return;
        }
        State oldState = cells[j][i].getState();
        State newState;
        switch (oldState) {
            case DEAD: newState = State.ALIVE; break;
            case ALIVE: newState = State.DEAD; break;
            default: throw new RuntimeException("Unexpected cell state");
        }
        setStateAt(i, j, newState);
    }

    public void setStateAt(int i, int j, State state) {
        cells[j][i].setState(state);
        cells[j][i].getAllNeighbours().forEach(this::recomputeImpact);

        notifyStateChanged(j, i, state);
        notifyImpactChanged();
    }

    public void recomputeImpact(Cell cell) {
        long aliveFirstNeighbours = cell.getFirstNeighbours().stream()
                .filter(c -> c.getState() == State.ALIVE)
                .count();
        long aliveSecondNeighbours = cell.getSecondNeighbours().stream()
                .filter(c -> c.getState() == State.ALIVE)
                .count();
        double impact = aliveFirstNeighbours * config.getFirstImpact() +
                aliveSecondNeighbours * config.getSecondImpact();
        cell.setImpact(impact);
    }

    public void nextGeneration() {
        recomputeAllImpacts();
        for (int i = 0; i < rowsCount; ++i) {
            for (int j = 0; j < columnsCount - (i & 1); ++j) {
                Cell cell = cells[i][j];
                switch (cell.getState()) {
                    case DEAD:
                        if (config.getBirthBegin() <= cell.getImpact() &&
                                cell.getImpact() <= config.getBirthEnd()) {
                            cell.setState(State.ALIVE);
                        }
                        break;
                    case ALIVE:
                        if (cell.getImpact() < config.getLiveBegin() ||
                                cell.getImpact() > config.getLiveEnd()) {
                            cell.setState(State.DEAD);
                        }
                        break;
                    default: throw new RuntimeException("Unexpected cell state");
                }
            }
        }
        recomputeAllImpacts();
    }

    private void recomputeAllImpacts() {
        for (int i = 0; i < rowsCount; ++i) {
            for (int j = 0; j < columnsCount - (i & 1); ++j) {
                recomputeImpact(cells[i][j]);
                notifyStateChanged(i, j, cells[i][j].getState());
            }
        }
        notifyImpactChanged();
    }

    private boolean doesFieldContainCellWithCoordinates(int i, int j) {
        if (i < 0 || i >= rowsCount || j < 0) {
            return false;
        }

        if ((i & 1) == 0 && j >= columnsCount ||
            (i & 1) != 0 && j >= columnsCount - 1) {
            return false;
        }

        return true;
    }

    private List<Cell> getFirstNeighboursOf(int i, int j) {
        return Stream.of(
                    new Point(i - 1, j - 1 + (i & 1)),
                    new Point(i - 1, j + (i & 1)),
                    new Point(i, j - 1),
                    new Point(i, j + 1),
                    new Point(i + 1, j - 1 + (i & 1)),
                    new Point(i + 1, j + (i & 1))
               ).filter(p -> doesFieldContainCellWithCoordinates(p.getX(), p.getY()))
                .map(p -> cells[p.getX()][p.getY()])
                .collect(Collectors.toList());
    }

    private List<Cell> getSecondNeighboursOf(int i, int j) {
        return Stream.of(
                    new Point(i - 1, j - 2 + (i & 1)),
                    new Point(i + 1, j - 2 + (i & 1)),
                    new Point(i - 2, j),
                    new Point(i + 2, j),
                    new Point(i - 1, j + 1 + (i & 1)),
                    new Point(i + 1, j + 1 + (i & 1))
               ).filter(p -> doesFieldContainCellWithCoordinates(p.getX(), p.getY()))
                .map(p -> cells[p.getX()][p.getY()])
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < rowsCount; ++i) {
            if ((i & 1) > 0) {
                result.append("   ");
            }
            for (int j = 0; j < columnsCount - (i & 1); ++j) {
                result.append(String.format("%2.1f   ", cells[i][j].getImpact()));
            }
            result.append("   ");
            if ((i & 1) > 0) {
                result.append("    ");
            }
            for (int j = 0; j < columnsCount - (i & 1); ++j) {
                result.append(String.format("%s ", cells[i][j].getState() == State.ALIVE ? "A" : "D"));
            }
            result.append("\n");
        }
        return result.toString();
    }

    public void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                nextGeneration();
            }
        }, TIME_TICK, TIME_TICK);
    }

    public void stopTimer() {
        timer.cancel();
    }

    public int getRowsCount() {
        return rowsCount;
    }

    public int getColumnsCount() {
        return columnsCount;
    }

    private List<CellStateChangedObserver> cellStateChangedObservers = new ArrayList<>();

    public void addCellStateObserver(CellStateChangedObserver observer) {
        cellStateChangedObservers.add(observer);
    }

    public void notifyStateChanged(int i, int j, State state) {
        for (CellStateChangedObserver observer : cellStateChangedObservers) {
            observer.onCellStateChanged(i, j, state);
        }
    }

    private List<CellsImpactChangedObserver> cellsImpactChangedObservers = new ArrayList<>();

    public void addCellsImpactObserver(CellsImpactChangedObserver observer) {
        cellsImpactChangedObservers.add(observer);
    }

    public void notifyImpactChanged() {
        for (CellsImpactChangedObserver observer : cellsImpactChangedObservers) {
            observer.onImpactChanged(getImpacts());
        }
    }

    public static boolean rulesChanged(Config config, Config other) {
        return config.getLiveBegin() != other.getLiveBegin() ||
                config.getLiveEnd() != other.getLiveEnd() ||
                config.getBirthBegin() != other.getBirthBegin() ||
                config.getBirthEnd() != other.getBirthEnd() ||
                config.getFirstImpact() != other.getFirstImpact() ||
                config.getSecondImpact() != other.getSecondImpact();
    }

    public double[] getImpacts() {
        int cellsCount = rowsCount * columnsCount - rowsCount / 2;
        double[] impacts = new double[cellsCount];
        int k = 0;
        for (int i = 0; i < rowsCount; ++i) {
            for (int j = 0; j < columnsCount - (i & 1); ++j) {
                impacts[k] = cells[i][j].getImpact();
                ++k;
            }
        }
        return impacts;
    }

    private void initializeMatrix(int rowsCount, int columnsCount) {
        cells = new Cell[rowsCount][columnsCount];
        for (int i = 0; i < rowsCount; ++i) {
            for (int j = 0; j < columnsCount - (i & 1); ++j) {
                cells[i][j]  = new Cell(State.DEAD);
            }
        }
        setNeighboursToAll();
    }

    public void setConfig(Config newConfig) {
        this.config = newConfig;
        rowsCount = newConfig.getFieldHeight();
        columnsCount = newConfig.getFieldWidth();
        initializeMatrix(rowsCount, columnsCount);
        for (Point alive : config.getAliveCells()) {
            if (doesFieldContainCellWithCoordinates(alive.getX(), alive.getY())) {
                setStateAt(alive.getY(), alive.getX(), State.ALIVE);
            }
        }
    }


    public ArrayList<Point> getAliveCells() {
        ArrayList<Point> aliveCells = new ArrayList<>();
        for (int i = 0; i < rowsCount; ++i) {
            for (int j = 0; j < columnsCount - (i & 1); ++j) {
                if (cells[i][j].getState() == State.ALIVE) {
                    aliveCells.add(new Point(i, j));
                }
            }
        }
        return aliveCells;
    }
}
