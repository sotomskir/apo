package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.charts.CurvedFittedAreaChart;

import java.util.ArrayList;
import java.util.List;


public class SampleTool extends VBox {
    private static SampleTool instance;
    private final ToolController toolControler;

    public static SampleTool getInstance(ToolController controller) {
        if(instance == null) {
            instance = new SampleTool(controller);
        }
        return instance;
    }

    protected SampleTool(ToolController controller) {
        this.toolControler = controller;
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label("Sample Tool");
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll("method 1", "method 2", "method 3");
        NumberAxis xAxis = new NumberAxis(0, 255, 10);
        NumberAxis yAxis =  new NumberAxis(0, 255, 10);
        CurvedFittedAreaChart chart = new CurvedFittedAreaChart(xAxis, yAxis);
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        List<XYChart.Data<Number, Number>> datas = new ArrayList<>();
        datas.add(new XYChart.Data<>(0, 0));
        datas.add(new XYChart.Data<>(64, 64));
        datas.add(new XYChart.Data<>(128, 128));
        datas.add(new XYChart.Data<>(192, 192));
        datas.add(new XYChart.Data<>(255, 255));
        series.getData().addAll(datas);
        chart.getData().addAll(series);
        for (XYChart.Data data : series.getData()) {
            Node node = data.getNode() ;
            node.setCursor(Cursor.HAND);
            node.setOnMouseDragged(e -> {
                Point2D pointInScene = new Point2D(e.getSceneX(), e.getSceneY());
                double xAxisLoc = xAxis.sceneToLocal(pointInScene).getX();
                double yAxisLoc = yAxis.sceneToLocal(pointInScene).getY();
                Number x = xAxis.getValueForDisplay(xAxisLoc);
                Number y = yAxis.getValueForDisplay(yAxisLoc);
                data.setXValue(x);
                data.setYValue(y);
            });

        }
        Button button = new Button("Apply");
        getChildren().addAll(separator, label, choiceBox, chart, button);
    }

    public void handleApply(ActionEvent actionEvent) {

    }
}
