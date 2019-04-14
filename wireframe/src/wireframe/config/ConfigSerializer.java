package wireframe.config;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javafx.scene.paint.Color;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import static java.lang.String.format;

public class ConfigSerializer {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private ByteArrayOutputStream output;

    public ByteArrayOutputStream serialize(GeneralConfig config) throws IOException {
        output = new ByteArrayOutputStream();
        writeSceneParameters(config);
        writeInt(config.getBodyConfigs().size());
        for (BodyConfig bodyConfig: config.getBodyConfigs()) {
            writeBodyConfig(bodyConfig);
        }
        return output;
    }

    private void writeInt(int number) throws IOException {
        writeString(format("%d", number));
    }

    private void writeString(String string) throws IOException {
        output.write((string + "\n").getBytes(CHARSET));
    }

    private void writeBodyConfig(BodyConfig bodyConfig) throws IOException {
        writeString(comment("BODY"));
        writeRow(bodyConfig.getCenter());
        writeRow(bodyConfig.getAngles());
        writeSplineConfig(bodyConfig.getSplineConfig());
    }

    private void writeSplineConfig(SplineConfig splineConfig) throws IOException {
        writeInt(splineConfig.getuGridCount());
        writeInt(splineConfig.getvGridCount());
        writeInt(splineConfig.getkParameter());
        writeDouble(splineConfig.getuStart());
        writeDouble(splineConfig.getuEnd());
        writeDouble(splineConfig.getvStart());
        writeDouble(splineConfig.getvEnd());
        writeColor(splineConfig.getColor());
        writeInt(splineConfig.getControlPoints().size());
        for (RealVector point: splineConfig.getControlPoints()) {
            writePoint(point);
        }
    }

    private void writePoint(RealVector vector) throws IOException {
        writeString(format("%f %f", vector.getEntry(0), vector.getEntry(1)));
    }

    private void writeSceneParameters(GeneralConfig config) throws IOException {
        writeString(comment("SCENE"));
        writeColor(config.getBackgroundColor());
        writeDouble(config.getPyramidFront());
        writeDouble(config.getPyramidBack());
        writeDouble(config.getPyramidWidth());
        writeDouble(config.getPyramidHeight());
        writeRow(config.getAngles());
    }

    private void writeMatrix(RealMatrix matrix) throws IOException {
        writeRow(matrix.getRowVector(0));
        writeRow(matrix.getRowVector(1));
        writeRow(matrix.getRowVector(2));
    }

    private void writeRow(RealVector vector) throws IOException {
        writeString(format("%f %f %f", vector.getEntry(0),
                vector.getEntry(1), vector.getEntry(2)));
    }

    private void writeDouble(double value) throws IOException {
        writeString(String.valueOf(value));
    }

    private void writeColor(Color color) throws IOException {
        writeString(format("%d %d %d",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255)));
    }

    private String comment(String string) {
        return "// " + string;
    }

}

