package ru.nsu.fit.g15201.sogreshilin.rendering;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Config {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private ArrayList<AbsorptionPoint> absorption  = new ArrayList<>();
    private ArrayList<EmissionPoint> emission = new ArrayList<>();
    private ArrayList<ChargePoint> charge = new ArrayList<>();

    public Config() {
    }

    public ArrayList<AbsorptionPoint> getAbsorption() {
        return absorption;
    }

    public ArrayList<EmissionPoint> getEmission() {
        return emission;
    }

    public ArrayList<ChargePoint> getCharge() {
        return charge;
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
        while ((rawString = removeComments(rawString)).isEmpty()) {
            rawString = reader.readLine();
        }
        return rawString
                .replaceAll("^\\s+|\\s+$", "")
                .replaceAll("\\s+", " ");
    }

    public void readConfigFromFile(InputStream input) throws IOException {
        String rawStringValue = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, CHARSET))) {
            readAbsorptionPoints(reader);
            readEmissionPoints(reader);
            readChargePoints(reader);
        } catch (IOException | NumberFormatException e) {
            throw new IOException("Invalid config file format. Unable to deserialize");
        }
    }

    private void readChargePoints(BufferedReader reader) throws IOException {
        String rawStringValue;
        rawStringValue = readLineAndRemoveComments(reader);
        int chargePointCount = Integer.parseInt(rawStringValue);
        for (int i = 0; i < chargePointCount; ++i) {
            rawStringValue = readLineAndRemoveComments(reader);
            String[] coordinateAndCharge = rawStringValue.split(" ");
            double x = Double.parseDouble(coordinateAndCharge[0]);
            double y = Double.parseDouble(coordinateAndCharge[1]);
            double z = Double.parseDouble(coordinateAndCharge[2]);
            double value = Double.parseDouble(coordinateAndCharge[3]);
            for (double coordinate: List.of(x, y, z)) {
                if (coordinate < 0 || coordinate > 1) {
                    throw new IOException("Invalid coordinate value. " +
                            "Has to be floating point between 0..1");
                }
            }
            charge.add(new ChargePoint(x, y, z, value));
        }
    }

    private void readEmissionPoints(BufferedReader reader) throws IOException {
        String rawStringValue;
        rawStringValue = readLineAndRemoveComments(reader);
        int emissionPointCount = Integer.parseInt(rawStringValue);
        for (int i = 0; i < emissionPointCount; ++i) {
            rawStringValue = readLineAndRemoveComments(reader);
            String[] emissionCoordinateAndColor = rawStringValue.split(" ");
            int coordinate = Integer.parseInt(emissionCoordinateAndColor[0]);
            if (coordinate < 0 || coordinate > 100) {
                throw new IOException("Invalid coordinate value. " +
                        "Has to be integer between 0..100");
            }
            int red = Integer.parseInt(emissionCoordinateAndColor[1]);
            int green = Integer.parseInt(emissionCoordinateAndColor[2]);
            int blue = Integer.parseInt(emissionCoordinateAndColor[3]);
            for (int color: List.of(red, green, blue)) {
                if (color < 0 || color > 255) {
                    throw new IOException("Invalid color value. " +
                            "Has to be integer between 0..255");
                }
            }
            emission.add(new EmissionPoint(coordinate, red, green, blue));
        }
    }

    private void readAbsorptionPoints(BufferedReader reader) throws IOException {
        String rawStringValue;
        rawStringValue = readLineAndRemoveComments(reader);
        int absorptionPointCount = Integer.parseInt(rawStringValue);
        for (int i = 0; i < absorptionPointCount; ++i) {
            rawStringValue = readLineAndRemoveComments(reader);
            String[] absorptionCoordinateAndValue = rawStringValue.split(" ");
            int coordinate = Integer.parseInt(absorptionCoordinateAndValue[0]);
            if (coordinate < 0 || coordinate > 100) {
                throw new IOException("Invalid coordinate value. " +
                        "Has to be integer between 0..100");
            }
            double value = Double.parseDouble(absorptionCoordinateAndValue[1]);
            if (value < 0 || value > 255) {
                throw new IOException("Invalid color value. " +
                        "Has to be integer between 0..255");
            }
            absorption.add(new AbsorptionPoint(coordinate, value));
        }
    }
}
