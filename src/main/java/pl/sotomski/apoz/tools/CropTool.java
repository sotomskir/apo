package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.controllers.MainController;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.nodes.CropRectangle;
import pl.sotomski.apoz.nodes.HistogramPane;
import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.nodes.ImageTab;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.ResourceBundle;

public class CropTool extends VBox {

    private static VBox instance;
    private ToolController toolController;
    ResourceBundle bundle;

    protected CropTool(ToolController controller) {
        bundle = controller.getBundle();
        this.toolController = controller;
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("CropTool"));
        Button button = new Button(bundle.getString("Apply"));
        button.setOnAction((actionEvent) -> {
            try {
                handleApply(actionEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        getChildren().addAll(separator, label, button);
    }

    public static VBox getInstance(ToolController controller) {
        if(instance == null) instance = new CropTool(controller);
        return instance;
    }

    public void handleApply(ActionEvent actionEvent) throws Exception {
        ImagePane imagePane = toolController.getActivePaneProperty();
        BufferedImage image = toolController.getBufferedImage();
        CropRectangle cropRectangle = toolController.getActivePaneProperty().getCropRectangle();
        BufferedImage croppedImage = ImageUtils.crop(image, cropRectangle);
        HistogramPane histogramPane = toolController.getActivePaneProperty().getHistogramPane();
        String tabName = imagePane.getFile().getName() + " " + bundle.getString("cropped");
        ImagePane newImagePane = new ImagePane(histogramPane, croppedImage);
        ((MainController)toolController).attachTab(new ImageTab(newImagePane, tabName));
    }

}
