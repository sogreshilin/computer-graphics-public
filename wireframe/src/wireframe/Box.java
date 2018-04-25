package wireframe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.DoubleStream;
import javafx.scene.paint.Color;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

import static java.lang.Math.abs;


public class Box extends RevolutionBody {
    public double xMin = 1000;
    public double xMax = -1000;
    public double yMin = 1000;
    public double yMax = -1000;
    public double zMin = 1000;
    public double zMax = -1000;

    public void update(RealVector point) {
        xMin = Math.min(point.getEntry(0), xMin);
        xMax = Math.max(point.getEntry(0), xMax);
        yMin = Math.min(point.getEntry(1), yMin);
        yMax = Math.max(point.getEntry(1), yMax);
        zMin = Math.min(point.getEntry(2), zMin);
        zMax = Math.max(point.getEntry(2), zMax);
    }

    public ArrayList<Segment> getSegments() {
        ArrayList<Segment> cube = new ArrayList<>();
        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{xMin, yMin, zMin}), MatrixUtils.createRealVector(new double[]{xMin, yMin, zMax}), Color.GRAY));//side, e
        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{xMax, yMin, zMax}), MatrixUtils.createRealVector(new double[]{xMax, yMin, zMin}), Color.GRAY));//side, f
        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{xMax, yMax, zMax}), MatrixUtils.createRealVector(new double[]{xMax, yMax, zMin}), Color.GRAY));//side, g
        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{xMin, yMax, zMax}), MatrixUtils.createRealVector(new double[]{xMin, yMax, zMin}), Color.GRAY));//side, h

        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{xMin, yMin, zMin}), MatrixUtils.createRealVector(new double[]{xMin, yMax, zMin}), Color.GRAY));//top, i
        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{xMin, yMin, zMin}), MatrixUtils.createRealVector(new double[]{xMax, yMin, zMin}), Color.GRAY));//top, j
        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{xMax, yMax, zMin}), MatrixUtils.createRealVector(new double[]{xMax, yMin, zMin}), Color.GRAY));//top, k
        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{xMax, yMax, zMin}), MatrixUtils.createRealVector(new double[]{xMin, yMax, zMin}), Color.GRAY));//top,l

        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{xMin, yMin, zMax}), MatrixUtils.createRealVector(new double[]{xMax, yMin, zMax}), Color.GRAY));//bottom, b
        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{xMin, yMin, zMax}), MatrixUtils.createRealVector(new double[]{xMin, yMax, zMax}), Color.GRAY));//bottom, a
        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{xMax, yMin, zMax}), MatrixUtils.createRealVector(new double[]{xMax, yMax, zMax}), Color.GRAY));//bottom, c
        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{xMin, yMax, zMax}), MatrixUtils.createRealVector(new double[]{xMax, yMax, zMax}), Color.GRAY));//bottom, d

        double axisLength = DoubleStream.of(abs(xMax) + abs(xMin), abs(yMax )+ abs(yMin), abs(zMax) + abs(zMin)).min().orElse(1) / 2;
        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{xMax + xMin, yMax + yMin, zMax + zMin}).mapMultiply(0.5), MatrixUtils.createRealVector(new double[]{(xMax + xMin) / 2 + axisLength, (yMax + yMin) / 2, (zMax + zMin) / 2}), Color.RED));//Ox
        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{xMax + xMin, yMax + yMin, zMax + zMin}).mapMultiply(0.5), MatrixUtils.createRealVector(new double[]{(xMax + xMin) / 2, (yMax + yMin) / 2 + axisLength, (zMax + zMin) / 2}), Color.GREEN));//Oy
        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{xMax + xMin, yMax + yMin, zMax + zMin}).mapMultiply(0.5), MatrixUtils.createRealVector(new double[]{(xMax + xMin) / 2, (yMax + yMin) / 2, (zMax + zMin) / 2 + axisLength}), Color.BLUE));//Oz
        return cube;
    }

    @Override
    public RealVector getCenter() {
        return MatrixUtils.createRealVector(new double[3]);
    }

    @Override
    public RealVector getAngles() {
        return MatrixUtils.createRealVector(new double[3]);
    }

    public RealVector getBox() {
        return MatrixUtils.createRealVector(new double[]{1, 1, 1});
    }

    public static ArrayList<Segment> generateBoxSegments(RealVector box) {
        ArrayList<Segment> cube = new ArrayList<>();
        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{-box.getEntry(0), -box.getEntry(1), -box.getEntry(2)}), MatrixUtils.createRealVector(new double[]{-box.getEntry(0), -box.getEntry(1), +box.getEntry(2)}), Color.TRANSPARENT));//side, e
        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{+box.getEntry(0), -box.getEntry(1), +box.getEntry(2)}), MatrixUtils.createRealVector(new double[]{+box.getEntry(0), -box.getEntry(1), -box.getEntry(2)}), Color.TRANSPARENT));//side, f
        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{+box.getEntry(0), +box.getEntry(1), +box.getEntry(2)}), MatrixUtils.createRealVector(new double[]{+box.getEntry(0), +box.getEntry(1), -box.getEntry(2)}), Color.TRANSPARENT));//side, g
        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{-box.getEntry(0), +box.getEntry(1), +box.getEntry(2)}), MatrixUtils.createRealVector(new double[]{-box.getEntry(0), +box.getEntry(1), -box.getEntry(2)}), Color.TRANSPARENT));//side, h

        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{-box.getEntry(0), -box.getEntry(1), -box.getEntry(2)}), MatrixUtils.createRealVector(new double[]{-box.getEntry(0), +box.getEntry(1), -box.getEntry(2)}), Color.TRANSPARENT));//top, i
        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{-box.getEntry(0), -box.getEntry(1), -box.getEntry(2)}), MatrixUtils.createRealVector(new double[]{+box.getEntry(0), -box.getEntry(1), -box.getEntry(2)}), Color.TRANSPARENT));//top, j
        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{+box.getEntry(0), +box.getEntry(1), -box.getEntry(2)}), MatrixUtils.createRealVector(new double[]{+box.getEntry(0), -box.getEntry(1), -box.getEntry(2)}), Color.TRANSPARENT));//top, k
        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{+box.getEntry(0), +box.getEntry(1), -box.getEntry(2)}), MatrixUtils.createRealVector(new double[]{-box.getEntry(0), +box.getEntry(1), -box.getEntry(2)}), Color.TRANSPARENT));//top,l

        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{-box.getEntry(0), -box.getEntry(1), +box.getEntry(2)}), MatrixUtils.createRealVector(new double[]{+box.getEntry(0), -box.getEntry(1), +box.getEntry(2)}), Color.TRANSPARENT));//bottom, b
        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{-box.getEntry(0), -box.getEntry(1), +box.getEntry(2)}), MatrixUtils.createRealVector(new double[]{-box.getEntry(0), +box.getEntry(1), +box.getEntry(2)}), Color.TRANSPARENT));//bottom, a
        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{+box.getEntry(0), -box.getEntry(1), +box.getEntry(2)}), MatrixUtils.createRealVector(new double[]{+box.getEntry(0), +box.getEntry(1), +box.getEntry(2)}), Color.TRANSPARENT));//bottom, c
        cube.add(new Segment(MatrixUtils.createRealVector(new double[]{-box.getEntry(0), +box.getEntry(1), +box.getEntry(2)}), MatrixUtils.createRealVector(new double[]{+box.getEntry(0), +box.getEntry(1), +box.getEntry(2)}), Color.TRANSPARENT));//bottom, d
        return cube;
    }
}
