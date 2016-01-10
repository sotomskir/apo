package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import pl.sotomski.apoz.commands.BinaryOperationCommand;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.FileMenuUtils;

import java.io.File;
import java.util.ResourceBundle;

public class BinaryOperationsTool extends Tool {

    private static BinaryOperationsTool instance;
    private ChoiceBox<String> choiceBox;
    private String[] methods = {"add", "sub", "multiply", "divide", "AND", "OR", "XOR"};
    private File secondFile;

    protected BinaryOperationsTool(ToolController controller) {
        super(controller);
        ResourceBundle bundle = controller.getBundle();
        this.choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll(methods);
        choiceBox.getSelectionModel().select(0);

        Separator separator = new Separator(Orientation.HORIZONTAL);
        Button buttonApply = new Button(bundle.getString("Apply"));
        Label label = new Label(bundle.getString("BinaryOperations"));
        TextField textField = new TextField(bundle.getString("FilePath"));
        Button buttonFile = new Button("...");
        HBox hBox = new HBox(textField, buttonFile);

        buttonFile.setOnAction(event -> {
            secondFile = FileMenuUtils.openFileDialog((BorderPane) getScene().getRoot());
            textField.setText(secondFile.getAbsolutePath());
        });

        buttonApply.setOnAction((actionEvent) -> {
            try {
                handleApply(actionEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        getChildren().addAll(separator, label, choiceBox, hBox, applyCancelBtns);
    }

    public static BinaryOperationsTool getInstance(ToolController controller) {
        if(instance == null) instance = new BinaryOperationsTool(controller);
        return instance;
    }

    public void handleApply(ActionEvent actionEvent) {
        String method = choiceBox.getValue();
        ImagePane imagePane = toolController.getActivePaneProperty();
        CommandManager manager = imagePane.getCommandManager();
        manager.executeCommand(new BinaryOperationCommand(imagePane, FileMenuUtils.loadImage(secondFile), method));
        imagePane.setImage(imagePane.getImage());
    }

}
