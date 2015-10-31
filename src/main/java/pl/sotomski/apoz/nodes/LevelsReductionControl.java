package pl.sotomski.apoz.nodes;

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
//        this.getPlotChildren().removeAll(intervalData);
        this.getPlotChildren().removeAll(levelLines);
        intervalData.clear();
        levelLines.clear();
        double intervalLength = 255.0 /intervals;

        for (int i = 0; i<=intervals; ++i) {
            double startX = intervalLength * i;
            double startY = startX - intervalLength;
            IntervalData d = new IntervalData(startX, startY, startX);
//            IntervalData d = new IntervalData(startX);
            intervalData.add(d);
        }

        for (int i = 0; i<intervals; ++i) {
            IntervalLine left  = intervalData.get(i).getLine();
            IntervalLine right = intervalData.get(i+1).getLine();
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

//        this.getPlotChildren().addAll(intervalData);
        this.getPlotChildren().addAll(levelLines);
        for (int i = 1; i < intervalData.size()-1; ++i) intervalData.get(i).getLine().enableDrag();
        for (int i = 1; i < levelLines.size(); ++i) levelLines.get(i).enableDrag();
        updateLUT();
    }

}
