package pl.sotomski.apoz.tools;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.LUTCommand;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ResourceBundle;

public class ThresholdTool extends VBox {

    private static VBox instance;
    private ToolController toolController;
    private Slider slider;
    private CheckBox reverseCheckBox;
    private Label sliderValue;

    protected ThresholdTool(ToolController controller) {
        ResourceBundle bundle = controller.getBundle();
        this.toolController = controller;
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("Thresholding"));
        slider = new Slider(0, 255, 128);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(25.0f);
        slider.setBlockIncrement(1.0f);
        slider.setOrientation(Orientation.HORIZONTAL);
        sliderValue = new Label("128");
        reverseCheckBox = new CheckBox(bundle.getString("Reverse"));
        reverseCheckBox.selectedProperty().addListener(e -> updateImageView());
        slider.valueProperty().addListener(e -> updateImageView());
        Button buttonApply = new Button(bundle.getString("Apply"));
        Button buttonCancel = new Button(bundle.getString("Cancel"));
        buttonApply.setOnAction((actionEvent) -> {
            try {
                handleApply(actionEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        buttonCancel.setOnAction((actionEvent) -> toolController.getActivePaneProperty().refresh());
        getChildren().addAll(separator, label, slider, sliderValue, reverseCheckBox, buttonApply, buttonCancel);
        updateImageView();
    }

    private void updateImageView() {
        sliderValue.setText(String.valueOf(((int) slider.getValue())));
        ImagePane ap = toolController.getActivePaneProperty();
        BufferedImage image = calculateImage(ap.getImage(), (int) slider.getValue(), reverseCheckBox.isSelected());
        ap.getImageView().setImage(SwingFXUtils.toFXImage(image, null));
        ap.getHistogramPane().update(image);
        ap.getHistogramPane().update(image);
    }

    public static VBox getInstance(ToolController controller) {
        if(instance == null) instance = new ThresholdTool(controller);
        return instance;
    }

    public void handleApply(ActionEvent actionEvent) throws Exception {
        ImagePane imagePane = toolController.getActivePaneProperty();
        CommandManager manager = imagePane.getCommandManager();
        manager.executeCommand(new LUTCommand(imagePane, getLUT()));
    }

    public static BufferedImage calculateImage(BufferedImage bi, int threshold, boolean reverse) {
        BufferedImage grayBI;
        if(bi.getColorModel().getNumComponents()>1) bi = ImageUtils.rgbToGrayscale(bi);
        grayBI = ImageUtils.deepCopy(bi);
        int width = grayBI.getWidth();
        int height = grayBI.getHeight();
        BufferedImage binaryImage = new BufferedImage(grayBI.getWidth(), grayBI.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        final byte[] a = ((DataBufferByte) grayBI.getRaster().getDataBuffer()).getData();
        final byte[] b = ((DataBufferByte) binaryImage.getRaster().getDataBuffer()).getData();
        int max, min;
        if (reverse) {
            max = 0;
            min = 255;
        } else {
            max = 255;
            min = 0;
        }
        for (int p = width*height-1; p>=0; p-- ) b[p] = (byte) ((a[p] & 0xFF) > threshold?max:min);
        return binaryImage;
    }

    public int[] getLUT() {
        int[] LUT = new int[256];
        if (reverseCheckBox.isSelected()) for (int i = 0; i<(int)slider.getValue(); ++i) LUT[i]=255;
        else for (int i=(int)slider.getValue(); i<256; ++i) LUT[i]=255;
        return LUT;
    }
}
