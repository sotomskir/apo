package pl.sotomski.apoz.nodes;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.charts.CumulativeHistogramChart;
import pl.sotomski.apoz.charts.ProfileLineChart;
import pl.sotomski.apoz.charts.MonoHistogramChart;
import pl.sotomski.apoz.charts.RGBHistogramChart;
import pl.sotomski.apoz.utils.Histogram;
import pl.sotomski.apoz.utils.ImageUtils;

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
    private ProfileLineChart profileLineChart;
    Tab profileLineTab;
    ResourceBundle bundle;

    public HistogramPane(ResourceBundle bundle) {
        super();
        this.bundle = bundle;
        Tab tab1 = new Tab(bundle.getString("monoHistogram"));
        Tab tab2 = new Tab(bundle.getString("rgbHistogram"));
        Tab tab3 = new Tab(bundle.getString("cumulativeHistogram"));
        profileLineTab = new Tab(bundle.getString("lineProfile"));
        this.getTabs().addAll(tab1, tab2, tab3, profileLineTab);
        rgbHistogramChart = new RGBHistogramChart();
        monoHistogramChart = new MonoHistogramChart();
        cumulativeHistogramChart = new CumulativeHistogramChart();
        profileLineChart = new ProfileLineChart();
        VBox histogramPane = new VBox(monoHistogramChart);
        VBox rgbHistogramPane = new VBox(rgbHistogramChart);
        VBox cumulativeHistogramPane = new VBox(cumulativeHistogramChart);
        VBox lineProfilePane = new VBox(profileLineChart);
        tab1.setContent(histogramPane);
        tab2.setContent(rgbHistogramPane);
        tab3.setContent(cumulativeHistogramPane);
        profileLineTab.setContent(lineProfilePane);
    }

    public void update(BufferedImage image) {
        histogram = new Histogram(image);
        rgbHistogramChart.update(histogram);
        monoHistogramChart.update(histogram);
        cumulativeHistogramChart.update(histogram);
    }

    public void updateProfileLineChart(BufferedImage image, ProfileLine line) {
        profileLineChart.update(ImageUtils.getLineProfilePixels(image, line));
    }

    public ProfileLineChart getProfileLineChart() {
        return profileLineChart;
    }

    public void selectProfileLineChart() {
        getSelectionModel().select(profileLineTab);
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public MonoHistogramChart getMonoHistogramChart() {
        return monoHistogramChart;
    }
}
