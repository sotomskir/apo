package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.MedianCommand;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.nodes.ImagePane;

public class MedianTool extends Tool {

    private static Tool instance;
    private Spinner<Integer> radiusSpinner;

    protected MedianTool(ToolController controller) {
        super(controller);
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("MedianTool"));
        radiusSpinner = new Spinner<>(3, 11, 3, 2);
        getChildren().addAll(separator, label, radiusSpinner, applyCancelBtns);
    }

    public static Tool getInstance(ToolController controller) {
        if(instance == null) instance = new MedianTool(controller);
        return instance;
    }

    @Override
    public void handleApply(ActionEvent actionEvent) {
        ImagePane imagePane = toolController.getActivePane();
        CommandManager manager = imagePane.getCommandManager();
        int radius = radiusSpinner.getValue();
        manager.executeCommand(new MedianCommand(imagePane, radius));
        imagePane.setImage(imagePane.getImage());
        disableTool();
    }

}
