package pl.sotomski.apoz.controls;

import javafx.animation.Interpolator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import pl.sotomski.apoz.utils.BestFitSplineInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sotomski on 11/4/15.
 */
public class CurvesControl extends LineChart {
    private static final int PLOT_SIZE = 255;
    private static final int N_SEGS    = 255;
    private Path path;
    private Path mousingPath;
    private List<Anchor> anchors;
    double[] x;
    double[] y;
    protected IntegerProperty changed;

    public CurvesControl() {
        super(new NumberAxis(0, 255, 10), new NumberAxis(0, 255, 10));
        changed = new SimpleIntegerProperty();
        path = new Path();
        mousingPath = new Path();
        mousingPath.setStrokeWidth(12);
        mousingPath.setStroke(Color.rgb(255, 255, 255, 0.01));
        Bindings.bindContent(mousingPath.getElements(), path.getElements());
        mousingPath.setOnMouseClicked(event -> {
            Anchor anchor = new Anchor(xValue(event.getSceneX()), yValue(event.getSceneY()));
            System.out.println("Anchor: "+anchor.x+", "+anchor.y);
            anchor.enableDrag(true, true);
            anchors.add(anchor);
            x = new double[x.length + 1];
            y = new double[y.length + 1];
            layoutPlotChildren();
        });
        path.setSmooth(true);
        path.setStroke(Color.DARKGRAY);
        anchors = new ArrayList<>();
        anchors.add(new Anchor(0, 0));
        anchors.add(new Anchor(128, 128));
        anchors.add(new Anchor(255, 255));
        x = new double[anchors.size()];
        y = new double[anchors.size()];
        anchors.forEach(anchor -> {
            if (anchors.indexOf(anchor) == 0 || anchors.indexOf(anchor) == anchors.size() - 1) {
                anchor.enableDrag(false, true);
            } else {
                anchor.enableDrag(true, true);
            }
        });
        layoutPlotChildren();
    }

    @Override protected void layoutPlotChildren() {
        System.out.println("layoutPlotChildren");
        super.layoutPlotChildren();
        getPlotChildren().removeAll(path);
        getPlotChildren().removeAll(mousingPath);
        getPlotChildren().removeAll(anchors);

        anchors.sort((o1, o2) -> o1.getCenterX() < o2.getCenterX() ? -1 : o1.getCenterX() == o2.getCenterX() ? 0 : 1);
        for (int i=0; i<anchors.size(); ++i) {
            x[i] = anchors.get(i).x / PLOT_SIZE;
            y[i] = anchors.get(i).y / PLOT_SIZE;
            System.out.println("x:y\t"+x[i]+":\t"+y[i]);
        }
        Interpolator pathInterpolator = new BestFitSplineInterpolator(x, y);
        getPlotChildren().addAll(path);
        getPlotChildren().addAll(mousingPath);
        getPlotChildren().addAll(anchors);
        final double y0 = pathInterpolator.interpolate(0, PLOT_SIZE, 0);
        path.getElements().clear();
        path.getElements().addAll(
                new MoveTo(xDisplay(0), yDisplay(y0))
        );

        for (int i = 0; i < N_SEGS; i++) {
            final double frac = (i + 1.0) / N_SEGS;
            final double x = xDisplay(frac * PLOT_SIZE);
            final double y = yDisplay(pathInterpolator.interpolate(0, PLOT_SIZE, frac));
            System.out.println(i+"\t(\t"+x+",\t"+y+"\t)");
            path.getElements().add(new LineTo(x, y));
        }
        anchors.forEach(anchor -> {
            anchor.setCenterX(xDisplay(anchor.x));
            anchor.setCenterY(yDisplay(anchor.y));
        });
        anchors.forEach(Node::toFront);
    }

    private double xDisplay(double x) {
        return getXAxis().getDisplayPosition(x);
    }

    private double yDisplay(double y) {
        return getYAxis().getDisplayPosition(y);
    }

    private double xValue(double x) {
        return (double) getXAxis().getValueForDisplay(x);
    }

    private double yValue(double y) {
        return (double) getYAxis().getValueForDisplay(y);
    }

    public int getChanged() {
        return changed.get();
    }

    public IntegerProperty changedProperty() {
        return changed;
    }

    public int[] getLUT() {
        return new int[0];
    }

    class Anchor extends Circle {
        double x, y;

        Anchor(double x, double y) {
            super(x, y, 4);
            this.x = x;
            this.y = y;
            this.setStroke(Color.BLACK);
            this.setFill(Color.rgb(222, 222, 222));
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
                if (horizontal) x = xValue(newX);
//                }
                double newY = mouseEvent.getY() + dragDelta.y;
//                min = getScene().getY();
//                max = getScene().getY()+getScene().getHeight();
//                if (newY > min && newY < max) {
                if (vertical) y = yValue(newY);
//                }
                layoutPlotChildren();
            });
            setOnMouseEntered(mouseEvent -> {
                if (!mouseEvent.isPrimaryButtonDown()) getScene().setCursor(Cursor.MOVE);

            });
            setOnMouseExited(mouseEvent -> {
                if (!mouseEvent.isPrimaryButtonDown()) getScene().setCursor(Cursor.DEFAULT);
            });
        }
        // records relative x and y co-ordinates.
        class Delta { double x, y; }
    }

}
