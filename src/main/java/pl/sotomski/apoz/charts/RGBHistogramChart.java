package pl.sotomski.apoz.charts;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.shape.Line;
import pl.sotomski.apoz.utils.Histogram;

/**
 * Created by sotomski on 19/10/15.
 */
public class RGBHistogramChart extends StackedAreaChart<Number, Number> {
    private XYChart.Data<Number, Number> dataR[] = new XYChart.Data[256];
    private XYChart.Data<Number, Number> dataG[] = new XYChart.Data[256];
    private XYChart.Data<Number, Number> dataB[] = new XYChart.Data[256];
    private XYChart.Data<Number, Number> dataC[] = new XYChart.Data[256];
    private XYChart.Data<Number, Number> dataM[] = new XYChart.Data[256];
    private XYChart.Data<Number, Number> dataY[] = new XYChart.Data[256];
    private XYChart.Data<Number, Number> dataK[] = new XYChart.Data[256];
    Series<Number, Number> seriesR;
    Series<Number, Number> seriesG;
    Series<Number, Number> seriesB;
    Series<Number, Number> seriesC;
    Series<Number, Number> seriesM;
    Series<Number, Number> seriesY;
    Series<Number, Number> seriesK;
    Data<Number, Number> verticalMarker = new Data<>(10, 0);


    public RGBHistogramChart() {
        super(new NumberAxis(), new NumberAxis());
        seriesR = new Series<>();
        seriesG = new Series<>();
        seriesB = new Series<>();
        seriesC = new Series<>();
        seriesM = new Series<>();
        seriesY = new Series<>();
        seriesK = new Series<>();
        for (int i=0;i<256;++i) {
            dataR[i] = new XYChart.Data<>(i, 0);
            dataG[i] = new XYChart.Data<>(i, 0);
            dataB[i] = new XYChart.Data<>(i, 0);
            dataC[i] = new XYChart.Data<>(i, 0);
            dataM[i] = new XYChart.Data<>(i, 0);
            dataY[i] = new XYChart.Data<>(i, 0);
            dataK[i] = new XYChart.Data<>(i, 0);
            seriesR.getData().add(dataR[i]);
            seriesG.getData().add(dataG[i]);
            seriesB.getData().add(dataB[i]);
            seriesC.getData().add(dataC[i]);
            seriesM.getData().add(dataM[i]);
            seriesY.getData().add(dataY[i]);
            seriesK.getData().add(dataK[i]);
        }

        this.getData().addAll(seriesK, seriesC, seriesM, seriesY, seriesR, seriesG, seriesB);
//        seriesR.getNode().getStyleClass().add("series-red");
//        seriesG.getNode().getStyleClass().add("series-green");
//        seriesB.getNode().getStyleClass().add("series-blue");

        Line line = new Line();
        verticalMarker.setNode(line);

        MenuItem level = new MenuItem();
        MenuItem red = new MenuItem();
        MenuItem green = new MenuItem();
        MenuItem blue = new MenuItem();
        ContextMenu tooltip = new ContextMenu(level,red,green,blue);
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
            System.out.println(xShift);
            Number x = getXAxis().getValueForDisplay(event.getX() - xShift);
            verticalMarker.setXValue(x);
            tooltip.setX(event.getX() + screenXShift);
            tooltip.setY(screenBounds.getMaxY() + 20);
            level.setText("Level:"+x.intValue());
            try {
                int c = (int) seriesC.getData().get((x.intValue())).getYValue();
                int m = (int) seriesM.getData().get((x.intValue())).getYValue();
                int y = (int) seriesY.getData().get((x.intValue())).getYValue();
                int k = (int) seriesK.getData().get((x.intValue())).getYValue();
                int r = (int) seriesR.getData().get((x.intValue())).getYValue() + k + m + y;
                int g = (int) seriesG.getData().get((x.intValue())).getYValue() + k + y + c;
                int b = (int) seriesB.getData().get((x.intValue())).getYValue() + k + m + c;
                red.setText("R:"+r);
                green.setText("G:"+g);
                blue.setText("B:"+b);
            } catch (IndexOutOfBoundsException e) {
                red.setText("R: -");
                green.setText("G: -");
                blue.setText("B: -");
            }
            layoutPlotChildren();
        });

        applyStyle();
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
        line.setStartX(getXAxis().getDisplayPosition(verticalMarker.getXValue()) + 0.5);  // 0.5 for crispness
        line.setEndX(line.getStartX());
        line.setStartY(0d);
        line.setEndY(getBoundsInLocal().getHeight());
        line.toFront();
    }

    public void update(Histogram histogram) {
        long startTime;
        if (histogram.getChannels()>1) {
//        xAxis.setUpperBound(levels-1);
            for (int i = 0; i < histogram.getLevels(); ++i) {
                int r = histogram.getRGB()[0][i];
                int g = histogram.getRGB()[1][i];
                int b = histogram.getRGB()[2][i];
                int[] colors = getCMYKRGB(r, g, b);
                dataC[i].setYValue(colors[0]);
                dataM[i].setYValue(colors[1]);
                dataY[i].setYValue(colors[2]);
                dataK[i].setYValue(colors[3]);
                dataR[i].setYValue(colors[4]);
                dataG[i].setYValue(colors[5]);
                dataB[i].setYValue(colors[6]);
            }
        } else {
            for (int i = 0; i < histogram.getLevels(); ++i) {
                dataR[i].setYValue(0);
                dataG[i].setYValue(0);
                dataB[i].setYValue(0);
                dataC[i].setYValue(0);
                dataM[i].setYValue(0);
                dataY[i].setYValue(0);
                dataK[i].setYValue(0);
            }
        }
//        seriesR.getNode().setStyle( "-fx-bar-fill: #e90000;" );
//        seriesB.getNode().setStyle( "-fx-bar-fill: #0029e9;" );
//        seriesG.getNode().setStyle( "-fx-bar-fill: #1be900;" );
    }

    public int[] getCMYKRGB(int r, int g, int b) {
        int[] colors = new int[7];
        colors[3] = min(r, g, b); //K
        r-=colors[3];
        g-=colors[3];
        b-=colors[3];
        colors[0] = min(g, b); //C
        colors[1] = min(r, b); //M
        colors[2] = min(r, g); //Y
        int maxCMYK = max(colors[0], colors[1], colors[2]);
        colors[4] = r - maxCMYK; //R
        colors[5] = g - maxCMYK; //G
        colors[6] = b - maxCMYK; //B
        for(int i = 0; i < colors.length; ++i) if(colors[i]<0) colors[i] = 0;
        return colors;
    }

    private int min(int... args) {
        int min = Integer.MAX_VALUE;
        for (int i :args) if(i < min) min = i;
        return min;
    }

    private int max(int... args) {
        int max = Integer.MIN_VALUE;
        for (int i :args) if(i > max) max = i;
        return max;
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
        xAxis.setAutoRanging(false);
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
