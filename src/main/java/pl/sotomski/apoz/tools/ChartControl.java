package pl.sotomski.apoz.tools;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sotomski on 25/10/15.
 */
public class ChartControl extends LineChart {

    protected List<IntervalLine> intervalLines;
    protected List<LevelLine> levelLines;
    protected int[] LUT = new int[256];
    protected IntegerProperty changed;
    protected boolean keepLevels;

    public ChartControl() {
        super(new NumberAxis(0, 255, 25), new NumberAxis(0, 255, 25));
        setMaxWidth(Double.MAX_VALUE);
        changed = new SimpleIntegerProperty();
        intervalLines = new ArrayList<>();
        levelLines    = new ArrayList<>();
        keepLevels = false;
    }

    public ChartControl(double maxWidth, double maxHeight) {
        this();
        setMaxHeight(maxHeight);
        setMaxWidth(maxWidth);
    }

    public void invert() {
        for (IntervalLine line : intervalLines) {
            if (keepLevels) {
                double startY = yValue(line.getStartY());
                line.setStartY(startY>0 ? yDisplay(0) : yDisplay(255));
            } else {
                double startY = line.getStartY();
                double endY = line.getEndY();
                line.setStartY(endY);
                line.setEndY(startY);
            }
        }
        updateLUT();
    }

    public void createDefaultIntervals(int intervals) {
        System.out.println("createDefaultIntervals "+getWidth());
        this.getPlotChildren().removeAll(intervalLines);
        this.getPlotChildren().removeAll(levelLines);
        intervalLines.clear();
        levelLines.clear();
        for (int i = 0; i<intervals; ++i) {
            IntervalLine l = new IntervalLine(255.0 / intervals * i);
            intervalLines.add(l);
            System.out.println(l);
        }
        intervalLines.add(new IntervalLine(255));
        for (int i = 0; i<intervals; ++i) {
            IntervalLine left  = intervalLines.get(i);
            IntervalLine right = intervalLines.get(i+1);
            DoubleProperty startX = left.startXProperty();
            DoubleProperty endX   = right.startXProperty();
            DoubleProperty startY = (i % 2 == 0) ? left.startYProperty()  : left.endYProperty();
            DoubleProperty endY   = (i % 2 == 0) ? right.startYProperty() : right.endYProperty();
            levelLines.add(new LevelLine(startX, startY, endX, endY));
        }
        this.getPlotChildren().addAll(intervalLines);
        this.getPlotChildren().addAll(levelLines);
        for (int i = 1; i < intervalLines.size()-1; ++i) intervalLines.get(i).enableDrag();
        updateLUT();
    }

    class IntervalLine extends Line {

        IntervalLine(double x) {
            setStartX(xDisplay(x));
            setStartY(yDisplay(0));
            setEndX(xDisplay(x));
            setEndY(yDisplay(255));
            setStrokeWidth(2);
            setStroke(Color.GRAY.deriveColor(0, 1, 1, 0.5));
            setStrokeLineCap(StrokeLineCap.BUTT);
            getStrokeDashArray().setAll(10.0, 5.0);
        }

        // make a node movable by dragging it around with the mouse.
        public void enableDrag() {
            final Delta dragDelta = new Delta();
            setOnMousePressed(mouseEvent -> {
                // record a delta distance for the drag and drop operation.
                dragDelta.x = getStartX() - mouseEvent.getX();
                dragDelta.y = getStartY() - mouseEvent.getY();
                getScene().setCursor(Cursor.MOVE);
            });

            setOnMouseReleased(mouseEvent -> getScene().setCursor(Cursor.E_RESIZE));

            setOnMouseDragged(mouseEvent -> {
                double newX = mouseEvent.getX() + dragDelta.x;
                if (newX > 0 && newX < getScene().getWidth()) {
                    setStartX(newX);
                    setEndX(newX);
                    updateLUT();
                }
            });
            setOnMouseEntered(mouseEvent -> {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    getScene().setCursor(Cursor.E_RESIZE);

                }
            });
            setOnMouseExited(mouseEvent -> {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    getScene().setCursor(Cursor.DEFAULT);
                }
            });
        }

        public void bindYtoX() {
            System.out.println(yDisplay(xValue(getEndX())));
            endYProperty().bind(endXProperty().multiply(yDisplay(xValue(getEndX()))).divide(getEndX()));
        }

        public void unBindYfromX() {
            endYProperty().unbind();
            endYProperty().setValue(yValue(getEndY())>0 ? yDisplay(255) : yDisplay(0));
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            return sb.append("Start: (\t").append(getStartX()).append(",\t").append(getStartY()).append("); End:(\t")
                    .append(getEndX()).append(",\t").append(getEndY()).append(");").toString();
        }

        // records relative x and y co-ordinates.
        private class Delta { double x, y; }

    }

    class LevelLine extends Line {
        LevelLine(DoubleProperty startX, DoubleProperty startY, DoubleProperty endX, DoubleProperty endY) {
            startXProperty().bind(startX);
            startYProperty().bind(startY);
            endXProperty().bind(endX);
            endYProperty().bind(endY);
            setStrokeWidth(1);
            setStroke(Color.BLACK);
        }
    }


    protected void updateLUT() {
        System.out.println("updateLUT");
        for (int i=0; i<LUT.length; ++i) LUT[i]=i;
        for (LevelLine levelLine : levelLines) {
            int scaledStartX = ((Double)getXAxis().getValueForDisplay(levelLine.getStartX())).intValue();
            int scaledEndX = ((Double)getXAxis().getValueForDisplay(levelLine.getEndX())).intValue();
            //TODO przedzial 0:1 zamiast 0:255
            int value = yValue(levelLine.getEndY()) > 0 ? 255 : 0;
            if (keepLevels) value = (int) yValue(levelLine.getEndY());
            if (!keepLevels || yValue(levelLine.getStartY()) == yValue(levelLine.getEndY()))
                for (int x = scaledStartX; x < scaledEndX; ++x) LUT[x] = value;
        }
        changed.setValue(changed.get() + 1);
        System.out.println("updateLUT2");
    }

    public IntegerProperty changedProperty() {
        return changed;
    }

    public void setKeepLevels(boolean keepLevels) {
        this.keepLevels = keepLevels;
        if (keepLevels) {
            System.out.println("setKeepLevels");
            for (IntervalLine l : intervalLines) l.bindYtoX();
            System.out.println("setKeepLevels2");
        }
        else for (IntervalLine l : intervalLines) l.unBindYfromX();
        updateLUT();
    }

    public int[] getLUT() {
        return LUT;
    }

    private double xValue(double x) {
        return (double) getXAxis().getValueForDisplay(x);
    }

    private double yValue(double y) {
        return (double) getYAxis().getValueForDisplay(y);
    }

    private double xDisplay(double x) {
        return getXAxis().getDisplayPosition(x);
    }

    private double yDisplay(double y) {
        return getYAxis().getDisplayPosition(y);
    }
}
