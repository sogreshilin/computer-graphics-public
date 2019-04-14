package wireframe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.scene.paint.Color;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;
import wireframe.config.BodyConfig;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class RevolutionBody implements SplineChangedListener {
    private static final double FROM_ANGLE = 0;
    private static final double TO_ANGLE = 2 * PI;
    private BodyConfig config;
    private Spline spline;
    private ArrayList<Segment> segments = new ArrayList<>();
    private RealVector box = MatrixUtils.createRealVector(new double[3]);
    private Optional<RevolutionBodyChangedListener> maybeListener = Optional.empty();
    private boolean areAxesDrawn = true;

    public RevolutionBody() {}

    public RevolutionBody(RevolutionBody other) {
        this.spline = new Spline(other.spline);
        this.spline.addListener(this);
        this.config = other.config.copy();
    }

    public RevolutionBody(BodyConfig config) {
        this.config = config;
        this.spline = new Spline(config.getSplineConfig());
        this.spline.addListener(this);
    }

    private RealVector findBox(List<RealVector> points) {
        RealVector box = MatrixUtils.createRealVector(new double[3]);
        box.setEntry(0, points.stream()
                .mapToDouble(point -> abs(point.getEntry(0))).max().orElse(0));
        box.setEntry(1, points.stream()
                .mapToDouble(point -> abs(point.getEntry(1))).max().orElse(0));
        box.setEntry(2, points.stream()
                .mapToDouble(point -> abs(point.getEntry(2))).max().orElse(0));
        return box;
    }

    private void recompute() {
        segments = new ArrayList<>();
        if (spline.getControlPoints().size() >= 4) {
            Translator vTranslator = new Translator(FROM_ANGLE, TO_ANGLE, spline.getVStart(), spline.getVEnd());

            for (int j = 0; j <= spline.getVGridCount(); ++j) {
                double v = vTranslator.translate(TO_ANGLE * j / spline.getVGridCount());
                Optional<RealVector> previousPoint = getRevolutionPoint(spline.getUStart(), v);
                for (int i = 0; i <= spline.getKParameter() * spline.getUGridCount(); ++i) {
                    double deltaU = (double) i / (spline.getKParameter() * spline.getUGridCount());
                    double currentU = spline.getUStart() * (1 - deltaU) + spline.getUEnd() * deltaU;
                    Optional<RealVector> currentPoint = getRevolutionPoint(currentU, v);
                    if (previousPoint.isPresent() && currentPoint.isPresent()) {
                        Segment segment = new Segment(previousPoint.get(), currentPoint.get(), spline.getColor(), this);
                        segments.add(segment);
                    }
                    previousPoint = currentPoint;
                }
            }

            for (int i = 0; i <= spline.getUGridCount(); ++i) {
                double deltaU = (double) i / spline.getUGridCount();
                double u = spline.getUStart() * (1 - deltaU) + spline.getUEnd() * deltaU;
                double previousV = spline.getVStart();
                for (int j = 0; j <= spline.getVGridCount(); ++j) {
                    double deltaV = (double) j / spline.getVGridCount();
                    double currentV = vTranslator.translate(0 * (1 - deltaV) + 2 * PI * deltaV);
                    Optional<RealVector> previousPoint = getRevolutionPoint(u, previousV);
                    Optional<RealVector> currentPoint = getRevolutionPoint(u, currentV);
                    if (previousPoint.isPresent() && currentPoint.isPresent()) {
                        Segment segment = new Segment(previousPoint.get(),
                                currentPoint.get(), spline.getColor(), this);
                        segments.add(segment);
                    }
                    previousV = currentV;
                }
            }

            box = findBox(getPoints(segments));
            double max = Arrays.stream(box.toArray()).max().orElse(1);
            RealVector zero = MatrixUtils.createRealVector(new double[]{0, 0, 0});
            RealVector Ox = MatrixUtils.createRealVector(new double[]{max, 0, 0});
            RealVector Oy = MatrixUtils.createRealVector(new double[]{0, max, 0});
            RealVector Oz = MatrixUtils.createRealVector(new double[]{0, 0, max});
            if (areAxesDrawn) {
                segments.add(new Segment(zero, Ox, Color.RED, this));
                segments.add(new Segment(zero, Oy, Color.GREEN, this));
                segments.add(new Segment(zero, Oz, Color.BLUE, this));
            }
            segments.addAll(Box.generateBoxSegments(box));
            notifyListener();
        }
    }

    private Optional<RealVector> getRevolutionPoint(double u, double v) {
        Optional<RealVector> maybePoint = spline.getSplinePointCorrespondsTo(u);
        if (maybePoint.isPresent()) {
            RealVector point = maybePoint.get();

            point.setEntry(0, point.getEntry(0) - spline.getXRevolutionAxis());
            point.setEntry(1, point.getEntry(1) - spline.getYRevolutionAxis());
            return Optional.of(MatrixUtils.createRealVector(new double[]{
                    point.getEntry(1) * cos(v),
                    point.getEntry(1) * sin(v),
                    point.getEntry(0)
            }));
        }
        return Optional.empty();
    }

    public static List<RealVector> getPoints(ArrayList<Segment> segments) {
        if (segments.isEmpty()) {
            return new ArrayList<>();
        }
        return Stream.concat(
                Stream.of(segments.get(0).getStart()),
                segments.stream()
                    .map(Segment::getEnd))
                    .collect(Collectors.toList());
    }

    public void setListener(RevolutionBodyChangedListener listener) {
        this.maybeListener = Optional.of(listener);
    }

    public RealVector getBox() {
        return box;
    }

    public void setAngles(RealVector angles) {
        config.setAngles(angles);
        notifyListener();
    }

    public RealVector getAngles() {
        return config.getAngles();
    }

    public ArrayList<Segment> getSegments() {
        return segments;
    }

    public void setSegments(ArrayList<Segment> segments) {
        this.segments = segments;
    }

    public Spline getSpline() {
        return spline;
    }

    @Override
    public void onSplineChanged(Spline spline) {
        recompute();
    }

    public void setCenter(RealVector center) {
        config.setCenter(center);
        notifyListener();
    }

    public RealVector getCenter() {
        return config.getCenter();
    }

    private void notifyListener() {
        maybeListener.ifPresent(listener -> listener.onRevolutionBodyChanged(this));
    }

    public void setXCenter(int x) {
        config.getCenter().setEntry(0, x);
        notifyListener();
    }

    public void setYCenter(int y) {
        config.getCenter().setEntry(1, y);
        notifyListener();
    }

    public void setZCenter(int z) {
        config.getCenter().setEntry(2, z);
        notifyListener();
    }

    public int getXCenter() {
        return (int) Math.round(config.getCenter().getEntry(0));
    }

    public int getYCenter() {
        return (int) Math.round(config.getCenter().getEntry(1));
    }

    public int getZCenter() {
        return (int) Math.round(config.getCenter().getEntry(2));
    }

    public void setXAngle(int x) {
        config.getAngles().setEntry(0, x * PI / 180);
        notifyListener();
    }

    public void setYAngle(int x) {
        config.getAngles().setEntry(1, x * PI / 180);
        notifyListener();
    }

    public void setZAngle(int x) {
        config.getAngles().setEntry(2, x * PI / 180);
        notifyListener();
    }

    public int getXAngle() {
        return (int) Math.round(config.getAngles().getEntry(0) * 180 / PI);
    }

    public int getYAngle() {
        return (int) Math.round(config.getAngles().getEntry(1) * 180 / PI);
    }

    public int getZAngle() {
        return (int) Math.round(config.getAngles().getEntry(2) * 180 / PI);
    }

    public void setDrawAxes(boolean drawAxes) {
        this.areAxesDrawn = drawAxes;
        recompute();
    }

    public boolean getAreAxesDrawn() {
        return areAxesDrawn;
    }

    public BodyConfig getConfig() {
        return config;
    }
}
