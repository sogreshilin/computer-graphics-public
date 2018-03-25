package ru.nsu.fit.g15201.sogreshilin;

import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.nsu.fit.g15201.sogreshilin.controller.Controller;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setResources(ResourceBundle.getBundle("ru.nsu.fit.g15201.sogreshilin.bundles.Strings", new Locale("en")));
        VBox root = fxmlLoader.load(getClass().getResource("view/fxml/main-window.fxml").openStream());
        primaryStage.setTitle("Countour lines");
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        Controller controller = fxmlLoader.getController();
        scene.widthProperty().addListener((observable, oldValue, newValue) -> controller.redrawCanvas());
        scene.heightProperty().addListener((observable, oldValue, newValue) -> controller.redrawCanvas());
        controller.initializeCanvas();
        controller.redrawCanvas();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
