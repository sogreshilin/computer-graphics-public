package wireframe.config;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import javafx.scene.paint.Color;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class ConfigDeserializer {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private GeneralConfig config;

    public GeneralConfig deserialize(InputStream stream) throws IOException {
        config = new GeneralConfig();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, CHARSET))) {
            readSceneParameters(reader);
            int bodiesCount = parseInt(readLineAndRemoveComments(reader));
            for (int i = 0; i < bodiesCount; ++i) {
                readBodyConfig(reader);
            }
        } catch (Throwable e) {
            throw new IOException("Unable to parse config file: ", e);
        }
        return config;
    }

    private void readBodyConfig(BufferedReader reader) throws IOException {
        BodyConfig bodyConfig = new BodyConfig();
        bodyConfig.setCenter(parseVector(readLineAndRemoveComments(reader).split(" ")));
        bodyConfig.setAngles(parseVector(readLineAndRemoveComments(reader).split(" ")));
        bodyConfig.setSplineConfig(parseSplineConfig(reader));
        config.getBodyConfigs().add(bodyConfig);
    }

    private SplineConfig parseSplineConfig(BufferedReader reader) throws IOException {
        SplineConfig splineConfig = new SplineConfig();
        splineConfig.setuGridCount(parseInt(readLineAndRemoveComments(reader)));
        splineConfig.setvGridCount(parseInt(readLineAndRemoveComments(reader)));
        splineConfig.setkParameter(parseInt(readLineAndRemoveComments(reader)));
        splineConfig.setuStart(parseDouble(readLineAndRemoveComments(reader)));
        splineConfig.setuEnd(parseDouble(readLineAndRemoveComments(reader)));
        splineConfig.setvStart(parseDouble(readLineAndRemoveComments(reader)));
        splineConfig.setvEnd(parseDouble(readLineAndRemoveComments(reader)));
        splineConfig.setColor(parseColor(readLineAndRemoveComments(reader).split(" ")));
        splineConfig.setControlPoints(parseControlPoints(reader));
        return splineConfig;
    }

    private ArrayList<RealVector> parseControlPoints(BufferedReader reader) throws IOException {
        ArrayList<RealVector> points = new ArrayList<>();
        int count = parseInt(readLineAndRemoveComments(reader));
        for (int i = 0; i < count; ++i) {
            points.add(parsePoint(readLineAndRemoveComments(reader).split(" ")));
        }
        return points;
    }

    private RealVector parsePoint(String[] values) {
        return MatrixUtils.createRealVector(new double[] {
                parseDouble(values[0]), parseDouble(values[1])
        });
    }

    private RealVector parseVector(String[] values) {
        return MatrixUtils.createRealVector(new double[] {
                parseDouble(values[0]), parseDouble(values[1]), parseDouble(values[2])
        });
    }

    private void readSceneParameters(BufferedReader reader) throws IOException {
        config.setBackgroundColor(parseColor(readLineAndRemoveComments(reader).split(" ")));
        config.setPyramidFront(parseDouble(readLineAndRemoveComments(reader)));
        config.setPyramidBack(parseDouble(readLineAndRemoveComments(reader)));
        config.setPyramidWidth(parseDouble(readLineAndRemoveComments(reader)));
        config.setPyramidHeight(parseDouble(readLineAndRemoveComments(reader)));
        config.setAngles(parseVector(readLineAndRemoveComments(reader).split(" ")));
    }

    private RealMatrix parseMatrix(String[] row0, String[] row1, String[] row2) {
        return MatrixUtils.createRealMatrix(new double[][] {
                {parseDouble(row0[0]), parseDouble(row0[1]), parseDouble(row0[2]), 0},
                {parseDouble(row1[0]), parseDouble(row1[1]), parseDouble(row1[2]), 0},
                {parseDouble(row2[0]), parseDouble(row2[1]), parseDouble(row2[2]), 0},
                {0, 0, 0, 1}
        });
    }

    private Color parseColor(String[] colorComponents) {
        int red = parseInt(colorComponents[0]);
        int green = parseInt(colorComponents[1]);
        int blue = parseInt(colorComponents[2]);
        return Color.rgb(red, green, blue);
    }

    private static String removeComments(String s) {
        int offset = s.indexOf("//");
        if (offset >= 0) {
            s = s.substring(0, offset);
        }
        return s;
    }

    private static String readLineAndRemoveComments(BufferedReader reader) throws IOException {
        String rawString = reader.readLine();
        if (rawString == null) {
            return null;
        }
        while ((rawString = removeComments(rawString)).isEmpty()) {
            rawString = reader.readLine();
        }
        return rawString
                .replaceAll("^\\s+|\\s+$", "")
                .replaceAll("\\s+", " ");
    }
}

