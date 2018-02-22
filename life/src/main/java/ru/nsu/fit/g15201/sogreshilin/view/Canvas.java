package ru.nsu.fit.g15201.sogreshilin.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import ru.nsu.fit.g15201.sogreshilin.model.io.Config;
import ru.nsu.fit.g15201.sogreshilin.model.State;

public class Canvas extends JPanel {
    private BufferedImage canvas;
    private BufferedImage impacts;
    private int columnsCount;
    private int rowsCount;
    private int hexWidth;
    private int hexHeight;
    private int hexRadius;
    private BresenhamLiner liner = new BresenhamLiner();
    private Config config;
    private boolean showImpacts = false;

    private static final Color ALIVE_CELL_COLOR = new Color(0, 128, 43);
    private static final Color DEAD_CELL_COLOR = new Color(238, 238, 238);


    private List<HexagonClickedObserver> observers = new ArrayList<>();
    private boolean mouseListenersEnabled = true;

    public Canvas(Config config) {
        setConfig(config);
        setMouseListeners();
    }

    private int ceilToEven(int number) {
        return number + (number & 1);
    }

    private void drawText(BufferedImage image, int x, int y, String text) {
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setPaint(Color.BLACK);
        int size = 3 * hexRadius / 4;
        graphics2D.setFont(new Font("Serif", Font.BOLD, size));
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        x -= fontMetrics.stringWidth(text) / 2;
        y += 1 * fontMetrics.getHeight() / 3;
        graphics2D.drawString(text, x, y);
        graphics2D.dispose();
    }

    private Point lastColoredHex;

