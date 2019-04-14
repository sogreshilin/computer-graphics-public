package ru.nsu.fit.g15201.sogreshilin.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import ru.nsu.fit.g15201.sogreshilin.ContourLines;
import ru.nsu.fit.g15201.sogreshilin.controller.Config;
import ru.nsu.fit.g15201.sogreshilin.controller.Controller;
import ru.nsu.fit.g15201.sogreshilin.model.Domain;
import ru.nsu.fit.g15201.sogreshilin.model.Segment;
import ru.nsu.fit.g15201.sogreshilin.model.Translator;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import static java.lang.Math.min;

public class GraphicsCanvas extends Canvas {
    private Controller controller;
    private Coordinates coordinates;
    private Translator translatorX;
    private Translator translatorY;
    private Translator translatorZ;
    private Domain domain;
    private double rangeMin;
    private double rangeMax;
    private ArrayList<MouseMovedObserver> mouseMovedObservers = new ArrayList<>();
    private ArrayList<MouseClickedObserver> mouseClickedObservers = new ArrayList<>();
    private boolean gridEnabled = true;
    private boolean colorMapEnabled = true;

    private List<Color> colors;
    private HashMap<Double, ArrayList<Segment>> isolines = new HashMap<>();
    private boolean isolinesEnabled = true;
    private boolean controlPointsEnabled = false;
    private boolean interpolationEnabled = false;

    public GraphicsCanvas(Controller controller) {
        super(ContourLines.DEFAULT_WIDTH, ContourLines.DEFAULT_HEIGHT);
        this.controller = controller;
        this.coordinates = new Coordinates(this.getWidth(), this.getHeight());

        setOnMouseMoved(e -> {
            double x = e.getX();
            double y = e.getY();
            if (isInsideGraphics(x, y)) {
                double domainX = translatorX.translate(x);
                double domainY = translatorY.translate(y);
                notifyMouseMoved(domainX, domainY);
            }
        });

        setOnMouseClicked(e -> {
            double x = e.getX();
            double y = e.getY();
            if (isInsideGraphics(x, y)) {
                double domainX = translatorX.translate(x);
                double domainY = translatorY.translate(y);
                notifyMouseClicked(domainX, domainY);
            }
        });

        setOnMouseDragged(e -> {
            double x = e.getX();
            double y = e.getY();
            if (isInsideGraphics(x, y)) {
                double domainX = translatorX.translate(x);
                double domainY = translatorY.translate(y);
                notifyMouseMoved(domainX, domainY);
                double z = controller.getFunction().valueAt(domainX, domainY);
                redraw();
                drawIsoline(controller.getFunction().getIsoline(z));
            }
        });
    }

    public void redraw() {
        GraphicsContext context = getGraphicsContext2D();
        context.clearRect(0, 0, getWidth(), getHeight());
        if (colorMapEnabled) {
            drawColorMap(context);
        }
        if (gridEnabled) {
            drawGrid(context);
        }
        if (isolinesEnabled) {
            drawAllIsolines(context);
        }
        if (controlPointsEnabled) {
            drawAllControlPoints(context);
        }
        drawColorMapBorder(context);
        drawBorders(context);
        drawLegendBorder(context);
        drawLegendValues(context);
    }

    public void addIsoline(double value, ArrayList<Segment> segments) {
        isolines.put(value, segments);
        redraw();
    }

    public void setRange(double min, double max) {
        rangeMin = min;
        rangeMax = max;
        translatorZ = new Translator(coordinates.getLegendY1(), coordinates.getLegendY0(), min, max);
    }

    public void setGridEnabled(boolean gridEnabled) {
        this.gridEnabled = gridEnabled;
        redraw();
    }

    public void setControlPointsEnabled(boolean controlPointsEnabled) {
        this.controlPointsEnabled = controlPointsEnabled;
        redraw();
    }

    public void setColorMapEnabled(boolean colorMapEnabled) {
        this.colorMapEnabled = colorMapEnabled;
        redraw();
    }

    public void setIsolinesEnabled(boolean isolinesEnabled) {
        this.isolinesEnabled = isolinesEnabled;
        redraw();
    }

    public void clearIsolines() {
        isolines.clear();
        addDefaultIsolines();
        redraw();
    }

