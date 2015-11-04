package pl.sotomski.apoz.tools;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.LUTCommand;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.controls.ChartControl;
import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ResourceBundle;

public class IntervalThresholdTool extends VBox {

    private static VBox instance;
    private ToolController toolController;
    private Spinner<Integer> spinner;
    private ChartControl chartControl;

    protected IntervalThresholdTool(ToolController controller) {
        this.toolController = controller;
        ResourceBundle bundle = controller.getBundle();

        // create controls
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("IntervalThresholding"));
        CheckBox checkBoxKeepLevels = new CheckBox(bundle.getString("KeepLevels"));
        CheckBox checkBoxInvert = new CheckBox(bundle.getString("Reverse"));
        chartControl = new ChartControl();
        spinner = new Spinner<>(2, 255, 3);
        Button buttonCancel = new Button(bundle.getString("Cancel"));
        Button buttonApply = new Button(bundle.getString("Apply"));
        HBox hBox = new HBox(spinner, buttonCancel, buttonApply);

        // add listeners
        spinner.valueProperty().addListener(e -> {
            checkBoxInvert.selectedProperty().setValue(false);
            checkBoxKeepLevels.selectedProperty().setValue(false);
            chartControl.createDefaultIntervals(spinner.getValue());
        });
        checkBoxInvert.selectedProperty().addListener(observable1 -> {
            chartControl.invert();
            updateImageView();
        });
        checkBoxKeepLevels.selectedProperty().addListener(observable1 -> {
            chartControl.setKeepLevels(checkBoxKeepLevels.isSelected());
            updateImageView();
        });
        buttonApply.setOnAction((actionEvent) -> {
            try {
                handleApply(actionEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        buttonCancel.setOnAction((actionEvent) -> toolController.getActivePaneProperty().refresh());
        chartControl.changedProperty().addListener(observable -> updateImageView());

        // add controls to view and init
        getChildren().addAll(separator, label, chartControl, checkBoxInvert, checkBoxKeepLevels, hBox);
        chartControl.createDefaultIntervals(spinner.getValue());
        updateImageView();
    }


    private void updateImageView() {
        ImagePane ap = toolController.getActivePaneProperty();
        BufferedImage image = calculateImage();
        ap.getImageView().setImage(SwingFXUtils.toFXImage(image, null));
        ap.getHistogramPane().update(image);
    }

    public static VBox getInstance(ToolController controller) {
        if(instance == null) instance = new IntervalThresholdTool(controller);
        return instance;
    }

    public void handleApply(ActionEvent actionEvent) throws Exception {
        ImagePane imagePane = toolController.getActivePaneProperty();
        CommandManager manager = imagePane.getCommandManager();
        manager.executeCommand(new LUTCommand(imagePane, chartControl.getLUT()));
    }

    public BufferedImage calculateImage() {
        BufferedImage grayBI, image = toolController.getBufferedImage();
        if(image.getColorModel().getNumComponents()>1) grayBI = ImageUtils.rgbToGrayscale(image);
        else grayBI = ImageUtils.deepCopy(image);
        int width = grayBI.getWidth();
        int height = grayBI.getHeight();
        BufferedImage binaryImage = new BufferedImage(grayBI.getWidth(), grayBI.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        final byte[] a = ((DataBufferByte) grayBI.getRaster().getDataBuffer()).getData();
        final byte[] b = ((DataBufferByte) binaryImage.getRaster().getDataBuffer()).getData();
        for (int p = width*height-1; p>=0; p-- ) b[p] = (byte) (chartControl.getLUT()[a[p] & 0xFF]);
        return binaryImage;
    }

}
