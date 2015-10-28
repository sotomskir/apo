package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.nodes.CurvedFittedAreaChart;

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
        CurvedFittedAreaChart chart = new CurvedFittedAreaChart(new NumberAxis(0, 100, 10), new NumberAxis(0, 100, 10));
        XYChart.Series series = new XYChart.Series();
        List<XYChart.Data> data = new ArrayList<>();
        data.add(new XYChart.Data(10, 10));
        data.add(new XYChart.Data(30, 40));
        data.add(new XYChart.Data(50, 20));
        data.add(new XYChart.Data(70, 90));
        data.add(new XYChart.Data(90, 50));
        series.getData().addAll(data);
        chart.getData().addAll(series);
        Button button = new Button("Apply");
        getChildren().addAll(separator, label, choiceBox, chart, button);
    }

    public void handleApply(ActionEvent actionEvent) {

    }
}
