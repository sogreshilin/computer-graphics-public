package ru.nsu.fit.g15201.sogreshilin.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;
import ru.nsu.fit.g15201.sogreshilin.model.io.Config;
import ru.nsu.fit.g15201.sogreshilin.view.Point;

public class GameModel {
    static double LIVE_BEGIN = 2.0;
    static double LIVE_END = 3.3;
    static double BIRTH_BEGIN = 2.3;
    static double BIRTH_END = 2.9;
    static double FST_IMPACT = 1.0;
    static double SND_IMPACT = 0.3;

    /* 1-ая координата - строка, 2-ая - столбец */
    private Cell[][] cells;
    private int rowsCount;
    private int columnsCount;

    private Config config;

    private GameModel(int rowsCount, int columnsCount) {
        this.rowsCount = rowsCount;
        this.columnsCount = columnsCount;
        this.cells = new Cell[rowsCount][columnsCount];
        for (int i = 0; i < rowsCount; ++i) {
            Cell[] line = new Cell[columnsCount - (i & 1)];
            for (int j = 0; j < columnsCount - (i & 1); ++j) {
                line[j] = new Cell(State.DEAD);
            }
            cells[i] = line;
        }
        setNeighboursToAll();
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
//        System.out.println("at func switchStateAt");
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
        cells[j][i].getAllNeighbours().forEach(Cell::recomputeImpact);
//        System.out.println(this);

        notifyStateChanged(j, i, state);
        notifyImpactChanged();
//        System.out.println(String.format("Model changed state of hex(%d, %d)", j, i));
    }

    public void nextGeneration() {
        for (int i = 0; i < rowsCount; ++i) {
            for (int j = 0; j < columnsCount - (i & 1); ++j) {
                cells[i][j].nextGeneration();
            }
        }
        recomputeAllImpacts();
    }

    private void recomputeAllImpacts() {
        for (int i = 0; i < rowsCount; ++i) {
            for (int j = 0; j < columnsCount - (i & 1); ++j) {
                cells[i][j].recomputeImpact();
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

    private Timer timer;
    public void startTimer() {
        timer = new Timer();
        long delay  = 1000L;
        long period = 1000L;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println(GameModel.this);
                nextGeneration();
            }
        }, delay, period);
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

    public void setConfig(Config newConfig) {
        if (config.getFieldWidth() != newConfig.getFieldWidth() ||
                config.getFieldHeight() != newConfig.getFieldHeight()) {
//            System.out.println("HERE");
            int oldRowsCount = rowsCount;
            int oldColumnsCount = columnsCount;
            rowsCount = newConfig.getFieldHeight();
            columnsCount = newConfig.getFieldWidth();
            Cell[][] oldCells = cells;
            Cell[][] newCells = new Cell[rowsCount][columnsCount];
            for (int i = 0; i < rowsCount; ++i) {
                Cell[] line = new Cell[columnsCount - (i & 1)];
                for (int j = 0; j < columnsCount - (i & 1); ++j) {
                    line[j] = new Cell(State.DEAD);
                }
                newCells[i] = line;
            }
            cells = newCells;
            setNeighboursToAll();
//            System.out.println("MODEL:\n" + this);

//            for (int i = 0; i < rowsCount; ++i) {
//                for (int j = 0; j < columnsCount - (i & 1); ++j) {
////                    if (i < oldRowsCount && j < oldColumnsCount) {
////                        setStateAt(j, i, oldCells[i][j].getState());
////                    } else {
//                        setStateAt(i, j, State.DEAD);
////                    }
//                }
//            }
            recomputeAllImpacts();
            notifyImpactChanged();
        }
        if (rulesChanged(config, newConfig)) {
            config = newConfig;
        }
    }


}
