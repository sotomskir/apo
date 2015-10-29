package pl.sotomski.apoz.tools;

import pl.sotomski.apoz.nodes.ImagePane;

import java.awt.image.BufferedImage;
import java.util.ResourceBundle;

public interface ToolController {
    BufferedImage getBufferedImage();
    ResourceBundle getBundle();
    ImagePane getActivePaneProperty();
}
