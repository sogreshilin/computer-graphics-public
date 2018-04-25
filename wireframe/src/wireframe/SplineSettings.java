package wireframe;

import component.DomainFields;
import component.SliderWithTextField;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;
import wireframe.config.BodyConfig;

public class SplineSettings extends Stage implements SplineChangedListener {
    private final Wireframe mainController;
    private static final int POINT_RADIUS = 2;
    private static final int POINT_VISIBLE_RADIUS = 5;
    private static final Paint POINT_FILL = Color.WHITE;
    private static final Paint POINT_STROKE = Color.GRAY;
    private static final Color LINE_COLOR = Color.GRAY;

    @FXML private Pane canvasContainer;
    @FXML private Canvas splineCanvas;
    @FXML private Canvas pointsCanvas;
    @FXML private Canvas serifsCanvas;
    @FXML private Canvas axesCanvas;

    @FXML private SliderWithTextField uGridCount;
    @FXML private SliderWithTextField vGridCount;
    @FXML private SliderWithTextField kParameter;
    @FXML private ColorPicker colorPicker;
    @FXML private SliderWithTextField xCenter;
    @FXML private SliderWithTextField yCenter;
    @FXML private SliderWithTextField zCenter;
    @FXML private SliderWithTextField xAngle;
    @FXML private SliderWithTextField yAngle;
    @FXML private SliderWithTextField zAngle;
    @FXML private DomainFields domainU;
    @FXML private DomainFields domainV;
    @FXML private Button okButton;
    @FXML private Button previousBodyButton;
    @FXML private Button nextBodyButton;
    @FXML private Button removeBodyButton;
    @FXML private CheckBox showAxesCheckBox;

    private int currentBodyIndex;
    private Optional<RealVector> pointUnderCursor;
    private RevolutionBody body;
    private ArrayList<RevolutionBody> bodies;
    private RevolutionBody bodyBeforeEditing;

    public SplineSettings(Wireframe mainController) throws IOException {
        this.mainController = mainController;
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);
        Pane mainPane = loader.load(getClass().getResource("SplineSettings.fxml").openStream());
        Scene scene = new Scene(mainPane);
        this.setScene(scene);
        this.setTitle("Edit");
        this.setResizable(false);

