package ru.nsu.fit.g15201.sogreshilin.filter;

import java.awt.image.BufferedImage;
import ru.nsu.fit.g15201.sogreshilin.controller.Controller;
import ru.nsu.fit.g15201.sogreshilin.controller.FilterAppliedObserver;

import static java.lang.Math.*;

public class Rotation implements Filter {

    public static final int MIN_ANGLE = 0;
    public static final int MAX_ANGLE = 360;

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

        double a = angle;
        while (a > PI / 2) {
            a -= PI / 2;
        }

        int rotatedWidth = (int) round(width * cos(a) + height * sin(a));
        int rotatedHeight = (int) round(height * cos(a) + width * sin(a));

        int halfRotatedWidth = rotatedWidth / 2;
        int halfRotatedHeight = rotatedHeight / 2;

        BufferedImage rotatedImage = new BufferedImage(rotatedWidth, rotatedHeight, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < rotatedWidth; ++x) {
            for (int y = 0; y < rotatedHeight; ++y) {
                int xOld = (int) round(cos(angle) * (x - halfRotatedWidth) - sin(angle) * (y - halfRotatedHeight));
                int yOld = (int) round(sin(angle) * (x - halfRotatedWidth) + cos(angle) * (y - halfRotatedHeight));
                xOld += halfWidth;
                yOld += halfHeight;

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
