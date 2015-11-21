package pl.sotomski.apoz.tools;

import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.MorphCommand;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.nodes.ImagePane;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MorphTool extends VBox {

    private static VBox instance;
    private ToolController toolController;
    private List<Button> buttons;

    protected MorphTool(ToolController controller) {
        ResourceBundle bundle = controller.getBundle();
        this.toolController = controller;
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("MorphTool"));
        buttons = new ArrayList<>();
        buttons.add(new Button(bundle.getString("Dilatation")));
        buttons.add(new Button(bundle.getString("Erosion")));
        buttons.add(new Button(bundle.getString("Open2")));
        buttons.add(new Button(bundle.getString("Close")));
        buttons.add(new Button(bundle.getString("Thinning")));
        buttons.add(new Button(bundle.getString("Thickening")));
        buttons.add(new Button(bundle.getString("Outline")));
        buttons.add(new Button(bundle.getString("Skeleton")));

        for (Button button : buttons) {
            button.setOnAction((actionEvent) -> handleAction(buttons.indexOf(button)));
            button.setMaxWidth(Double.MAX_VALUE);
        }
        getChildren().addAll(separator, label);
        getChildren().addAll(buttons);
    }

    private void handleAction(int action) {
        ImagePane imagePane = toolController.getActivePaneProperty();
        CommandManager manager = imagePane.getCommandManager();
        manager.executeCommand(new MorphCommand(imagePane, action));
        imagePane.refresh();
    }

    public static VBox getInstance(ToolController controller) {
        if(instance == null) instance = new MorphTool(controller);
        return instance;
    }

}
