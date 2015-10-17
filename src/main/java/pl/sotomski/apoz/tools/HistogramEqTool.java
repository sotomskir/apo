package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.ImagePane;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.HistogramEqCommand;

public class HistogramEqTool extends VBox {

    private static VBox instance;
    private ToolController toolController;
    private ChoiceBox<String> choiceBox;
    private String methods[] = {"metoda średnich", "metoda losowa", "metoda sąsiedztwa", "metoda własna"};

    protected HistogramEqTool(ToolController controller) {
        this.toolController = controller;
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label("Histogram equalisation");
        this.choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll(methods);
        Button button = new Button("Apply");
        button.setOnAction((actionEvent) -> {
            try {
                handleApply(actionEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        getChildren().addAll(separator, label, choiceBox, button);
    }

    public static VBox getInstance(ToolController controller) {
        if(instance == null) instance = new HistogramEqTool(controller);
        return instance;
    }

    public void handleApply(ActionEvent actionEvent) throws Exception {
        String method = choiceBox.getValue();
        if(methods[0].equals(method)) {
            ImagePane imagePane = toolController.getActivePaneProperty();
            CommandManager manager = imagePane.getCommandManager();
            manager.executeCommand(new HistogramEqCommand(imagePane.getImageProperty(), 1));
            imagePane.setImage(imagePane.getImage());
        } else       if(methods[1].equals(method)) {
            ImagePane imagePane = toolController.getActivePaneProperty();
            CommandManager manager = imagePane.getCommandManager();
            manager.executeCommand(new HistogramEqCommand(imagePane.getImageProperty(), 2));
            imagePane.setImage(imagePane.getImage());
        } else       if(methods[2].equals(method)) {
            ImagePane imagePane = toolController.getActivePaneProperty();
            CommandManager manager = imagePane.getCommandManager();
            manager.executeCommand(new HistogramEqCommand(imagePane.getImageProperty(), 3));
            imagePane.setImage(imagePane.getImage());
        } else       if(methods[3].equals(method)) {
            ImagePane imagePane = toolController.getActivePaneProperty();
            CommandManager manager = imagePane.getCommandManager();
            manager.executeCommand(new HistogramEqCommand(imagePane.getImageProperty(), 4));
            imagePane.setImage(imagePane.getImage());
        }
    }

}
