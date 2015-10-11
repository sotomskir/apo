package pl.sotomski.apoz.utils;

import javafx.scene.chart.*;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by sotomski on 23/09/15.
 */
public class HistogramChart {

    private BarChart<String, Number> barChart;
    private CategoryAxis xAxis;
    private NumberAxis yAxis;
    private XYChart.Series seriesR;
    private XYChart.Series seriesG;
    private XYChart.Series seriesB;
    private XYChart.Series seriesM;
    private int hR[];
    private int hG[];
    private int hB[];
    private int hM[];
    private double hRavg, hGavg, hBavg, Havg;
    private boolean mono;
    private int channels;
    public int[] gethM() {
        return hM;
    }

    public double gethBavg() {
        return hBavg;
    }

    public double gethGavg() {

        return hGavg;
    }

    public int[] gethR() {
        return hR;
    }

    public int[] gethG() {
        return hG;
    }

    public int[] gethB() {
        return hB;
    }

    public double gethRavg() {

        return hRavg;
    }

    public double getHavg() {
        return Havg;
    }

    public HistogramChart() {
        seriesR = new XYChart.Series();
        seriesG = new XYChart.Series();
        seriesB = new XYChart.Series();
        seriesM = new XYChart.Series();
        xAxis = new CategoryAxis();
//        xAxis.setAutoRanging(false);
//        xAxis.setLowerBound(0d);
//        xAxis.setTickUnit(25);
        yAxis = new NumberAxis();

        barChart = new BarChart<String, Number>(xAxis, yAxis);
        barChart.getData().addAll(seriesM);
//        seriesM.getNode().getStyleClass().add("series-mono");
        applyStyle();
    }

    public BarChart getBarChart() {
        return barChart;
    }

    public void update(BufferedImage image) {
        seriesR.getData().clear();
        seriesG.getData().clear();
        seriesB.getData().clear();
        seriesM.getData().clear();
        int height = image.getHeight();
        int width = image.getWidth();
        channels = image.getColorModel().getNumComponents();
        int bitDepth = image.getColorModel().getPixelSize()/channels;
        int levels = (int)Math.pow(2, bitDepth);
//        xAxis.setUpperBound(levels-1);

        hR = new int[levels];
        hG = new int[levels];
        hB =  new int[levels];
        hM =  new int[levels];

        double Hsum = 0, hRsum = 0, hGsum = 0, hBsum = 0;
        for (int x=0;x<width;++x)
            for (int y=0;y<height;++y) {
                Color rgb = new Color(image.getRGB(x, y));
                ++hR[rgb.getRed()];
                ++hB[rgb.getBlue()];
                ++hG[rgb.getGreen()];
            }

        for (int i=0;i<levels;++i) {
            seriesR.getData().add(new XYChart.Data<String, Number>(Integer.toString(i), hR[i]));
            seriesG.getData().add(new XYChart.Data<String, Number>(Integer.toString(i), hG[i]));
            seriesB.getData().add(new XYChart.Data<String, Number>(Integer.toString(i), hB[i]));
            seriesM.getData().add(new XYChart.Data<String, Number>(Integer.toString(i), hR[i]+ hG[i]+ hB[i]));
            hM[i]= hR[i]+ hG[i]+ hB[i];
            Hsum += hM[i];
            hRsum += hR[i];
            hGsum += hG[i];
            hBsum += hB[i];
        }

        hRavg=hRsum/levels;
        hGavg=hGsum/levels;
        hBavg=hBsum/levels;
        Havg=Hsum/levels;

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
        if (mono && channels>1) {
            barChart.getData().remove(seriesM);
            barChart.getData().addAll(seriesR, seriesG, seriesB);
            mono = false;
        } else {
            barChart.getData().removeAll(seriesR, seriesG, seriesB);
            barChart.getData().add(seriesM);
            mono = true;
        }
    }

}
