package pl.sotomski.apoz.tools;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import pl.sotomski.apoz.commands.BinaryOperationCommand;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.controllers.MainController;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.nodes.ImageTab;

import java.awt.image.BufferedImage;
import java.util.ResourceBundle;

public class BinaryOperationsTool extends Tool {

    private static BinaryOperationsTool instance;
    private ChoiceBox<String> choiceBox;
    private String[] methods = {"add", "sub", "multiply", "divide", "AND", "OR", "XOR"};
    ChoiceBox<ImagePane> secondImageChoiceBox = new ChoiceBox<>();

    protected BinaryOperationsTool(ToolController controller) {
        super(controller);
        ResourceBundle bundle = controller.getBundle();
        this.choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll(methods);
        choiceBox.getSelectionModel().select(0);

        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("BinaryOperations"));
        updateChoiceBoxItems();
        secondImageChoiceBox.setOnMouseClicked(event1 -> updateChoiceBoxItems());

        getChildren().addAll(separator, label, choiceBox, secondImageChoiceBox, applyCancelBtns);
    }

    private void updateChoiceBoxItems() {
        MainController controller1 = (MainController) toolController;
        ObservableList<Tab> tabList = controller1.getTabPane().getTabs();
        secondImageChoiceBox.getItems().clear();
        for (Tab tab: tabList) secondImageChoiceBox.getItems().add(((ImageTab)tab).getPane());
    }
    public static BinaryOperationsTool getInstance(ToolController controller) {
        if(instance == null) instance = new BinaryOperationsTool(controller);
        return instance;
    }

    public void handleApply(ActionEvent actionEvent) {
        String method = choiceBox.getValue();
        ImagePane imagePane = toolController.getActivePane();
        CommandManager manager = imagePane.getCommandManager();
        BufferedImage secondImage = secondImageChoiceBox.getSelectionModel().getSelectedItem().getImage();
        manager.executeCommand(new BinaryOperationCommand(imagePane, secondImage, method));
        imagePane.setImage(imagePane.getImage());
    }

}
