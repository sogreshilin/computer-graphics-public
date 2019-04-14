package wireframe;

import javafx.scene.paint.Color;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

public class Segment {
    private static final Color DEFAULT_COLOR = Color.BLACK;
    private RealVector start;
    private RealVector end;
    private Color color;
    private RevolutionBody body = null;

    public Segment(double x1, double y1, double x2, double y2) {
        this.start = MatrixUtils.createRealVector(new double[] {x1, y1});
        this.end = MatrixUtils.createRealVector(new double[] {x2, y2});
        this.color = DEFAULT_COLOR;
    }

    public Segment(double x1, double y1, double x2, double y2, Color color) {
        this.start = MatrixUtils.createRealVector(new double[] {x1, y1});
        this.end = MatrixUtils.createRealVector(new double[] {x2, y2});
        this.color = color;
    }

    public Segment(RealVector start, RealVector end) {
        this.start = start;
        this.end = end;
        this.color = DEFAULT_COLOR;
    }

    public Segment(RealVector start, RealVector end, Color color) {
        this.start = start;
        this.end = end;
        this.color = color;
    }

    public Segment(RealVector start, RealVector end, Color color, RevolutionBody body) {
        this.start = start;
        this.end = end;
        this.color = color;
        this.body = body;
    }

    public Segment(Segment segment) {
        this.start = segment.start;
        this.end = segment.end;
        this.color = segment.color;
    }

    public RealVector getStart() {
        return start;
    }

    public RealVector getEnd() {
        return end;
    }

    public Color getColor() {
        return color;
    }

    public RevolutionBody getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "(" + start + ", " + end + "), color = " + color;
    }
}
