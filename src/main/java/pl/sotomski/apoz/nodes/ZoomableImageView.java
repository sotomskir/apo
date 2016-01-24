package pl.sotomski.apoz.nodes;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.*;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;

public class ZoomableImageView extends ImageView {

    private BufferedImage image;
    private DoubleProperty zoomFactor = new SimpleDoubleProperty(1);

    public ZoomableImageView() {
        super();
        zoomFactor.addListener(e -> refresh());
    }

    public void refresh() {
        setImage(SwingFXUtils.toFXImage(resampleImage(image, zoomFactor.doubleValue()), null));
    }

    private BufferedImage resampleImage(BufferedImage image, double zoomFactor) {
        final int W = image.getWidth();
        final int H = image.getHeight();
        final double S = zoomFactor;
        BufferedImage output = new BufferedImage((int) (W * S), (int) (H * S), image.getType());

        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                final int[] pixel = ImageUtils.getPixel(image, x, y);
                for (int dy = 0; dy < S; dy++) {
                    for (int dx = 0; dx < S; dx++)
                        //TODO
                        try {
                            ImageUtils.setPixel(output, (int) (x * S + dx), (int) (y * S + dy), pixel);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                }
            }
        }
        return output;
    }

    public ZoomableImageView(BufferedImage image) {
        this();
        this.image = image;
        setImage(SwingFXUtils.toFXImage(image, null));
    }

    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor.setValue(zoomFactor);
    }

    public double getZoomFactor() {
        return zoomFactor.get();
    }

    public void setBufferedImage(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getBufferedImage() {
        return image;
    }

    public DoubleProperty zoomFactorProperty() {
        return zoomFactor;
    }

}