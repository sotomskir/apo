package pl.sotomski.apoz.tools;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.LUTCommand;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class BrightnessContrastTool extends Tool {

    private static BrightnessContrastTool instance;
    private Slider contrastSlider;
    private Slider brightnessSlider;
    private Slider gammaSlider;
    private int[] LUT;


    private BrightnessContrastTool(ToolController controller) {
        super(controller);
        contrastSlider = new Slider(-100, 100, 0);
        brightnessSlider = new Slider(-100, 100, 0);
        gammaSlider = new Slider(0.3, 6, 1);
        LUT = new int[256];

        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label brightnessLabel = new Label(bundle.getString("Brightness"));
        Label contrastLabel = new Label(bundle.getString("Contrast"));
        Label gammaLabel = new Label(bundle.getString("Gamma"));
        Label brightnessValueLabel = new Label("0");
        Label contrastValueLabel = new Label("0");
        Label gammaValueLabel = new Label("0");

        sliderStyle(contrastSlider);
        sliderStyle(brightnessSlider);
//        sliderStyle(gammaSlider);

        contrastSlider.valueProperty().addListener(e -> {
            contrastValueLabel.setText(String.valueOf(((int) contrastSlider.getValue())));
            updateImageView();
        });

        brightnessSlider.valueProperty().addListener(e -> {
            brightnessValueLabel.setText(String.valueOf(((int) brightnessSlider.getValue())));
            updateImageView();
        });

        gammaSlider.valueProperty().addListener(e -> {
            gammaValueLabel.setText(String.valueOf((gammaSlider.getValue())));
            updateImageView();
        });

//        getChildren().addAll(separator, brightnessLabel, brightnessSlider,brightnessValueLabel, contrastLabel,
//                contrastSlider, contrastValueLabel, gammaLabel, gammaSlider, gammaValueLabel, buttonApply, buttonCancel);
        getChildren().addAll(separator, brightnessLabel, brightnessSlider,brightnessValueLabel, contrastLabel,
                contrastSlider, contrastValueLabel, applyCancelBtns);
    }

    private void updateImageView() {
        ImagePane ap = toolController.getActivePaneProperty();
        updateLUT();
        BufferedImage image = calculateImage();
        ap.getImageView().setImage(SwingFXUtils.toFXImage(image, null));
        ap.getHistogramPane().update(image);
    }

    private void updateLUT() {
        for (int i=0; i<256; ++i) {
            double slope = 1 + 0.01 * contrastSlider.getValue();
            LUT[i] = (int) (slope * (i - 128) + 128);
            LUT[i] = (int) (LUT[i]+brightnessSlider.getValue());
            LUT[i] = (int) Math.pow(LUT[i], gammaSlider.getValue());
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
        slider.setMaxWidth(Double.MAX_VALUE);
        slider.setPrefWidth(300);
    }

    public static Tool getInstance(ToolController controller) {
        if(instance == null) instance = new BrightnessContrastTool(controller);
        else {
            instance.contrastSlider.setValue(0);
            instance.brightnessSlider.setValue(0);
        }
        return instance;
    }

    @Override
    public void handleApply(ActionEvent actionEvent) {
        ImagePane imagePane = toolController.getActivePaneProperty();
        CommandManager manager = imagePane.getCommandManager();
        manager.executeCommand(new LUTCommand(imagePane, LUT));
        disableTool();
    }

    private BufferedImage calculateImage() {
        BufferedImage grayBI, image = this.toolController.getBufferedImage();
        if(image.getColorModel().getNumComponents()>1) image = ImageUtils.rgbToGrayscale(image);
        grayBI = ImageUtils.deepCopy(image);
        int width = grayBI.getWidth();
        int height = grayBI.getHeight();
        BufferedImage binaryImage = new BufferedImage(grayBI.getWidth(), grayBI.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        final byte[] a = ((DataBufferByte) grayBI.getRaster().getDataBuffer()).getData();
        final byte[] b = ((DataBufferByte) binaryImage.getRaster().getDataBuffer()).getData();
        for (int p = width*height-1; p>=0; p-- ) b[p] = (byte) (LUT[a[p] & 0xFF]);
        return binaryImage;
    }

}
