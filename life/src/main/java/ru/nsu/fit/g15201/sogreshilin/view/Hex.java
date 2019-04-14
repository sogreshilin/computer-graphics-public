package ru.nsu.fit.g15201.sogreshilin.view;

class Hex {
    public static final int VERTEX_COUNT = 6;
    private final int x;
    private final int y;
    private final int radius;
    private final Point[] vertexes = new Point[VERTEX_COUNT];


    public Hex(int x, int y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        initVertexes();
    }

    private void initVertexes() {
        int halfWidth = (int) Math.round(radius * Math.sin(Math.PI / 3));
        int halfRadius = radius / 2;

        vertexes[0] = new Point(x, y + radius);
        vertexes[1] = new Point(x + halfWidth, y + halfRadius);
        vertexes[2] = new Point(x + halfWidth, y - halfRadius);
        vertexes[3] = new Point(x, y - radius);
        vertexes[4] = new Point(x - halfWidth, y - halfRadius);
        vertexes[5] = new Point(x - halfWidth, y + halfRadius);
    }

    public Point[] getVertexes() {
        return vertexes;
    }
}
