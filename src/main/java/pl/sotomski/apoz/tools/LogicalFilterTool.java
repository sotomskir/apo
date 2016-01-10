package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleGroup;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.LogicalFilterCommand;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.nodes.ImagePane;

import java.util.ArrayList;
import java.util.List;

public class LogicalFilterTool extends Tool {

    private static Tool instance;
    private List<RadioButton> buttons;
    private ToggleGroup toggleGroup;

    protected LogicalFilterTool(ToolController controller) {
        super(controller);
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("LogicalFilterTool"));
        toggleGroup = new ToggleGroup();
        buttons = new ArrayList<>();
        buttons.add(new RadioButton(bundle.getString("horizontal")));
        buttons.add(new RadioButton(bundle.getString("45degrees")));
        buttons.add(new RadioButton(bundle.getString("vertical")));
        buttons.add(new RadioButton(bundle.getString("-45degrees")));
        toggleGroup.getToggles().addAll(buttons);
        getChildren().addAll(separator, label);
        getChildren().addAll(buttons);
        getChildren().addAll(applyCancelBtns);
    }

    @Override
    public void handleApply(ActionEvent actionEvent) {
        ImagePane imagePane = toolController.getActivePane();
        CommandManager manager = imagePane.getCommandManager();
        int direction = buttons.indexOf((RadioButton)toggleGroup.getSelectedToggle());
        manager.executeCommand(new LogicalFilterCommand(imagePane, direction));
        imagePane.refresh();
    }

    public static Tool getInstance(ToolController controller) {
        if(instance == null) instance = new LogicalFilterTool(controller);
        return instance;
    }

}
