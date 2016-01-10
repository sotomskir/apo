package pl.sotomski.apoz.charts;

import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import pl.sotomski.apoz.utils.Histogram;

import java.util.ResourceBundle;

/**
 * Created by sotomski on 19/10/15.
 */
public class MonoHistogramChart extends BarChart<String, Number> {
    private XYChart.Data<String, Number> data[] = new XYChart.Data[256];
    Label valueLabel;
    private ResourceBundle bundle;

    public MonoHistogramChart() {
        super(new CategoryAxis(), new NumberAxis());
        for (int i=0;i<256;++i) data[i] = new XYChart.Data<>(Integer.toString(i), 0);
        Series<String, Number> series = new Series<>();
        series.getData().addAll(data);
//        series.getNode().getStyleClass().add("series-mono");
        this.getData().add(series);
        applyStyle();
        setupHover(series);
    }

    public MonoHistogramChart(ResourceBundle bundle) {
        this();
        this.bundle = bundle;
    }

    public void update(Histogram histogram) {
//        xAxis.setUpperBound(levels-1);
        for (int i=0;i<histogram.getLevels();++i) data[i].setYValue(histogram.getMono()[i]);
    }

    private void setupHover(Series<String, Number> series) {
        //find highest bar
        int max = 0;
        for (Data<String, Number> data : series.getData()) {
            if (data.getYValue().intValue() > max) max = data.getYValue().intValue();
        }

        for (Data<String, Number> data : series.getData()) {
            Node n = data.getNode();
            n.setOnMouseEntered(e -> {
                if (valueLabel != null)
                    valueLabel.setText(
                            String.format(bundle.getString("LevelValue"), data.getXValue(), data.getYValue())
                    );
                n.setStyle("-fx-bar-fill: blue;");
            });
            n.setOnMouseExited(e -> {
                n.setStyle("-fx-bar-fill: #e90000;");
            });
            n.setOnMouseClicked(e -> {
                System.out.println("openDetailsScreen(<selected Bar>)");
                System.out.println(data.getXValue() + " : " + data.getYValue());
            });
        }
    }

    public void setValueLabel(Label valueLabel) {
        this.valueLabel = valueLabel;
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
