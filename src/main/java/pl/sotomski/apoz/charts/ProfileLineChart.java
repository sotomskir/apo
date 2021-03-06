package pl.sotomski.apoz.charts;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
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
public class ProfileLineChart extends LineChart<Number, Number> {
    private List<XYChart.Data<Number, Number>> data = new ArrayList<>();
    Series<Number, Number> series = new Series<>();
    Data<Number, Number> verticalMarker = new Data<>(10, 0);
    Data<Number, Number> circleMarker = new Data<>(10, 0);
    ProfileLine profileLine;
    ContextMenu tooltip;
    Rectangle pixelMarker;
    private int type = 0;

    public ProfileLineChart() {
        super(new NumberAxis(), new NumberAxis());
        this.setMaxHeight(200.0);
        getData().add(series);
        applyStyle();
        setupHover();
        setLineType();
        tooltip.getStyleClass().add("dark");
    }

    public void update(ProfileLine profileLine) {
        if(profileLine == null) {
            series.getData().clear();
            layoutPlotChildren();
        } else {
            List<ProfileLine.LinePoint> points = profileLine.getLinePoints();
            this.profileLine = profileLine;
            // TODO optimize this
            data.clear();
            //TODO add RGB support
            for (int i = 0; i < points.size(); ++i) data.add(new XYChart.Data<>(i, points.get(i).b));
            series.getData().clear();
            series.getData().addAll(data);
            System.out.println("UPDATE");
            if (type == 0) setLineType();
            else setScatterType();
        }
    }
    private void setupHover() {
        Line line = new Line();
        line.setStrokeWidth(2);
        verticalMarker.setNode(line);
        Circle circle = new Circle(5, Color.GREENYELLOW);
        circleMarker.setNode(circle);
        MenuItem level = new MenuItem();
        MenuItem xLabel = new MenuItem();
        MenuItem yLabel = new MenuItem();
        MenuItem value = new MenuItem();
        pixelMarker = new Rectangle(2, 2, new Color(0,0,0,0.01));
        pixelMarker.setStroke(Color.GREENYELLOW);
        tooltip = new ContextMenu(level, xLabel, yLabel, value);

        setOnMouseEntered(event1 -> addMarkers());
        setOnMouseExited(event1 -> removeMarkers());
        setOnMouseMoved(event -> {
            final Node chartBackground = lookup(".chart-plot-background");
            Bounds chartAreaBounds = chartBackground.localToParent(chartBackground.getBoundsInLocal());
            Bounds screenBounds = chartBackground.localToScreen(chartBackground.getBoundsInLocal());
//        Bounds chartAreaBounds = chartBackground.getBoundsInLocal();
            // remember scene position of chart area
            double xShift = chartAreaBounds.getMinX() + 5;
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
            circleMarker.setXValue(x);
            tooltip.setX(event.getX() + screenXShift);
            tooltip.setY(screenBounds.getMaxY() + 20);
            level.setText("Piksel:"+x);
            try {
                circleMarker.setYValue(data.get(x).getYValue());
                int k = (int) series.getData().get(x).getYValue();
                ProfileLine.LinePoint point = profileLine.getLinePoints().get(x);
                double zoomFactor = profileLine.getImagePane().getZoomLevel();
                pixelMarker.setWidth(zoomFactor);
                pixelMarker.setHeight(zoomFactor);
                pixelMarker.setX((point.x - 1) * zoomFactor);
                pixelMarker.setY((point.y - 1) * zoomFactor);
                xLabel.setText("X:"+(point.x));
                yLabel.setText("Y:"+(point.y));
                value.setText("Wartość:"+k);
                if (!getPlotChildren().contains(circleMarker.getNode())) addMarkers();
            } catch (IndexOutOfBoundsException e) {
                xLabel.setText("X: -");
                yLabel.setText("Y: -");
                value.setText("Wartość: -");
                removeMarkers();
            }
            layoutPlotChildren();
        });
    }


    public void addMarkers() {
        Line line = (Line) verticalMarker.getNode();
        Circle circle = (Circle) circleMarker.getNode();
        getPlotChildren().addAll(line, circle);
        tooltip.show(getScene().getWindow());
        //show pixel marker
        if (profileLine != null) profileLine.getImagePane().getImageStack().push(pixelMarker);
    }

    public void removeMarkers() {
        Line line = (Line) verticalMarker.getNode();
        Circle circle = (Circle) circleMarker.getNode();
        getPlotChildren().removeAll(line, circle);
        tooltip.hide();
        if (profileLine != null) profileLine.getImagePane().getImageStack().remove(pixelMarker);
        if (profileLine != null) {
            for (Circle node : profileLine.getNodes()) {
                Color color = Color.YELLOW;
                node.setFill(color);
                node.setStroke(color);
            }
        }
    }

    @Override
    protected void layoutPlotChildren() {
        super.layoutPlotChildren();
        Line line = (Line) verticalMarker.getNode();
        line.setStartX(getXAxis().getDisplayPosition(verticalMarker.getXValue().doubleValue()));
        line.setEndX(line.getStartX());
        line.setStartY(0d);
        line.setEndY(getBoundsInLocal().getHeight());
        line.toFront();
        Circle circle = (Circle) circleMarker.getNode();
        circle.setCenterX(getXAxis().getDisplayPosition(circleMarker.getXValue().doubleValue()));
        circle.setCenterY(getYAxis().getDisplayPosition(circleMarker.getYValue().doubleValue()));
        circle.toFront();
    }

    public void setLineType() {
//        setCreateSymbols(false);
        type = 0;
        for(Data d : data) d.getNode().setStyle("-fx-background-color: rgba(0,0,0,0);");
        getData().get(0).getNode().setId("series-visible");
    }

    public void setScatterType() {
//        setCreateSymbols(true);
        type = 1;
        for(Data d : data) d.getNode().setStyle("-fx-background-color: yellow; -fx-background-radius: 5px;");
        getData().get(0).getNode().setId("series-hidden");
    }

    private void applyStyle() {
//        this.setHorizontalGridLinesVisible(false);
//        this.setVerticalGridLinesVisible(false);
//        this.setVerticalZeroLineVisible(false);
//        this.setCategoryGap(0d);
//        this.setBarGap(0d);

        this.setAnimated(false);
        this.setLegendVisible(false);

        NumberAxis xAxis = (NumberAxis) this.getXAxis();
        NumberAxis yAxis = (NumberAxis) this.getYAxis();
        yAxis.setAutoRanging(true);
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
