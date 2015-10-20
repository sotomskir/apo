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
    private XYChart.Data<String, Number> dataR[] = new XYChart.Data[256];
    private XYChart.Data<String, Number> dataG[] = new XYChart.Data[256];
    private XYChart.Data<String, Number> dataB[] = new XYChart.Data[256];

    public RGBHistogramChart() {
        super(new CategoryAxis(), new NumberAxis());
        Series<String, Number> seriesR = new Series<>();
        Series<String, Number> seriesG = new Series<>();
        Series<String, Number> seriesB = new Series<>();
        for (int i=0;i<256;++i) {
            dataR[i] = new XYChart.Data<>(Integer.toString(i), 0);
            dataG[i] = new XYChart.Data<>(Integer.toString(i), 0);
            dataB[i] = new XYChart.Data<>(Integer.toString(i), 0);
            seriesR.getData().add(dataR[i]);
            seriesG.getData().add(dataG[i]);
            seriesB.getData().add(dataB[i]);
        }
        this.getData().addAll(seriesR, seriesG, seriesB);
//        seriesM.getNode().getStyleClass().add("series-mono");
        applyStyle();
    }

    public void update(Histogram histogram) {
        long startTime;
        if (histogram.getChannels()>1) {
//        xAxis.setUpperBound(levels-1);
            for (int i = 0; i < histogram.getLevels(); ++i) {
                dataR[i].setYValue(histogram.getRGB()[0][i]);
                dataG[i].setYValue(histogram.getRGB()[1][i]);
                dataB[i].setYValue(histogram.getRGB()[2][i]);
            }
        } else {
            for (int i = 0; i < histogram.getLevels(); ++i) {
                dataR[i].setYValue(0);
                dataG[i].setYValue(0);
                dataB[i].setYValue(0);
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
