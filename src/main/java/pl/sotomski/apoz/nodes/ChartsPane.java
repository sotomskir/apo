package pl.sotomski.apoz.nodes;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.charts.CumulativeHistogramChart;
import pl.sotomski.apoz.charts.MonoHistogramChart;
import pl.sotomski.apoz.charts.ProfileLineChart;
import pl.sotomski.apoz.charts.RGBHistogramChart;
import pl.sotomski.apoz.utils.Histogram;

import java.awt.image.BufferedImage;
import java.util.ResourceBundle;

/**
 * Created by sotomski on 23/09/15.
 */
public class ChartsPane extends TabPane {
    private Histogram histogram;
    private RGBHistogramChart rgbHistogramChart;
    private MonoHistogramChart monoHistogramChart;
    private CumulativeHistogramChart cumulativeHistogramChart;
    private ProfileLineChart profileLineChart;
    Tab profileLineTab;
    ResourceBundle bundle;

    public ChartsPane(ResourceBundle bundle) {
        super();
        this.bundle = bundle;
        Tab tab1 = new Tab(bundle.getString("monoHistogram"));
        Tab tab2 = new Tab(bundle.getString("rgbHistogram"));
        Tab tab3 = new Tab(bundle.getString("cumulativeHistogram"));
        profileLineTab = new Tab(bundle.getString("lineProfile"));
        this.getTabs().addAll(tab1, tab2, tab3, profileLineTab);
        for(Tab tab : getTabs()) tab.setClosable(false);
        rgbHistogramChart = new RGBHistogramChart();
        monoHistogramChart = new MonoHistogramChart(bundle);
        cumulativeHistogramChart = new CumulativeHistogramChart();
        profileLineChart = new ProfileLineChart();
        VBox histogramPane = new VBox(monoHistogramChart);
        VBox rgbHistogramPane = new VBox(rgbHistogramChart);
        VBox cumulativeHistogramPane = new VBox(cumulativeHistogramChart);
        ToggleGroup tg = new ToggleGroup();
        ToggleButton btn1 = new ToggleButton(bundle.getString("scatterChart"));
        btn1.setToggleGroup(tg);
        ToggleButton btn2 = new ToggleButton(bundle.getString("lineChart"));
        btn1.setMinWidth(Button.USE_PREF_SIZE);
        btn2.setMinWidth(Button.USE_PREF_SIZE);
        btn1.setMaxWidth(Double.MAX_VALUE);
        btn2.setMaxWidth(Double.MAX_VALUE);
        btn2.setToggleGroup(tg);
        btn2.setSelected(true);
        btn1.setOnAction(event -> profileLineChart.setScatterType());
        btn2.setOnAction(event -> profileLineChart.setLineType());
        VBox vbButtons = new VBox();
        vbButtons.setSpacing(10);
        vbButtons.setPadding(new Insets(20, 5, 20, 5));
        vbButtons.getChildren().addAll(btn1, btn2);
        HBox lineProfilePane = new HBox(profileLineChart, vbButtons);
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

    public void clear() {
        rgbHistogramChart.getData().clear();
        monoHistogramChart.getData().clear();
        cumulativeHistogramChart.getData().clear();
    }

    public void updateProfileLineChart(ProfileLine line) {
        profileLineChart.update(line);
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
