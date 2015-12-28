package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.GradientEdgeDetectionCommand;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.nodes.BordersMethodToggles;
import pl.sotomski.apoz.nodes.ImagePane;

import java.util.ResourceBundle;

public class GradientEdgeDetectionTool extends VBox {

    private static VBox instance;
    private ToolController toolController;
    private ChoiceBox<String> choiceBox;
    private String[] masks;
    ComboBox<String> directionComboBox;
    BordersMethodToggles bordersMethodToggles;


    protected GradientEdgeDetectionTool(ToolController controller) {
        ResourceBundle bundle = controller.getBundle();
        masks = new String[4];
        masks[0] = bundle.getString("RobertsMask");
        masks[1] = bundle.getString("SobelMask");
        masks[2] = bundle.getString("PrewittMask");
        masks[3] = bundle.getString("KirshMask");
        this.choiceBox = new ChoiceBox<>();
        this.toolController = controller;
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("EdgeDetection"));
        Label directionLabel = new Label(bundle.getString("EdgeDirection"));

        directionComboBox = new ComboBox<>();
        directionComboBox.getItems().addAll("N", "NE", "E", "SE", "S", "SW", "W", "NW");
        directionComboBox.getSelectionModel().selectFirst();
        directionComboBox.setVisible(false);
        directionLabel.setVisible(false);

        choiceBox.getItems().addAll(masks);
        choiceBox.getSelectionModel().selectFirst();
        choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (masks[2].equals(newValue) || masks[3].equals(newValue)) {
                directionComboBox.setVisible(true);
                directionLabel.setVisible(true);
            }
            else {
                directionComboBox.setVisible(false);
                directionLabel.setVisible(false);
            }
        });

        Button button = new Button(bundle.getString("Apply"));
        button.setOnAction((actionEvent) -> {
            try {
                handleApply(actionEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        bordersMethodToggles = new BordersMethodToggles(bundle);
        getChildren().addAll(
                label,
                separator,
                new Label(bundle.getString("Mask")),
                choiceBox,
                directionLabel,
                directionComboBox,
                bordersMethodToggles,
                button
        );
    }

    public static VBox getInstance(ToolController controller) {
        if(instance == null) instance = new GradientEdgeDetectionTool(controller);
        return instance;
    }

    public void handleApply(ActionEvent actionEvent) throws Exception {
        ImagePane imagePane = toolController.getActivePaneProperty();
        CommandManager manager = imagePane.getCommandManager();
        String maskName;
        switch (choiceBox.getSelectionModel().getSelectedIndex()) {
            case 0:
                maskName = "Roberts";
                break;
            case 1:
                maskName = "Sobel";
                break;
            case 2:
                maskName = "Prewitt";
                break;
            case 3:
                maskName = "Kirsh";
                break;
            default:
                maskName = "Roberts";
        }
        int direction = directionComboBox.getSelectionModel().getSelectedIndex();
        int bordersMethod = bordersMethodToggles.getMethod();
        manager.executeCommand(new GradientEdgeDetectionCommand(imagePane, maskName, direction, bordersMethod));
        imagePane.setImage(imagePane.getImage());
    }

}
