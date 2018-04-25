package wireframe;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;
import wireframe.config.BodyConfig;
import wireframe.config.ConfigDeserializer;
import wireframe.config.ConfigSerializer;
import wireframe.config.GeneralConfig;

public class Wireframe extends Application implements RevolutionBodyChangedListener {
    private static final int FAR_Z = 1000;
    private static final String PATH = "FIT_15201_Sogreshilin_Wireframe_Data/";
    @FXML private BorderPane mainPane;
    @FXML private StackPane holder;
    @FXML private Canvas revolutionCanvas;
    @FXML private Button addSpline;
    @FXML private Button editSpline;
    @FXML private Button saveConfig;
    @FXML private Button openConfig;
    @FXML private Button resetAngles;
    private final FileChooser fileChooser = new FileChooser();
    private Optional<File> currentFile = Optional.empty();

    private Box box = new Box();

    private GeneralConfig config;
    private Scene scene;
    private SplineSettings splineSettings = new SplineSettings(this);
    private ArrayList<RevolutionBody> bodies = new ArrayList<>();
    private Renderer renderer = new Renderer();

    private RealVector v0 = MatrixUtils.createRealVector(new double[] {0, 0, 0});
    private double phi0;
    private double theta0;

    private RealVector v1 = MatrixUtils.createRealVector(new double[] {0, 0, 0});
    private double phi1;
    private double theta1;

    private double angleX;
    private double angleY;
    private Window stage;