        drawAxes();
        pointsCanvas.toFront();
        setListeners(scene);
    }

    private void setListeners(Scene scene) {
        setCursorListeners(scene);

        uGridCount.valueProperty().addListener((observable, oldValue, newValue) -> body.getSpline().setUGridCount((int) Math.round((double) newValue)));
        vGridCount.valueProperty().addListener((observable, oldValue, newValue) -> body.getSpline().setVGridCount((int) Math.round((double) newValue)));
        kParameter.valueProperty().addListener((observable, oldValue, newValue) -> body.getSpline().setKParameter((int) Math.round((double) newValue)));
        colorPicker.setOnAction(event -> { body.getSpline().setColor(colorPicker.getValue()); });

        domainU.setOnChange((observable, oldValue, newValue) -> { body.getSpline().setUDomain(newValue); });
        domainV.setOnChange((observable, oldValue, newValue) -> { body.getSpline().setVDomain(newValue); });

        xCenter.valueProperty().addListener(((observable, oldValue, newValue) -> body.setXCenter((int) Math.round((double) newValue))));
        yCenter.valueProperty().addListener(((observable, oldValue, newValue) -> body.setYCenter((int) Math.round((double) newValue))));
        zCenter.valueProperty().addListener(((observable, oldValue, newValue) -> body.setZCenter((int) Math.round((double) newValue))));
        xAngle.valueProperty().addListener(((observable, oldValue, newValue) -> body.setXAngle((int) Math.round((double) newValue))));
        yAngle.valueProperty().addListener(((observable, oldValue, newValue) -> body.setYAngle((int) Math.round((double) newValue))));
        zAngle.valueProperty().addListener(((observable, oldValue, newValue) -> body.setZAngle((int) Math.round((double) newValue))));
        showAxesCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> { body.setDrawAxes(showAxesCheckBox.isSelected()); });

        okButton.setOnAction(event -> onOk());
        previousBodyButton.setOnAction(event -> onPreviousBody());
        nextBodyButton.setOnAction(event -> onNextBody());
        removeBodyButton.setOnAction(event -> onRemove());

    }

    private void setCursorListeners(Scene scene) {
        pointsCanvas.setOnMouseClicked(event -> {
            if (!pointUnderCursor.isPresent()) {
                RealVector point = MatrixUtils.createRealVector(new double[] {event.getX(), event.getY()});
                body.getSpline().addControlPoint(point);
                pointUnderCursor = Optional.ofNullable(point);
                scene.setCursor(Cursor.HAND);
            } else if (event.isShiftDown()) {
                body.getSpline().removeControlPoint(pointUnderCursor.get());
                pointUnderCursor = Optional.empty();
            }
        });

        pointsCanvas.setOnMouseMoved(event -> {
            pointUnderCursor = getPointAt(event.getX(), event.getY());
            scene.setCursor(pointUnderCursor.isPresent() ? Cursor.HAND : Cursor.DEFAULT);
        });

        pointsCanvas.setOnMouseDragged(event -> {
            pointUnderCursor.ifPresent(realVector -> body.getSpline().setControlPoint(realVector, event.getX(), event.getY()));
        });

        pointsCanvas.setOnMouseReleased(event -> {
            double x = event.getX();
            double y = event.getY();
            double scaleX = 1;
            double scaleY = 1;
            double width = splineCanvas.getWidth();
            double height = splineCanvas.getHeight();
            double centerX = splineCanvas.getWidth() / 2;
            double centerY = splineCanvas.getHeight() / 2;
            if (isOutOfCanvasHorizontal(x)) {
                scaleX = Math.abs(centerX - x) / centerX;
            }
            if (isOutOfCanvasVertical(y)) {
                scaleY = Math.abs(centerY - y) / centerY;
            }
            double scaleFactor = Math.max(scaleX, scaleY);
            if (scaleFactor != 1) {
                Translator xTranslator = new Translator(
                        centerX - scaleFactor * centerX,
                        centerX + scaleFactor * centerX,
                        POINT_VISIBLE_RADIUS, width - POINT_VISIBLE_RADIUS);
                Translator yTranslator = new Translator(
                        centerY - scaleFactor * centerY,
                        centerY + scaleFactor * centerY,
                        POINT_VISIBLE_RADIUS, height - POINT_VISIBLE_RADIUS);
                body.getSpline().rescaleControlPoints(xTranslator, yTranslator);
            }
        });
    }

    private boolean isOutOfCanvasHorizontal(double x) {
        return x < 0 || x > splineCanvas.getWidth();
    }

    private boolean isOutOfCanvasVertical(double y) {
        return y < 0 || y > splineCanvas.getHeight();
    }



    private void onRemove() {
        mainController.removeRevolutionBody(currentBodyIndex);
        currentBodyIndex--;
        if (currentBodyIndex < 0) {
            currentBodyIndex = 0;
        }
        if (currentBodyIndex == bodies.size() - 1) {
            nextBodyButton.setDisable(true);
        }
        if (currentBodyIndex == 0) {
            previousBodyButton.setDisable(true);
        }
        if (bodies.isEmpty()) {
            hide();
            return;
        }
        editSpline(bodies.get(currentBodyIndex));
    }

    private void onOk() {
        this.hide();
    }

    private void onPreviousBody() {
        currentBodyIndex--;
        if (currentBodyIndex == 0) {
            previousBodyButton.setDisable(true);
        }
        if (currentBodyIndex < bodies.size() - 1) {
            nextBodyButton.setDisable(false);
        }
        editSpline(bodies.get(currentBodyIndex));
    }

    private void onNextBody() {
        currentBodyIndex++;
        if (currentBodyIndex == bodies.size() - 1) {
            nextBodyButton.setDisable(true);
        }
        if (currentBodyIndex > 0) {
            previousBodyButton.setDisable(false);
        }
        editSpline(bodies.get(currentBodyIndex));
    }

    private void onCancel() {
        if (bodyBeforeEditing != null) {
            bodyBeforeEditing.getSpline().addListener(this);
            bodyBeforeEditing.setListener(mainController);
            mainController.setRevolutionBody(currentBodyIndex, bodyBeforeEditing);
        } else {
            mainController.removeRevolutionBody(currentBodyIndex);
        }
        this.hide();
    }

    private void drawAxes() {
        GraphicsContext context = axesCanvas.getGraphicsContext2D();
        context.clearRect(0, 0, axesCanvas.getWidth(), axesCanvas.getHeight());
        context.strokeLine(axesCanvas.getWidth() / 2, 0, axesCanvas.getWidth() / 2, axesCanvas.getHeight());
        context.strokeLine(0, axesCanvas.getHeight() / 2, axesCanvas.getWidth(), axesCanvas.getHeight() / 2);
        context.strokeRect(0, 0, axesCanvas.getWidth(), axesCanvas.getHeight());
    }

    private void redrawSpline() {
        GraphicsContext context = splineCanvas.getGraphicsContext2D();
        context.clearRect(0, 0, splineCanvas.getWidth(), splineCanvas.getHeight());
        for (Segment segment: body.getSpline().getPolygonalChain()) {
            context.setStroke(segment.getColor());
            splineCanvas.getGraphicsContext2D().strokeLine(
                    segment.getStart().getEntry(0),
                    segment.getStart().getEntry(1),
                    segment.getEnd().getEntry(0),
                    segment.getEnd().getEntry(1));
        }
    }

    private void redrawControlPointsAndLine() {
        GraphicsContext context = pointsCanvas.getGraphicsContext2D();
        context.clearRect(0, 0, pointsCanvas.getWidth(), pointsCanvas.getHeight());
        Paint strokeBefore = context.getStroke();
        Paint fillBefore = context.getFill();

        context.setStroke(LINE_COLOR);
        context.setLineDashes(3);

        ArrayList<RealVector> points = body.getSpline().getControlPoints();
        for (int i = 0; i < points.size() - 1; ++i) {
            context.strokeLine(points.get(i).getEntry(0), points.get(i).getEntry(1),
                    points.get(i + 1).getEntry(0), points.get(i + 1).getEntry(1));
        }
        context.setLineDashes();

        context.setStroke(POINT_STROKE);
        context.setFill(POINT_FILL);
        points.forEach(this::drawPoint);

        context.setFill(fillBefore);
        context.setStroke(strokeBefore);

    }

    private void drawPoint(RealVector point) {
        GraphicsContext context = pointsCanvas.getGraphicsContext2D();
        context.strokeRect(point.getEntry(0) - POINT_RADIUS,
                point.getEntry(1) - POINT_RADIUS,
                2 * POINT_RADIUS, 2 * POINT_RADIUS);
        context.fillRect(point.getEntry(0) - POINT_RADIUS,
                point.getEntry(1) - POINT_RADIUS,
                2 * POINT_RADIUS, 2 * POINT_RADIUS);
    }


    private double distance(RealVector point1, RealVector point2) {
        return Math.sqrt(
                (point1.getEntry(0) - point2.getEntry(0)) *
                (point1.getEntry(0) - point2.getEntry(0)) +
                (point1.getEntry(1) - point2.getEntry(1)) *
                (point1.getEntry(1) - point2.getEntry(1)));
    }

    private Optional<RealVector> getPointAt(double x, double y) {
        final RealVector toPoint = MatrixUtils.createRealVector(new double[] {x, y});
        return body.getSpline().getControlPoints().stream()
                .filter(fromPoint -> distance(fromPoint, toPoint) < POINT_VISIBLE_RADIUS)
                .findFirst();
    }

    private void drawSerifs() {
        serifsCanvas.getGraphicsContext2D().clearRect(0, 0, serifsCanvas.getWidth(), serifsCanvas.getHeight());
        double rad = 1.5;
        serifsCanvas.getGraphicsContext2D().setFill(body.getSpline().getColor());
        for (int i = 0; i <= body.getSpline().getUGridCount(); ++i) {
            double delta = (double) i / body.getSpline().getUGridCount();
            double current = body.getSpline().getUStart() * (1 - delta) + body.getSpline().getUEnd() * delta;
            Optional<RealVector> point = body.getSpline().getSplinePointCorrespondsTo(current);
            point.ifPresent(realVector -> serifsCanvas
                    .getGraphicsContext2D()
                    .fillOval(realVector.getEntry(0) - rad,
                            realVector.getEntry(1) - rad,
                            2 * rad, 2 * rad));
        }
    }

    public void addSpline() {
        this.setTitle("Add");
        bodyBeforeEditing = null;
        previousBodyButton.setDisable(true);
        nextBodyButton.setDisable(true);
        removeBodyButton.setDisable(true);
        body = new RevolutionBody(new BodyConfig());
        mainController.addRevolutionBody(body);
        currentBodyIndex = mainController.getBodies().size() - 1;
        editSpline(body);
    }

    public void editSpline(ArrayList<RevolutionBody> bodies) {
        this.setTitle("Edit");
        previousBodyButton.setVisible(true);
        nextBodyButton.setVisible(true);
        removeBodyButton.setVisible(true);
        this.bodies = bodies;
        currentBodyIndex = 0;
        previousBodyButton.setDisable(true);
        nextBodyButton.setDisable(!(currentBodyIndex < bodies.size() - 1));
        bodyBeforeEditing = new RevolutionBody(bodies.get(currentBodyIndex));
        editSpline(bodies.get(currentBodyIndex));
        removeBodyButton.setDisable(false);
    }

    private void editSpline(RevolutionBody body) {
        this.body = body;
        body.getSpline().addListener(SplineSettings.this);
        uGridCount.setCurrentValue(body.getSpline().getUGridCount());
        vGridCount.setCurrentValue(body.getSpline().getVGridCount());
        kParameter.setCurrentValue(body.getSpline().getKParameter());
        colorPicker.setValue(body.getSpline().getColor());
        domainU.setValue(body.getSpline().getDomainU());
        domainV.setValue(body.getSpline().getDomainV());
        xCenter.setCurrentValue(body.getXCenter());
        yCenter.setCurrentValue(body.getYCenter());
        zCenter.setCurrentValue(body.getZCenter());
        xAngle.setCurrentValue(body.getXAngle());
        yAngle.setCurrentValue(body.getYAngle());
        zAngle.setCurrentValue(body.getZAngle());
        showAxesCheckBox.setSelected(body.getAreAxesDrawn());
        this.show();
        body.getSpline().setXRevolutionAxis(axesCanvas.getWidth() / 2);
        body.getSpline().setYRevolutionAxis(axesCanvas.getHeight() / 2);
        onSplineChanged(body.getSpline());
    }

    @Override
    public void onSplineChanged(Spline spline) {
        redrawControlPointsAndLine();
        redrawSpline();
        drawSerifs();
    }
}
