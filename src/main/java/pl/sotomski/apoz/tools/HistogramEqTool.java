package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.HistogramEqCommand;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.nodes.ImagePane;

import java.util.ResourceBundle;

public class HistogramEqTool extends Tool {

    private static Tool instance;
    private ChoiceBox<String> choiceBox;
    private String[] methods;

    protected HistogramEqTool(ToolController controller) {
        super(controller);
        ResourceBundle bundle = controller.getBundle();
        methods = new String[4];
        methods[0] = bundle.getString("HEmethod1");
        methods[1] = bundle.getString("HEmethod2");
        methods[2] = bundle.getString("HEmethod3");
        methods[3] = bundle.getString("HEmethod4");
        this.choiceBox = new ChoiceBox<>();
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("HistogramEqualisation"));
        choiceBox.getItems().addAll(methods);
        choiceBox.getSelectionModel().select(0);

        getChildren().addAll(separator, label, choiceBox, applyCancelBtns);
    }

    public static Tool getInstance(ToolController controller) {
        if(instance == null) instance = new HistogramEqTool(controller);
        return instance;
    }

    @Override
    public void handleApply(ActionEvent actionEvent) {
        String method = choiceBox.getValue();
        if(methods[0].equals(method)) {
            ImagePane imagePane = toolController.getActivePaneProperty();
            CommandManager manager = imagePane.getCommandManager();
            manager.executeCommand(new HistogramEqCommand(imagePane, 1));
            imagePane.setImage(imagePane.getImage());
        } else       if(methods[1].equals(method)) {
            ImagePane imagePane = toolController.getActivePaneProperty();
            CommandManager manager = imagePane.getCommandManager();
            manager.executeCommand(new HistogramEqCommand(imagePane, 2));
            imagePane.setImage(imagePane.getImage());
        } else       if(methods[2].equals(method)) {
            ImagePane imagePane = toolController.getActivePaneProperty();
            CommandManager manager = imagePane.getCommandManager();
            manager.executeCommand(new HistogramEqCommand(imagePane, 3));
            imagePane.setImage(imagePane.getImage());
        } else       if(methods[3].equals(method)) {
            ImagePane imagePane = toolController.getActivePaneProperty();
            CommandManager manager = imagePane.getCommandManager();
            manager.executeCommand(new HistogramEqCommand(imagePane, 4));
            imagePane.setImage(imagePane.getImage());
        } else       if(methods[4].equals(method)) {
            ImagePane imagePane = toolController.getActivePaneProperty();
            CommandManager manager = imagePane.getCommandManager();
            manager.executeCommand(new HistogramEqCommand(imagePane, 5));
            imagePane.setImage(imagePane.getImage());
        }
    }

}
