package ru.nsu.fit.g15201.sogreshilin.view;

import java.util.ArrayList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import ru.nsu.fit.g15201.sogreshilin.controller.Config;
import ru.nsu.fit.g15201.sogreshilin.controller.Controller;
import ru.nsu.fit.g15201.sogreshilin.model.Domain;
import ru.nsu.fit.g15201.sogreshilin.model.Segment;
import ru.nsu.fit.g15201.sogreshilin.model.Translator;

public class MyCanvas extends Canvas {
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

    private ArrayList<ArrayList<Segment>> isolines = new ArrayList<>();
    private boolean isolinesEnabled = true;

    public MyCanvas(Controller controller) {
        super(800, 600 - 39 - 20);
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
        drawColorMapBorder(context);
        drawBorders(context);
        drawLegend(context);
        drawLegendBorder(context);
        drawLegendValues(context);
    }

    public void drawAllIsolines(GraphicsContext context) {
        context.setStroke(controller.getConfig().getContourColor());
        context.setLineWidth(1);
        context.setLineDashes();
        isolines.forEach(this::drawIsoline);
    }

    public void drawIsoline(ArrayList<Segment> segments) {
        GraphicsContext context = getGraphicsContext2D();
        for (Segment segment : segments) {
            double x1 = translatorX.inverseTranslate(segment.getX1());
            double y1 = translatorY.inverseTranslate(segment.getY1());
            double x2 = translatorX.inverseTranslate(segment.getX2());
            double y2 = translatorY.inverseTranslate(segment.getY2());
            context.strokeLine(x1, y1, x2, y2);
        }
    }

    public void addIsoline(ArrayList<Segment> segments) {
        isolines.add(segments);
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

    public void setGridX(int x) {
        setGrid(x, coordinates.getGridYCount());
    }

    public void setGridY(int y) {
        setGrid(coordinates.getGridXCount(), y);
    }

    public void setGrid(int x, int y) {
        coordinates.setGridXCount(x);
        coordinates.setGridYCount(y);
    }



    private boolean isInsideGraphics(double x, double y) {
        return coordinates.getGraphicsX0() <= x && x <= coordinates.getGraphicsX1() &&
                coordinates.getGraphicsY0() <= y && y <= coordinates.getGraphicsY1();
    }

    public void setConfig(Config config) {
        coordinates.setGridXCount(config.getGrid().getFirst());
        coordinates.setGridYCount(config.getGrid().getSecond());
    }

    public void onWidthChanged(double value) {
        coordinates.setWidth(value);
        setTranslators();
    }

    public void onHeightChanged(double value) {
        coordinates.setHeight(value - 39-20);
        setTranslators();
    }

    public void onDomainChanged() {
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

    public void setDomain(Domain domain) {
        this.domain = domain;
        setTranslators();
    }

    private void drawLegend(GraphicsContext context) {
        for (int i = 0; i < coordinates.getLegendWidth(); ++i) {
            for (int j = 0; j < coordinates.getLegendHeight(); ++j) {
                int xPixel = i + (int)coordinates.getLegendX0();
                int yPixel = j + (int)coordinates.getLegendY0();
                double z = translatorZ.translate(yPixel);

                int colorIndex = controller.getChunk(z, controller.getConfig().getKeyValueCount());
                Color color = controller.getConfig().getColors().get(colorIndex);
                context.getPixelWriter().setColor(xPixel, yPixel, color);
            }
        }
    }

    private void drawLegendValues(GraphicsContext context) {
        double step = coordinates.getLegendHeight() / (controller.getConfig().getKeyValueCount() + 1);
        if (step > 20 && coordinates.getLegendWidth() > 42) {
            for (double j = 1; j <= controller.getConfig().getKeyValueCount(); ++j) {
                int xPixel = (int) coordinates.getLegendX0();
                int yPixel = (int) (j * step + coordinates.getLegendY0());
                double z = translatorZ.translate(yPixel);
                context.fillText(String.format(" %s%.2f", z >= 0 ? " " : "", z), xPixel, yPixel);
            }
        }
    }

    private void drawColorMap(GraphicsContext context) {
        for(int i = 0; i < coordinates.getGraphicsWidth(); ++i) {
            for(int j = 0; j < coordinates.getGraphicsHeight(); ++j) {
                int xPixel = i + (int)coordinates.getGraphicsX0();
                int yPixel = j + (int)coordinates.getGraphicsY0();

                double xDomain = translatorX.translate(xPixel);
                double yDomain = translatorY.translate(yPixel);


                int colorIndex = controller.getChunk(xDomain, yDomain, controller.getConfig().getKeyValueCount());
                Color color = controller.getConfig().getColors().get(colorIndex);
                context.getPixelWriter().setColor(xPixel, yPixel, color);
            }
        }
    }

    private void drawGrid(GraphicsContext context) {
        context.setStroke(Color.GRAY);
        context.setLineWidth(1);
        context.setLineDashes(5);
        context.setLineJoin(StrokeLineJoin.ROUND);

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

