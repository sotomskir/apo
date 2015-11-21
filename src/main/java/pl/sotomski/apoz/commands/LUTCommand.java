package pl.sotomski.apoz.commands;

import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Created by sotomski on 21/10/15.
 */
public class LUTCommand extends UndoableCommand implements Command {
    private int[] LUT;

    public LUTCommand(ImagePane image, int[] LUT) {
        super(image);
        this.LUT = LUT;
    }

    @Override
    public void execute() {
        BufferedImage image = imagePane.getImage();
        if (image.getColorModel().getNumComponents() > 1) ImageUtils.rgbToGrayscale(image);
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage binaryImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        final byte[] a = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final byte[] b = ((DataBufferByte) binaryImage.getRaster().getDataBuffer()).getData();
        for (int p = width * height - 1; p >= 0; p--) b[p] = (byte) (LUT[a[p] & 0xFF]);
    }
}
