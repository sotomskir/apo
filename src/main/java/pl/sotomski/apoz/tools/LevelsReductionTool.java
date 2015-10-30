package pl.sotomski.apoz.tools;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.LUTCommand;
import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.nodes.LevelsReductionControl;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class LevelsReductionTool extends Tool {

    private Spinner<Integer> spinner;
    private LevelsReductionControl chartControl;

    protected LevelsReductionTool(ToolController controller) {
        super(controller);

        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("LevelsReduction"));
        Button buttonApply = new Button(bundle.getString("Apply"));
        Button buttonCancel = new Button(bundle.getString("Cancel"));
        chartControl = new LevelsReductionControl();
        spinner = new Spinner<>(2, 255, 2);
        spinner.setEditable(true);
        spinner.valueProperty().addListener(e -> {
            chartControl.createDefaultIntervals(spinner.getValue());
        });

        chartControl.createDefaultIntervals(spinner.getValue());
        buttonApply.setOnAction((actionEvent) -> {
            try {
                handleApply(actionEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        buttonCancel.setOnAction((actionEvent) -> toolController.getActivePaneProperty().refresh());
        HBox hBox = new HBox(spinner, buttonCancel, buttonApply);
        getChildren().addAll(separator, label, chartControl, hBox);
        chartControl.changedProperty().addListener(observable -> updateImageViewAndHistogram());
        chartControl.createDefaultIntervals(3);
        updateImageViewAndHistogram();
    }


    private void updateImageViewAndHistogram() {
        ImagePane ap = toolController.getActivePaneProperty();
        BufferedImage image = calculateImage();
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
