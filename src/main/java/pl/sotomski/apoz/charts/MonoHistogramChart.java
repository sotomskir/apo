package pl.sotomski.apoz.charts;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.shape.Line;
import pl.sotomski.apoz.utils.Histogram;

import java.util.ResourceBundle;

/**
 * Created by sotomski on 19/10/15.
 */
public class MonoHistogramChart extends AreaChart<Number, Number> {
    // Two dimensional array is for ploting bars. Single bar is consisted from 4 points.
    // data[i][0] is left upper bar point and data[i][1] is right upper.
    private XYChart.Data<Number, Number> data[][] = new XYChart.Data[256][2];

    Data<Number, Number> verticalMarker = new Data<>(10, 0);
    Series<Number, Number> series = new Series<>();

    private ResourceBundle bundle;

    public MonoHistogramChart() {
        super(new NumberAxis(), new NumberAxis());
        // create histogram bars
        for (int i=0;i<256;i++) {
            data[i][0] = new XYChart.Data<>(i, 0);
            data[i][1] = new XYChart.Data<>(i+1, 0);

            series.getData().addAll(
                    // Add bar points
                    new XYChart.Data<>(i, 0), //left lower
                    data[i][0], //left upper
                    data[i][1], //right upper
                    new XYChart.Data<>(i+1, 0) //right lower
            );
        }
//        series.getNode().getStyleClass().add("series-mono");
        this.getData().add(series);
        applyStyle();
        setupHover();
    }

    public MonoHistogramChart(ResourceBundle bundle) {
        this();
        this.bundle = bundle;
    }

    public void update(Histogram histogram) {
//        xAxis.setUpperBound(levels-1);
        for (int i=0;i<histogram.getLevels();i++) {
            data[i][0].setYValue(histogram.getMono()[i]);
            data[i][1].setYValue(histogram.getMono()[i]);
        }
    }

    private void setupHover() {
        Line line = new Line();
        verticalMarker.setNode(line);
        MenuItem level = new MenuItem();
        MenuItem value = new MenuItem();
        ContextMenu tooltip = new ContextMenu(level, value);
        setOnMouseEntered(event1 -> {
            addVerticalMarker();
            tooltip.show(getScene().getWindow());
        });
        setOnMouseExited(event1 -> {
            removeVerticalMarker();
            tooltip.hide();
        });
        setOnMouseMoved(event -> {
            final Node chartBackground = lookup(".chart-plot-background");
            Bounds chartAreaBounds = chartBackground.localToParent(chartBackground.getBoundsInLocal());
            Bounds screenBounds = chartBackground.localToScreen(chartBackground.getBoundsInLocal());
//        Bounds chartAreaBounds = chartBackground.getBoundsInLocal();
            // remember scene position of chart area
            double xShift = chartAreaBounds.getMinX() +5;
            double screenXShift = screenBounds.getMinX() - 100;
            Number x = getXAxis().getValueForDisplay(event.getX() - xShift);
            verticalMarker.setXValue(x);
            tooltip.setX(event.getX() + screenXShift);
            tooltip.setY(screenBounds.getMaxY() + 20);
            level.setText("Poziom:"+x.intValue());
            try {
                int k = (int) data[(x.intValue())][0].getYValue();
                value.setText("K:"+k);
            } catch (IndexOutOfBoundsException e) {
                value.setText("K: -");
            }
            layoutPlotChildren();
        });
    }

    public void addVerticalMarker() {
        Line line = (Line) verticalMarker.getNode();
        getPlotChildren().add(line);
    }

    public void removeVerticalMarker() {
        Line line = (Line) verticalMarker.getNode();
        getPlotChildren().remove(line);
    }

    @Override
    protected void layoutPlotChildren() {
        super.layoutPlotChildren();
        Line line = (Line) verticalMarker.getNode();
        line.setStartX(getXAxis().getDisplayPosition(verticalMarker.getXValue()));  // 0.5 for crispness
        line.setEndX(line.getStartX());
        line.setStartY(0d);
        line.setEndY(getBoundsInLocal().getHeight());
        line.toFront();
    }

//    public void setValueLabel(Label valueLabel) {
//        this.valueLabel = valueLabel;
//    }

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
        xAxis.setAutoRanging(false);
//        xAxis.setLowerBound(0);
        xAxis.setUpperBound(255);
        xAxis.setTickUnit(50);

        NumberAxis yAxis = (NumberAxis) this.getYAxis();
        yAxis.setMinorTickVisible(false);
        yAxis.setTickMarkVisible(false);
//        xAxis.setTickMarkVisible(false);
        yAxis.setTickLabelsVisible(false);
//        xAxis.setTickLabelsVisible(false);
        this.setMaxHeight(200.0);

    }

}
