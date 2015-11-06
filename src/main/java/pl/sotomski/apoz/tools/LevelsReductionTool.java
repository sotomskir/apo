package pl.sotomski.apoz.tools;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.LUTCommand;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.controls.LevelsReductionControl;
import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;

public class LevelsReductionTool extends Tool {

    private static LevelsReductionTool instance;
    private Spinner<Integer> spinner;
    private LevelsReductionControl chartControl;

    protected LevelsReductionTool(ToolController controller) {
        super(controller);

        // create controls
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("LevelsReduction"));
        Button buttonApply = new Button(bundle.getString("Apply"));
        Button buttonCancel = new Button(bundle.getString("Cancel"));
        chartControl = new LevelsReductionControl();
        spinner = new Spinner<>(2, 255, 2);
        spinner.setEditable(true);
        HBox hBox = new HBox(spinner, buttonCancel, buttonApply);

        // create listeners
        spinner.valueProperty().addListener(e -> {
            chartControl.createDefaultIntervals(spinner.getValue());
        });

        buttonApply.setOnAction((actionEvent) -> {
            try {
                handleApply(actionEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        buttonCancel.setOnAction((actionEvent) -> toolController.getActivePaneProperty().refresh());
        chartControl.changedProperty().addListener(observable -> updateImageViewAndHistogram());

        //
        getChildren().addAll(separator, label, chartControl, hBox);
        chartControl.createDefaultIntervals(spinner.getValue());
        updateImageViewAndHistogram();
    }


    private void updateImageViewAndHistogram() {
        ImagePane ap = toolController.getActivePaneProperty();
        BufferedImage image = ImageUtils.applyLUT(toolController.getBufferedImage(), chartControl.getLUT());
        ap.getImageView().setImage(SwingFXUtils.toFXImage(image, null));
        ap.getHistogramPane().update(image);
    }

    public static Tool getInstance(ToolController controller) {
        if(instance == null) instance = new LevelsReductionTool(controller);
        return instance;
    }

    public void handleApply(ActionEvent actionEvent) throws Exception {
        ImagePane imagePane = toolController.getActivePaneProperty();
        CommandManager manager = imagePane.getCommandManager();
        manager.executeCommand(new LUTCommand(imagePane, chartControl.getLUT()));
    }

}
