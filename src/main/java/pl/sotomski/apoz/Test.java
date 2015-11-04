package pl.sotomski.apoz;

import javafx.animation.Interpolator;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;
import pl.sotomski.apoz.utils.BestFitSplineInterpolator;

import java.util.ArrayList;
import java.util.List;

public class Test extends Application {

    private static final int PLOT_SIZE = 800;
    private static final int N_SEGS    = 255;
    Path path;
    List<Anchor> anchors;
    Group group;
    double[] x;
    double[] y;
    private Path mousingPath;

    class Anchor extends Circle {
        double x, y;
        Anchor(double x, double y) {
            super(x, y, 4);
            this.x = x;
            this.y = y;
            this.setStroke(Color.BLACK);
            this.setFill(Color.rgb(222,222,222));
        }
        public void enableDrag(boolean horizontal, boolean vertical) {
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
                if (horizontal) setCenterX(newX);
//                }
                double newY = mouseEvent.getY() + dragDelta.y;
//                min = getScene().getY();
//                max = getScene().getY()+getScene().getHeight();
//                if (newY > min && newY < max) {
                if (vertical) setCenterY(newY);
//                }

                plotSpline();

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
        mousingPath = new Path();
        mousingPath.setStrokeWidth(12);
        mousingPath.setStroke(Color.rgb(255, 255, 255, 0.01));
        Bindings.bindContent(mousingPath.getElements(), path.getElements());
        mousingPath.setOnMouseClicked(event -> {
            Anchor anchor = new Anchor(event.getSceneX(), event.getSceneY());
            anchor.enableDrag(true, true);
            anchors.add(anchor);
            group.getChildren().add(anchor);
            x = new double[x.length + 1];
            y = new double[y.length + 1];
            plotSpline();
        });
        path.setSmooth(true);
        path.setStroke(Color.DARKGRAY);
        anchors = new ArrayList<>();
        anchors.add(new Anchor(0, 800));
        anchors.add(new Anchor(400, 400));
        anchors.add(new Anchor(800, 0));
        x = new double[anchors.size()];
        y = new double[anchors.size()];
        anchors.forEach(anchor -> {
            if (anchors.indexOf(anchor) == 0 || anchors.indexOf(anchor) == anchors.size()-1) {
                anchor.enableDrag(false, true);
            } else {
                anchor.enableDrag(true, true);
            }
        });

        group = new Group();
        group.getChildren().addAll(anchors);
        plotSpline();
        stage.setScene(
                new Scene(group, Color.rgb(222, 222, 222))
        );
        stage.show();
    }

    private void plotSpline() {
        group.getChildren().removeAll(path);
        group.getChildren().removeAll(mousingPath);
        anchors.sort((o1, o2) -> o1.getCenterX() < o2.getCenterX() ? -1 : o1.getCenterX() == o2.getCenterX() ? 0 : 1);
        for (int i=0; i< anchors.size(); ++i) {
            x[i] = anchors.get(i).getCenterX() / PLOT_SIZE;
            y[i] = anchors.get(i).getCenterY() / PLOT_SIZE;
        }
        Interpolator pathInterpolator = new BestFitSplineInterpolator(x, y);
        group.getChildren().addAll(path);
        group.getChildren().addAll(mousingPath);
        final double y0 = pathInterpolator.interpolate(0, PLOT_SIZE, 0);
        path.getElements().clear();
        path.getElements().addAll(
                new MoveTo(0, y0)
        );

        for (int i = 0; i < N_SEGS; i++) {
            final double frac = (i + 1.0) / N_SEGS;
            final double x = frac * PLOT_SIZE;
            final double y = pathInterpolator.interpolate(0, PLOT_SIZE, frac);
            path.getElements().add(new LineTo(x, y));
        }
        anchors.forEach(Node::toFront);
    }

    public static void main(String[] args) { launch(args); }
}
