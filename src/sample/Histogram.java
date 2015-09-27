package sample;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by sotomski on 23/09/15.
 */
public class Histogram {
    LineChart<Number, Number> areaChart;
    NumberAxis xAxis;
    NumberAxis yAxis;
    XYChart.Series seriesR;
    XYChart.Series seriesG;
    XYChart.Series seriesB;
    XYChart.Series seriesM;
    int red[];
    int green[];
    int blue[];
    boolean mono;

    public Histogram(BufferedImage image) {
        seriesR = new XYChart.Series();
        seriesG = new XYChart.Series();
        seriesB = new XYChart.Series();
        seriesM = new XYChart.Series();
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();

        areaChart = new LineChart<Number, Number>(xAxis, yAxis);
        update(image);
        applyStyle();
    }

    public LineChart getAreaChart() {
        return areaChart;
    }

    public void update(BufferedImage image) {
//        seriesR.getData().clear();
//        seriesG.getData().clear();
//        seriesB.getData().clear();
        int height = image.getHeight();
        int width = image.getWidth();
        int bitDepth = image.getColorModel().getPixelSize()/3;
        int levels = (int)Math.pow(2, bitDepth);
        xAxis.setUpperBound(levels);
        red = new int[levels];
        green = new int[levels];
        blue =  new int[levels];
        for (int x=0;x<width;++x)
            for (int y=0;y<height;++y) {
                Color rgb = new Color(image.getRGB(x, y));
                ++red[rgb.getRed()];
                ++blue[rgb.getBlue()];
                ++green[rgb.getGreen()];
            }
        for (int x=0;x<levels;++x) {
            seriesR.getData().add(new XYChart.Data<Number, Number>(x, red[x]));
            seriesG.getData().add(new XYChart.Data<Number, Number>(x, green[x]));
            seriesB.getData().add(new XYChart.Data<Number, Number>(x, blue[x]));
            seriesM.getData().add(new XYChart.Data<Number, Number>(x, red[x]+green[x]+blue[x]));
        }
        areaChart.getData().addAll(seriesM, seriesR, seriesG, seriesB);

    }

    private void applyStyle() {
        areaChart.setHorizontalGridLinesVisible(false);
//        areaChart.setVerticalGridLinesVisible(false);
        areaChart.setVerticalZeroLineVisible(false);
        areaChart.setLegendVisible(false);
        areaChart.setCreateSymbols(false);
        yAxis.setMinorTickVisible(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setTickLabelsVisible(false);
        seriesR.getNode().getStyleClass().addAll("series-red", "hidden");
        seriesG.getNode().getStyleClass().addAll("series-green", "hidden");
        seriesB.getNode().getStyleClass().addAll("series-blue", "hidden");
        seriesM.getNode().getStyleClass().add("series-mono");
        mono = true;
    }

    public void switchType() {
        if (mono) {
            seriesR.getNode().getStyleClass().remove("hidden");
            seriesG.getNode().getStyleClass().remove("hidden");
            seriesB.getNode().getStyleClass().remove("hidden");
            seriesM.getNode().getStyleClass().add("hidden");
            mono = false;
        } else {
            seriesR.getNode().getStyleClass().add("hidden");
            seriesG.getNode().getStyleClass().add("hidden");
            seriesB.getNode().getStyleClass().add("hidden");
            seriesM.getNode().getStyleClass().remove("hidden");
            mono = true;
        }
    }

}
