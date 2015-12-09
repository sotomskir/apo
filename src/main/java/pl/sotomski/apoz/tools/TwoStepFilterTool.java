package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.LinearFilterCommand;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.Mask;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TwoStepFilterTool extends VBox {

    private static VBox instance;
    private ToolController toolController;
    private ChoiceBox<String> choiceBox;
    private int[] mask = new int[9];
    private List<Spinner<Integer>> spinners;
    private List<Label> labels = new ArrayList<>();
    private Mask maskM = new Mask(5);
    private Mask maskF = new Mask(7);
    private Mask maskG = new Mask(3);
    private RadioButton oneStepRadio;
    private RadioButton twoStepRadio;



    protected TwoStepFilterTool(ToolController controller) {
        ResourceBundle bundle = controller.getBundle();
        this.toolController = controller;
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("TwoStepFilterTool"));
        GridPane gridPane1 = new GridPane();
        GridPane gridPane2 = new GridPane();
        GridPane gridPane3 = new GridPane();

        // create spinners
        spinners = new ArrayList<>();
        addSpinners(gridPane1, 3, 3);
        addSpinners(gridPane2, 3, 3);
        initSpinners();
        addLabels(gridPane3, 5, 5);
        updateMasksValues();
        updateLabels();

        EventHandler<MouseEvent> updateMaskM = event -> {
            updateMasksValues();
            updateLabels();
//            System.out.println(maskF);
//            System.out.println(maskG);
        };

        spinners.forEach(spinner -> spinner.setOnMouseClicked(updateMaskM));

        ToggleGroup toggleGroup = new ToggleGroup();
        oneStepRadio = new RadioButton(bundle.getString("OneStepFiltration"));
        oneStepRadio.setToggleGroup(toggleGroup);
        twoStepRadio = new RadioButton(bundle.getString("TwoStepFiltration"));
        twoStepRadio.setToggleGroup(toggleGroup);
        oneStepRadio.setSelected(true);

        Button button = new Button(bundle.getString("Apply"));
        button.setOnAction((actionEvent) -> {
            try {
                handleApply(actionEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        getChildren().addAll(
                separator,
                label,
                new Label("mask 1"),
                gridPane1,
                new Label("mask 2"),
                gridPane2,
                new Label("mask 3"),
                gridPane3,
                oneStepRadio,
                twoStepRadio,
                button
        );
    }

    private void updateLabels() {
        int[] maskData = maskM.getData();
        for (int i = 0; i < 25; ++i) labels.get(i).setText(String.valueOf(maskData[i]));
    }

    private void updateMasksValues() {
        int i = 0;
        for (int y = 2; y < 5; ++y)
            for (int x = 2; x < 5; ++x) maskF.set(x, y, spinners.get(i++).getValue());

        for (int y = 0; y < 3; ++y)
            for (int x = 0; x < 3; ++x) maskG.set(x, y, spinners.get(i++).getValue());

        calculateMaskM();
    }


    private void addSpinners(GridPane gridPane, int xV, int yV) {
        for (int y = 0; y < yV; y++)
            for (int x = 0; x < xV; x++) {
                Spinner<Integer> spinner = new Spinner<>(-9, 9, 1);
                spinner.setPrefSize(60, 60);
                spinners.add(spinner);
                gridPane.add(spinner, x, y);
            }
    }

    private void initSpinners() {
        final int[] template = new int[] {
                1,2,1,
                2,4,2,
                1,2,1,
                0,-1, 0,
                -1, 4,-1,
                0,-1, 0
        };
        for (int i = 0; i < 18; ++i) spinners.get(i).getValueFactory().setValue(template[i]);
    }

    private void addLabels(GridPane gridPane, int xV, int yV) {
        for (int y = 0; y < yV; y++)
            for (int x = 0; x < xV; x++) {
                Label label = new Label();
                label.setPrefSize(60, 60);
                labels.add(label);
                gridPane.add(label, x, y);
            }
    }

    public static VBox getInstance(ToolController controller) {
        if(instance == null) instance = new TwoStepFilterTool(controller);
        return instance;
    }

    public void handleApply(ActionEvent actionEvent) throws Exception {
        if (oneStepRadio.isSelected()) {
            ImagePane imagePane = toolController.getActivePaneProperty();
            CommandManager manager = imagePane.getCommandManager();
            manager.executeCommand(new LinearFilterCommand(imagePane, maskM.getData(), 0));
            imagePane.refresh();
        } else if (twoStepRadio.isSelected()) {
            ImagePane imagePane = toolController.getActivePaneProperty();
            CommandManager manager = imagePane.getCommandManager();
            manager.executeCommand(new LinearFilterCommand(imagePane, maskF.getData(), 0));
            manager.executeCommand(new LinearFilterCommand(imagePane, maskG.getData(), 0));
            imagePane.refresh();
        }
    }

    private void calculateMaskM() {
        for (int y = 0; y < 5; ++y)
            for (int x = 0; x < 5; ++x) {
                maskM.set(x, y, maskG.multiply(maskF.get3x3Mask(x, y)));
            }
    }


}
