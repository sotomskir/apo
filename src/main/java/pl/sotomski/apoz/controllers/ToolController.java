package pl.sotomski.apoz.controllers;

import pl.sotomski.apoz.nodes.CropRectangle;
import pl.sotomski.apoz.nodes.ImagePane;

import java.awt.image.BufferedImage;
import java.util.ResourceBundle;

public interface ToolController {
    BufferedImage getBufferedImage();
    ResourceBundle getBundle();
    ImagePane getActivePane();
    CropRectangle getCropRectangle();
}
