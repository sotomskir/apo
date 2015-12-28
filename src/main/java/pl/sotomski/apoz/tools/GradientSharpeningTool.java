package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.GradientSharpeningCommand;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.nodes.BordersMethodToggles;
import pl.sotomski.apoz.nodes.ImagePane;

import java.util.ResourceBundle;

public class GradientSharpeningTool extends VBox {

    private static VBox instance;
    private ToolController toolController;
    private BordersMethodToggles bordersMethodToggles;

    protected GradientSharpeningTool(ToolController controller) {
        ResourceBundle bundle = controller.getBundle();
        this.toolController = controller;
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("GradientSharpening"));
        TilePane tilePane = new TilePane();
        tilePane.setPrefColumns(3);
        tilePane.setPrefRows(3);

        // add other controls
        bordersMethodToggles = new BordersMethodToggles(bundle);
        Button button = new Button(bundle.getString("Apply"));
        button.setOnAction((actionEvent) -> {
            try {
                handleApply(actionEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        getChildren().addAll(separator, label, tilePane, bordersMethodToggles, button);
    }

    public static VBox getInstance(ToolController controller) {
        if(instance == null) instance = new GradientSharpeningTool(controller);
        return instance;
    }

    public void handleApply(ActionEvent actionEvent) throws Exception {
        ImagePane imagePane = toolController.getActivePaneProperty();
        CommandManager manager = imagePane.getCommandManager();
        manager.executeCommand(new GradientSharpeningCommand(imagePane, bordersMethodToggles.getMethod()));
        imagePane.setImage(imagePane.getImage());
    }

}
