package sample;

import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;

/**
 * Created by sotomski on 27/09/15.
 */
public interface ToolController {
    public Histogram getHistogram();
    public BufferedImage getBufferedImage();
    public ImageView getImageCanvas();
    public void setBufferedImage(BufferedImage image);
}
