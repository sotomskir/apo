package sample;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by sotomski on 23/09/15.
 */
public class Histogram extends BarChart{
    int max;
    public Histogram(BufferedImage image) {
        super(new CategoryAxis(), new NumberAxis());
        XYChart.Series series = new XYChart.Series();
        int height = image.getHeight();
        int width = image.getWidth();
        int bitDepth = image.getColorModel().getPixelSize()/3;
        max = (int)Math.pow(2, bitDepth);
        int histogram[] = new int[max];
        for (int x=0;x<width;++x)
            for (int y=0;y<height;++y) {
                Color rgb = new Color(image.getRGB(x, y));
                ++histogram[rgb.getRed()];
                ++histogram[rgb.getBlue()];
                ++histogram[rgb.getGreen()];
            }
        for (int x=0;x<max;++x)
        series.getData().addAll(new XYChart.Data<String, Number>(""+x, histogram[x]));
        this.getData().addAll(series);
    }



}
