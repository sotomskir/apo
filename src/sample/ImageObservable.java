package sample;

import javafx.scene.control.Label;

import java.awt.image.BufferedImage;
import java.util.Observable;

/**
 * Created by sotomski on 30/09/15.
 */
public class ImageObservable extends Observable  {
    private BufferedImage bufferedImage;

    int channels;
    int depth;

    Label labelWidth;
    Label labelHeight;
    Label labelDepth;
    public ImageObservable() {
    }

    public ImageObservable(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public ImageObservable(Label labelDepth, Label labelHeight, Label labelWidth) {
        this.labelDepth = labelDepth;
        this.labelHeight = labelHeight;
        this.labelWidth = labelWidth;
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
        channels = +bufferedImage.getColorModel().getColorSpace().getNumComponents();
        depth = bufferedImage.getColorModel().getPixelSize();
        labelDepth.setText("Depth: " + depth + "bit, "+channels+" channel");
        labelWidth.setText("Width: " +bufferedImage.getWidth());
        labelHeight.setText("Heigth: " +bufferedImage.getHeight());
    }
}
