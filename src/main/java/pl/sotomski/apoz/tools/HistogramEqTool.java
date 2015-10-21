package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.HistogramEqCommand;

import java.util.ResourceBundle;

public class HistogramEqTool extends VBox {

    private static VBox instance;
    private ToolController toolController;
    private ChoiceBox<String> choiceBox;
    private String[] methods;

    protected HistogramEqTool(ToolController controller) {
        ResourceBundle bundle = controller.getBundle();
        methods = new String[5];
        methods[0] = bundle.getString("HEmethod1");
        methods[1] = bundle.getString("HEmethod2");
        methods[2] = bundle.getString("HEmethod3");
        methods[3] = bundle.getString("HEmethod4");
//        methods[4] = bundle.getString("HEmethod5");
        this.choiceBox = new ChoiceBox<>();
        this.toolController = controller;
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("HistogramEqualisation"));
        choiceBox.getItems().addAll(methods);
        choiceBox.getSelectionModel().select(0);
        Button button = new Button(bundle.getString("Apply"));
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
            manager.executeCommand(new HistogramEqCommand(imagePane, 1));
            imagePane.setImage(imagePane.getImage());
        } else       if(methods[1].equals(method)) {
            ImagePane imagePane = toolController.getActivePaneProperty();
            CommandManager manager = imagePane.getCommandManager();
            manager.executeCommand(new HistogramEqCommand(imagePane, 2));
            imagePane.setImage(imagePane.getImage());
        } else       if(methods[2].equals(method)) {
            ImagePane imagePane = toolController.getActivePaneProperty();
            CommandManager manager = imagePane.getCommandManager();
            manager.executeCommand(new HistogramEqCommand(imagePane, 3));
            imagePane.setImage(imagePane.getImage());
        } else       if(methods[3].equals(method)) {
            ImagePane imagePane = toolController.getActivePaneProperty();
            CommandManager manager = imagePane.getCommandManager();
            manager.executeCommand(new HistogramEqCommand(imagePane, 4));
            imagePane.setImage(imagePane.getImage());
        } else       if(methods[4].equals(method)) {
            ImagePane imagePane = toolController.getActivePaneProperty();
            CommandManager manager = imagePane.getCommandManager();
            manager.executeCommand(new HistogramEqCommand(imagePane, 5));
            imagePane.setImage(imagePane.getImage());
        }
    }

}
