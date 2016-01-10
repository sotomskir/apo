package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.MorphCommand;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.nodes.ImagePane;

import java.util.ArrayList;
import java.util.List;

public class MorphTool extends Tool {

    private static Tool instance;
    private List<Button> buttons;
    private RadioButton squareRadio;

    protected MorphTool(ToolController controller) {
        super(controller);
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("MorphTool"));
        buttons = new ArrayList<>();
        buttons.add(new Button(bundle.getString("Dilatation")));
        buttons.add(new Button(bundle.getString("Erosion")));
        buttons.add(new Button(bundle.getString("Open2")));
        buttons.add(new Button(bundle.getString("Close")));
        buttons.add(new Button(bundle.getString("Outline")));
        buttons.add(new Button(bundle.getString("Skeleton")));
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

    @Override
    protected void handleApply(ActionEvent actionEvent) {

    }

    private void handleAction(int action) {
        ImagePane imagePane = toolController.getActivePane();
        CommandManager manager = imagePane.getCommandManager();
        manager.executeCommand(new MorphCommand(imagePane, action, (squareRadio.isSelected() ? 0 : 1) ));
        imagePane.refresh();
    }

    public static Tool getInstance(ToolController controller) {
        if(instance == null) instance = new MorphTool(controller);
        return instance;
    }

}
