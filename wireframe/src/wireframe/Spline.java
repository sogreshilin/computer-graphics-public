package wireframe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import static java.lang.Math.abs;
import static org.apache.commons.math3.linear.MatrixUtils.*;

import java.util.Optional;
import javafx.scene.paint.Color;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import wireframe.config.SplineConfig;

public class Spline {
    private SplineConfig config = new SplineConfig();
    private ArrayList<SplineChangedListener> listeners = new ArrayList<>();
    private double length;
    private LinkedHashMap<Double, RealVector> lengths = new LinkedHashMap<>();
    private double yRevolutionAxis = 145;
    private double xRevolutionAxis = 245;
    private RealMatrix splineMatrix = createRealMatrix(new double[][]{
            {-1, +3, -3, +1},
            {+3, -6, +3, +0},
            {-3, +0, +3, +0},
            {+1, +4, +1, +0}}).scalarMultiply(1D / 6);

    public Spline(SplineConfig config) {
        this.config = config;
        recompute();
    }

    public Spline(Spline other) {
        new Spline(other.config.copy());
        recompute();
    }

    public void addListener(SplineChangedListener listener) {
        this.listeners.add(listener);
        recompute();
    }

    public void addControlPoint(RealVector controlPoint) {
        config.getControlPoints().add(controlPoint);
        recompute();
    }

    public void rescaleControlPoints(Translator xTranslator, Translator yTranslator) {
        for (RealVector point: config.getControlPoints()) {
            point.setEntry(0, xTranslator.translate(point.getEntry(0)));
            point.setEntry(1, yTranslator.translate(point.getEntry(1)));
        }
        recompute();
    }

    private void recompute() {
        length = 0;
        lengths.clear();
        double dt = (config.getuEnd() - config.getuStart()) / (config.getuGridCount() * config.getkParameter());
        RealVector previousPoint = createRealVector(new double[] {0, 0});
        boolean firstIteration = true;
        for (int i = 1; i < config.getControlPoints().size() - 2; ++i) {
            RealMatrix Gx = getHermiteGeometricVector(i, 0);
            RealMatrix Gy = getHermiteGeometricVector(i, 1);
            for (double t = 0; t <= 1; t += dt) {
                RealMatrix tVector = createRealMatrix(new double[][]{{t * t * t, t * t, t, 1}});
                RealVector currentPoint = createRealVector(new double[] {
                    tVector.multiply(splineMatrix).multiply(Gx).getEntry(0, 0),
                    tVector.multiply(splineMatrix).multiply(Gy).getEntry(0, 0)
                });

                if (!firstIteration) {
                    length += previousPoint.getDistance(currentPoint);
                }
                lengths.put(length, currentPoint);
                previousPoint = currentPoint;
                firstIteration = false;
            }
        }
        listeners.forEach(listener -> listener.onSplineChanged(this));
    }

    public ArrayList<Segment> getPolygonalChain() {
        ArrayList<Segment> rv = new ArrayList<>();
        ArrayList<RealVector> points = new ArrayList<>(lengths.values());
        if (points.size() > 0) {
            RealVector previousPoint = points.get(0);
            for (int i = 1; i < points.size(); ++i) {
                RealVector currentPoint = points.get(i);
                rv.add(new Segment(previousPoint, currentPoint, config.getColor()));
                previousPoint = currentPoint;
            }
        }
        return rv;
    }

    private RealMatrix getHermiteGeometricVector(int i, int entry) {
        return createRealMatrix(new double[][]{
                {config.getControlPoints().get(i - 1).getEntry(entry)},
                {config.getControlPoints().get(i).getEntry(entry)},
                {config.getControlPoints().get(i + 1).getEntry(entry)},
                {config.getControlPoints().get(i + 2).getEntry(entry)}
        });
    }

    public ArrayList<RealVector> getControlPoints() {
        return config.getControlPoints();
    }

    public void removeControlPoint(RealVector controlPoint) {
        config.getControlPoints().remove(controlPoint);
        recompute();
    }

    public void setControlPoint(RealVector controlPoint, double x, double y) {
        controlPoint.setEntry(0, x);
        controlPoint.setEntry(1, y);
        recompute();
    }

    public Optional<RealVector> getSplinePointCorrespondsTo(double value) {
        Translator translator = new Translator(config.getuStart(), config.getuEnd(), 0, length);
        double coordinate = translator.translate(config.getuStart() * (1 - value) + config.getuEnd() * value);
        int index = abs(Arrays.binarySearch(lengths.keySet().toArray(), coordinate));
        if (index < lengths.size()) {
            RealVector point = (RealVector) lengths.values().toArray()[index];
            return Optional.of(createRealVector(new double[]{point.getEntry(0), point.getEntry(1)}));
        } else if (lengths.size() - 1 < 0) {
            return Optional.empty();
        }
        RealVector point = (RealVector) lengths.values().toArray()[lengths.size() - 1];
        return Optional.of(createRealVector(new double[]{point.getEntry(0), point.getEntry(1)}));
    }

    public void setUGridCount(int n) {
        config.setuGridCount(n);
        recompute();
    }

    public void setVGridCount(int m) {
        config.setvGridCount(m);
        recompute();
    }

    public void setKParameter(int k) {
        config.setkParameter(k);
        recompute();
    }

    public void setColor(Color color) {
        config.setColor(color);
        recompute();
    }


    public void setUDomain(RealVector domain) {
        config.setuStart(domain.getEntry(0));
        config.setuEnd(domain.getEntry(1));
        recompute();
    }

    public void setVDomain(RealVector domain) {
        config.setvStart(domain.getEntry(0));
        config.setvEnd(domain.getEntry(1));
        recompute();
    }

    public RealVector getDomainU() {
        return createRealVector(new double[] {config.getuStart(), config.getuEnd()});
    }

    public RealVector getDomainV() {
        return createRealVector(new double[] {config.getvStart(), config.getvEnd()});
    }

    public double getYRevolutionAxis() {
        return yRevolutionAxis;
    }

    public double getXRevolutionAxis() {
        return xRevolutionAxis;
    }

    public void setYRevolutionAxis(double yRevolutionAxis) {
        this.yRevolutionAxis = yRevolutionAxis;
    }

    public void setXRevolutionAxis(double xRevolutionAxis) {
        this.xRevolutionAxis = xRevolutionAxis;
    }

    public double getUStart() {
        return config.getuStart();
    }

    public double getUEnd() {
        return config.getuEnd();
    }

    public double getVStart() {
        return config.getvStart();
    }

    public double getVEnd() {
        return config.getvEnd();
    }

    public int getUGridCount() {
        return config.getuGridCount();
    }

    public int getVGridCount() {
        return config.getvGridCount();
    }

    public int getKParameter() {
        return config.getkParameter();
    }

    public Color getColor() {
        return config.getColor();
    }


}
