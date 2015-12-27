package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.LinearFilterCommand;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.nodes.BordersComboBox;
import pl.sotomski.apoz.nodes.ImagePane;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LinearFilterTool extends VBox {

    private static VBox instance;
    private ToolController toolController;
    private ChoiceBox<String> choiceBox;
    private String[] masks;
    private int[] mask = new int[9];
    private List<Spinner> spinners;
    private BordersComboBox bordersComboBox;
    private static int[][] maskTemplates = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 8, 1, 1, 1, 1},
            {1, 2, 1, 2, 4, 2, 1, 2, 1},
            {0, 1, 0, 1, 4, 1, 0, 1, 0},
            {-1, -1, -1, -1, 8, -1, -1, -1, -1},
            {0, -1, 0, -1, 4, -1, 0, -1, 0},
            {1, -2, 1, -2, 4, -2, 1, -2, 1},
            {0, 1, 0, 1, -4, 1, 0, 1, 0},
            {1, -2, 1, -2, 5, -2, 1, -2, 1},
            {-1, -1, -1, -1, 9, -1, -1, -1, -1},
            {0, -1, 0, -1, 5, -1, 0, -1, 0}
    };

    protected LinearFilterTool(ToolController controller) {
        ResourceBundle bundle = controller.getBundle();
        masks = new String[11];
        masks[0] = bundle.getString("BlurMask1");
        masks[1] = bundle.getString("BlurMask2");
        masks[2] = bundle.getString("BlurMask3");
        masks[3] = bundle.getString("BlurMask4");
        masks[4] = bundle.getString("SharpenMask1");
        masks[5] = bundle.getString("SharpenMask2");
        masks[6] = bundle.getString("SharpenMask3");
        masks[7] = bundle.getString("SharpenMask4");
        masks[8] = bundle.getString("EdgeDetectionMask1");
        masks[9] = bundle.getString("EdgeDetectionMask2");
        masks[10] = bundle.getString("EdgeDetectionMask3");
        this.choiceBox = new ChoiceBox<>();
        this.toolController = controller;
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("MaskTool"));
        TilePane tilePane = new TilePane();
        tilePane.setPrefColumns(3);
        tilePane.setPrefRows(3);

        // create spinners
        spinners = new ArrayList<>();
        for (int x = 0; x < 9; x++) {
            Spinner<Integer> spinner = new Spinner<>(-9, 9, 1);
            spinners.add(spinner);
            tilePane.getChildren().add(spinner);
        }
        choiceBox.getItems().addAll(masks);
        choiceBox.getSelectionModel().select(0);

        // Update spinners on mask selection
        choiceBox.getSelectionModel().selectedItemProperty().addListener(observable -> {
            int selectedMask = choiceBox.getSelectionModel().getSelectedIndex();
            for (int i = 0; i < 9; ++i) spinners.get(i).getValueFactory().setValue(maskTemplates[selectedMask][i]);

        });

        // add other controls
        bordersComboBox = new BordersComboBox(bundle);
        Button button = new Button(bundle.getString("Apply"));
        button.setOnAction((actionEvent) -> {
            try {
                handleApply(actionEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        getChildren().addAll(separator, label, tilePane, choiceBox, bordersComboBox, button);
    }

    public static VBox getInstance(ToolController controller) {
        if(instance == null) instance = new LinearFilterTool(controller);
        return instance;
    }

    public void handleApply(ActionEvent actionEvent) throws Exception {
        ImagePane imagePane = toolController.getActivePaneProperty();
        CommandManager manager = imagePane.getCommandManager();
        for (int i = 0; i < 9; ++i) mask[i] = (int) spinners.get(i).getValue();
        manager.executeCommand(new LinearFilterCommand(imagePane, mask, bordersComboBox.getMethod()));
        imagePane.setImage(imagePane.getImage());
    }

}