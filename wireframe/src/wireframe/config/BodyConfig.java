package wireframe.config;

import jfork.nproperty.Cfg;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

@Cfg
public class BodyConfig {
    private RealVector angles = MatrixUtils.createRealVector(new double[3]);
    private RealVector center = MatrixUtils.createRealVector(new double[3]);
    private SplineConfig splineConfig = new SplineConfig();

    public RealVector getAngles() {
        return angles;
    }

    public void setAngles(RealVector angles) {
        this.angles = angles;
    }

    public RealVector getCenter() {
        return center;
    }

    public void setCenter(RealVector center) {
        this.center = center;
    }

    public SplineConfig getSplineConfig() {
        return splineConfig;
    }

    public void setSplineConfig(SplineConfig splineConfig) {
        this.splineConfig = splineConfig;
    }

    public BodyConfig copy() {
        BodyConfig config = new BodyConfig();
        config.angles = angles.copy();
        config.center = center.copy();
        config.splineConfig = splineConfig.copy();
        return config;
    }
}
