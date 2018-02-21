package ru.nsu.fit.g15201.sogreshilin.model.io;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import ru.nsu.fit.g15201.sogreshilin.view.Point;

public class ConfigParser {
    private Path path;

//    public ConfigParser(Path path) {
//        this.path = path;
//    }
//
//    public ConfigParser(File file) {
//        this.path = Paths.get(file.getPath());
//    }
    private static final Charset charset = StandardCharsets.UTF_8;

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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset))) {
            String rawString;
            try {
                rawString = readLineAndRemoveComments(reader);
                String[] ss = rawString.split(" ");
                int m = Integer.parseInt(ss[0]);
                int n = Integer.parseInt(ss[1]);
                config.setFieldWidth(m);
                config.setFieldHeight(n);

                rawString = readLineAndRemoveComments(reader);
                int w = Integer.parseInt(rawString);
                config.setLineThickness(w);

                rawString = readLineAndRemoveComments(reader);
                int k = Integer.parseInt(rawString);
                config.setCellSize(k);

                rawString = readLineAndRemoveComments(reader);
                int all = Integer.parseInt(rawString);

                ArrayList<Point> aliveCells = new ArrayList<>();
                for (int i = 0; i < all; ++i) {
                    rawString = readLineAndRemoveComments(reader);
                    ss = rawString.split(" ");
                    int x = Integer.parseInt(ss[0]);
                    int y = Integer.parseInt(ss[1]);
                    aliveCells.add(new Point(x, y));
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
        output.write((config.getFieldWidth() + " " + config.getFieldHeight() + "\n").getBytes(charset));
        output.write((config.getLineThickness() + "\n").getBytes(charset));
        output.write((config.getCellSize() + "\n").getBytes(charset));
        output.write((config.getAliveCells().size() + "\n").getBytes(charset));
        for (Point point : config.getAliveCells()) {
            output.write((point.getX() + " " + point.getY() + "\n").getBytes(charset));
        }
        return output;
    }
}
