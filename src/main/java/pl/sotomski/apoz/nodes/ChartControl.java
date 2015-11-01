package pl.sotomski.apoz.nodes;

import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
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
    protected List<IntervalData> intervalDatas;
    protected List<LevelLine> levelLines;
    protected int[] LUT = new int[256];
    protected IntegerProperty changed;
    protected boolean keepLevels;
    protected boolean inverted = false;
    protected Series<Number, Number> series;

    public ChartControl() {
        super(new NumberAxis(0, 255, 25), new NumberAxis(0, 255, 25));
        data = new ArrayList<>();
        series = new Series<>();
        setMaxWidth(Double.MAX_VALUE);
        changed = new SimpleIntegerProperty();
        intervalDatas = new ArrayList<>();
        levelLines    = new ArrayList<>();
        keepLevels = false;
    }

    public ChartControl(double maxWidth, double maxHeight) {
        this();
        setMaxHeight(maxHeight);
        setMaxWidth(maxWidth);
    }

    /**
     * Inverts level lines
     */
    public void invert() {
        System.out.println("invert");
        for (IntervalData data : intervalDatas) {
            if (keepLevels) {
                double startY = data.getStartY();
                System.out.println("before"+data);
                data.setStartY(startY > 1 ? 0 : 255);
                System.out.println("after"+data);
            } else {
                double startY = data.getStartY();
                double endY = data.getEndY();
                data.setStartY(endY);
                data.setEndY(startY);
            }
        }
        inverted = !inverted;
        layoutPlotChildren();
    }

    /** @inheritDoc */
    @Override protected void layoutPlotChildren() {
        System.out.println("layoutPlotChildren");
        super.layoutPlotChildren();
        for (IntervalData d : intervalDatas) {
            IntervalLine line = d.getLine();
            line.setStartX(xDisplay(d.getX().getValue()));
            line.setEndX(xDisplay(d.getX().getValue()));
            line.setStartY(yDisplay(d.getStartY()));
            line.setEndY(yDisplay(d.getEndY()));
            System.out.println(d);
        }
        updateLUT();
    }

    /** creates default equal intervals
     * @param intervals number of intervals
     * */
    public void createDefaultIntervals(int intervals) {
        System.out.println("createDefaultIntervals " + getWidth());

        //clear plotChildren and lists
        intervalDatas.forEach(intervalData1 -> getPlotChildren().remove(intervalData1.getLine()));
        this.getPlotChildren().removeAll(levelLines);
        intervalDatas.clear();
        levelLines.clear();

        // create new intervals
        for (int i = 0; i<intervals; ++i) {
            IntervalData d = new IntervalData(255.0 / intervals * i);
            intervalDatas.add(d);
            System.out.println(d);
            this.getPlotChildren().add(d.getLine());
        }
        intervalDatas.add(new IntervalData(255));

        // create new levelLines
        for (int i = 0; i<intervals; ++i) {
            IntervalLine left  = intervalDatas.get(i).getLine();
            IntervalLine right = intervalDatas.get(i+1).getLine();
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
        this.getPlotChildren().addAll(levelLines);

        // enable drag
        for (int i = 1; i < intervalDatas.size()-1; ++i) intervalDatas.get(i).getLine().enableDrag();
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
            line = new IntervalLine(this, x);
            startY.setValue(0);
            endY.setValue(255);
            this.x.setValue(x);
        }

        IntervalData(double x, double startY, double endY) {
            this();
            line = new IntervalLine(this, x, startY, endY);
            this.x.setValue(x);
            this.startY.setValue(startY);
            this.endY.setValue(endY);
        }

        @Override
        public String toString() {
            return "Data:\t(\t"+x.getValue()+",\t"+startY.getValue()+",\t"+endY.getValue()+")";//\nLine:\t(\t"+line.getStartX()+",\t"+line.getStartY()+",\t"+line.getEndY()+")";
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

        public void setStartY(double startY) {
            this.startY.setValue(startY);
        }

        public void setEndY(double endY) {
            this.endY.setValue(endY);
        }

        private DoubleProperty endXProperty() {
            return x;
        }

        public void bindYtoX() {
            System.out.println(getX().getValue());
            endXProperty().addListener(bindXYListener());
            setEndY(getX().getValue());
        }
        public void unBindYfromX() {
            endYProperty().removeListener(bindXYListener());
            if (inverted) endYProperty().setValue(getEndY() < 255 ? 0 : 255);
            else endYProperty().setValue(getEndY() > 0 ? 255 : 0);
            // special case for first and last line
            if (x.getValue() == 0 || x.getValue() == 255) {
                endYProperty().setValue(inverted ? 0 : 255);
                startYProperty().setValue(inverted ? 255 : 0);
            }
        }

        private InvalidationListener bindXYListener() {
            return l -> setEndY(getX().getValue());
        }

        public void setX(double x) {
            this.x.setValue(x);
        }
    }


    class IntervalLine extends Line {

        private IntervalData data;
        private ContextMenu menu;
        private MenuItem menuItem;

        IntervalLine(IntervalData data, double x) {
            super();
            this.data = data;
            this.menu = new ContextMenu();
            this.menuItem = new MenuItem();
            menu.getItems().add(menuItem);
            setStartX(xDisplay(x));
            setStartY(yDisplay(0));
            setEndX(xDisplay(x));
            setEndY(yDisplay(255));
            setStrokeWidth(2);
            setStroke(Color.GRAY.deriveColor(0, 1, 1, 0.5));
            setStrokeLineCap(StrokeLineCap.BUTT);
            getStrokeDashArray().setAll(10.0, 5.0);
        }

        public IntervalLine(IntervalData data, double startX, double startY, double endY) {
            this(data, startX);
            setStartY(yDisplay(startY));
            setEndY(yDisplay(endY));
        }
        public void bindYtoX() {
            setEndY(yDisplay(xValue(getEndX())));
            System.out.println(getEndX());
            endXProperty().addListener(bindXYListener());
        }
        public void unBindYfromX() {
            endYProperty().removeListener(bindXYListener());
            endYProperty().setValue(getEndY() > 0 ? 255 : 0);
        }

        private InvalidationListener bindXYListener() {
            return l -> setEndY(yDisplay(xValue(getEndX())));
        }

        private IntervalLine getLeft() {
            int index = intervalDatas.indexOf(this.getData());
            return index <= 0 ? null : intervalDatas.get(index - 1).getLine();
        }

        private IntervalLine getRight() {
            int index = intervalDatas.indexOf(this.getData());
            return index > intervalDatas.size() ? null : intervalDatas.get(index+1).getLine();
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
                double min =  getLeft().getEndX();
                double max = getRight().getEndX();
                if (newX > min && newX < max) {
                    setStartX(newX);
                    setEndX(newX);
                    this.data.setX(xValue(newX));
                    updateLUT();
                    layoutPlotChildren();
                }
            });

            setOnMouseEntered(mouseEvent -> {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    getScene().setCursor(Cursor.E_RESIZE);
                    menuItem.setText("X:" + data.getX() + " sY:" + data.getStartY() + " eY:" + data.getEndY());
                    menu.show(getScene().getWindow(), mouseEvent.getX() + 25, mouseEvent.getY() + 25);
                }
            });

            setOnMouseExited(mouseEvent -> {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    getScene().setCursor(Cursor.DEFAULT);
                    menu.hide();
                }
            });

        }


        @Override
        public String toString() {
            return "Start: (\t" + getStartX() + ",\t" + getStartY() + "); End:(\t" + getEndX() + ",\t" + getEndY() + ");";
        }

        public Object getData() {
            return data;
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
                    layoutPlotChildren();
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
            for (IntervalData l : intervalDatas) l.bindYtoX();
        }
        else for (IntervalData l : intervalDatas) l.unBindYfromX();
        layoutPlotChildren();
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
