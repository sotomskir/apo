package pl.sotomski.apoz.tools;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.LUTCommand;
import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.nodes.LevelsReductionControl;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ResourceBundle;

public class LevelsReductionTool extends VBox {

    private static VBox instance;
    private ToolController toolController;
    private Slider slider;
    private Label sliderValue;
    private LevelsReductionControl chartControl;

    protected LevelsReductionTool(ToolController controller) {
        this.toolController = controller;
        ResourceBundle bundle = controller.getBundle();

        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("LevelsReduction"));
        Button buttonApply = new Button(bundle.getString("Apply"));
        Button buttonCancel = new Button(bundle.getString("Cancel"));
        chartControl = new LevelsReductionControl();

        slider = new Slider(2, 255, 3);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(25);
        slider.setMinorTickCount(25);
        slider.setSnapToTicks(true);
        slider.setOrientation(Orientation.HORIZONTAL);
        sliderValue = new Label("3");
        slider.valueProperty().addListener(e -> {
            chartControl.createDefaultIntervals((int) slider.getValue());
        });

        chartControl.createDefaultIntervals((int) slider.getValue());
        buttonApply.setOnAction((actionEvent) -> {
            try {
                handleApply(actionEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        buttonCancel.setOnAction((actionEvent) -> toolController.getActivePaneProperty().refresh());
        getChildren().addAll(separator, label, chartControl, slider, sliderValue, buttonApply, buttonCancel);
        chartControl.changedProperty().addListener(observable -> updateImageView());
        chartControl.createDefaultIntervals(3);
        updateImageView();
    }


    private void updateImageView() {
        sliderValue.setText(String.valueOf(((int) slider.getValue())));
        ImagePane ap = toolController.getActivePaneProperty();
        ap.getImageView().setImage(liveThreshold());
    }

    public static VBox getInstance(ToolController controller) {
        if(instance == null) instance = new LevelsReductionTool(controller);
        return instance;
    }

    public void handleApply(ActionEvent actionEvent) throws Exception {
        ImagePane imagePane = toolController.getActivePaneProperty();
        CommandManager manager = imagePane.getCommandManager();
        manager.executeCommand(new LUTCommand(imagePane, chartControl.getLUT()));
    }

    public Image liveThreshold() {
        BufferedImage grayBI, image = toolController.getBufferedImage();
        if(image.getColorModel().getNumComponents()>1) grayBI = ImageUtils.rgbToGrayscale(image);
        else grayBI = ImageUtils.deepCopy(image);
        int width = grayBI.getWidth();
        int height = grayBI.getHeight();
        BufferedImage binaryImage = new BufferedImage(grayBI.getWidth(), grayBI.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        final byte[] a = ((DataBufferByte) grayBI.getRaster().getDataBuffer()).getData();
        final byte[] b = ((DataBufferByte) binaryImage.getRaster().getDataBuffer()).getData();
        for (int p = width*height-1; p>=0; p-- ) b[p] = (byte) (chartControl.getLUT()[a[p] & 0xFF]);
        return SwingFXUtils.toFXImage(binaryImage, null);
    }

}
