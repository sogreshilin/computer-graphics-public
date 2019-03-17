package ru.nsu.fit.g15201.sogreshilin.filter.affine;

import java.awt.*;
import java.awt.image.BufferedImage;
import ru.nsu.fit.g15201.sogreshilin.controller.FilterAppliedObserver;
import ru.nsu.fit.g15201.sogreshilin.filter.Filter;
import static java.lang.Math.*;
import static ru.nsu.fit.g15201.sogreshilin.filter.ColorUtils.*;


/**
 * coefficient * [filterMatrix, matrixAt(x, y)] + bias
 * here [ , ] - scalar product of matrices
 */
public class MatrixFilter implements Filter {
    private FilterAppliedObserver observer;

    private double coefficient = 1.0;
    private Matrix filterMatrix = Matrix.of(1.0);
    private int bias = 0;

    public MatrixFilter(FilterAppliedObserver observer) {
        this.observer = observer;
    }

    public MatrixFilter(double coefficient, Matrix filterMatrix) {
        this.coefficient = coefficient;
        this.filterMatrix = filterMatrix;
    }

    public void setCoefficient(double coefficient) {
        this.coefficient = coefficient;
    }

    public void setFilterMatrix(Matrix filterMatrix) {
        this.filterMatrix = filterMatrix;
    }

    public void setBias(int bias) {
        this.bias = bias;
    }

    public BufferedImage apply(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage filteredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int radius = filterMatrix.getRadius();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int red = 0;
                int green = 0;
                int blue = 0;

                for (int i = -radius; i <= radius; ++i) {
                    for (int j = -radius; j <= radius; ++j) {
                        Color color = new Color(image.getRGB(abs(x + i) % width, abs(y + j) % height));
                        red += color.getRed() * filterMatrix.at(i, j);
                        green += color.getGreen() * filterMatrix.at(i, j);
                        blue += color.getBlue() * filterMatrix.at(i, j);
                    }
                }

                red = truncate((int) round(red * coefficient) + bias);
                green = truncate((int) round(green * coefficient) + bias);
                blue = truncate((int) round(blue * coefficient) + bias);

                filteredImage.setRGB(x, y, new Color(red, green, blue).getRGB());
            }
        }
        if (observer != null) {
            observer.onFilterApplied(filteredImage);
        }
        return filteredImage;
    }
}
