package ru.nsu.fit.g15201.sogreshilin.model;

public interface CellStateChangedObserver {
    void onCellStateChanged(int i, int j, State newState);
    void onClear();
}