    public void setInterpolationEnabled(boolean value) {
        this.interpolationEnabled = value;
        redraw();
    }

    public interface MouseMovedObserver {
        void onMouseMoved(double x, double y);
    }

    public void addMouseMovedObserver(MouseMovedObserver observer) {
        mouseMovedObservers.add(observer);
    }

    private void notifyMouseMoved(double x, double y) {
        mouseMovedObservers.forEach(observer -> observer.onMouseMoved(x, y));
    }

    public interface MouseClickedObserver {
        void onMouseClicked(double x, double y);
    }

    public void addMouseClickedObserver(MouseClickedObserver observer) {
        mouseClickedObservers.add(observer);
    }

    private void notifyMouseClicked(double x, double y) {
        mouseClickedObservers.forEach(observer -> observer.onMouseClicked(x, y));
    }

    private boolean isInsideGraphics(double x, double y) {
        return coordinates.getGraphicsX0() <= x && x <= coordinates.getGraphicsX1() &&
                coordinates.getGraphicsY0() <= y && y <= coordinates.getGraphicsY1();
    }

    public void setConfig(Config config) {
        coordinates.setGridXCount(config.getGrid().getFirst());
        coordinates.setGridYCount(config.getGrid().getSecond());
        this.domain = config.getDomain();
        this.colors = config.getColors();

        ArrayList<Double> zValues = new ArrayList<>(isolines.keySet());
        isolines.clear();
        addDefaultIsolines();
        for (double z : zValues) {
            ArrayList<Segment> segments = controller.getFunction().getIsoline(z);
            isolines.put(z, segments);
        }

        setTranslators();
        redraw();
    }

    private void addDefaultIsolines() {
        Translator translator = new Translator(1, colors.size() - 1, rangeMin, rangeMax);
        for (int i = 1; i < colors.size(); ++i) {
            double z = translator.translate(i);
            ArrayList<Segment> segments = controller.getFunction().getIsoline(z);
            isolines.put(z, segments);
        }
    }

    public void onWidthChanged(double value) {
        coordinates.setWidth(value);
        setTranslators();
    }

    public void onHeightChanged(double value) {
        coordinates.setHeight(value);
        setTranslators();
    }

    public boolean isResizable() {
        return true;
    }

    private void setTranslators() {
        translatorX = new Translator(coordinates.getGraphicsX0(), coordinates.getGraphicsX1(), domain.getX1(), domain.getX2());
        translatorY = new Translator(coordinates.getGraphicsY1(), coordinates.getGraphicsY0(), domain.getY1(), domain.getY2());
        translatorZ = new Translator(coordinates.getLegendY1(), coordinates.getLegendY0(), rangeMin, rangeMax);
    }

    private Color getColorInterpolation(double cur, double min, double max, Color minColor, Color maxColor) {
        double rMin = minColor.getRed();
        double gMin = minColor.getGreen();
        double bMin = minColor.getBlue();
        double rMax = maxColor.getRed();
        double gMax = maxColor.getGreen();
        double bMax = maxColor.getBlue();

        double r = truncate(((max - cur) * rMin + (cur - min) * rMax) / (max - min), 0, 1);
        double g = truncate(((max - cur) * gMin + (cur - min) * gMax) / (max - min), 0, 1);
        double b = truncate(((max - cur) * bMin + (cur - min) * bMax) / (max - min), 0, 1);

        return Color.color(r, g, b);
    }

    private double truncate(double color, double lo, double hi) {
        if (color > hi) {
            return hi;
        }
        if (color < lo) {
            return lo;
        }
        return color;
    }

    private void drawAllIsolines(GraphicsContext context) {
        context.setStroke(controller.getConfig().getContourColor());
        context.setLineWidth(1);
        context.setLineDashes();
        isolines.values().forEach(this::drawIsoline);
    }

    private void drawIsoline(ArrayList<Segment> segments) {
        GraphicsContext context = getGraphicsContext2D();
        context.setStroke(controller.getConfig().getContourColor());
        for (Segment segment : segments) {
            double x1 = translatorX.inverseTranslate(segment.getX1());
            double y1 = translatorY.inverseTranslate(segment.getY1());
            double x2 = translatorX.inverseTranslate(segment.getX2());
            double y2 = translatorY.inverseTranslate(segment.getY2());
            context.strokeLine(x1, y1, x2, y2);
        }
    }

