package pl.sotomski.apoz.nodes;

import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
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

    protected List<Data<Number, Number>> data;
    protected List<IntervalData> intervalData;
    protected List<LevelLine> levelLines;
    protected int[] LUT = new int[256];
    protected IntegerProperty changed;
    protected boolean keepLevels;
    protected Series<Number, Number> series;

    public ChartControl() {
        super(new NumberAxis(0, 255, 25), new NumberAxis(0, 255, 25));
        data = new ArrayList<>();
        series = new Series<>();
        setMaxWidth(Double.MAX_VALUE);
        changed = new SimpleIntegerProperty();
        intervalData = new ArrayList<>();
        levelLines    = new ArrayList<>();
        keepLevels = false;
    }

    public ChartControl(double maxWidth, double maxHeight) {
        this();
        setMaxHeight(maxHeight);
        setMaxWidth(maxWidth);
    }

    public void invert() {
//        for (IntervalData line : intervalData) {
//            if (keepLevels) {
//                double startY = yValue(line.getStartY());
//                line.setStartY(startY>0 ? yDisplay(0) : yDisplay(255));
//            } else {
//                double startY = line.getStartY();
//                double endY = line.getEndY();
//                line.setStartY(endY);
//                line.setEndY(startY);
//            }
//        }
//        updateLUT();
    }

    @Override
    protected void layoutPlotChildren() {
        super.layoutPlotChildren();
        for (IntervalData d : intervalData) {
            IntervalLine line = d.getLine();
            line.setStartX(xDisplay(d.getX().getValue()));
            line.setEndX(xDisplay(d.getX().getValue()));
            line.setStartY(yDisplay(d.getStartY()));
            line.setEndY(yDisplay(d.getEndY()));
            this.getPlotChildren().add(line);
        }
    }

    public void createDefaultIntervals(int intervals) {
        System.out.println("createDefaultIntervals " + getWidth());
//        this.getPlotChildren().removeAll(intervalData);
        this.getPlotChildren().removeAll(levelLines);
        intervalData.clear();
        levelLines.clear();
        for (int i = 0; i<intervals; ++i) {
            IntervalData d = new IntervalData(255.0 / intervals * i);
            intervalData.add(d);
            System.out.println(d);
        }
        intervalData.add(new IntervalData(255));
        for (int i = 0; i<intervals; ++i) {
            IntervalLine left  = intervalData.get(i).getLine();
            IntervalLine right = intervalData.get(i+1).getLine();
            DoubleProperty startX = left.startXProperty();
            DoubleProperty endX   = right.startXProperty();
            DoubleProperty startY = (i % 2 == 0) ? left.startYProperty()  : left.endYProperty();
            DoubleProperty endY   = (i % 2 == 0) ? right.startYProperty() : right.endYProperty();
            LevelLine levelLine = new LevelLine(startX, startY, endX, endY);
            levelLine.startXProperty().bind(startX);
            levelLine.startYProperty().bind(startY);
            levelLine.endXProperty().bind(endX);
            levelLine.endYProperty().bind(endY);
            levelLines.add(levelLine);
        }
//        this.getPlotChildren().addAll(intervalData);
        this.getPlotChildren().addAll(levelLines);
        for (int i = 1; i < intervalData.size()-1; ++i) intervalData.get(i).getLine().enableDrag();
        updateLUT();
    }

    class IntervalData {
        DoubleProperty x, startY, endY;
        IntervalLine line;
        IntervalData() {
            x = new SimpleDoubleProperty();
            startY = new SimpleDoubleProperty();
            endY = new SimpleDoubleProperty();
        }

        IntervalData(double x) {
            this();
            line = new IntervalLine(x);
            this.x.setValue(x);
        }

        IntervalData(double x, double startY, double endY) {
            this();
            line = new IntervalLine(x, startY, endY);
            this.x.setValue(x);
            this.startY.setValue(startY);
            this.endY.setValue(endY);
        }

        public IntervalLine getLine() {
            return line;
        }

        public DoubleProperty getX() {
            return x;
        }

        public double getStartY() {
            return startY.get();
        }

        public DoubleProperty startYProperty() {
            return startY;
        }

        public double getEndY() {
            return endY.get();
        }

        public DoubleProperty endYProperty() {
            return endY;
        }
    }


    class IntervalLine extends Line {

        IntervalLine(double x) {
            super();
            setStartX(xDisplay(x));
            setStartY(yDisplay(0));
            setEndX(xDisplay(x));
            setEndY(yDisplay(255));
            setStrokeWidth(2);
            setStroke(Color.GRAY.deriveColor(0, 1, 1, 0.5));
            setStrokeLineCap(StrokeLineCap.BUTT);
            getStrokeDashArray().setAll(10.0, 5.0);
        }

        public IntervalLine(double startX, double startY, double endY) {
            this(startX);
            setStartY(yDisplay(startY));
            setEndY(yDisplay(endY));
        }

        private IntervalLine getLeft() {
            int index = intervalData.indexOf(this);
            return index == 0 ? null : intervalData.get(index-1).getLine();
        }

        private IntervalLine getRight() {
            int index = intervalData.indexOf(this);
            return index == intervalData.size()-1 ? null : intervalData.get(index+1).getLine();
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
                double min = getLeft().getEndX();
                double max = getRight().getEndX();
                if (newX > min && newX < max) {
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

        private InvalidationListener bindXYListener() {
            return l -> setEndY(yDisplay(xValue(getEndX())));
        }

        public void bindYtoX() {
            System.out.println(yDisplay(xValue(getEndX())));
            endXProperty().addListener(bindXYListener());
            setEndY(yDisplay(xValue(getEndX())));
        }

        public void unBindYfromX() {
            endYProperty().removeListener(bindXYListener());
            endYProperty().setValue(yValue(getEndY())>0 ? yDisplay(255) : yDisplay(0));
        }

        @Override
        public String toString() {
            return "Start: (\t" + getStartX() + ",\t" + getStartY() + "); End:(\t" + getEndX() + ",\t" + getEndY() + ");";
        }

        // records relative x and y co-ordinates.
        private class Delta { double x, y; }

    }

    class LevelLine extends Line {
        LevelLine(DoubleProperty startX, DoubleProperty startY, DoubleProperty endX, DoubleProperty endY) {
            setStrokeWidth(1);
            setStroke(Color.BLACK);
            setStartX(startX.getValue());
            setStartY(startY.getValue());
            setEndX(endX.getValue());
            setEndY(endY.getValue());
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

            setOnMouseReleased(mouseEvent -> getScene().setCursor(Cursor.V_RESIZE));

            setOnMouseDragged(mouseEvent -> {
                double newY = mouseEvent.getY() + dragDelta.y;
                if (newY > 0 && newY < getScene().getHeight()) {
                    setStartY(newY);
                    setEndY(newY);
                    updateLUT();
                }
            });

            setOnMouseEntered(mouseEvent -> {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    getScene().setCursor(Cursor.V_RESIZE);

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


    protected void updateLUT() {
        for(LevelLine l : levelLines) {
            double slope = (yValue(l.getEndY()) - yValue(l.getStartY())) / (xValue(l.getEndX()) - xValue(l.getStartX()));
            for (int x = (int) xValue(l.getStartX()); x<=xValue(l.getEndX()); ++x) {
                LUT[x] = (int) (slope * (x - xValue(l.getStartX())) + yValue(l.getStartY()));
                LUT[x] = LUT[x]>255 ? 255 : LUT[x]<0 ? 0 : LUT[x];
            }
        }
        changed.setValue(changed.get() + 1);
    }

    public IntegerProperty changedProperty() {
        return changed;
    }

    public void setKeepLevels(boolean keepLevels) {
        this.keepLevels = keepLevels;
        if (keepLevels) {
            for (IntervalData l : intervalData) l.getLine().bindYtoX();
        }
        else for (IntervalData l : intervalData) l.getLine().unBindYfromX();
        updateLUT();
    }

    public int[] getLUT() {
        return LUT;
    }

    protected double xValue(double x) {
        return (double) getXAxis().getValueForDisplay(x);
    }

    protected double yValue(double y) {
        return (double) getYAxis().getValueForDisplay(y);
    }

    protected double xDisplay(double x) {
        return getXAxis().getDisplayPosition(x);
    }

    protected double yDisplay(double y) {
        return getYAxis().getDisplayPosition(y);
    }
}
