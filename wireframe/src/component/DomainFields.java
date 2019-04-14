package component;

import wireframe.Utils;
import java.io.IOException;
import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

public class DomainFields extends HBox {
    private static final double EPS = 0.0045;
    private double min;
    private double max;
    private RealVector value = MatrixUtils.createRealVector(new double[2]);
    private Optional<ChangeListener<RealVector>> listener = Optional.empty();
    @FXML private TextField start;
    @FXML private TextField finish;

    public DomainFields() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DomainFields.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        start.setOnAction(event -> {
            String enteredText = start.textProperty().get();
            try {
                if (enteredText.isEmpty()) {
                    value.setEntry(0, min);
                } else {
                    double enteredValue = Utils.truncate(Double.parseDouble(enteredText), min, value.getEntry(1) - EPS);
                    value.setEntry(0, enteredValue);
                }
            } catch (NumberFormatException e) {
                value.setEntry(0, value.getEntry(0));
            }
            listener.ifPresent(listener -> listener.changed(null, null, value));
            start.setText(String.format("%.2f", value.getEntry(0)));
        });

        finish.setOnAction(event -> {
            String enteredText = finish.textProperty().get();
            try {
                if (enteredText.isEmpty()) {
                    value.setEntry(1, value.getEntry(1));
                } else {
                    double enteredValue = Utils.truncate(Double.parseDouble(enteredText), value.getEntry(0) + EPS, max);
                    value.setEntry(1, enteredValue);
                }
            } catch (NumberFormatException e) {
                value.setEntry(1, value.getEntry(1));
            }
            listener.ifPresent(listener -> listener.changed(null, null, value));
            finish.setText(String.format("%.2f", value.getEntry(1)));
        });
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public void setOnChange(ChangeListener<RealVector> listener) {
        this.listener = Optional.of(listener);
    }

    public void setValue(RealVector value) {
        this.value = value;
        start.setText(String.format("%.2f", value.getEntry(0)));
        finish.setText(String.format("%.2f", value.getEntry(1)));
    }
}
