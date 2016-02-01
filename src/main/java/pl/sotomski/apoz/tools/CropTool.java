package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import pl.sotomski.apoz.controllers.MainController;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.nodes.CropRectangle;
import pl.sotomski.apoz.nodes.ChartsPane;
import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.nodes.ImageTab;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;

public class CropTool extends Tool {

    private static Tool instance;

    protected CropTool(ToolController controller) {
        super(controller);
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("CropTool"));
        System.out.println(separator);
        System.out.println(applyCancelBtns);
        System.out.println(label);
        getChildren().addAll(
                separator,
                label,
                applyCancelBtns);
    }

    private void handleCancel(ActionEvent actionEvent) {
        ((MainController)toolController).disableTools();
    }

    public static Tool getInstance(ToolController controller) {
        if(instance == null) instance = new CropTool(controller);
        return instance;
    }

    @Override
    public void handleApply(ActionEvent actionEvent) {
        ImagePane imagePane = toolController.getActivePane();
        BufferedImage image = toolController.getBufferedImage();
        CropRectangle cropRectangle = toolController.getActivePane().getCropRectangle();
        BufferedImage croppedImage = ImageUtils.crop(image, cropRectangle);
        ChartsPane histogramPane = toolController.getActivePane().getHistogramPane();
        String oldName = imagePane.getName();
        String cropStr = histogramPane.getBundle().getString("cropped");
        String tabName;
        int cropLength = cropStr.length();
        int indexOfCrop = oldName.indexOf(cropStr);
        if (indexOfCrop < 0) tabName = oldName + " " + cropStr;
        else {
            int cropNumber;
            try {
                cropNumber = Integer.valueOf(oldName.substring(indexOfCrop + cropLength + 1));
                ++cropNumber;
            } catch (NumberFormatException|StringIndexOutOfBoundsException e) {
                cropNumber = 1;
            }
            tabName = oldName.substring(0, indexOfCrop-1) + " " + cropStr + " " + cropNumber;
        }
        ImagePane newImagePane = new ImagePane(histogramPane, croppedImage, tabName);
        disableTool();
        ((MainController)toolController).attachTab(new ImageTab(newImagePane));
    }

}
