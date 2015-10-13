package pl.sotomski.apoz.tools;

import pl.sotomski.apoz.ImagePane;
import pl.sotomski.apoz.ImageTab;
import pl.sotomski.apoz.utils.HistogramChart;

import java.awt.image.BufferedImage;

/**
 * Created by sotomski on 27/09/15.
 */
public interface ToolController {
    public HistogramChart getHistogramChart();
    public BufferedImage getBufferedImage();
    public void setBufferedImage(BufferedImage image);

    ImagePane getActivePaneProperty();
}
