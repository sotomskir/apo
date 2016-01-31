package pl.sotomski.apoz.charts;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import pl.sotomski.apoz.nodes.ProfileLine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sotomski on 19/10/15.
 */
public class ProfileLineChart extends ScatterChart<Number, Number> {
    private List<XYChart.Data<Number, Number>> data = new ArrayList<>();
    Series<Number, Number> series = new Series<>();
    Data<Number, Number> verticalMarker = new Data<>(10, 0);
    ProfileLine profileLine;

    public ProfileLineChart() {
        super(new NumberAxis(), new NumberAxis());
        this.setMaxHeight(200.0);
        getData().add(series);
        applyStyle();
        setupHover();
    }

    public void update(ProfileLine profileLine) {
        int[][] pixels = profileLine.getPixels();
        this.profileLine = profileLine;
        // TODO optimize this
        data.clear();
        //TODO add RGB support
        for (int i = 0; i < pixels.length; ++i) data.add(new XYChart.Data<>(i, pixels[i][0]));
        series.getData().clear();
        series.getData().addAll(data);
        System.out.println("UPDATE");
    }
    private void setupHover() {
        Line line = new Line();
        verticalMarker.setNode(line);
        MenuItem level = new MenuItem();
        MenuItem xLabel = new MenuItem();
        MenuItem yLabel = new MenuItem();
        MenuItem value = new MenuItem();
        Rectangle pixelMarker = new Rectangle(2, 2, new Color(0,0,0,0.01));
        pixelMarker.setStroke(Color.GREENYELLOW);
        ContextMenu tooltip = new ContextMenu(level, xLabel, yLabel, value);
        setOnMouseEntered(event1 -> {
            addVerticalMarker();
            tooltip.show(getScene().getWindow());
            //show pixel marker
            if (profileLine != null) profileLine.getImagePane().getImageStack().push(pixelMarker);
        });
        setOnMouseExited(event1 -> {
            removeVerticalMarker();
            tooltip.hide();
            if (profileLine != null) profileLine.getImagePane().getImageStack().remove(pixelMarker);
        });
        setOnMouseMoved(event -> {
            final Node chartBackground = lookup(".chart-plot-background");
            Bounds chartAreaBounds = chartBackground.localToParent(chartBackground.getBoundsInLocal());
            Bounds screenBounds = chartBackground.localToScreen(chartBackground.getBoundsInLocal());
//        Bounds chartAreaBounds = chartBackground.getBoundsInLocal();
            // remember scene position of chart area
            double xShift = chartAreaBounds.getMinX() +5;
            double screenXShift = screenBounds.getMinX() - 100;
            int x = (int) Math.round(getXAxis().getValueForDisplay(Math.round(event.getX() - xShift)).doubleValue());
            //mark image node
            if (profileLine != null) {
                for (Circle node : profileLine.getNodes()) {
                    Color color = profileLine.getNodes().indexOf(node) == x ? Color.GREENYELLOW : Color.YELLOW;
                    node.setFill(color);
                    node.setStroke(color);
                }
            }
            verticalMarker.setXValue(x);
            tooltip.setX(event.getX() + screenXShift);
            tooltip.setY(screenBounds.getMaxY() + 20);
            level.setText("Piksel:"+x);
            try {
                int k = (int) series.getData().get(x).getYValue();
                int[] point = profileLine.getLinePoints()[x];
                double zoomFactor = profileLine.getImagePane().getZoomLevel();
                pixelMarker.setWidth(zoomFactor);
                pixelMarker.setHeight(zoomFactor);
                pixelMarker.setX(point[0] * zoomFactor);
                pixelMarker.setY((point[1]) * zoomFactor);
                xLabel.setText("X:"+point[0]);
                yLabel.setText("Y:"+point[1]);
                value.setText("Wartość:"+k);
            } catch (IndexOutOfBoundsException e) {
                xLabel.setText("X: -");
                yLabel.setText("Y: -");
                value.setText("Wartość: -");
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
        line.setStartX(getXAxis().getDisplayPosition(verticalMarker.getXValue().doubleValue())+1);  // 0.5 for crispness
        line.setEndX(line.getStartX());
        line.setStartY(0d);
        line.setEndY(getBoundsInLocal().getHeight());
        line.toFront();
    }

    private void applyStyle() {
//        this.setHorizontalGridLinesVisible(false);
//        this.setVerticalGridLinesVisible(false);
//        this.setVerticalZeroLineVisible(false);
//        this.setCategoryGap(0d);
//        this.setBarGap(0d);
        this.setAnimated(false);
        this.setLegendVisible(false);
//        this.setCreateSymbols(false);

        NumberAxis xAxis = (NumberAxis) this.getXAxis();
        NumberAxis yAxis = (NumberAxis) this.getYAxis();
//        yAxis.setMinorTickVisible(false);
//        yAxis.setTickMarkVisible(false);
//        xAxis.setTickMarkVisible(false);
//        yAxis.setTickLabelsVisible(false);
//        xAxis.setTickLabelsVisible(false);
        this.setMaxHeight(200.0);
        xAxis.setTickUnit(10);
        getStyleClass().add("thick-chart");
    }
}
