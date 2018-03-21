package ru.nsu.fit.g15201.sogreshilin.filter;

import java.awt.*;
import java.awt.image.BufferedImage;
import ru.nsu.fit.g15201.sogreshilin.controller.Controller;
import ru.nsu.fit.g15201.sogreshilin.controller.FilterAppliedObserver;

import static java.lang.Math.*;

public class Rotation implements Filter {

    public static final int MIN_ANGLE = -180;
    public static final int MAX_ANGLE = 180;

    private double angle = 3 * PI / 2;
    private FilterAppliedObserver observer;


    public Rotation(Controller observer) {
        this.observer = observer;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void setAngle(int angle) {
        this.angle = angle * PI / 180;
    }

    @Override
    public BufferedImage apply(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int halfWidth = width / 2;
        int halfHeight = height / 2;

        double cos = cos(angle);
        double sin = sin(angle);

        int rotatedWidth = (int) max(
                round(abs(width * cos - height * sin)),
                round(abs(width * cos + height * sin))
        );
        int rotatedHeight = (int) max(
                round(abs(width * sin - height * cos)),
                round(abs(width * sin + height * cos))
        );

        int halfRotatedWidth = rotatedWidth / 2 - 1;
        int halfRotatedHeight = rotatedHeight / 2 - 1;

        BufferedImage rotatedImage = new BufferedImage(rotatedWidth, rotatedHeight, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < rotatedWidth; ++x) {
            for (int y = 0; y < rotatedHeight; ++y) {

                int xOld = (int) round(cos * (x - halfRotatedWidth) - sin * (y - halfRotatedHeight));
                int yOld = (int) round(sin * (x - halfRotatedWidth) + cos * (y - halfRotatedHeight));
                xOld += halfWidth - 1;
                yOld += halfHeight - 1;
                xOld = xOld == -1 ? 0 : xOld;
                yOld = yOld == -1 ? 0 : yOld;

                if (0 <= xOld && xOld < width && 0 <= yOld && yOld < height) {
                    int rgbOld = image.getRGB(xOld, yOld);
                    rotatedImage.setRGB(x, y, rgbOld);
                }
            }
        }

        observer.onFilterApplied(rotatedImage);
        return rotatedImage;
    }
}