    private void drawAllControlPoints(GraphicsContext context) {
        context.setFill(controller.getConfig().getContourColor());
        context.setLineWidth(1);
        context.setLineDashes();
        isolines.values().forEach(this::drawIsolineControlPoints);
    }

    private void drawIsolineControlPoints(ArrayList<Segment> segments) {
        double ovalRadius = 3;
        GraphicsContext context = getGraphicsContext2D();
        for (Segment segment : segments) {
            double x1 = translatorX.inverseTranslate(segment.getX1());
            double y1 = translatorY.inverseTranslate(segment.getY1());
            double x2 = translatorX.inverseTranslate(segment.getX2());
            double y2 = translatorY.inverseTranslate(segment.getY2());
            context.fillOval(x1 - ovalRadius / 2, y1 - ovalRadius / 2, ovalRadius, ovalRadius);
            context.fillOval(x2 - ovalRadius / 2, y2 - ovalRadius / 2, ovalRadius, ovalRadius);
        }
    }

    private void drawLegend(GraphicsContext context, int x0, int width) {
        int colorsCount = colors.size();
        double colorCellHeight = coordinates.getLegendColorMapHeight() / colorsCount;

        Translator translator = new Translator(
                coordinates.getLegendColorMapY1() - colorCellHeight,
                coordinates.getLegendColorMapY0() + colorCellHeight,
                rangeMin, rangeMax);

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < coordinates.getLegendColorMapHeight(); ++j) {
                int xPixel = i + x0;
                int yPixel = j + (int)coordinates.getLegendColorMapY0();
                double z = translator.translate(yPixel);

                Color color = getColor(z);
                context.getPixelWriter().setColor(xPixel, yPixel, color);
            }
        }
    }

    private int getChunk(double z, int chunkCount) {
        if (z >= rangeMax) {
            return chunkCount - 1;
        }
        if (z < rangeMin) {
            return 0;
        }
        return (int) floor(new Translator(rangeMin, rangeMax, 1, chunkCount - 1).translate(z));
    }

    private Color getColor(double z) {
        if (!interpolationEnabled) {
            int colorIndex = getChunk(z, colors.size());
            return colors.get(colorIndex);
        }

        double shift = (rangeMax - rangeMin) / (colors.size() - 1) / 2;
        Translator translator = new Translator(rangeMin + shift, rangeMax + shift,
                1, colors.size() - 1);

        double currentColorIndex = translator.translate(z);
        int colorBeforeIndex = (int) floor(truncate(currentColorIndex, 0, colors.size() - 1));
        int colorAfterIndex = (int) ceil(truncate(currentColorIndex, 0, colors.size() - 1));
        if (colorAfterIndex == colorBeforeIndex) {
            return colors.get(colorAfterIndex);
        }

        double zMin = translator.inverseTranslate(colorBeforeIndex);
        double zMax = translator.inverseTranslate(colorAfterIndex);
        return getColorInterpolation(z, zMin, zMax, colors.get(colorBeforeIndex), colors.get(colorAfterIndex));
    }

    private void drawColorMap(GraphicsContext context) {
        for(int i = 0; i < coordinates.getGraphicsWidth(); ++i) {
            for(int j = 0; j < coordinates.getGraphicsHeight(); ++j) {
                int xPixel = i + (int)coordinates.getGraphicsX0();
                int yPixel = j + (int)coordinates.getGraphicsY0();

                double xDomain = translatorX.translate(xPixel);
                double yDomain = translatorY.translate(yPixel);
                double z = controller.getFunction().valueAt(xDomain, yDomain);

                Color color = getColor(z);
                context.getPixelWriter().setColor(xPixel, yPixel, color);
            }
        }
    }

    private void drawLegendValues(GraphicsContext context) {
        context.setFill(Color.BLACK);
        double colorHeight = coordinates.getLegendColorMapHeight() / colors.size();
        context.setFont(new Font(min(12, colorHeight / 2)));
        ArrayList<String> values = legendValuesToString();
        Bounds bounds = getLongestLineBounds(values, context.getFont());

        double valuesWidth = 0;
        if (doValuesFit(bounds)) {
            valuesWidth = bounds.getWidth() + coordinates.getPadding();
            int xPixel = (int) coordinates.getLegendColorMapX0();
            for (int j = 1; j <= values.size(); ++j) {
                int yPixel = (int) (j * colorHeight + coordinates.getLegendColorMapY0());
                context.fillText(values.get(j - 1), xPixel, yPixel + context.getFont().getSize() / 2);
            }
        }

        double colorMapWidth = coordinates.getLegendColorMapWidth() - valuesWidth;
        int x0 = (int) (coordinates.getLegendX0() + valuesWidth + coordinates.getPadding());
        int width = (int) colorMapWidth;

        drawLegend(context, x0, width);
        context.strokeRect(x0, coordinates.getLegendColorMapY0(),
                width, coordinates.getLegendColorMapHeight());
    }

    private boolean doValuesFit(Bounds bounds) {
        double colorHeight = coordinates.getLegendColorMapHeight() / colors.size();
        double widthLeft = coordinates.getLegendColorMapWidth() - coordinates.getMinLegendColorMapWidth();
        return colorHeight >= bounds.getHeight() && widthLeft >= bounds.getWidth();
    }

    private Bounds getLongestLineBounds(ArrayList<String> values, Font font) {
        String longestString = "";
        for (String string : values) {
            if (string.length() > longestString.length()) {
                longestString = string;
            }
        }
        Text text = new Text(longestString);
        text.setFont(font);
        return text.getBoundsInLocal();
    }

    private ArrayList<String> legendValuesToString() {
        int colorsCount = colors.size();
        double colorCellHeight = coordinates.getLegendColorMapHeight() / colorsCount;
        Translator translator = new Translator(
                coordinates.getLegendColorMapY1() - colorCellHeight,
                coordinates.getLegendColorMapY0() + colorCellHeight,
                rangeMin, rangeMax);

        ArrayList<String> values = new ArrayList<>();
        for (double j = 1; j < colorsCount; ++j) {
            int yPixel = (int) (j * colorCellHeight + coordinates.getLegendColorMapY0());
            double z = translator.translate(yPixel);
            values.add(String.format("%s%.2f", z >= 0 ? " " : "", z));
        }
        return values;
    }

    private void drawGrid(GraphicsContext context) {
        context.setStroke(Color.WHITE);
        context.setLineWidth(1);
        context.setLineDashes(1, 5);
        context.setLineJoin(StrokeLineJoin.ROUND);

        BlendMode mode = context.getGlobalBlendMode();
        context.setGlobalBlendMode(BlendMode.DIFFERENCE);
        double x = coordinates.getGraphicsX0();
        for(int i = 1; i < coordinates.getGridXCount(); ++i) {
            x += coordinates.getGridXUnitLength();
            context.strokeLine(x, coordinates.getGraphicsY0(), x, coordinates.getGraphicsY1());
        }

        double y = coordinates.getGraphicsY0();
        for(int i = 1; i < coordinates.getGridYCount(); ++i) {
            y += coordinates.getGridYUnitLength();
            context.strokeLine(coordinates.getGraphicsX0(), y, coordinates.getGraphicsX1(), y);
        }
        context.setGlobalBlendMode(mode);
    }

    private void drawLegendBorder(GraphicsContext context) {
        context.setStroke(Color.BLACK);
        context.setLineWidth(1);
        context.setLineDashes();
        context.strokeRect(coordinates.getLegendX0(), coordinates.getLegendY0(),
                coordinates.getLegendWidth(), coordinates.getLegendHeight());
    }

    private void drawColorMapBorder(GraphicsContext context) {
        context.setStroke(Color.BLACK);
        context.setLineWidth(1);
        context.setLineDashes();
        context.strokeRect(coordinates.getGraphicsX0(), coordinates.getGraphicsY0(),
                coordinates.getGraphicsWidth(), coordinates.getGraphicsHeight());
    }

    private void drawBorders(GraphicsContext context) {
        context.setStroke(Color.BLACK);
        context.setLineWidth(1);
        context.setLineDashes();
        context.strokeRect(0.5D, 0.5D, coordinates.getWidth() - 1,
                coordinates.getHeight() - 1);
    }
}

