package pl.sotomski.apoz;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import pl.sotomski.apoz.utils.HistogramManager;

import java.awt.image.BufferedImage;
import java.util.Observable;

/**
 * Created by sotomski on 30/09/15.
 */
public class ImageObservable extends Observable  {
    private BufferedImage bufferedImage;
    int channels;
    int depth;
    int zoom;

    Label labelWidth;
    Label labelHeight;
    Label labelDepth;
    HistogramManager histogram;

    public ImageObservable() {

    }

    public ImageObservable(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        this.zoom = 100;
    }

    public ImageObservable(Label labelDepth, Label labelHeight, Label labelWidth, HistogramManager histogram) {
        this.labelDepth = labelDepth;
        this.labelHeight = labelHeight;
        this.labelWidth = labelWidth;
        this.histogram = histogram;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public int getChannels() {
        return channels;
    }

    public int getDepth() {
        return depth;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        channels = bufferedImage.getColorModel().getColorSpace().getNumComponents();
        depth = bufferedImage.getColorModel().getPixelSize();
        labelDepth.setText("Depth: " + depth + "bit, "+channels+" channel");
        labelWidth.setText("Width: " +bufferedImage.getWidth());
        labelHeight.setText("Heigth: " +bufferedImage.getHeight());
        histogram.update();
    }

    public void zoomIn() {
        zoom *=1.25;

    }

    public Image getFxImage() {
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }
}
