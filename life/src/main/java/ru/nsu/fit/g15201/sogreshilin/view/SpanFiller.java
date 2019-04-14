package ru.nsu.fit.g15201.sogreshilin.view;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class SpanFiller {

    private static class Span {
        final BufferedImage image;
        final int x0;
        final int x1;
        final int y;

        Span(BufferedImage image, int x0, int x1, int y) {
            this.image = image;
            this.x0 = x0;
            this.x1 = x1;
            this.y = y;
        }

        void fill(Color color) {
            for (int x = x0 + 1; x < x1; ++x) {
                image.setRGB(x, y, color.getRGB());
            }
        }

        int getColor() {
            return image.getRGB(x0 + 1, y);
        }

        @Override
        public String toString() {
            return String.format("([%d..%d], %d)", x0, x1, y);
        }
    }


    private static Span getSpanAt(BufferedImage image, int x, int y) {
        int color = image.getRGB(x, y);

        int xLeft = x;
        while (xLeft != 0 && image.getRGB(xLeft, y) == color) {
            --xLeft;
        }

        int xRight = x;
        while (xRight != image.getWidth() - 1 && image.getRGB(xRight, y) == color) {
            ++xRight;
        }

        return new Span(image, xLeft, xRight, y);
    }


    private static List<Span> getAllSimilarSpansAt(Span span, int y, BufferedImage image) {
        ArrayList<Span> spans = new ArrayList<>();
        for (int x = span.x0 + 1; x < span.x1; ++x) {
            if (span.getColor() == image.getRGB(x, y)) {
                Span s = getSpanAt(image, x, y);
                spans.add(s);
                x = s.x1 + 1;
            }
        }
        return spans;
    }


    private static List<Span> getAdjacentSpansAbove(Span span, BufferedImage image) {
        if (span.y == image.getHeight() - 1) {
            return Collections.emptyList();
        }
        return getAllSimilarSpansAt(span, span.y + 1, image);
    }


    private static List<Span> getAdjacentSpansBelow(Span span, BufferedImage image) {
        if (span.y == 0) {
            return Collections.emptyList();
        }
        return getAllSimilarSpansAt(span, span.y - 1, image);
    }


    public static void fill(BufferedImage image, int x, int y, Color color) {
        int oldColor = image.getRGB(x, y);
        int newColor = color.getRGB();
        if (oldColor == newColor) {
            return;
        }
        ArrayList<Span> spanStack = new ArrayList<>();
        spanStack.add(getSpanAt(image, x, y));

        while (!spanStack.isEmpty()) {
            Span span = spanStack.remove(spanStack.size() - 1);
            spanStack.addAll(getAdjacentSpansAbove(span, image));
            spanStack.addAll(getAdjacentSpansBelow(span, image));
            span.fill(color);
        }
    }
}
