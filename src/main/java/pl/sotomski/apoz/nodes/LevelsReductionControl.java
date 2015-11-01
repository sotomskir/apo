package pl.sotomski.apoz.nodes;

import javafx.beans.property.DoubleProperty;

/**
 * Created by sotomski on 28/10/15.
 */
public class LevelsReductionControl extends ChartControl {
    public LevelsReductionControl() {
        super();
    }

//    @Override
//    protected void layoutPlotChildren() {
//        super.layoutPlotChildren();
//
//    }

    @Override
    public void createDefaultIntervals(int intervals) {
        System.out.println("createDefaultIntervals "+getWidth());
//        this.getPlotChildren().removeAll(intervalDatas);
        this.getPlotChildren().removeAll(levelLines);
        intervalDatas.clear();
        levelLines.clear();
        double intervalLength = 255.0 /intervals;

        for (int i = 0; i<=intervals; ++i) {
            double startX = intervalLength * i;
            double startY = startX - intervalLength;
            IntervalData d = new IntervalData(startX, startY, startX);
//            IntervalData d = new IntervalData(startX);
            intervalDatas.add(d);
        }

        for (int i = 0; i<intervals; ++i) {
            IntervalLine left  = intervalDatas.get(i).getLine();
            IntervalLine right = intervalDatas.get(i+1).getLine();
            DoubleProperty startX = left.startXProperty();
            DoubleProperty endX   = right.startXProperty();
            DoubleProperty startY = left.endYProperty();
            DoubleProperty endY   = right.startYProperty();
            LevelLine levelLine = new LevelLine(startX, startY, endX, endY);
//            startY.bind(levelLine.startYProperty());
//            endY.bind(levelLine.endYProperty());
//            levelLine.startXProperty().bind(startX);
//            levelLine.endXProperty().bind(endX);
            levelLines.add(levelLine);

        }

//        this.getPlotChildren().addAll(intervalDatas);
        this.getPlotChildren().addAll(levelLines);
        for (int i = 1; i < intervalDatas.size()-1; ++i) intervalDatas.get(i).getLine().enableDrag();
        for (int i = 1; i < levelLines.size(); ++i) levelLines.get(i).enableDrag();
        updateLUT();
    }

}
