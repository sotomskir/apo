package pl.sotomski.apoz.nodes;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import pl.sotomski.apoz.charts.CumulativeHistogramChart;
import pl.sotomski.apoz.charts.LineProfileChart;
import pl.sotomski.apoz.charts.MonoHistogramChart;
import pl.sotomski.apoz.charts.RGBHistogramChart;
import pl.sotomski.apoz.utils.Histogram;

import java.awt.image.BufferedImage;
import java.util.ResourceBundle;

/**
 * Created by sotomski on 23/09/15.
 */
public class HistogramPane extends TabPane {
    private Histogram histogram;
    private RGBHistogramChart rgbHistogramChart;
    private MonoHistogramChart monoHistogramChart;
    private CumulativeHistogramChart cumulativeHistogramChart;
    private LineProfileChart lineProfileChart;

    public HistogramPane(ResourceBundle bundle) {
        super();
        Tab tab1 = new Tab(bundle.getString("monoHistogram"));
        Tab tab2 = new Tab(bundle.getString("rgbHistogram"));
        Tab tab3 = new Tab(bundle.getString("cumulativeHistogram"));
        Tab tab4 = new Tab(bundle.getString("lineProfile"));
        this.getTabs().addAll(tab1, tab2, tab3, tab4);
        rgbHistogramChart = new RGBHistogramChart();
        monoHistogramChart = new MonoHistogramChart();
        cumulativeHistogramChart = new CumulativeHistogramChart();
        lineProfileChart = new LineProfileChart();
        Pane histogramPane = new Pane(monoHistogramChart);
        Pane rgbHistogramPane = new Pane(rgbHistogramChart);
        Pane cumulativeHistogramPane = new Pane(cumulativeHistogramChart);
        Pane lineProfilePane = new Pane(lineProfileChart);
        tab1.setContent(histogramPane);
        tab2.setContent(rgbHistogramPane);
        tab3.setContent(cumulativeHistogramPane);
        tab4.setContent(lineProfilePane);

    }

    public void update(BufferedImage image) {
        long startTime;
        startTime = System.currentTimeMillis();
        histogram = new Histogram(image);
        System.out.println(histogram.getClass() + ": " + (System.currentTimeMillis()-startTime));
        startTime = System.currentTimeMillis();
        rgbHistogramChart.update(histogram);
        System.out.println(rgbHistogramChart.getClass() + ": " + (System.currentTimeMillis()-startTime));
        startTime = System.currentTimeMillis();
        monoHistogramChart.update(histogram);
        System.out.println(monoHistogramChart.getClass() + ": " + (System.currentTimeMillis()-startTime));
        startTime = System.currentTimeMillis();
        cumulativeHistogramChart.update(histogram);
        System.out.println(cumulativeHistogramChart.getClass() + ": " + (System.currentTimeMillis()-startTime));
        startTime = System.currentTimeMillis();
        lineProfileChart.update(histogram);
        System.out.println(lineProfileChart.getClass() + ": " + (System.currentTimeMillis()-startTime));
    }

}
