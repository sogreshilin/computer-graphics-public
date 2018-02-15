package ru.nsu.fit.g15201.sogreshilin;

import javax.swing.*;
import ru.nsu.fit.g15201.sogreshilin.Bresenham;
import ru.nsu.fit.g15201.sogreshilin.SpanFiller;



import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class Canvas extends JPanel {
    private BufferedImage canvas;
    private int columnsCount;
    private int rowsCount;
    private int hexWidth;
    private int hexHeight;
    private int hexRadius;

    public Canvas(int columnsCount, int rowsCount, int radius) {
        this.hexRadius = 2 * radius;
        this.hexWidth = 2 * (int) Math.round(hexRadius * Math.sin(Math.PI / 3));
        this.hexHeight = 2 * hexRadius;

        this.columnsCount = columnsCount;
        this.rowsCount = rowsCount;

        int imageWidth = columnsCount * hexWidth + 1;
        int imageHeight = (3 * rowsCount + 1) * hexRadius/ 2 + 1;

        this.canvas = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        setMouseListeners();
    }


    private void setMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                if (isInsideImage(x, y) && canvas.getRGB(x, y) != Constants.GRID_BORDER_COLOR.getRGB()) {
                    if (isInField(getHexByPixel(x, y))) {
                        fill(x, y, Constants.FILL_COLOR);
                    }
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                if (isInsideImage(x, y) && canvas.getRGB(x, y) != Constants.GRID_BORDER_COLOR.getRGB()) {
                    if (isInField(getHexByPixel(x, y))) {
                        fill(x, y, Constants.FILL_COLOR);
                    }
                }
            }
        });
    }


    private boolean isInsideImage(int x, int y) {
        return x >= 0 && x < canvas.getWidth() &&
               y >= 0 && y < canvas.getHeight();
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


    public void drawLine(int x0, int y0, int x1, int y1, Color color) {
        Bresenham.drawLine(canvas, x0, y0, x1,y1, color);
    }


    public void drawLine(int x0, int y0, int x1, int y1, Color color, int stroke) {
        if (stroke == 1) {
            drawLine(x0, y0, x1, y1, color);
            return;
        }
        BasicStroke bs = new BasicStroke(stroke);
        Graphics2D graphics2D = canvas.createGraphics();
        graphics2D.setColor(color);
        graphics2D.setStroke(bs);
        graphics2D.drawLine(x0, y0, x1, y1);
    }


    public void drawHex(int x, int y, int radius, Color color){
        Hex hex = new Hex(x, y, radius);
        Point[] vertexes = hex.getVertexes();
        for (int i = 0; i < Hex.VERTEX_COUNT; ++i) {
            int j = (i + 1) % Hex.VERTEX_COUNT;
            drawLine(vertexes[i].getX(), vertexes[i].getY(),
                     vertexes[j].getX(), vertexes[j].getY(),
                     color);
        }
    }


    public void fill(int x, int y, Color color) {
        SpanFiller.fill(canvas, x, y, color);
        repaint();
    }


    public void drawField() {
        int x = 0;
        int y = hexRadius;
        for (int i = 0; i < rowsCount; ++i){
            x = (i % 2 == 0) ? hexWidth / 2 : hexWidth;
            for (int j = 0; j < (columnsCount - i % 2); ++j) {
                drawHex(x, y, hexRadius, Constants.GRID_BORDER_COLOR);
                x += hexWidth;
            }
            y += 3 * hexRadius / 2;
        }
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ((Graphics2D) g).drawImage(canvas, null, null);
    }


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(canvas.getWidth(), canvas.getHeight());
    }


    public Point getHexByPixel(int x, int y) {
        int s = hexWidth;
        int h = 3 * hexRadius / 2;

        // cannot just use (y / h) because it's not equivalent for negatives
        int jt = (int) Math.floor(1. * y / h);
        int xts = x - (jt % 2) * (s / 2);
        int it = (int) Math.floor(1. * xts / s);

        int xt = xts - it * s;
        int yt = y - jt * h;

        int j = (yt > hexRadius * Math.abs(.5 - 1. * xt / s)) ? jt : jt - 1;
        int deltai = (xt > s / 2) ? 1 : 0;
        int i = (yt > hexRadius * Math.abs(.5 - 1. * xt / s)) ? it : it - (j % 2) + deltai;

        return new Point(i, j);
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Life");
        Canvas panel = new Canvas(10,10, 12);
        JScrollPane scrollPane = new JScrollPane(panel);

        frame.add(scrollPane);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel.drawField();
    }
}
