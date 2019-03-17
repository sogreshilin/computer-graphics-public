package ru.nsu.fit.g15201.sogreshilin.filter;

import java.awt.image.BufferedImage;

public interface Filter {
    BufferedImage apply(BufferedImage image);
}
