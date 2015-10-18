package pl.sotomski.apoz.utils;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.awt.image.BufferedImage;

/**
 * Created by sotomski on 23/09/15.
 */
public class HistogramManager {
    private BufferedImage image;
    private Histogram histogram;
    private BarChart<String, Number> barChart;
    private CategoryAxis xAxis;
    private NumberAxis yAxis;
    private XYChart.Series seriesR;
    private XYChart.Series seriesG;
    private XYChart.Series seriesB;
    private XYChart.Series seriesM;
    private boolean mono;

    private HistogramManager() {
        seriesR = new XYChart.Series();
        seriesG = new XYChart.Series();
        seriesB = new XYChart.Series();
        seriesM = new XYChart.Series();
        xAxis = new CategoryAxis();
//        xAxis.setAutoRanging(false);
//        xAxis.setLowerBound(0d);
//        xAxis.setTickUnit(25);
        yAxis = new NumberAxis();
        mono = true;
        barChart = new BarChart<String, Number>(xAxis, yAxis);
        barChart.getData().addAll(seriesM);
//        seriesM.getNode().getStyleClass().add("series-mono");
        applyStyle();
    }

    public HistogramManager(BufferedImage image) {
        this();
        this.image = image;
        update();
    }

    public BarChart getBarChart() {
        return barChart;
    }

    public void update() {
        histogram = new Histogram(image);
        seriesR.getData().clear();
        seriesG.getData().clear();
        seriesB.getData().clear();
        seriesM.getData().clear();
//        xAxis.setUpperBound(levels-1);
        for (int i=0;i<histogram.getLevels();++i) {
            seriesR.getData().add(new XYChart.Data<String, Number>(Integer.toString(i), histogram.gethR()[i]));
            seriesG.getData().add(new XYChart.Data<String, Number>(Integer.toString(i), histogram.gethG()[i]));
            seriesB.getData().add(new XYChart.Data<String, Number>(Integer.toString(i), histogram.gethB()[i]));
            seriesM.getData().add(new XYChart.Data<String, Number>(Integer.toString(i), histogram.gethM()[i]));
        }
    }

    private void applyStyle() {
//        barChart.setHorizontalGridLinesVisible(false);
        barChart.setVerticalGridLinesVisible(false);
        barChart.setVerticalZeroLineVisible(false);
        barChart.setCategoryGap(0d);
        barChart.setBarGap(0d);
        barChart.setAnimated(false);
        barChart.setLegendVisible(false);
//        barChart.setCreateSymbols(false);
        yAxis.setMinorTickVisible(false);
        yAxis.setTickMarkVisible(false);
//        xAxis.setTickMarkVisible(false);
        yAxis.setTickLabelsVisible(false);
//        xAxis.setTickLabelsVisible(false);
        barChart.setMaxHeight(200.0);
        mono = true;
    }

    public void switchType() {
        if (mono && histogram.getChannels()>1) {
            barChart.getData().remove(seriesM);
            barChart.getData().addAll(seriesR, seriesG, seriesB);
            mono = false;
        } else if (!mono) {
            barChart.getData().removeAll(seriesR, seriesG, seriesB);
            barChart.getData().add(seriesM);
            mono = true;
        }
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
