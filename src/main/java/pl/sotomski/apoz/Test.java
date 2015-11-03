package pl.sotomski.apoz;

import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Test extends Application {

    private static final Duration CYCLE_TIME = Duration.seconds(7);

    private static final int PLOT_SIZE = 800;
    private static final int N_SEGS    = PLOT_SIZE / 10;
    Path path;

    double[] x;
    double[] y;

    class Anchor extends Circle {

        Anchor(double x, double y) {
            super(x, y, 4);
            this.setStroke(Color.BLUEVIOLET);
        }
        public void enableDrag() {
            final Delta dragDelta = new Delta();
            setOnMousePressed(mouseEvent -> {
                // record a delta distance for the drag and drop operation.
                dragDelta.x = getCenterX() - mouseEvent.getX();
                dragDelta.y = getCenterY() - mouseEvent.getY();
                getScene().setCursor(Cursor.MOVE);
            });

            setOnMouseReleased(mouseEvent -> getScene().setCursor(Cursor.MOVE));

            setOnMouseDragged(mouseEvent -> {
                double newX = mouseEvent.getX() + dragDelta.x;
//                double min = getScene().getX();
//                double max = getScene().getX()+getScene().getWidth();
//                if (newX > min && newX < max) {
                setCenterX(newX);
//                }
                double newY = mouseEvent.getY() + dragDelta.y;
//                min = getScene().getY();
//                max = getScene().getY()+getScene().getHeight();
//                if (newY > min && newY < max) {
                setCenterY(newY);
//                }
                Interpolator pathInterpolator = new BestFitSplineInterpolator(x, y);
                // interpolated spline function plot.
                plotSpline(path, pathInterpolator, true);

            });

            setOnMouseEntered(mouseEvent -> {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    getScene().setCursor(Cursor.MOVE);
                }
            });

            setOnMouseExited(mouseEvent -> {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    getScene().setCursor(Cursor.DEFAULT);
                }
            });
        }
        // records relative x and y co-ordinates.
        class Delta { double x, y; }
    }

    public void start(Stage stage) {
        path = new Path();
        path.setStroke(Color.DARKGREEN);
        List<Anchor> anchors = new ArrayList<>();
        anchors.add(new Anchor(0, 0));
        anchors.add(new Anchor(100, 100));
        anchors.add(new Anchor(200, 200));
        anchors.add(new Anchor(300, 300));
        anchors.add(new Anchor(400, 400));
        path.getElements().addAll();
        x = new double[anchors.size()];
        y = new double[anchors.size()];
        anchors.forEach(Test.Anchor::enableDrag);

        for (int i=0; i< anchors.size(); ++i) {
            x[i] = anchors.get(i).getCenterX();
            y[i] = anchors.get(i).getCenterY();
        }
        Group group = new Group(path);
        group.getChildren().addAll(anchors);

        stage.setScene(
                new Scene(group, Color.rgb(35,39,50))
        );
        stage.show();
    }

    // plots an interpolated curve in segments along a path
    // if invert is true then y=0 will be in the bottom left, otherwise it is in the top right
    private void plotSpline(Path path, Interpolator pathInterpolator, boolean invert) {
        final double y0 = pathInterpolator.interpolate(0, PLOT_SIZE, 0);
        path.getElements().addAll(
                new MoveTo(0, invert ? PLOT_SIZE - y0 : y0)
        );

        for (int i = 0; i < N_SEGS; i++) {
            final double frac = (i + 1.0) / N_SEGS;
            final double x = frac * PLOT_SIZE;
            final double y = pathInterpolator.interpolate(0, PLOT_SIZE, frac);
            path.getElements().add(new LineTo(x, invert ? PLOT_SIZE - y : y));
        }
    }

    public static void main(String[] args) { launch(args); }
}
