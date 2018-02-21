package ru.nsu.fit.g15201.sogreshilin.model;

public interface CellsImpactChangedObserver {
    void onImpactChanged(double[] impacts);
    void onClear();
}
