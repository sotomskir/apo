package pl.sotomski.apoz.charts;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sotomski on 19/10/15.
 */
public class ProfileLineChart extends LineChart<Number, Number> {
    private List<XYChart.Data<Number, Number>> data = new ArrayList<>();
    Series<Number, Number> series = new Series<>();

    public ProfileLineChart() {
        super(new NumberAxis(), new NumberAxis());
        this.setMaxHeight(200.0);
        getData().add(series);
        applyStyle();
    }

    public void update(int[][] pixels) {
        // TODO optimize this
        data.clear();
        //TODO add RGB support
        for (int i = 0; i < pixels.length; ++i) data.add(new XYChart.Data<>(i, pixels[i][0]));
        series.getData().clear();
        series.getData().addAll(data);
        System.out.println("UPDATE");
    }

    private void applyStyle() {
//        this.setHorizontalGridLinesVisible(false);
//        this.setVerticalGridLinesVisible(false);
//        this.setVerticalZeroLineVisible(false);
//        this.setCategoryGap(0d);
//        this.setBarGap(0d);
        this.setAnimated(false);
        this.setLegendVisible(false);
        this.setCreateSymbols(false);
        NumberAxis xAxis = (NumberAxis) this.getXAxis();
        NumberAxis yAxis = (NumberAxis) this.getYAxis();
//        yAxis.setMinorTickVisible(false);
//        yAxis.setTickMarkVisible(false);
//        xAxis.setTickMarkVisible(false);
//        yAxis.setTickLabelsVisible(false);
//        xAxis.setTickLabelsVisible(false);
        this.setMaxHeight(200.0);
        getStyleClass().add("thick-chart");
    }
}
