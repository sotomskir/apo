package pl.sotomski.apoz.charts;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import pl.sotomski.apoz.utils.Histogram;

/**
 * Created by sotomski on 19/10/15.
 */
public class RGBHistogramChart extends BarChart<String, Number> {
    private XYChart.Series<String, Number> seriesR;
    private XYChart.Series<String, Number> seriesG;
    private XYChart.Series<String, Number> seriesB;

    public RGBHistogramChart() {
        super(new CategoryAxis(), new NumberAxis());
        seriesR = new XYChart.Series<>();
        seriesG = new XYChart.Series<>();
        seriesB = new XYChart.Series<>();
        this.getData().addAll(seriesR, seriesG, seriesB);
//        seriesM.getNode().getStyleClass().add("series-mono");
        applyStyle();
    }

    public void update(Histogram histogram) {
        seriesR.getData().clear();
        seriesG.getData().clear();
        seriesB.getData().clear();
        if (histogram.getChannels()>1) {
//        xAxis.setUpperBound(levels-1);
            for (int i = 0; i < histogram.getLevels(); ++i) {
                seriesR.getData().add(new XYChart.Data<>(Integer.toString(i), histogram.gethR()[i]));
                seriesG.getData().add(new XYChart.Data<>(Integer.toString(i), histogram.gethG()[i]));
                seriesB.getData().add(new XYChart.Data<>(Integer.toString(i), histogram.gethB()[i]));
            }
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