    public Wireframe() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);
        mainPane = loader.load(getClass().getResource("Wireframe.fxml").openStream());
        fileChooser.setInitialDirectory(new File(PATH));
        tryLoadConfigOrUseDefault();
    }

    private void tryLoadConfigOrUseDefault() {
        File file = new File(PATH + "Config.txt");
        try {
            ConfigDeserializer serializer = new ConfigDeserializer();
            setConfig(serializer.deserialize(new FileInputStream(file)));
            currentFile = Optional.of(file);
        } catch (IOException ignored) {
            setConfig(GeneralConfig.DEFAULT_CONFIG);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        scene = new Scene(mainPane);
        this.stage = stage;
        stage.setScene(scene);
        stage.setTitle("Wireframe");
        
        revolutionCanvas.setOnZoom(event -> {
            config.setPyramidFront(config.getPyramidFront() * event.getZoomFactor());
            config.setPyramidBack(config.getPyramidBack() * event.getZoomFactor());
            update();
        });

        addSpline.setOnAction(event -> splineSettings.addSpline());
        editSpline.setOnAction(event -> splineSettings.editSpline(bodies));
        editSpline.setDisable(bodies.isEmpty());
        saveConfig.setOnAction(event -> saveConfig());
        openConfig.setOnAction(event -> openConfig());
        resetAngles.setOnAction(event -> resetAngles());
        setRotationListeners();

        stage.setResizable(false);
        stage.show();
        stage.setOnCloseRequest(event -> Platform.exit());
    }

    private void resetAngles() {
        config.setAngles(MatrixUtils.createRealVector(new double[] { 0, 0, 0 }));
        update();
    }

    private void openConfig() {
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                ConfigDeserializer deserializer = new ConfigDeserializer();
                setConfig(deserializer.deserialize(new FileInputStream(file)));
                currentFile = Optional.of(file);
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Invalid file format").show();
            }
        }
    }

    private void saveConfig() {
        File file = currentFile.orElseGet(() -> fileChooser.showSaveDialog(stage));
        if (file != null) {
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                ConfigSerializer serializer = new ConfigSerializer();
                serializer.serialize(config).writeTo(outputStream);
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "File was not saved").show();
            }
        }
    }

    private void setRotationListeners() {
        revolutionCanvas.setOnMousePressed(event -> {
            double dx = revolutionCanvas.getWidth() / 2;
            double dy = revolutionCanvas.getHeight() / 2;
            angleX = config.getAngles().getEntry(0);
            angleY = config.getAngles().getEntry(1);
            v0 = MatrixUtils.createRealVector(new double[] {event.getX() - dx, event.getY() - dy, FAR_Z});
            phi0 = Math.atan(v0.getEntry(1) / v0.getEntry(2));
            theta0 = Math.atan(v0.getEntry(0)/ v0.getEntry(2));
            phi1 = Math.atan(v0.getEntry(1) / v0.getEntry(2));
            theta1 = Math.atan(v0.getEntry(0)/ v0.getEntry(2));
        });

        revolutionCanvas.setOnMouseDragged(event -> {
            double dx = revolutionCanvas.getWidth() / 2;
            double dy = revolutionCanvas.getHeight() / 2;
            v1 = MatrixUtils.createRealVector(new double[] {event.getX() - dx, event.getY() - dy, FAR_Z});
            phi1 = Math.atan(v1.getEntry(1) / v1.getEntry(2));
            theta1 = Math.atan(v1.getEntry(0)/ v1.getEntry(2));
            rotateBodies(angleX - (phi1 - phi0), angleY - (theta1 - theta0));
        });

        revolutionCanvas.setOnMouseReleased(event -> {
            config.getAngles().setEntry(0, angleX - (phi1 - phi0));
            config.getAngles().setEntry(1, angleY - (theta1 - theta0));
        });
    }

    private void rotateBodies(double angleX, double angleY) {
        config.setAngles(MatrixUtils.createRealVector(new double[] { angleX, angleY, config.getAngles().getEntry(2) }));
        update();
    }


    public void addRevolutionBody(RevolutionBody body) {
        body.setListener(this);
        bodies.add(body);
        config.getBodyConfigs().add(body.getConfig());
        editSpline.setDisable(false);
        update();
    }

    private void update() {
        revolutionCanvas.getGraphicsContext2D().clearRect(0, 0, revolutionCanvas.getWidth(), revolutionCanvas.getHeight());
        ArrayList<Segment> segments = new ArrayList<>();
        for (RevolutionBody body: bodies) {
            segments.addAll(renderer.render(body));
        }
        if (!segments.isEmpty()) {
            updateBox(segments);
            segments.addAll(renderer.render(box));
            drawSegments(segments);
        }
    }

    private void drawSegments(ArrayList<Segment> segments) {
        double dx = revolutionCanvas.getWidth() / 2;
        double dy = revolutionCanvas.getHeight() / 2;
        segments = Renderer.operate((Renderer.shiftMatrix(Renderer.cameraPoint))
                        .multiply(Renderer.rotationMatrix(config.getAngles()))
                        .multiply(Renderer.shiftMatrix(MatrixUtils.createRealVector(new double[] {
                                -(box.xMax + box.xMin) / 2,
                                -(box.yMax + box.yMin) / 2,
                                -(box.zMax + box.zMin) / 2
                        }))), segments);
        segments = clip(segments);
        segments = Renderer.operate(Renderer.projectionMatrix(config.getPyramidWidth(), config.getPyramidHeight(), config.getPyramidFront(), config.getPyramidBack()), segments);
        segments = segments.stream().sorted(Comparator.comparingDouble(object -> -object.getEnd().getEntry(2))).collect(Collectors.toCollection(ArrayList::new));
        for (Segment segment : segments) {
            segment.getStart().mapMultiplyToSelf(1/ segment.getStart().getEntry(3));
            segment.getEnd().mapMultiplyToSelf(1/ segment.getEnd().getEntry(3));
            revolutionCanvas.getGraphicsContext2D().setStroke(segment.getColor());
            revolutionCanvas.getGraphicsContext2D().strokeLine(
                    segment.getStart().getEntry(0) + dx,
                    segment.getStart().getEntry(1) + dy,
                    segment.getEnd().getEntry(0) + dx,
                    segment.getEnd().getEntry(1) + dy);
        }
    }

    private ArrayList<Segment> clip(ArrayList<Segment> segments) {
        ArrayList<Segment> rv = new ArrayList<>();
        for (Segment segment: segments) {
            RealVector start = segment.getStart();
            RealVector end = segment.getEnd();
            double x1 = start.getEntry(0);
            double x2 = end.getEntry(0);
            double y1 = start.getEntry(1);
            double y2 = end.getEntry(1);
            double z1 = start.getEntry(2);
            double z2 = end.getEntry(2);
            if (z1 > config.getPyramidBack() && z2 > config.getPyramidBack() ||
                    z1 < config.getPyramidFront() && z2 <= config.getPyramidFront()) {
                continue;
            } else if (config.getPyramidFront() <= z1 && z1 <= config.getPyramidBack() &&
                    config.getPyramidFront() <= z2 && z2 <= config.getPyramidBack()) {
                rv.add(segment);
            } else if (z1 < config.getPyramidFront() && z2 > config.getPyramidFront()) {
                double k = (config.getPyramidFront() - z1) / (z2 - z1);
                RealVector intersection = MatrixUtils.createRealVector(new double[] {
                        x1 + (x2 - x1) * k, y1 + (y2 - y1) * k, config.getPyramidFront(), 1
                });
                rv.add(new Segment(intersection, end, segment.getColor()));
            } else if (z2 < config.getPyramidFront() && z1 > config.getPyramidFront()) {
                double k = (config.getPyramidFront() - z1) / (z2 - z1);
                RealVector intersection = MatrixUtils.createRealVector(new double[] {
                        x1 + (x2 - x1) * k, y1 + (y2 - y1) * k, config.getPyramidFront(), 1
                });
                rv.add(new Segment(intersection, start, segment.getColor()));
            } else if (z2 < config.getPyramidBack() && z1 > config.getPyramidBack()) {
                double k = (config.getPyramidBack() - z1) / (z2 - z1);
                RealVector intersection = MatrixUtils.createRealVector(new double[] {
                        x1 + (x2 - x1) * k, y1 + (y2 - y1) * k, config.getPyramidBack(), 1
                });
                rv.add(new Segment(intersection, end, segment.getColor()));
            } else if (z1 < config.getPyramidBack() && z2 > config.getPyramidBack()) {
                double k = (config.getPyramidBack() - z1) / (z2 - z1);
                RealVector intersection = MatrixUtils.createRealVector(new double[] {
                        x1 + (x2 - x1) * k, y1 + (y2 - y1) * k, config.getPyramidBack(), 1
                });
                rv.add(new Segment(intersection, start, segment.getColor()));
            }
        }
        return rv;
    }

    private void updateBox(ArrayList<Segment> segments) {
        box = new Box();
        for (Segment segment: segments) {
            RealVector start = segment.getStart();
            RealVector end = segment.getEnd();
            box.update(start);
            box.update(end);
        }
    }

    @Override
    public void onRevolutionBodyChanged(RevolutionBody body) {
        update();
    }

    public void setRevolutionBody(int i, RevolutionBody body) {
        bodies.set(i, body);
        config.getBodyConfigs().set(i, body.getConfig());
        update();
    }

    public void removeRevolutionBody(int i) {
        bodies.remove(i);
        config.getBodyConfigs().remove(i);
        if (bodies.size() == 0) {
            editSpline.setDisable(true);
        }
        update();
    }

    public void setConfig(GeneralConfig config) {
        bodies.clear();
        this.config = config;
        Color color = config.getBackgroundColor();
        String hex = String.format( "#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
        this.holder.setStyle("-fx-background-color: " + hex);
        for (BodyConfig bodyConfig: config.getBodyConfigs()) {
            RevolutionBody body = new RevolutionBody(bodyConfig);
            body.setListener(this);
            bodies.add(body);
        }
        editSpline.setDisable(bodies.isEmpty());
        update();

    }

    public ArrayList<RevolutionBody> getBodies() {
        return bodies;
    }
}
