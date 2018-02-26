package ru.nsu.fit.g15201.sogreshilin.model.io;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import ru.nsu.fit.g15201.sogreshilin.view.Point;

public class ConfigParser {
    private static final Charset CHARSET = StandardCharsets.UTF_8;

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
        return rawString.replaceAll("^\\s+|\\s+$", "").replaceAll("\\s+", " ");
    }

    public static Config deserialize(InputStream in) throws IOException {
        Config config = new Config();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, CHARSET))) {
            String rawString;
            try {
                rawString = readLineAndRemoveComments(reader);
                String[] fieldSizes = rawString.split(" ");
                int rawWidth = Integer.parseInt(fieldSizes[0]);
                int rawHeight = Integer.parseInt(fieldSizes[1]);
                config.setFieldWidth(rawWidth);
                config.setFieldHeight(rawHeight);

                rawString = readLineAndRemoveComments(reader);
                int rawThickness = Integer.parseInt(rawString);
                config.setLineThickness(rawThickness);

                rawString = readLineAndRemoveComments(reader);
                int rawCellSize = Integer.parseInt(rawString);
                config.setCellSize(rawCellSize);

                rawString = readLineAndRemoveComments(reader);
                int rawAliveCount = Integer.parseInt(rawString);

                ArrayList<Point> aliveCells = new ArrayList<>();
                for (int i = 0; i < rawAliveCount; ++i) {
                    rawString = readLineAndRemoveComments(reader);
                    fieldSizes = rawString.split(" ");
                    int xCoordinate = Integer.parseInt(fieldSizes[0]);
                    int yCoordinate = Integer.parseInt(fieldSizes[1]);
                    aliveCells.add(new Point(xCoordinate, yCoordinate));
                }
                config.setAliveCells(aliveCells);

            } catch (NumberFormatException e) {
                throw new IOException("Unable to deserialize number in config file", e);
            }
        } catch (IOException e) {
            throw new IOException("Invalid config file format: unable to deserialize", e);
        }
        return config;
    }

    public static ByteArrayOutputStream serialize(Config config) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write((config.getFieldWidth() + " " + config.getFieldHeight() + "\n").getBytes(CHARSET));
        output.write((config.getLineThickness() + "\n").getBytes(CHARSET));
        output.write((config.getCellSize() + "\n").getBytes(CHARSET));
        output.write((config.getAliveCells().size() + "\n").getBytes(CHARSET));
        for (Point point : config.getAliveCells()) {
            output.write((point.getX() + " " + point.getY() + "\n").getBytes(CHARSET));
        }
        return output;
    }
}
