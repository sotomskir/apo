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
import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ResourceBundle;

public class PosterizeTool extends VBox {

    private static VBox instance;
    private ToolController toolController;
    private Slider slider;

    protected PosterizeTool(ToolController controller) {
        ResourceBundle bundle = controller.getBundle();
        this.toolController = controller;
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("Posterize"));
        slider = new Slider(0, 255, 128);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(25.0f);
        slider.setBlockIncrement(1.0f);
        slider.setOrientation(Orientation.HORIZONTAL);
        Label sliderValue = new Label("128");

        slider.valueProperty().addListener(e -> {
            sliderValue.setText(String.valueOf(((int) slider.getValue())));
            ImagePane ap = toolController.getActivePaneProperty();
            ap.getImageView().setImage(livePosterize(ap.getImage(), (int) slider.getValue()));
        });

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
        getChildren().addAll(separator, label, slider, sliderValue, buttonApply, buttonCancel);
    }

    public static VBox getInstance(ToolController controller) {
        if(instance == null) instance = new PosterizeTool(controller);
        return instance;
    }

    public void handleApply(ActionEvent actionEvent) throws Exception {
        ImagePane imagePane = toolController.getActivePaneProperty();
        CommandManager manager = imagePane.getCommandManager();
//        manager.executeCommand(new TresholdCommand(imagePane, (int) slider.getValue()));
    }

    public static Image livePosterize(BufferedImage bi, int treshold) {
        BufferedImage posterized;
        if(bi.getColorModel().getNumComponents()>1) posterized = ImageUtils.rgbToGrayscale(bi);
        else posterized = ImageUtils.deepCopy(bi);
//        posterized = ImageUtils.deepCopy(bi);
//        Histogram histogram = new Histogram(grayBI);
        int width = posterized.getWidth();
        int height = posterized.getHeight();
        int channels = posterized.getColorModel().getNumComponents();
        int bitDepth = posterized.getColorModel().getPixelSize()/channels;
        int levels = (int)Math.pow(2, bitDepth);
        BufferedImage binaryImage = new BufferedImage(posterized.getWidth(), posterized.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        final byte[] a = ((DataBufferByte) posterized.getRaster().getDataBuffer()).getData();
        final byte[] b = ((DataBufferByte) binaryImage.getRaster().getDataBuffer()).getData();
//        int bp = b.length-1;
        byte LUT[] = new byte[levels];
        double tresholdSize = levels/(double)treshold;
        for (int t=0; t<levels-tresholdSize+1; t+=tresholdSize)
        for (int i=0; i<tresholdSize; ++i) {
                LUT[i+t] = (byte) (t);
                System.out.println(i+t+":"+(LUT[i+t] & 0xff));
            }
        for (int p = width*height-1; p>=0; p-- ) {
            //TODO
            b[p] = LUT[(a[p] & 0xFF)];
//            b[bp] = 0x00;
//            for (int i=0; i<8; ++i ) {
//                if ((a[p-i] & 0xFF) > treshold) b[bp] = (byte) (b[bp] & 0x01);
//                b[bp]= (byte) (b[bp] >> 1);
//            }
//            --bp;

        }
        return SwingFXUtils.toFXImage(binaryImage, null);
    }

}
