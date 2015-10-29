package pl.sotomski.apoz.tools;

import javafx.scene.layout.VBox;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.ResourceBundle;

/**
 * Created by sotomski on 29/10/15.
 */
public abstract class Tool extends VBox {

    protected ToolController toolController;
    protected static Tool instance;
    protected ResourceBundle bundle;
    protected BufferedImage originalImage;

    protected Tool(ToolController toolController) {
        this.toolController = toolController;
        bundle = toolController.getBundle();
        originalImage = ImageUtils.deepCopy(toolController.getBufferedImage());
    }

}
