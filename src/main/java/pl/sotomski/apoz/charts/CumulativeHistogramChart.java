package pl.sotomski.apoz.charts;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import pl.sotomski.apoz.utils.Histogram;

/**
 * Created by sotomski on 19/10/15.
 */
public class CumulativeHistogramChart extends LineChart<Number, Number> {
    private XYChart.Data<Number, Number> data[] = new XYChart.Data[256];

    public CumulativeHistogramChart() {
        super(new NumberAxis(), new NumberAxis());
        for (int i=0;i<256;++i) data[i] = new XYChart.Data<>(i, 0);
        Series<Number, Number> series = new Series<>();
        series.getData().addAll(data);
        this.getData().add(series);
//        series.getNode().getStyleClass().add("series-mono");
        applyStyle();
    }

    public void update(Histogram histogram) {
//        xAxis.setUpperBound(levels-1);
        for (int i = 0; i < histogram.getLevels(); ++i) {
            int[][] c = histogram.getCumulative();
            data[i].setYValue(c[0][i]+c[1][i]+c[2][i]);
        }
    }

    private void applyStyle() {
//        this.setHorizontalGridLinesVisible(false);
        this.setVerticalGridLinesVisible(false);
        this.setVerticalZeroLineVisible(false);
//        this.setCategoryGap(0d);
//        this.setBarGap(0d);
        this.setAnimated(false);
        this.setLegendVisible(false);
        this.setCreateSymbols(false);
        NumberAxis xAxis = (NumberAxis) this.getXAxis();
        NumberAxis yAxis = (NumberAxis) this.getYAxis();
        yAxis.setMinorTickVisible(false);
        yAxis.setTickMarkVisible(false);
        xAxis.setTickMarkVisible(false);
        yAxis.setTickLabelsVisible(false);
        xAxis.setTickLabelsVisible(false);
        this.setMaxHeight(200.0);
    }
}