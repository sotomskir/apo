package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.TilePane;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.GradientSharpeningCommand;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.nodes.BordersMethodToggles;
import pl.sotomski.apoz.nodes.ImagePane;

public class GradientSharpeningTool extends Tool {

    private static Tool instance;
    private BordersMethodToggles bordersMethodToggles;

    protected GradientSharpeningTool(ToolController controller) {
        super(controller);
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("GradientSharpening"));
        TilePane tilePane = new TilePane();
        tilePane.setPrefColumns(3);
        tilePane.setPrefRows(3);

        // add other controls
        bordersMethodToggles = new BordersMethodToggles(bundle);
        getChildren().addAll(separator, label, tilePane, bordersMethodToggles, applyCancelBtns);
    }

    public static Tool getInstance(ToolController controller) {
        if(instance == null) instance = new GradientSharpeningTool(controller);
        return instance;
    }

    @Override
    public void handleApply(ActionEvent actionEvent) {
        ImagePane imagePane = toolController.getActivePane();
        CommandManager manager = imagePane.getCommandManager();
        manager.executeCommand(new GradientSharpeningCommand(imagePane, bordersMethodToggles.getMethod()));
        imagePane.setImage(imagePane.getImage());
    }

}