    private void setMouseListeners() {
        addMouseListener(new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (mouseListenersEnabled) {
                    int x = e.getX();
                    int y = e.getY();
                    if (isInsideImage(x, y) && canvas.getRGB(x, y) != Constants.GRID_BORDER_COLOR.getRGB()) {
                        Point currentHex = getHexByPixel(x, y);
                        if (currentHex.equals(lastColoredHex)) {
                            return;
                        }
                        lastColoredHex = currentHex;
                        if (isInField(lastColoredHex)) {
                            notifyHexagonClicked(lastColoredHex.getX(), lastColoredHex.getY());
                            repaint();
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (mouseListenersEnabled) {
                    lastColoredHex = null;
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (mouseListenersEnabled) {
                    int x = e.getX();
                    int y = e.getY();
                    if (isInsideImage(x, y) && canvas.getRGB(x, y) != Constants.GRID_BORDER_COLOR.getRGB()) {
                        Point currentHexagon = getHexByPixel(x, y);
                        if (currentHexagon.equals(lastColoredHex)) {
                            return;
                        }
                        lastColoredHex = currentHexagon;
                        if (isInField(lastColoredHex)) {
                            notifyHexagonClicked(lastColoredHex.getX(), lastColoredHex.getY());
                            repaint();
                        }
                    }
                }
            }
        });
    }

    private int imageWidth;
    private int imageHeight;

    public void setConfig(Config config) {
        this.config = config;
        this.hexRadius = ceilToEven(config.getCellSize());
        this.hexWidth = 2 * (int) Math.round(hexRadius * Math.sin(Math.PI / 3));
        this.hexHeight = 2 * hexRadius;

        this.columnsCount = config.getFieldWidth();
        this.rowsCount = config.getFieldHeight();

        this.topMargin = config.getLineThickness() / 2 + 1;
        this.leftMargin = config.getLineThickness() / 2 + 1;
        this.imageWidth = columnsCount * hexWidth + 2 * topMargin;
        this.imageHeight = (3 * rowsCount + 1) * hexRadius / 2 + 2 * topMargin;

        this.canvas = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        this.impacts = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

        liner.setLineThickness(config.getLineThickness());
        drawField();
        repaint();
    }

    private boolean isInsideImage(int x, int y) {
        return x >= 0 && x < canvas.getWidth() &&
                y >= 0 && y < canvas.getHeight();
    }

    public void setLiner(BresenhamLiner liner) {
        this.liner = liner;
    }

    private boolean isInField(Point hexagon) {
        int i = hexagon.getX();
        int j = hexagon.getY();
        if (j < 0 || i < 0 || j >= rowsCount) {
            return false;
        }

        boolean isEvenRow = j % 2 == 0;
        return (!isEvenRow || i < columnsCount) && (isEvenRow || i < columnsCount - 1);
    }


    public void drawLine(int x0, int y0, int x1, int y1) {
        if (liner.getLineThickness() == 1) {
            liner.drawLine(canvas, x0, y0, x1, y1);
        } else {
            drawLine(x0, y0, x1, y1, Color.BLACK, liner.getLineThickness());
        }
    }


    public void drawLine(int x0, int y0, int x1, int y1, Color color, int stroke) {
        if (stroke == 1) {
            drawLine(x0, y0, x1, y1);
            return;
        }
        BasicStroke bs = new BasicStroke(stroke);
        Graphics2D graphics2D = canvas.createGraphics();
        graphics2D.setColor(color);
        graphics2D.setStroke(bs);
        graphics2D.drawLine(x0, y0, x1, y1);
    }


    public void drawHex(int x, int y, int radius, Color color) {
        Hex hex = new Hex(x, y, radius);
        Point[] vertexes = hex.getVertexes();
        if (liner.getLineThickness() == 1) {
            for (int i = 0; i < Hex.VERTEX_COUNT; ++i) {
                int j = (i + 1) % Hex.VERTEX_COUNT;
                drawLine(vertexes[i].getX(), vertexes[i].getY(),
                        vertexes[j].getX(), vertexes[j].getY());
            }
        } else {
            BasicStroke bs = new BasicStroke(liner.getLineThickness());
            Graphics2D graphics2D = canvas.createGraphics();
            graphics2D.setColor(color);
            graphics2D.setStroke(bs);
            int[] xs = new int[Hex.VERTEX_COUNT];
            int[] ys = new int[Hex.VERTEX_COUNT];
            for (int i = 0; i < Hex.VERTEX_COUNT; ++i) {
                xs[i] = vertexes[i].getX();
                ys[i] = vertexes[i].getY();
            }
            graphics2D.drawPolygon(xs, ys, Hex.VERTEX_COUNT);
        }

    }


    public void fill(int x, int y, Color color) {
        SpanFiller.fill(canvas, x, y, color);
    }

    private int leftMargin;
    private int topMargin;

    public void drawField() {
        int x;
        int y = topMargin + hexRadius;
        for (int i = 0; i < rowsCount; ++i) {
            x = leftMargin + ((i % 2 == 0) ? hexWidth / 2 : hexWidth);
            for (int j = 0; j < (columnsCount - i % 2); ++j) {
                drawHex(x, y, hexRadius, Constants.GRID_BORDER_COLOR);
                x += hexWidth;
            }
            y += 3 * hexRadius / 2;
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ((Graphics2D) g).drawImage(canvas, null, null);
        if (showImpacts) {
            ((Graphics2D) g).drawImage(impacts, null, null);
        }
    }


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(canvas.getWidth(), canvas.getHeight());
    }

    public Point getPixelByHex(int i, int j) {
        int x = (1 + (i & 1)) * hexWidth / 2 + hexWidth * j;
        int y = 3 * hexRadius * i / 2 + hexRadius;
        return new Point(x, y);
    }


    public Point getHexByPixel(int x, int y) {
        int s = hexWidth;
        int h = 3 * hexRadius / 2;

        /* (it, jt) - coordinates of rectangle */
        int jt = (int) Math.floor(1. * y / h);
        int xts = x - (jt % 2) * (s / 2);
        int it = (int) Math.floor(1. * xts / s);

        /* (xt, yt) - coordinates of pixel in rectangle */
        int xt = xts - it * s;
        int yt = y - jt * h;

        /* (i, j) - coordinates of hexagon */
        int j = (yt > hexRadius * Math.abs(.5 - 1. * xt / s)) ? jt : jt - 1;
        int deltai = (xt > s / 2) ? 1 : 0;
        int i = (yt > hexRadius * Math.abs(.5 - 1. * xt / s)) ? it : it - (j % 2) + deltai;

        return new Point(i, j);
    }

    public void addObserver(HexagonClickedObserver observer) {
        observers.add(observer);
    }

    private void notifyHexagonClicked(int i, int j) {
        for (HexagonClickedObserver observer : observers) {
            observer.onHexagonClicked(i, j);
        }
    }

    private void clearImage(BufferedImage image) {
        Graphics2D graphics = image.createGraphics();
        graphics.setComposite(AlphaComposite.Clear);
        int imageWidth = columnsCount * hexWidth + 1;
        int imageHeight = (3 * rowsCount + 1) * hexRadius / 2 + 1;
        graphics.fillRect(0, 0, imageWidth, imageHeight);
        graphics.setComposite(AlphaComposite.SrcOver);
    }

    public boolean showsImpacts() {
        return showImpacts;
    }

    public void switchShowImpacts() {
        showImpacts ^= true;
    }

    public void fillHexagon(int i, int j, State state) {
        Color color;
        switch (state) {
            case ALIVE: color = ALIVE_CELL_COLOR; break;
            case DEAD: color = DEAD_CELL_COLOR; break;
            default: throw new RuntimeException("Unexpected case");
        }
        int x = (1 + (i & 1)) * hexWidth / 2 + hexWidth * j;
        int y = 3 * hexRadius * i / 2 + hexRadius;
        fill(x, y, color);
    }

    public void clearImpacts() {
        clearImage(impacts);
        repaint();
    }

    public void drawImpacts(double[] doubles) {
        if (!showImpacts) {
            repaint();
            return;
        }
        clearImage(impacts);
        int k = 0;
        for (int i = 0; i < rowsCount; ++i) {
            for (int j = 0; j < (columnsCount - i % 2); ++j) {
                Point hexCenter = getPixelByHex(i, j);
                double impact = doubles == null ? 0 : doubles[k];
                drawText(impacts, leftMargin + hexCenter.getX(), topMargin + hexCenter.getY(), numberToString(impact));
                ++k;
            }
        }
        repaint();
    }

    private String numberToString(double impact) {
        if ((int) impact == impact) {
            return String.format("%d", (int) impact);
        } else {
            return String.format("%2.1f", impact);
        }
    }

    public void clearField() {
        clearImage(canvas);
        drawField();
    }

    public void setMouseListenersEnabled(boolean b) {
        this.mouseListenersEnabled = b;
    }
}