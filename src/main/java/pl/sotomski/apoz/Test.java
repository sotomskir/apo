package pl.sotomski.apoz;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;

/** Example of how a cubic curve works, drag the anchors around to change the curve. */
public class Test extends Application {
    public static void main(String[] args) throws Exception { launch(args); }
    @Override public void start(final Stage stage) throws Exception {
        CubicCurve curve1 = createStartingCurve1();
        CubicCurve curve2 = createStartingCurve2();

        Line controlLine1 = new BoundLine(curve1.controlX1Property(), curve1.controlY1Property(), curve1.startXProperty(), curve1.startYProperty());
        Line controlLine2 = new BoundLine(curve1.controlX2Property(), curve1.controlY2Property(), curve1.endXProperty(),   curve1.endYProperty());

        Anchor control1 = new Anchor(Color.GOLD,      curve1.controlX1Property(), curve1.controlY1Property(), null, null);
        Anchor control2 = new Anchor(Color.GOLD,      curve1.controlX2Property(), curve1.controlY2Property(), null, null);
        Anchor control3 = new Anchor(Color.GOLDENROD, curve2.controlX1Property(), curve2.controlY1Property(), null, null);
        Anchor control4 = new Anchor(Color.GOLDENROD, curve2.controlX2Property(), curve2.controlY2Property(), null, null);
        Anchor start    = new Anchor(Color.PALEGREEN, curve1.startXProperty(),    curve1.startYProperty(), control1, null);
        Anchor mid      = new Anchor(Color.TOMATO,    curve1.endXProperty(),      curve1.endYProperty(), control2, control3);
        mid.bind(curve2.startXProperty(), curve2.startYProperty());
        Anchor end      = new Anchor(Color.ALICEBLUE, curve2.endXProperty(),      curve2.endYProperty(), control4, null);

        stage.setTitle("Cubic Curve Manipulation Sample");
        stage.setScene(new Scene(new Group(controlLine1, controlLine2, curve1, curve2, start, control1, control2, control3, control4, mid, end), 400, 400, Color.ALICEBLUE));
        stage.show();
    }

    private CubicCurve createStartingCurve1() {
        CubicCurve curve = new CubicCurve();
        curve.setStartX(0);
        curve.setStartY(0);
        curve.setControlX1(75);
        curve.setControlY1(75);
        curve.setControlX2(75);
        curve.setControlY2(75);
        curve.setEndX(150);
        curve.setEndY(150);
        curve.setStroke(Color.FORESTGREEN);
        curve.setStrokeWidth(4);
        curve.setStrokeLineCap(StrokeLineCap.ROUND);
        curve.setFill(Color.CORNSILK.deriveColor(0, 1.2, 1, 0.6));
        return curve;
    }

    private CubicCurve createStartingCurve2() {
        CubicCurve curve = new CubicCurve();
        curve.setStartX(150);
        curve.setStartY(150);
        curve.setControlX1(225);
        curve.setControlY1(225);
        curve.setControlX2(225);
        curve.setControlY2(225);
        curve.setEndX(300);
        curve.setEndY(300);
        curve.setStroke(Color.FORESTGREEN);
        curve.setStrokeWidth(4);
        curve.setStrokeLineCap(StrokeLineCap.ROUND);
        curve.setFill(Color.CORNSILK.deriveColor(0, 1.2, 1, 0.6));
        return curve;
    }

    class BoundLine extends Line {
        BoundLine(DoubleProperty startX, DoubleProperty startY, DoubleProperty endX, DoubleProperty endY) {
            startXProperty().bind(startX);
            startYProperty().bind(startY);
            endXProperty().bind(endX);
            endYProperty().bind(endY);
            setStrokeWidth(2);
            setStroke(Color.GRAY.deriveColor(0, 1, 1, 0.5));
            setStrokeLineCap(StrokeLineCap.BUTT);
            getStrokeDashArray().setAll(10.0, 5.0);
        }
    }

    // a draggable anchor displayed around a point.
    class Anchor extends Circle {
        Circle controlPoint1;
        Circle controlPoint2;

        Anchor(Color color, DoubleProperty x, DoubleProperty y, Circle controlPoint1, Circle controlPoint2) {
            super(x.get(), y.get(), 10);
            this.controlPoint1 = controlPoint1;
            this.controlPoint2 = controlPoint2;
            setFill(color.deriveColor(1, 1, 1, 0.5));
            setStroke(color);
            setStrokeWidth(2);
            setStrokeType(StrokeType.OUTSIDE);

            x.bind(centerXProperty());
            y.bind(centerYProperty());
            enableDrag();
        }

        public void bind(DoubleProperty x, DoubleProperty y) {
            x.bind(centerXProperty());
            y.bind(centerYProperty());
        }

        // make a node movable by dragging it around with the mouse.
        private void enableDrag() {
            final Delta dragDelta = new Delta();
            setOnMousePressed(mouseEvent -> {
                // record a delta distance for the drag and drop operation.
                dragDelta.x = getCenterX() - mouseEvent.getX();
                dragDelta.y = getCenterY() - mouseEvent.getY();
                getScene().setCursor(Cursor.MOVE);
            });

            setOnMouseReleased(mouseEvent -> getScene().setCursor(Cursor.HAND));

            setOnMouseDragged(mouseEvent -> {
                double newX = mouseEvent.getX() + dragDelta.x;
                if (newX > 0 && newX < getScene().getWidth()) {
                    if (controlPoint1 != null) {
                        double deltaX = getCenterX() - controlPoint1.getCenterX();
                        controlPoint1.setCenterX(newX - deltaX);
                    }
                    if (controlPoint2 != null) {
                        double deltaX = getCenterX() - controlPoint2.getCenterX();
                        controlPoint2.setCenterX(newX - deltaX);
                    }
                    setCenterX(newX);
                }
                double newY = mouseEvent.getY() + dragDelta.y;
                if (newY > 0 && newY < getScene().getHeight()) {
                    if (controlPoint1 != null) {
                        double deltaY = getCenterY() - controlPoint1.getCenterY();
                        controlPoint1.setCenterY(newY - deltaY);
                    }
                    if (controlPoint2 != null) {
                        double deltaY = getCenterY() - controlPoint2.getCenterY();
                        controlPoint2.setCenterY(newY - deltaY);
                    }
                    setCenterY(newY);
                }
            });
            setOnMouseEntered(mouseEvent -> {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    getScene().setCursor(Cursor.HAND);
                }
            });
            setOnMouseExited(mouseEvent -> {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    getScene().setCursor(Cursor.DEFAULT);
                }
            });
        }

        // records relative x and y co-ordinates.
        private class Delta { double x, y; }
    }
}