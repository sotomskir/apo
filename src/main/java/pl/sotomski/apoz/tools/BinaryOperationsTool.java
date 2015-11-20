package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.commands.BinaryOperationCommand;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.FileMenuUtils;

import java.io.File;
import java.util.ResourceBundle;

public class BinaryOperationsTool extends VBox {

    private static BinaryOperationsTool instance;
    private ToolController toolController;
    private ChoiceBox<String> choiceBox;
    private String[] methods = {"add", "sub", "multiply", "divide", "AND", "OR", "XOR"};
    private File secondFile;

    protected BinaryOperationsTool(ToolController controller) {
        ResourceBundle bundle = controller.getBundle();
        this.choiceBox = new ChoiceBox<>();
        this.toolController = controller;
        choiceBox.getItems().addAll(methods);
        choiceBox.getSelectionModel().select(0);

        Separator separator = new Separator(Orientation.HORIZONTAL);
        Button buttonApply = new Button(bundle.getString("Apply"));
        Label label = new Label(bundle.getString("BinaryOperations"));
        TextField textField = new TextField(bundle.getString("FilePath"));
        Button buttonFile = new Button("...");
        HBox hBox = new HBox(textField, buttonFile);

        buttonFile.setOnAction(event -> {
            secondFile = FileMenuUtils.openFileDialog((Pane) getScene().getRoot());
            textField.setText(secondFile.getAbsolutePath());
        });

        buttonApply.setOnAction((actionEvent) -> {
            try {
                handleApply(actionEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        getChildren().addAll(separator, label, choiceBox, hBox, buttonApply);
    }

    public static BinaryOperationsTool getInstance(ToolController controller) {
        if(instance == null) instance = new BinaryOperationsTool(controller);
        return instance;
    }

    public void handleApply(ActionEvent actionEvent) throws Exception {
        String method = choiceBox.getValue();
        ImagePane imagePane = toolController.getActivePaneProperty();
        CommandManager manager = imagePane.getCommandManager();
        manager.executeCommand(new BinaryOperationCommand(imagePane, FileMenuUtils.loadImage(secondFile), method));
        imagePane.setImage(imagePane.getImage());
    }

}
