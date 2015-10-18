package pl.sotomski.apoz.tools;

import pl.sotomski.apoz.ImagePane;
import pl.sotomski.apoz.utils.HistogramManager;

import java.awt.image.BufferedImage;
import java.util.ResourceBundle;

/**
 * Created by sotomski on 27/09/15.
 */
public interface ToolController {
    HistogramManager getHistogramChart();
    BufferedImage getBufferedImage();
    void setBufferedImage(BufferedImage image);
    ResourceBundle getBundle();
    ImagePane getActivePaneProperty();
}
