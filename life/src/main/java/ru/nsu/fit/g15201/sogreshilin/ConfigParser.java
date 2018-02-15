package ru.nsu.fit.g15201.sogreshilin;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class ConfigParser {
    private Path path;

    public ConfigParser(Path path) {
        this.path = path;
    }

    public Config parse() throws IOException {
        Config config = new Config();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String rawString = "";
            try {
                rawString = reader.readLine();
                String[] ss = rawString.split(" ");
                int m = Integer.parseInt(ss[0]);
                int n = Integer.parseInt(ss[1]);
                config.setFieldWidth(m);
                config.setFieldHeight(n);

                rawString = reader.readLine();
                int w = Integer.parseInt(rawString);
                config.setBorderWidth(w);

                rawString = reader.readLine();
                int k = Integer.parseInt(rawString);
                config.setCellRadius(k);

                rawString = reader.readLine();
                int all = Integer.parseInt(rawString);

                ArrayList<Point> cells = new ArrayList<>();
                for (int i = 0; i < all; ++i) {
                    rawString = reader.readLine();
                    ss = rawString.split(" ");
                    int x = Integer.parseInt(ss[0]);
                    int y = Integer.parseInt(ss[1]);
                    cells.add(new Point(x, y));
                }

            } catch (NumberFormatException e) {
                throw new IOException("Unable to parse number in config file", e);
            }
        } catch (IOException e) {
            throw new IOException("Invalid config file format: unable to parse", e);
        }
        return config;
    }
}
