package wireframe;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import static java.lang.Math.cos;
import static java.lang.StrictMath.sin;

public class Renderer {
    public static final RealVector cameraPoint = MatrixUtils.createRealVector(new double[] {0, 0, 10});
    private static final RealVector cameraUpVector = MatrixUtils.createRealVector(new double[] {0, 1, 0});
    public static final RealVector referencePoint = MatrixUtils.createRealVector(new double[] {0, 0, -10});
    private static final RealVector uOrt;
    private static final RealVector vOrt;
    private static final RealVector wOrt;

    static {
        Vector3D cameraPoint3d = new Vector3D(cameraPoint.toArray());
        Vector3D cameraUpVector3d = new Vector3D(cameraUpVector.toArray());
        Vector3D referencePoint3d = new Vector3D(referencePoint.toArray());

        Vector3D wOrt3d = cameraPoint3d.subtract(referencePoint3d).normalize();
        Vector3D uOrt3d = cameraUpVector3d.crossProduct(wOrt3d).normalize();
        Vector3D vOrt3d = wOrt3d.crossProduct(uOrt3d);

        uOrt = MatrixUtils.createRealVector(uOrt3d.toArray());
        vOrt = MatrixUtils.createRealVector(vOrt3d.toArray());
        wOrt = MatrixUtils.createRealVector(wOrt3d.toArray());
    }

    private ArrayList<Segment> convertToHomogeneousCoordinates(ArrayList<Segment> segments) {
        ArrayList<Segment> rv = new ArrayList<>();
        for (Segment segment: segments) {
            RealVector vector1 = segment.getStart().append(1);
            RealVector vector2 = segment.getEnd().append(1);
            rv.add(new Segment(vector1, vector2, segment.getColor(), segment.getBody()));
        }
        return rv;
    }

    public static RealMatrix projectionMatrix(double width, double height, double front, double back) {
        return MatrixUtils.createRealMatrix(new double[][] {
                {2 * front / width , 0, 0, 0},
                {0, 2  * front / height, 0, 0},
                {0, 0, front / (back - front), (-front * back) / (back - front)},
                {0, 0, 1, 0}
        });
    }

    public static RealMatrix worldToCameraMatrix() {
        return MatrixUtils.createRealMatrix(new double[][]{
                {uOrt.getEntry(0), uOrt.getEntry(1), uOrt.getEntry(2),
                        -uOrt.getEntry(0) * cameraPoint.getEntry(0)},
                {vOrt.getEntry(0), vOrt.getEntry(1), vOrt.getEntry(2),
                        -uOrt.getEntry(1) * cameraPoint.getEntry(1)},
                {wOrt.getEntry(0), wOrt.getEntry(1), wOrt.getEntry(2),
                        -uOrt.getEntry(2) * cameraPoint.getEntry(2)},
                {0, 0, 0, 1}
        });
    }

    public static RealMatrix shiftMatrix(RealVector vector) {
        return MatrixUtils.createRealMatrix(new double[][] {
                {1, 0, 0, vector.getEntry(0)},
                {0, 1, 0, vector.getEntry(1)},
                {0, 0, 1, vector.getEntry(2)},
                {0, 0, 0, 1}
        });
    }

    public static RealMatrix rotationMatrix(RealVector anglesVector) {
        double[] angles = anglesVector.toArray();
        RealMatrix rotationOverX = MatrixUtils.createRealMatrix(new double[][] {
                {1, 0, 0, 0},
                {0, cos(angles[0]), -sin(angles[0]), 0},
                {0, sin(angles[0]), cos(angles[0]), 0},
                {0, 0, 0, 1}
        });
        RealMatrix rotationOverY = MatrixUtils.createRealMatrix(new double[][] {
                {cos(angles[1]), 0, sin(angles[1]), 0},
                {0, 1, 0, 0},
                {-sin(angles[1]), 0, cos(angles[1]), 0},
                {0, 0, 0, 1}
        });
        RealMatrix rotationOverZ = MatrixUtils.createRealMatrix(new double[][] {
                {cos(angles[2]), -sin(angles[2]), 0, 0},
                {sin(angles[2]), cos(angles[2]), 0, 0},
                {0, 0, 1, 0},
                {0, 0 , 0, 1}
        });
        return rotationOverX.multiply(rotationOverY).multiply(rotationOverZ);
    }

    public static RealMatrix scaleMatrix(double scaleCoefficient) {
        RealMatrix scaleMatrix = MatrixUtils
                .createRealIdentityMatrix(4)
                .scalarMultiply(scaleCoefficient);
        scaleMatrix.setEntry(3, 3, 1);
        return scaleMatrix;
    }

    private double scaleCoefficient(RevolutionBody body) {
        return 1 / Arrays.stream(body.getBox().toArray()).max().orElse(1);
    }

    public static ArrayList<Segment> operate(RealMatrix matrix, ArrayList<Segment> segments) {
        ArrayList<Segment> rv = new ArrayList<>();
        for (Segment segment: segments) {
            RealVector vector1 = matrix.operate(segment.getStart());
            RealVector vector2 = matrix.operate(segment.getEnd());
            rv.add(new Segment(vector1, vector2, segment.getColor(), segment.getBody()));
        }
        return rv;
    }

    public ArrayList<Segment> render(RevolutionBody body) {
        RealMatrix transformation  =
                MatrixUtils.createRealIdentityMatrix(4)
                .multiply(worldToCameraMatrix())
                .multiply(shiftMatrix(body.getCenter()))
                .multiply(scaleMatrix(scaleCoefficient(body)))
                .multiply(rotationMatrix(body.getAngles()));
        return operate(transformation, convertToHomogeneousCoordinates(body.getSegments()));
    }
}
