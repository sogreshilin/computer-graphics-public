package ru.nsu.fit.g15201.sogreshilin.filter.edge;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import ru.nsu.fit.g15201.sogreshilin.controller.FilterAppliedObserver;
import ru.nsu.fit.g15201.sogreshilin.filter.ColorUtils;

public class RobertsEdgeDetection implements EdgeDetection {

    private final FilterAppliedObserver observer;
    private int threshold = 20;
    private boolean inverted = false;

    public RobertsEdgeDetection(FilterAppliedObserver observer) {
        this.observer = observer;
    }

    public BufferedImage apply(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage destination = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int[] components = new int[] {0, 0, 0};
                for (int k = 0; k < 3; ++k) {
                    components[k] =
                        Math.abs(
                            ColorUtils.getColorComponentAt(k, image, x, y) -
                            ColorUtils.getColorComponentAt(k, image, x + 1, y + 1)
                        ) +
                        Math.abs(
                            ColorUtils.getColorComponentAt(k, image, x, y + 1) -
                            ColorUtils.getColorComponentAt(k, image, x + 1, y)
                        );
                }
                for (int k = 0; k < 3; ++k) {
                    components[k] = ColorUtils.truncate(components[k]);
                }
                int sum = Arrays.stream(components).sum();

                int deltaThreshold = EdgeDetection.MAX_THRESHOLD - EdgeDetection.MIN_THRESHOLD;
                Color color = (sum >= threshold * 255 * 3 / deltaThreshold) ? Color.WHITE : Color.BLACK;
                if (inverted) {
                    color = ColorUtils.negateColor(color);
                }
                destination.setRGB(x, y, color.getRGB());
            }
        }
        observer.onFilterApplied(destination);
        return destination;
    }


    @Override
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public void setInverted(boolean value) {
        this.inverted = value;
    }
}
