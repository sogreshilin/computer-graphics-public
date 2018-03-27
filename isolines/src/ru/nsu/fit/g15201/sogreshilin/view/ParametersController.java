package ru.nsu.fit.g15201.sogreshilin.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.nsu.fit.g15201.sogreshilin.controller.Config;
import ru.nsu.fit.g15201.sogreshilin.controller.Controller;
import ru.nsu.fit.g15201.sogreshilin.model.Domain;

public class ParametersController {
    @FXML private TextField xLow;
    @FXML private TextField xHigh;
    @FXML private TextField yLow;
    @FXML private TextField yHigh;
    @FXML private TextField rowsCount;
    @FXML private TextField columnsCount;

    private Config config;
    private Stage stage;
    private Controller controller;

    public void onCancel(ActionEvent actionEvent) {
        stage.hide();
    }

    public void onOk(ActionEvent actionEvent) {
        double x1 = 0;
        double x2 = 0;
        double y1 = 0;
        double y2 = 0;
        int gridX = 0;
        int gridY = 0;

        try {
            x1 = Double.parseDouble(xLow.getText());
            x2 = Double.parseDouble(xHigh.getText());
            y1 = Double.parseDouble(yLow.getText());
            y2 = Double.parseDouble(yHigh.getText());
            gridX = Integer.parseInt(rowsCount.getText());
            gridY = Integer.parseInt(columnsCount.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to parse value");
            alert.showAndWait();
            return;
        }

        if (x1 >= x2 || y1 >= y2) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid domain specified");
            alert.showAndWait();
            return;
        }

        if (gridX <= 0 || gridY <= 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid grid size specified");
            alert.showAndWait();
        }

        config.setDomain(new Domain(x1, x2, y1, y2));
        config.setGridX(gridX);
        config.setGridY(gridY);
        controller.configChanged();
        stage.hide();
    }

    public void setConfig(Controller controller, Config config) {
        this.controller = controller;
        this.config = config;
        xLow.setText(String.valueOf(config.getDomain().getX1()));
        xHigh.setText(String.valueOf(config.getDomain().getX2()));
        yLow.setText(String.valueOf(config.getDomain().getY1()));
        yHigh.setText(String.valueOf(config.getDomain().getY2()));
        rowsCount.setText(String.valueOf(config.getGridX()));
        columnsCount.setText(String.valueOf(config.getGridY()));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
