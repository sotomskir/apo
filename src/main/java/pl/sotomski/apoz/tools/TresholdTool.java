package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.TresholdCommand;
import pl.sotomski.apoz.nodes.ImagePane;

import java.util.ResourceBundle;

public class TresholdTool extends VBox {

    private static VBox instance;
    private ToolController toolController;
    private Slider slider;

    protected TresholdTool(ToolController controller) {
        ResourceBundle bundle = controller.getBundle();
        this.toolController = controller;
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("Tresholding"));
        slider = new Slider(0, 255, 128);
        slider.setOrientation(Orientation.HORIZONTAL);
        Label sliderValue = new Label("128");
        slider.valueProperty().addListener(e -> sliderValue.setText(String.valueOf(slider.getValue())));
        Button button = new Button(bundle.getString("Apply"));
        button.setOnAction((actionEvent) -> {
            try {
                handleApply(actionEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        getChildren().addAll(separator, label, slider, sliderValue, button);
    }

    public static VBox getInstance(ToolController controller) {
        if(instance == null) instance = new TresholdTool(controller);
        return instance;
    }

    public void handleApply(ActionEvent actionEvent) throws Exception {
            ImagePane imagePane = toolController.getActivePaneProperty();
            CommandManager manager = imagePane.getCommandManager();
            manager.executeCommand(new TresholdCommand(imagePane, (int) slider.getValue()));
//            imagePane.setImage(imagePane.getImage());
    }

}
