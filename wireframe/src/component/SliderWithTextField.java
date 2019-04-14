package component;

import wireframe.Utils;
import java.io.IOException;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class SliderWithTextField extends HBox {
    private int min;
    private int max;
    private int currentValue;
    @FXML private Slider slider;
    @FXML private TextField textField;

    public SliderWithTextField() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SliderWithTextField.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        slider.valueProperty().addListener((observable, oldValue, newValue) -> { setCurrentValue((int) Math.round((double) newValue)); });
        textField.setOnAction(event -> {
            String enteredText = textField.textProperty().get();
            try {
                if (enteredText.isEmpty()) {
                    setCurrentValue(min);
                } else {
                    setCurrentValue(Math.round(Integer.parseInt(enteredText)));
                }
            } catch (NumberFormatException e) {
                setCurrentValue(currentValue);
            }
        });
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        slider.setMin(min);
        this.min = min;
    }

    public void setMax(int max) {
        slider.setMax(max);
        this.max = max;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        currentValue = Utils.truncate(currentValue, min, max);
        this.currentValue = currentValue;
        textField.textProperty().setValue(String.valueOf(Math.round((double) currentValue)));
        slider.valueProperty().setValue(currentValue);
    }

    public DoubleProperty valueProperty() {
        return slider.valueProperty();
    }

}
