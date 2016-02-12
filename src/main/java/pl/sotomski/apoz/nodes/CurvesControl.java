package pl.sotomski.apoz.nodes;

import javafx.animation.Interpolator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import org.apache.commons.math3.exception.OutOfRangeException;
import pl.sotomski.apoz.utils.BestFitSplineInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sotomski on 11/4/15.
 */
public class CurvesControl extends LineChart {
    private static final int PLOT_SIZE = 255;
    private static final int CHART_SIZE = 300;
    private Path path;
    private Path mousingPath;
    private List<Anchor> anchors;
    double[] x;
    double[] y;
    private int[] LUT;
    protected IntegerProperty changed;

    public CurvesControl() {
        super(new NumberAxis(0, 255, 64), new NumberAxis(0, 255, 64));
        setMinSize(CHART_SIZE, CHART_SIZE);
        setPrefSize(CHART_SIZE, CHART_SIZE);
        setMaxSize(CHART_SIZE, CHART_SIZE);
        changed = new SimpleIntegerProperty();
        path = new Path();
        LUT = new int[256];
        for (int i=0; i<LUT.length; ++i) LUT[i] = i;
        mousingPath = new Path();
        mousingPath.setStrokeWidth(12);
        mousingPath.setStroke(Color.rgb(255, 255, 255, 0.01));
        Bindings.bindContent(mousingPath.getElements(), path.getElements());
        mousingPath.setOnMouseClicked(event -> {
            Anchor anchor = new Anchor(xValue(event.getX()), yValue(event.getY()));
//            System.out.println("Anchor: "+anchor.x+", "+anchor.y);
            anchor.enableDrag(false, false);
            anchors.add(anchor);
            getPlotChildren().add(anchor);
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
        getPlotChildren().addAll(anchors);
        x = new double[anchors.size()];
        y = new double[anchors.size()];
        anchors.forEach(anchor -> {
            if (anchors.indexOf(anchor) == 0) {
                anchor.enableDrag(true, false);
            } else if (anchors.indexOf(anchor) == anchors.size() - 1) {
                anchor.enableDrag(false, true);
            } else {
                anchor.enableDrag(false, false);
            }
        });
        layoutPlotChildren();
    }

    @Override protected void layoutPlotChildren() {
//        System.out.println("layoutPlotChildren");
        super.layoutPlotChildren();
        getPlotChildren().removeAll(path);
        getPlotChildren().removeAll(mousingPath);

        anchors.sort((o1, o2) -> o1.x < o2.x ? -1 : o1.x == o2.x ? 0 : 1);
        for (int i=0; i<anchors.size(); ++i) {
            x[i] = anchors.get(i).x / PLOT_SIZE;
//            x[i] = anchors.get(i).x;
            y[i] = anchors.get(i).y / PLOT_SIZE;
//            y[i] = anchors.get(i).y;
//            System.out.println("x:y\t"+x[i]+":\t"+y[i]);
        }
        Interpolator pathInterpolator = new BestFitSplineInterpolator(x, y);
        getPlotChildren().addAll(path);
        getPlotChildren().addAll(mousingPath);
        double y0;
        try {
            y0 = pathInterpolator.interpolate(0, PLOT_SIZE, 0);
        } catch (OutOfRangeException e) {
            y0 = 0;
        }
        path.getElements().clear();
        path.getElements().addAll(new MoveTo(xDisplay(0), yDisplay(y0)));

        LUT[0] = (int) anchors.get(0).y;
//        System.out.println("LUT[0]="+LUT[0]);
        for (double i = 1; i < PLOT_SIZE; i++) {
            final double frac = (i) / PLOT_SIZE;
            final double x = frac * PLOT_SIZE;
            double y;
            try {
                y = pathInterpolator.interpolate(0, PLOT_SIZE, frac);
            } catch (OutOfRangeException e) {
                y = LUT[(int)Math.round(x)-1] > 128 ? 255 : 0;
            }
//            System.out.println(i+"\t(\t"+Math.round(xValue(x))+",\t"+Math.round(yValue(y))+"\t)"+" "+frac);
            LUT[((int) Math.round(x))] = (int) (Math.round(y) > 255 ? 255 : Math.round(y) < 0 ? 0 : Math.round(y));
            y = y > 255 ? 255 : y < 0 ? 0 : y;
            path.getElements().add(new LineTo(xDisplay(x), yDisplay(y)));
        }
        anchors.forEach(anchor -> {
            anchor.setCenterX(xDisplay(anchor.x));
            anchor.setCenterY(yDisplay(anchor.y));
        });
        anchors.forEach(Node::toFront);
        changedProperty().setValue(getChanged()+1);
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
        return LUT;
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

        public void enableDrag(boolean isStartpoint, boolean isEndpoint) {
            final Delta dragDelta = new Delta();
            setOnMousePressed(mouseEvent -> {
                System.out.println("Pressed");
                // record a delta distance for the drag and drop operation.
                dragDelta.x = getCenterX() - mouseEvent.getX();
                dragDelta.y = getCenterY() - mouseEvent.getY();
//                getScene().setCursor(Cursor.MOVE);
            });
            setOnMouseReleased(mouseEvent -> {
                System.out.println("Released");
                getScene().setCursor(Cursor.DEFAULT);
            });
            setOnMouseDragged(mouseEvent -> {
                System.out.println("Dragged");
                double newX = xValue(mouseEvent.getX() + dragDelta.x);
                double newY = yValue(mouseEvent.getY() + dragDelta.y);
                if(isEndpoint) {
                    if (newX > newY) {
                        setX(255);
                        setY(newY);
                    } else {
                        setX(newX);
                        setY(255);
                    }
                } else if(isStartpoint) {
                    if (newX < newY) {
                        setX(0);
                        setY(newY);
                    } else {
                        setX(newX);
                        setY(0);
                    }
                } else {
                    setX(newX);
                    setY(newY);
                }
                layoutPlotChildren();
            });
            setOnMouseEntered(mouseEvent -> {
                if (!mouseEvent.isPrimaryButtonDown()) getScene().setCursor(Cursor.MOVE);
                System.out.println("Entered");
            });
            setOnMouseExited(mouseEvent -> {
                if (!mouseEvent.isPrimaryButtonDown()) getScene().setCursor(Cursor.DEFAULT);
                System.out.println("Exited");
            });
        }

        private void setX(double newX) {
            double max = 255;
            double min = 0;
            if(newX >= min && newX <= max) x = newX;
            else x = newX > max ? max : min;
//            System.out.println("newX:"+newX);
//            System.out.println("X:"+x);
        }

        private void setY(double newY) {
            double min = 0;
            double max = 255;
            if(newY >= min && newY <= max) y = newY;
            else y = newY < min ? min : max;
//            System.out.println("Y:"+y);
        }

        // records relative x and y co-ordinates.
        class Delta { double x, y; }
    }

}
