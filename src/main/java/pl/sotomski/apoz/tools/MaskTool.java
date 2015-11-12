package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.MaskCommand;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.nodes.ImagePane;

import java.util.ResourceBundle;

public class MaskTool extends VBox {

    private static VBox instance;
    private ToolController toolController;
    private ChoiceBox<String> choiceBox;
    private String[] masks;

    protected MaskTool(ToolController controller) {
        ResourceBundle bundle = controller.getBundle();
        masks = new String[8];
        masks[0] = bundle.getString("BlurMask1");
        masks[1] = bundle.getString("BlurMask2");
        masks[2] = bundle.getString("BlurMask3");
        masks[3] = bundle.getString("BlurMask4");
        masks[4] = bundle.getString("SharpenMask1");
        masks[5] = bundle.getString("SharpenMask2");
        masks[6] = bundle.getString("SharpenMask3");
        masks[7] = bundle.getString("SharpenMask4");
        this.choiceBox = new ChoiceBox<>();
        this.toolController = controller;
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("Blur"));
        choiceBox.getItems().addAll(masks);
        choiceBox.getSelectionModel().select(0);
        Button button = new Button(bundle.getString("Apply"));
        button.setOnAction((actionEvent) -> {
            try {
                handleApply(actionEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        getChildren().addAll(separator, label, choiceBox, button);
    }

    public static VBox getInstance(ToolController controller) {
        if(instance == null) instance = new MaskTool(controller);
        return instance;
    }

    public void handleApply(ActionEvent actionEvent) throws Exception {
        ImagePane imagePane = toolController.getActivePaneProperty();
        CommandManager manager = imagePane.getCommandManager();
        manager.executeCommand(new MaskCommand(imagePane, choiceBox.getSelectionModel().getSelectedIndex()));
        imagePane.setImage(imagePane.getImage());
    }

}
