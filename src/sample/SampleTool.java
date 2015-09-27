package sample;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;


public class SampleTool extends VBox{
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
        ChoiceBox<String> choiceBox = new ChoiceBox<String>();
        choiceBox.getItems().addAll("method 1", "method 2", "method 3");
        Button button = new Button("Apply");
        getChildren().addAll(separator, label, choiceBox, button);
    }

    public void handleApply(ActionEvent actionEvent) {

    }
}
