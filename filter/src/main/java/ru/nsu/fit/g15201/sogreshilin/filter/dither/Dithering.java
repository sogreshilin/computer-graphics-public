package ru.nsu.fit.g15201.sogreshilin.filter.dither;

import ru.nsu.fit.g15201.sogreshilin.filter.Filter;

public interface Dithering extends Filter {
    void setLevels(int redLevel, int greenLevel, int blueLevel);
}
