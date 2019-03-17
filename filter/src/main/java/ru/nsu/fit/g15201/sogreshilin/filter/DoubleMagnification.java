package ru.nsu.fit.g15201.sogreshilin.filter;

import java.awt.*;
import java.awt.image.BufferedImage;
import ru.nsu.fit.g15201.sogreshilin.controller.FilterAppliedObserver;

public class DoubleMagnification implements Filter {

    private final FilterAppliedObserver observer;

    public DoubleMagnification(FilterAppliedObserver observer) {
        this.observer = observer;
    }

    @Override
    public BufferedImage apply(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage filteredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int quarterWidth = width / 4;
        int quarterHeight = height / 4;
        int halfWidth = width / 2;
        int halfHeight = height / 2;

        for (int y = 0; y < halfHeight; ++y) {
            for (int x = 0; x < halfWidth; ++x) {
                Color color = new Color(image.getRGB(x + quarterWidth, y + quarterHeight));
                filteredImage.setRGB(2 * x,  2 * y, color.getRGB());
                filteredImage.setRGB(2 * x + 1,  2 * y + 1, color.getRGB());
            }
        }

        for (int y = 0; y < height; ++y) {
            int shift = (y + 1) & 1;
            for (int x = shift; x < width; x += 2) {
                int xLeft = x == 0 ? 1 : x - 1;
                int xRight = x == width - 1 ? width - 2 : x + 1;
                int yUp = y == height - 1 ? height - 2 : y + 1;
                int yDown = y == 0 ? 1 : y - 1;

                Color up = new Color(filteredImage.getRGB(x, yUp));
                Color down = new Color(filteredImage.getRGB(x, yDown));
                Color left = new Color(filteredImage.getRGB(xLeft, y));
                Color right = new Color(filteredImage.getRGB(xRight, y));

                int red = (left.getRed() + right.getRed() + up.getRed() + down.getRed()) / 4;
                int green = (left.getGreen() + right.getGreen() + up.getGreen() + down.getGreen()) / 4;
                int blue = (left.getBlue() + right.getBlue() + up.getBlue() + down.getBlue()) / 4;

                filteredImage.setRGB(x,  y, new Color(red, green, blue).getRGB());
            }
        }


        if (observer != null) {
            observer.onFilterApplied(filteredImage);
        }
        return filteredImage;
    }
}
