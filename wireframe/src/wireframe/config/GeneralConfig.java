package wireframe.config;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

public class GeneralConfig {
    public static final GeneralConfig DEFAULT_CONFIG = new GeneralConfig();
    private Color backgroundColor = Color.WHITE;
    private double pyramidFront = 2;
    private double pyramidBack = 100;
    private double pyramidWidth = 0.002;
    private double pyramidHeight = 0.002;
    private RealVector angles = MatrixUtils.createRealVector(new double[3]);
    private List<BodyConfig> bodyConfigs = new ArrayList<>();

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public double getPyramidFront() {
        return pyramidFront;
    }

    public void setPyramidFront(double pyramidFront) {
        this.pyramidFront = pyramidFront;
    }

    public double getPyramidBack() {
        return pyramidBack;
    }

    public void setPyramidBack(double pyramidBack) {
        this.pyramidBack = pyramidBack;
    }

    public double getPyramidWidth() {
        return pyramidWidth;
    }

    public void setPyramidWidth(double pyramidWidth) {
        this.pyramidWidth = pyramidWidth;
    }

    public double getPyramidHeight() {
        return pyramidHeight;
    }

    public void setPyramidHeight(double pyramidHeight) {
        this.pyramidHeight = pyramidHeight;
    }

    public RealVector getAngles() {
        return angles;
    }

    public void setAngles(RealVector angles) {
        this.angles = angles;
    }

    public List<BodyConfig> getBodyConfigs() {
        return bodyConfigs;
    }

    public void setBodyConfigs(List<BodyConfig> bodyConfigs) {
        this.bodyConfigs = bodyConfigs;
    }
}
