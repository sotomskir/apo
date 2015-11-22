package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.MedianCommand;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.nodes.ImagePane;

import java.util.ResourceBundle;

public class MedianTool extends VBox {

    private static VBox instance;
    private ToolController toolController;
    private Spinner<Integer> radiusSpinner;

    protected MedianTool(ToolController controller) {
        ResourceBundle bundle = controller.getBundle();
        this.toolController = controller;
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("MedianTool"));
        radiusSpinner = new Spinner<>(3, 11, 3, 2);
        Button button = new Button(bundle.getString("Apply"));
        button.setOnAction((actionEvent) -> {
            try {
                handleApply(actionEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        getChildren().addAll(separator, label, radiusSpinner, button);
    }

    public static VBox getInstance(ToolController controller) {
        if(instance == null) instance = new MedianTool(controller);
        return instance;
    }

    public void handleApply(ActionEvent actionEvent) throws Exception {
        ImagePane imagePane = toolController.getActivePaneProperty();
        CommandManager manager = imagePane.getCommandManager();
        int radius = radiusSpinner.getValue();
        manager.executeCommand(new MedianCommand(imagePane, radius));
        imagePane.setImage(imagePane.getImage());
    }

}
