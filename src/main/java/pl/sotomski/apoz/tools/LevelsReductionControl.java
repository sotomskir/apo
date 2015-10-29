package pl.sotomski.apoz.tools;

import javafx.beans.property.DoubleProperty;

/**
 * Created by sotomski on 28/10/15.
 */
public class LevelsReductionControl extends ChartControl {
    public LevelsReductionControl() {
        super();
    }

    @Override
    public void createDefaultIntervals(int intervals) {
        System.out.println("createDefaultIntervals "+getWidth());
        this.getPlotChildren().removeAll(intervalLines);
        this.getPlotChildren().removeAll(levelLines);
        intervalLines.clear();
        levelLines.clear();
        double intervalLength = 255.0 /intervals;
        for (int i = 0; i<=intervals; ++i) {
            double startX = intervalLength * i;
            double startY = startX - intervalLength;
            double endY = startX;
            IntervalLine l = new IntervalLine(startX, startY, endY);
            intervalLines.add(l);
            System.out.println(l);
        }
//        intervalLines.add(new IntervalLine(255));
        for (int i = 0; i<intervals; ++i) {
            IntervalLine left  = intervalLines.get(i);
            IntervalLine right = intervalLines.get(i+1);
            DoubleProperty startX = left.startXProperty();
            DoubleProperty endX   = right.startXProperty();
            DoubleProperty startY = left.endYProperty();
            DoubleProperty endY   = right.startYProperty();
            LevelLine levelLine = new LevelLine(startX, startY, endX, endY);
            startY.bind(levelLine.startYProperty());
            endY.bind(levelLine.endYProperty());
            levelLine.startXProperty().bind(startX);
            levelLine.endXProperty().bind(endX);
            levelLines.add(levelLine);

        }
        this.getPlotChildren().addAll(intervalLines);
        this.getPlotChildren().addAll(levelLines);
        for (int i = 1; i < intervalLines.size()-1; ++i) intervalLines.get(i).enableDrag();
        for (int i = 1; i < levelLines.size()-1; ++i) levelLines.get(i).enableDrag();
        updateLUT();
    }

}
