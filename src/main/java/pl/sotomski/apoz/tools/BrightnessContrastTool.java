package pl.sotomski.apoz.tools;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.LUTCommand;
import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ResourceBundle;

public class BrightnessContrastTool extends VBox {

    private static VBox instance;
    private ToolController toolController;
    private Slider contrastSlider;
    private Slider brightnessSlider;
    private int[] LUT;

    protected BrightnessContrastTool(ToolController controller) {
        ResourceBundle bundle = controller.getBundle();
        this.toolController = controller;

        contrastSlider = new Slider(-100, 100, 0);
        brightnessSlider = new Slider(-100, 100, 0);
        LUT = new int[256];

        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label brightnessLabel = new Label(bundle.getString("Brightness"));
        Label contrastLabel = new Label(bundle.getString("Contrast"));
        Label brightnessValueLabel = new Label("0");
        Label contrastValueLabel = new Label("0");
        Button buttonApply = new Button(bundle.getString("Apply"));
        Button buttonCancel = new Button(bundle.getString("Cancel"));

        sliderStyle(contrastSlider);
        sliderStyle(brightnessSlider);

        contrastSlider.valueProperty().addListener(e -> {
            contrastValueLabel.setText(String.valueOf(((int) contrastSlider.getValue())));
            ImagePane ap = toolController.getActivePaneProperty();
            updateLUT();
            ap.getImageView().setImage(liveApply());
        });

        brightnessSlider.valueProperty().addListener(e -> {
            brightnessValueLabel.setText(String.valueOf(((int) brightnessSlider.getValue())));
            ImagePane ap = toolController.getActivePaneProperty();
            updateLUT();
            ap.getImageView().setImage(liveApply());
        });

        buttonApply.setOnAction((actionEvent) -> {
            try {
                handleApply(actionEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        buttonCancel.setOnAction((actionEvent) -> toolController.getActivePaneProperty().refresh());
        getChildren().addAll(separator, brightnessLabel, brightnessSlider,brightnessValueLabel, contrastLabel, contrastSlider, contrastValueLabel, buttonApply, buttonCancel);
    }

    private void updateLUT() {
        for (int i=0; i<255; ++i) {
            double slope = 1 + 0.01 * contrastSlider.getValue();
            LUT[i] = (int) (slope * (i - 128) + 128);
            LUT[i] = (int) (LUT[i]+brightnessSlider.getValue());
            if (LUT[i] > 255) LUT[i] = 255;
            if (LUT[i] < 0) LUT[i] = 0;
        }
    }

    private void sliderStyle(Slider slider) {
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(25.0f);
        slider.setBlockIncrement(1.0f);
        slider.setOrientation(Orientation.HORIZONTAL);
    }

    public static VBox getInstance(ToolController controller) {
        if(instance == null) instance = new BrightnessContrastTool(controller);
        return instance;
    }

    public void handleApply(ActionEvent actionEvent) throws Exception {
        ImagePane imagePane = toolController.getActivePaneProperty();
        CommandManager manager = imagePane.getCommandManager();
        manager.executeCommand(new LUTCommand(imagePane, LUT));
    }

    public Image liveApply() {
        BufferedImage grayBI, image = this.toolController.getBufferedImage();
        if(image.getColorModel().getNumComponents()>1) grayBI = ImageUtils.rgbToGrayscale(image);
        else grayBI = ImageUtils.deepCopy(image);
        int width = grayBI.getWidth();
        int height = grayBI.getHeight();
        BufferedImage binaryImage = new BufferedImage(grayBI.getWidth(), grayBI.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        final byte[] a = ((DataBufferByte) grayBI.getRaster().getDataBuffer()).getData();
        final byte[] b = ((DataBufferByte) binaryImage.getRaster().getDataBuffer()).getData();
        for (int p = width*height-1; p>=0; p-- ) b[p] = (byte) (LUT[a[p] & 0xFF]);
        return SwingFXUtils.toFXImage(binaryImage, null);
    }

}
