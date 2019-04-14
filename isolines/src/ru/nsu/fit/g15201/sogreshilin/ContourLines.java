package ru.nsu.fit.g15201.sogreshilin;

import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ru.nsu.fit.g15201.sogreshilin.controller.Controller;

public class ContourLines extends Application {
    public static final int DEFAULT_WIDTH = 800;
    public static final int DEFAULT_HEIGHT = 600;
    private static final int MIN_WIDTH = 300;
    private static final int MIN_HEIGHT = 300;
    public static final Locale LOCALE = new Locale("en");
    private static final String TITLE = "Countour lines";
    public static final String PATH_TO_BUNDLE =
            "ru.nsu.fit.g15201.sogreshilin.bundles.Strings";

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle(TITLE);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setResources(ResourceBundle.getBundle(PATH_TO_BUNDLE, LOCALE));
        BorderPane root = fxmlLoader.load(getClass().getResource("view/fxml/main-window.fxml").openStream());

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        Controller controller = fxmlLoader.getController();
        controller.setStage(primaryStage);
        scene.widthProperty().addListener((observable, oldValue, newValue) -> controller.redrawCanvas());
        scene.heightProperty().addListener((observable, oldValue, newValue) -> controller.redrawCanvas());

        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setWidth(DEFAULT_WIDTH);
        primaryStage.setHeight(DEFAULT_HEIGHT);

        controller.bindCanvasProperties();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
