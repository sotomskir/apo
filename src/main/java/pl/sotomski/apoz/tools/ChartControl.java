package pl.sotomski.apoz.tools;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Cursor;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sotomski on 25/10/15.
 */
public class ChartControl extends Pane {

    private List<IntervalLine> intervalLines;
    private List<LevelLine> levelLines;
    private int[] LUT = new int[256];
    private IntegerProperty changed;

    public ChartControl() {
        super();
        setMaxWidth(Double.MAX_VALUE);
        changed = new SimpleIntegerProperty();
        intervalLines = new ArrayList<>();
        levelLines    = new ArrayList<>();
    }

    public ChartControl(int intervals) {
        this();
        createDefaultIntervals(intervals);
        widthProperty().addListener(observable -> resize());
    }

    private void resize() {
        System.out.println(getWidth()+" r");
        for (int i = 0; i<intervalLines.size(); ++i) {
            intervalLines.get(i).setStartX( getWidth() / (intervalLines.size()-1) * i);
            intervalLines.get(i).setEndX(   getWidth() / (intervalLines.size()-1) * i);
        }
        intervalLines.get(intervalLines.size()-1).setStartX(getWidth()-2);
        intervalLines.get(intervalLines.size()-1).setEndX(getWidth()-2);
        updateLUT();
    }

    public void invert() {
        for (IntervalLine line : intervalLines) {
            double startY = line.getStartY();
            double endY   = line.getEndY();
            line.setStartY(endY);
            line.setEndY(startY);
        }
        updateLUT();
    }

    public void createDefaultIntervals(int intervals) {
        System.out.println(getWidth());
        this.getChildren().clear();
        intervalLines.clear();
        levelLines.clear();
        for (int i = 0; i<intervals; ++i) {
            intervalLines.add(new IntervalLine(getWidth() / intervals * i));
        }
        intervalLines.add(new IntervalLine(getWidth()-2));
        for (int i = 0; i<intervals; ++i) {
            IntervalLine left  = intervalLines.get(i);
            IntervalLine right = intervalLines.get(i+1);
            DoubleProperty startX = left.startXProperty();
            DoubleProperty endX   = right.startXProperty();
            DoubleProperty startY = (i % 2 == 0) ? left.startYProperty()  : left.endYProperty();
            DoubleProperty endY   = (i % 2 == 0) ? right.startYProperty() : right.endYProperty();
            levelLines.add(new LevelLine(startX, startY, endX, endY));
        }
        this.getChildren().addAll(intervalLines);
        this.getChildren().addAll(levelLines);
        for (int i = 1; i < intervalLines.size()-1; ++i) intervalLines.get(i).enableDrag();
        updateLUT();
    }

    class IntervalLine extends Line {
        Tooltip tooltip;

        IntervalLine(double x) {
            setStartX(x);
            setStartY(100);
            setEndX(x);
            setEndY(0);
            setStrokeWidth(2);
            setStroke(Color.GRAY.deriveColor(0, 1, 1, 0.5));
            setStrokeLineCap(StrokeLineCap.BUTT);
            getStrokeDashArray().setAll(10.0, 5.0);
            Tooltip.install(this, new Tooltip("X:" + getStartX()));
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

    private void updateLUT() {
        for (LevelLine levelLine : levelLines) {
            //TODO przedzial 0:1 zamiast 0:255
            int value = levelLine.getEndY()>0 ? 0 : 255;
            int scaledStartX = (int) (levelLine.getStartX() * 255 / getWidth());
            int scaledEndX = (int) (levelLine.getEndX() * 255 / getWidth());
            for (int x = scaledStartX ; x<scaledEndX; ++x) LUT[x] = value;
        }
        changed.setValue(changed.get()+1);
    }

    public IntegerProperty changedProperty() {
        return changed;
    }

    public int[] getLUT() {
        return LUT;
    }
}
