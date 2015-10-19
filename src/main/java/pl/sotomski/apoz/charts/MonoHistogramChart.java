package pl.sotomski.apoz.charts;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import pl.sotomski.apoz.utils.Histogram;

/**
 * Created by sotomski on 19/10/15.
 */
public class MonoHistogramChart extends BarChart<String, Number> {
    private Series<String, Number> series;

    public MonoHistogramChart() {
        super(new CategoryAxis(), new NumberAxis());
        series = new Series<>();
        this.getData().add(series);
//        series.getNode().getStyleClass().add("series-mono");
        applyStyle();
    }

    public void update(Histogram histogram) {
        series.getData().clear();
//        xAxis.setUpperBound(levels-1);
        for (int i=0;i<histogram.getLevels();++i) {
            series.getData().add(new Data<>(Integer.toString(i), histogram.gethM()[i]));
        }
    }

    private void applyStyle() {
//        this.setHorizontalGridLinesVisible(false);
        this.setVerticalGridLinesVisible(false);
        this.setVerticalZeroLineVisible(false);
        this.setCategoryGap(0d);
        this.setBarGap(0d);
        this.setAnimated(false);
        this.setLegendVisible(false);
//        this.setCreateSymbols(false);
        CategoryAxis xAxis = (CategoryAxis) this.getXAxis();
        NumberAxis yAxis = (NumberAxis) this.getYAxis();
        yAxis.setMinorTickVisible(false);
        yAxis.setTickMarkVisible(false);
//        xAxis.setTickMarkVisible(false);
        yAxis.setTickLabelsVisible(false);
//        xAxis.setTickLabelsVisible(false);
        this.setMaxHeight(200.0);
    }

}
