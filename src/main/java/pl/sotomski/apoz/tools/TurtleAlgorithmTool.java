package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.TurtleAlgorithmCommand;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.nodes.ImagePane;

import java.util.ResourceBundle;

public class TurtleAlgorithmTool extends VBox {

    private static TurtleAlgorithmTool instance;
    ToolController toolController;
    ToggleGroup toggleGroup = new ToggleGroup();
    ToggleButton blackButton = new ToggleButton();
    ToggleButton whiteButton = new ToggleButton();

    private TurtleAlgorithmTool(ToolController controller) {
        ResourceBundle bundle = controller.getBundle();
        this.toolController = controller;
        blackButton.setToggleGroup(toggleGroup);
        whiteButton.setToggleGroup(toggleGroup);
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("TurtleAlgorithm"));
        Label buttonsLabel = new Label(bundle.getString("ObjectColor"));
        blackButton.setText(bundle.getString("black"));
        whiteButton.setText(bundle.getString("white"));

        Button buttonApply = new Button(bundle.getString("Apply"));
        buttonApply.setOnAction((actionEvent) -> {
            try {
                handleApply(actionEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        getChildren().addAll(separator, label, blackButton, whiteButton, buttonApply);
    }

    public static VBox getInstance(ToolController controller) {
        if(instance == null) instance = new TurtleAlgorithmTool(controller);
        return instance;
    }

    private void handleApply(ActionEvent actionEvent) throws Exception {
        ImagePane imagePane = toolController.getActivePaneProperty();
        CommandManager manager = imagePane.getCommandManager();
        System.out.println("Command żółw");
        manager.executeCommand(new TurtleAlgorithmCommand(imagePane, blackButton.isSelected() ? 0 : 255));
    }

}
