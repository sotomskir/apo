package pl.sotomski.apoz.tools;

import javafx.geometry.Orientation;
import javafx.scene.control.*;
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
    private RadioButton squareRadio;

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
        buttons.add(new Button(bundle.getString("Outline")));
        for (Button button : buttons) {
            button.setOnAction((actionEvent) -> handleAction(buttons.indexOf(button)));
            button.setMaxWidth(Double.MAX_VALUE);
        }
        final ToggleGroup toggleGroup = new ToggleGroup();
        squareRadio = new RadioButton(bundle.getString("squareNeighborhood"));
        RadioButton diamondRadio = new RadioButton(bundle.getString("diamondNeighborhood"));
        squareRadio.setToggleGroup(toggleGroup);
        diamondRadio.setToggleGroup(toggleGroup);
        squareRadio.setSelected(true);
        getChildren().addAll(separator, label);
        getChildren().addAll(buttons);
        getChildren().addAll(squareRadio, diamondRadio);
    }

    private void handleAction(int action) {
        ImagePane imagePane = toolController.getActivePaneProperty();
        CommandManager manager = imagePane.getCommandManager();
        manager.executeCommand(new MorphCommand(imagePane, action, (squareRadio.isSelected() ? 0 : 1) ));
        imagePane.refresh();
    }

    public static VBox getInstance(ToolController controller) {
        if(instance == null) instance = new MorphTool(controller);
        return instance;
    }

}
