package pl.sotomski.apoz.tools;

import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.LogicalFilterCommand;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.nodes.ImagePane;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LogicalFilterTool extends VBox {

    private static VBox instance;
    private ToolController toolController;
    private List<RadioButton> buttons;
    private ToggleGroup toggleGroup;

    protected LogicalFilterTool(ToolController controller) {
        ResourceBundle bundle = controller.getBundle();
        this.toolController = controller;
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("LogicalFilterTool"));
        Button applyBtn = new Button(bundle.getString("Apply"));
        applyBtn.setOnAction((actionEvent) -> handleApply());
        toggleGroup = new ToggleGroup();
        buttons = new ArrayList<>();
        buttons.add(new RadioButton(bundle.getString("horizontal")));
        buttons.add(new RadioButton(bundle.getString("45degrees")));
        buttons.add(new RadioButton(bundle.getString("vertical")));
        buttons.add(new RadioButton(bundle.getString("-45degrees")));
        toggleGroup.getToggles().addAll(buttons);
        getChildren().addAll(separator, label);
        getChildren().addAll(buttons);
        getChildren().addAll(applyBtn);
    }

    private void handleApply() {
        ImagePane imagePane = toolController.getActivePaneProperty();
        CommandManager manager = imagePane.getCommandManager();
        int direction = buttons.indexOf((RadioButton)toggleGroup.getSelectedToggle());
        manager.executeCommand(new LogicalFilterCommand(imagePane, direction));
        imagePane.refresh();
    }

    public static VBox getInstance(ToolController controller) {
        if(instance == null) instance = new LogicalFilterTool(controller);
        return instance;
    }

}
