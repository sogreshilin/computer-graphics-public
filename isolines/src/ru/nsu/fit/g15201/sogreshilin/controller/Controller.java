package ru.nsu.fit.g15201.sogreshilin.controller;

import java.io.*;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.nsu.fit.g15201.sogreshilin.ContourLines;
import ru.nsu.fit.g15201.sogreshilin.model.DiscreteFunction;
import ru.nsu.fit.g15201.sogreshilin.view.GraphicsCanvas;
import ru.nsu.fit.g15201.sogreshilin.view.ParametersController;

public class Controller {
    @FXML private ToolBar toolBar;
    @FXML private ToggleButton drawButton;
    @FXML private CheckMenuItem drawMenuItem;
    @FXML private CheckMenuItem controlPointMenuItem;
    @FXML private CheckMenuItem interpolationMenuItem;
    @FXML private CheckMenuItem gridMenuItem;
    @FXML private CheckMenuItem colorMapMenuItem;
    @FXML private CheckMenuItem contourMenuItem;
    @FXML private ToggleButton controlPointButton;
    @FXML private ToggleButton interpolationButton;
    @FXML private ToggleButton gridButton;
    @FXML private ToggleButton colorMapButton;
    @FXML private ToggleButton contourButton;
    @FXML private BorderPane root;
    @FXML private BorderPane pane;
    @FXML private Label status;
    @FXML private GraphicsCanvas canvas;

    private final FileChooser fileChooser = new FileChooser();
    private Stage stage;

    private final Stage parameters = new Stage();
    private ParametersController parametersController;

    private DiscreteFunction function;
    private Config config = new Config();
    private boolean enableInterpolation = false;
    private boolean enableGrid = true;
    private boolean enableColorMap = true;
    private boolean enableContours = true;
    private boolean enableControlPoints = false;
    private boolean enableDraw = true;

    public Controller() {
        fileChooser.setInitialDirectory(new File("FIT_15201_Sogreshilin_Isolines_Data"));
        function = new DiscreteFunction();
        canvas = new GraphicsCanvas(this);
        function.addRangeChangedObserver((min, max) -> canvas.setRange(min, max));
        function.setConfig(config);
        canvas.setConfig(config);
        canvas.addMouseMovedObserver((x, y) -> {
            double value = function.valueAt(x, y);
            status.setText(String.format("f(%.2f, %.2f) = %.2f", x, y, value));
        });
        canvas.addMouseClickedObserver((x, y) -> {
            if (enableDraw) {
                double value = function.valueAt(x, y);
                canvas.addIsoline(value, function.getIsoline(value));
            }
        });
        loadParametersFxml();
        canvas.redraw();
    }

    private void loadParametersFxml() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/fxml/parameters.fxml"));
            fxmlLoader.setResources(ResourceBundle.getBundle(ContourLines.PATH_TO_BUNDLE, ContourLines.LOCALE));
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

    public final void bindCanvasProperties() {
        pane.setCenter(canvas);
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());
        canvas.widthProperty().addListener((observable, oldValue, newValue) -> {
            canvas.onWidthChanged((Double) newValue);
        });
        canvas.heightProperty().addListener((observable, oldValue, newValue) -> {
            canvas.onHeightChanged((Double) newValue - toolBar.getHeight() - status.getHeight());
        });
        canvas.redraw();
    }

    public final void redrawCanvas() {
        canvas.redraw();
    }

    public final void onOpen() {
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                config.readConfigFromFile(new FileInputStream(file));
                canvas.setConfig(config);
                function.setConfig(config);
                this.redrawCanvas();
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Invalid file format").show();
            }
        }
    }

    public final void onParameters() {
        parametersController.setConfig(this, config);
        parameters.show();
    }

    public final void onInterpolation() {
        enableInterpolation = !enableInterpolation;
        interpolationButton.setSelected(enableInterpolation);
        interpolationMenuItem.setSelected(enableInterpolation);
        canvas.setInterpolationEnabled(enableInterpolation);
    }

    public final void onGrid() {
        enableGrid = !enableGrid;
        gridButton.setSelected(enableGrid);
        gridMenuItem.setSelected(enableGrid);
        canvas.setGridEnabled(enableGrid);
    }

    public final void onColorMap() {
        enableColorMap = !enableColorMap;
        colorMapButton.setSelected(enableColorMap);
        colorMapMenuItem.setSelected(enableColorMap);
        canvas.setColorMapEnabled(enableColorMap);
    }

    public final void onContourLine() {
        enableContours = !enableContours;
        contourButton.setSelected(enableContours);
        contourMenuItem.setSelected(enableContours);
        canvas.setIsolinesEnabled(enableContours);
    }

    public final void onControlPoints() {
        enableControlPoints = !enableControlPoints;
        controlPointButton.setSelected(enableControlPoints);
        controlPointMenuItem.setSelected(enableControlPoints);
        canvas.setControlPointsEnabled(enableControlPoints);
    }

    public final void onDraw() {
        enableDraw = !enableDraw;
        drawButton.setSelected(enableDraw);
        drawMenuItem.setSelected(enableDraw);
    }

    public final void onErase() {
        canvas.clearIsolines();
    }

    public final void onAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Alexander Sogreshilin, FIT 15201");
        alert.setTitle("About");
        alert.setHeaderText("Counter lines");
        alert.showAndWait();
    }

    public final void onExit() {
        System.exit(0);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public DiscreteFunction getFunction() {
        return function;
    }

    public Config getConfig() {
        return config;
    }

    public void configChanged() {
        function.setConfig(config);
        canvas.setConfig(config);
        canvas.redraw();
    }
}