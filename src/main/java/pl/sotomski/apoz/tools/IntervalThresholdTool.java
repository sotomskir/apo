package pl.sotomski.apoz.tools;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.LUTCommand;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.nodes.ChartControl;
import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;

public class IntervalThresholdTool extends Tool {

    private static Tool instance;
    private Spinner<Integer> spinner;
    private ChartControl chartControl;

    protected IntervalThresholdTool(ToolController controller) {
        super(controller);

        // create controls
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("IntervalThresholding"));
        CheckBox checkBoxKeepLevels = new CheckBox(bundle.getString("KeepLevels"));
        CheckBox checkBoxInvert = new CheckBox(bundle.getString("Reverse"));
        CheckBox checkBoxNegative = new CheckBox(bundle.getString("NegativeConversion"));
        CheckBox checkBoxStretch = new CheckBox(bundle.getString("Stretch"));
        chartControl = new ChartControl();
        spinner = new Spinner<>(2, 255, 3);
        HBox hBox = new HBox(spinner, applyCancelBtns);

        // add listeners
        spinner.valueProperty().addListener(e -> {
            checkBoxInvert.selectedProperty().setValue(false);
            checkBoxKeepLevels.selectedProperty().setValue(false);
            checkBoxStretch.selectedProperty().setValue(false);
            chartControl.createDefaultIntervals(spinner.getValue());
        });
        checkBoxInvert.selectedProperty().addListener(observable1 -> {
            chartControl.invert();
            updateImageView();
        });
        checkBoxKeepLevels.selectedProperty().addListener(observable1 -> {
            if (checkBoxKeepLevels.isSelected()) checkBoxStretch.selectedProperty().setValue(false);
            if (!checkBoxKeepLevels.isSelected()) checkBoxNegative.selectedProperty().setValue(false);
            chartControl.setKeepLevels(checkBoxKeepLevels.isSelected());
            updateImageView();
        });
        checkBoxStretch.selectedProperty().addListener(observable1 -> {
            if (checkBoxStretch.isSelected()) checkBoxKeepLevels.selectedProperty().setValue(false);
            if (checkBoxStretch.isSelected()) checkBoxNegative.selectedProperty().setValue(false);
            chartControl.toggleStretch(checkBoxStretch.isSelected());
            updateImageView();
        });
        checkBoxNegative.selectedProperty().addListener(observable1 -> {
            if (checkBoxNegative.isSelected()) checkBoxKeepLevels.selectedProperty().setValue(true);
            chartControl.setNegative(checkBoxNegative.isSelected());
            updateImageView();
        });

        chartControl.changedProperty().addListener(observable -> updateImageView());

        VBox checkboxes = new VBox(checkBoxInvert, checkBoxNegative, checkBoxKeepLevels, checkBoxStretch);
        // add controls to view and init
        getChildren().addAll(
                separator,
                label,
                new HBox(chartControl, checkboxes),
                hBox
        );
        chartControl.createDefaultIntervals(spinner.getValue());
        updateImageView();
    }


    private void updateImageView() {
        ImagePane ap = toolController.getActivePane();
        BufferedImage image = ImageUtils.applyLUT(toolController.getBufferedImage(), chartControl.getLUT());
        ap.getImageView().setImage(SwingFXUtils.toFXImage(image, null));
        ap.getHistogramPane().update(image);
    }

    public static Tool getInstance(ToolController controller) {
        if(instance == null) instance = new IntervalThresholdTool(controller);
        return instance;
    }

    @Override
    public void handleApply(ActionEvent actionEvent) {
        ImagePane imagePane = toolController.getActivePane();
        CommandManager manager = imagePane.getCommandManager();
        manager.executeCommand(new LUTCommand(imagePane, chartControl.getLUT()));
        disableTool();
    }


}
