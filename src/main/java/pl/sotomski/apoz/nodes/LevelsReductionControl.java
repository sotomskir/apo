package pl.sotomski.apoz.nodes;

import javafx.beans.property.DoubleProperty;

/**
 * Created by sotomski on 28/10/15.
 */
public class LevelsReductionControl extends ChartControl {
    public LevelsReductionControl() {
        super();
    }

    /** @InheritDoc */
    @Override protected void layoutPlotChildren() {
        super.layoutPlotChildren();
        for (int i = 0; i<intervalDatas.size()-1; ++i) {
            IntervalData left  = intervalDatas.get(i);
            IntervalData right = intervalDatas.get(i+1);
            LevelLine levelLine = levelLines.get(i);
            levelLine.setStartX(xDisplay(left.getX().getValue()));
            levelLine.setStartY(yDisplay(left.getEndY()));
            levelLine.setEndX(xDisplay(right.getX().getValue()));
            levelLine.setEndY(yDisplay(right.getStartY()));
        }
    }

    @Override
    public void createDefaultIntervals(int intervals) {
        System.out.println("createDefaultIntervals "+getWidth());

        //clear plotChildren and lists
        intervalDatas.forEach(intervalData1 -> getPlotChildren().remove(intervalData1.getLine()));
        this.getPlotChildren().removeAll(levelLines);
        intervalDatas.clear();
        levelLines.clear();
        double intervalLength = 255.0 /intervals;

        // create new intervals
        for (int i = 0; i<=intervals; ++i) {
            double startX = intervalLength * i;
            double startY = startX - intervalLength;
            IntervalData d = new IntervalData(startX, startY, startX);
//            IntervalData d = new IntervalData(startX);
            this.getPlotChildren().add(d.getLine());
            intervalDatas.add(d);
        }

        // create new levelLines
        for (int i = 0; i<intervals; ++i) {
            IntervalData left  = intervalDatas.get(i);
            IntervalData right = intervalDatas.get(i+1);
            DoubleProperty leftStartX = left.getLine().startXProperty();
            DoubleProperty rightStartX   = right.getLine().startXProperty();
            DoubleProperty leftEndY = left.getLine().endYProperty();
            DoubleProperty rightStartY   = right.getLine().startYProperty();
            LevelLine levelLine = new LevelLine(leftStartX.getValue(), (leftEndY.getValue()), (rightStartX.getValue()), (rightStartY.getValue()));
//            leftEndY.setValue(yValue(levelLine.getStartY()));
//            rightStartY.setValue(yValue(levelLine.getEndY()));
//            levelLine.setStartX(leftStartX.getValue());
//            levelLine.setEndX(rightStartX.getValue());
            levelLine.startYProperty().addListener(observable1 -> {
                left.setEndY(yValue(levelLine.getStartY()));
                layoutPlotChildren();
            });
            levelLine.endYProperty().addListener(observable1 -> {
                right.setStartY(yValue(levelLine.getEndY()));
                layoutPlotChildren();
            });
            leftStartX.addListener(observable -> levelLine.setStartX(leftStartX.getValue()));
            rightStartX.addListener(observable -> levelLine.setEndX(rightStartX.getValue()));
            levelLines.add(levelLine);
        }

//        this.getPlotChildren().addAll(intervalDatas);
        this.getPlotChildren().addAll(levelLines);
        for (int i = 1; i < intervalDatas.size()-1; ++i) intervalDatas.get(i).getLine().enableDrag();
        for (int i = 1; i < levelLines.size(); ++i) levelLines.get(i).enableDrag();
//            layoutPlotChildren();
        updateLUT();
    }

}
