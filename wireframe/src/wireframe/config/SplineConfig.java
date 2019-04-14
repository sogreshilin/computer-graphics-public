package wireframe.config;

import java.util.ArrayList;
import javafx.scene.paint.Color;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import wireframe.Spline;

public class SplineConfig {
    private int uGridCount = 30;
    private int kParameter = 2;
    private int vGridCount = 30;
    private double uStart = 0;
    private double uEnd = 1;
    private double vStart = 0;
    private double vEnd = 2 * Math.PI;
    private Color color = Color.BLACK;
    private RealMatrix rotationMatrix = MatrixUtils.createRealIdentityMatrix(4);
    private ArrayList<RealVector> controlPoints = new ArrayList<>();

    public int getuGridCount() {
        return uGridCount;
    }

    public void setuGridCount(int uGridCount) {
        this.uGridCount = uGridCount;
    }

    public int getkParameter() {
        return kParameter;
    }

    public void setkParameter(int kParameter) {
        this.kParameter = kParameter;
    }

    public int getvGridCount() {
        return vGridCount;
    }

    public void setvGridCount(int vGridCount) {
        this.vGridCount = vGridCount;
    }

    public double getuStart() {
        return uStart;
    }

    public void setuStart(double uStart) {
        this.uStart = uStart;
    }

    public double getuEnd() {
        return uEnd;
    }

    public void setuEnd(double uEnd) {
        this.uEnd = uEnd;
    }

    public double getvStart() {
        return vStart;
    }

    public void setvStart(double vStart) {
        this.vStart = vStart;
    }

    public double getvEnd() {
        return vEnd;
    }

    public void setvEnd(double vEnd) {
        this.vEnd = vEnd;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public RealMatrix getRotationMatrix() {
        return rotationMatrix;
    }

    public void setRotationMatrix(RealMatrix rotationMatrix) {
        this.rotationMatrix = rotationMatrix;
    }

    public SplineConfig copy() {
        SplineConfig config = new SplineConfig();
        config.color = Color.color(color.getRed(), color.getGreen(), color.getBlue());
        config.rotationMatrix = rotationMatrix.copy();
        config.uGridCount = uGridCount;
        config.kParameter = kParameter;
        config.vGridCount = vGridCount;
        config.uStart = uStart;
        config.uEnd = uEnd;
        config.vStart = vStart;
        config.vEnd = vEnd;
        return config;
    }

    public void setControlPoints(ArrayList<RealVector> controlPoints) {
        this.controlPoints = controlPoints;
    }

    public ArrayList<RealVector> getControlPoints() {
        return controlPoints;
    }
}
