package sample;

import javafx.scene.chart.*;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by sotomski on 23/09/15.
 */
public class Histogram {
    private BarChart<String, Number> areaChart;
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

    public Histogram(BufferedImage image) {
        seriesR = new XYChart.Series();
        seriesG = new XYChart.Series();
        seriesB = new XYChart.Series();
        seriesM = new XYChart.Series();
        xAxis = new CategoryAxis();
//        xAxis.setAutoRanging(false);
//        xAxis.setLowerBound(0d);
//        xAxis.setTickUnit(25);
        yAxis = new NumberAxis();

        areaChart = new BarChart<String, Number>(xAxis, yAxis);
        update(image);
        areaChart.getData().addAll(seriesM);
//        seriesM.getNode().getStyleClass().add("series-mono");
        applyStyle();
    }

    public BarChart getAreaChart() {
        return areaChart;
    }

    public void update(BufferedImage image) {
        seriesR.getData().clear();
        seriesG.getData().clear();
        seriesB.getData().clear();
        seriesM.getData().clear();
        int height = image.getHeight();
        int width = image.getWidth();
        int bitDepth = image.getColorModel().getPixelSize()/3;
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
        areaChart.setHorizontalGridLinesVisible(false);
        areaChart.setVerticalGridLinesVisible(false);
        areaChart.setVerticalZeroLineVisible(false);
        areaChart.setCategoryGap(0d);
        areaChart.setBarGap(0d);
        areaChart.setAnimated(false);
        areaChart.setLegendVisible(false);
//        areaChart.setCreateSymbols(false);
        yAxis.setMinorTickVisible(false);
        yAxis.setTickMarkVisible(false);
        xAxis.setTickMarkVisible(false);
        yAxis.setTickLabelsVisible(false);
        xAxis.setTickLabelsVisible(false);
        areaChart.setMaxHeight(200.0);
        mono = true;
    }

    public void switchType() {
        if (mono) {
            areaChart.getData().remove(seriesM);
            areaChart.getData().addAll(seriesR, seriesG, seriesB);
//            seriesR.getNode().getStyleClass().addAll("series-red");
//            seriesG.getNode().getStyleClass().addAll("series-green");
//            seriesB.getNode().getStyleClass().addAll("series-blue");
            mono = false;
        } else {
            areaChart.getData().removeAll(seriesR, seriesG, seriesB);
            areaChart.getData().add(seriesM);
//            seriesM.getNode().getStyleClass().add("series-mono");
            mono = true;
        }
    }

}
