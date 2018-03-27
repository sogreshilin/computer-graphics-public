package ru.nsu.fit.g15201.sogreshilin;

import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ru.nsu.fit.g15201.sogreshilin.controller.Controller;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setResources(ResourceBundle.getBundle("ru.nsu.fit.g15201.sogreshilin.bundles.Strings", new Locale("en")));
        BorderPane root = fxmlLoader.load(getClass().getResource("view/fxml/main-window.fxml").openStream());
        primaryStage.setTitle("Countour lines");
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        Controller controller = fxmlLoader.getController();
        controller.setStage(primaryStage);
        scene.widthProperty().addListener((observable, oldValue, newValue) -> controller.redrawCanvas());
        scene.heightProperty().addListener((observable, oldValue, newValue) -> controller.redrawCanvas());
        controller.bindCanvasProperties();
        controller.redrawCanvas();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
//        ContinuousFunction f = new ContinuousFunction(new Domain(new Pair<>(-10.0, 10.0), new Pair<>(-6.0, 6.0)));
//        f.setGrid(new Pair<>(20, 12));
//        System.out.println(f.getValues());
//        f.isoline(1.66);

//        List.of(0x5778b1, 0x5d82b6, 0x648bbb, 0x6a95c1, 0x719fc6, 0x77a9cb, 0x7eb3d0, 0x84bcd5, 0x8ac6da, 0x91d0e0, 0x97dae5, 0x9ee3ea, 0xa4edef).forEach(element -> System.out.println(
//                        ((element >> 16) & 0xFF) + " " +
//                        ((element >> 8) & 0xFF) + " " +
//                        ((element) & 0xFF)
//            ));


    }
}
