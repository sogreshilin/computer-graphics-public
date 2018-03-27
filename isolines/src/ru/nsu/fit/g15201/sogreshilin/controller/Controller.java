package ru.nsu.fit.g15201.sogreshilin.controller;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.IntStream;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.nsu.fit.g15201.sogreshilin.model.DiscreteFunction;
import ru.nsu.fit.g15201.sogreshilin.model.Domain;
import ru.nsu.fit.g15201.sogreshilin.model.Segment;
import ru.nsu.fit.g15201.sogreshilin.view.MyCanvas;
import ru.nsu.fit.g15201.sogreshilin.view.ParametersController;


public class Controller {
    @FXML
    private ToggleButton drawButton;
    @FXML
    private CheckMenuItem drawMenuItem;
    @FXML
    private CheckMenuItem controlPointMenuItem;
    @FXML
    private CheckMenuItem interpolationMenuItem;
    @FXML
    private CheckMenuItem gridMenuItem;
    @FXML
    private CheckMenuItem colorMapMenuItem;
    @FXML
    private CheckMenuItem contourMenuItem;
    @FXML
    private ToggleButton controlPointButton;
    @FXML
    private ToggleButton interpolationButton;
    @FXML
    private ToggleButton gridButton;
    @FXML
    private ToggleButton colorMapButton;
    @FXML
    private ToggleButton contourButton;
    @FXML
    private BorderPane root;
    @FXML
    private BorderPane pane;
    @FXML
    private MyCanvas canvas;
    @FXML
    private Label status;
    private final FileChooser fileChooser = new FileChooser();
    private Stage stage;


    private DiscreteFunction function;
    private List<Double> zLowerBounds = new ArrayList<>();

    private Config config = new Config();
    private ParametersController parametersController;

    private boolean enableInterpolation = false;
    private boolean enableGrid = true;
    private boolean enableColorMap = true;
    private boolean enableContours = true;
    private boolean enableControlPoints = false;
    private boolean enableDraw = true;
    private final Stage parameters = new Stage();

    public Controller() {
        fileChooser.setInitialDirectory(new File("FIT_15201_Sogreshilin_Isolines_Data"));
        function = new DiscreteFunction();
        function.setConfig(config);
        canvas = new MyCanvas(this);
        function.addRangeChangedObserver(new DiscreteFunction.RangeChangedObserver() {
            @Override
            public void onRangeChanged(double min, double max) {
                canvas.setRange(min, max);
            }
        });
        function.setDomain(config.getDomain());
        canvas.setDomain(config.getDomain());
        setGrid(config.getGridX(), config.getGridY());
        canvas.addMouseMovedObserver((x, y) -> {
            double value = function.valueAt(x, y);
            status.setText(String.format("f(%.2f, %.2f) = %.2f", x, y, value));
        });
        canvas.addMouseClickedObserver((x, y) -> {
            if (enableDraw) {
                double value = function.valueAt(x, y);
                canvas.addIsoline(function.getIsoline(value));
            }
        });

        loadParametersFxml();

        canvas.redraw();
    }

    private void loadParametersFxml() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/fxml/parameters.fxml"));
            fxmlLoader.setResources(ResourceBundle.getBundle("ru.nsu.fit.g15201.sogreshilin.bundles.Strings", new Locale("en")));
            Parent root = fxmlLoader.load();
            parametersController = fxmlLoader.getController();
            parametersController.setStage(parameters);
            parameters.initModality(Modality.APPLICATION_MODAL);
            parameters.initStyle(StageStyle.UNDECORATED);
            parameters.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setGrid(int x, int y) {
        function.setGrid(x, y);
        canvas.setGrid(x, y);
        function.setGridZ(config.getKeyValueCount());
    }

    public final void bindCanvasProperties() {
        pane.setCenter(canvas);
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());
        canvas.widthProperty().addListener((observable, oldValue, newValue) -> canvas.onWidthChanged((Double) newValue));
        canvas.heightProperty().addListener((observable, oldValue, newValue) -> canvas.onHeightChanged((Double) newValue));
        redrawCanvas();
    }

    public final void redrawCanvas() {
        canvas.redraw();
    }

    public Color colorAt(double x, double y) {
        double zValue = function.valueAt(x, y);
        int index = IntStream.range(0, zLowerBounds.size())
                .filter(i -> zValue < zLowerBounds.get(i))
                .findFirst().orElse(zLowerBounds.size());

        return config.getColors().get(index);
    }

    public final void onOpen(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            FileInputStream var5 = null;
            try {
                config.readConfigFromFile(new FileInputStream(file));
                canvas.setConfig(config);
                function.setConfig(config);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.redrawCanvas();
        }
    }

    public final void onParameters(ActionEvent actionEvent) {
        parametersController.setConfig(this, config);
        parameters.show();
    }

    public final void onInterpolation(ActionEvent actionEvent) {
        enableInterpolation = !enableInterpolation;
        interpolationButton.setSelected(enableInterpolation);
        interpolationMenuItem.setSelected(enableInterpolation);
    }

    public final void onGrid(ActionEvent actionEvent) {
        enableGrid = !enableGrid;
        gridButton.setSelected(enableGrid);
        gridMenuItem.setSelected(enableGrid);
        canvas.setGridEnabled(enableGrid);
    }

    public final void onColorMap(ActionEvent actionEvent) {
        enableColorMap = !enableColorMap;
        colorMapButton.setSelected(enableColorMap);
        colorMapMenuItem.setSelected(enableColorMap);
        canvas.setColorMapEnabled(enableColorMap);
    }

    public final void onContourLine(ActionEvent actionEvent) {
        enableContours = !enableContours;
        contourButton.setSelected(enableContours);
        contourMenuItem.setSelected(enableContours);
        canvas.setIsolinesEnabled(enableContours);
    }

    public final void onControlPoints(ActionEvent actionEvent) {
        enableControlPoints = !enableControlPoints;
        controlPointButton.setSelected(enableControlPoints);
        controlPointMenuItem.setSelected(enableControlPoints);
    }

    public final void onDraw(ActionEvent actionEvent) {
        enableDraw = !enableDraw;
        drawButton.setSelected(enableDraw);
        drawMenuItem.setSelected(enableDraw);
    }

    public final void onErase(ActionEvent actionEvent) {
        canvas.clearIsolines();
    }

    public final void onAbout(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Alexander Sogreshilin, FIT 15201\n" +
            "e-mail: sogreshilin.ridder@gmail.com");
        alert.setTitle("About");
        alert.setHeaderText("Counter lines");
        alert.showAndWait();
    }

    public final void onExit(ActionEvent actionEvent) {

    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public DiscreteFunction getFunction() {
        return function;
    }

    public Domain getFunctionDomain() {
        return function.getDomain();
    }

    public int getChunk(double x, double y, int chunkCount) {
        return function.getChunk(x, y, chunkCount);
    }

    public int getChunk(double z, int chunkCount) {
        return function.getChunk(z, chunkCount);
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public void configChanged() {
        setGrid(config.getGridX(), config.getGridY());
        function.setDomain(config.getDomain());
        canvas.setDomain(config.getDomain());
        canvas.redraw();
    }
}