package pl.sotomski.apoz.charts;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import pl.sotomski.apoz.utils.Histogram;

/**
 * Created by sotomski on 19/10/15.
 */
public class LineProfileChart extends LineChart {
    public LineProfileChart() {
        super(new NumberAxis(), new NumberAxis());
        this.setMaxHeight(200.0);

    }

    public void update(Histogram histogram) {

    }
}
