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

    public LUTCommand(ImagePane imagePane, int[] LUT) {
        super(imagePane);
        this.LUT = LUT;
    }

    @Override
    public void execute() {
        BufferedImage bi = imagePane.getImage();
        if(bi.getColorModel().getNumComponents()>1) bi = ImageUtils.rgbToGrayscale(bi);
        int width = bi.getWidth();
        int height = bi.getHeight();
        BufferedImage binaryImage = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        final byte[] a = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        final byte[] b = ((DataBufferByte) binaryImage.getRaster().getDataBuffer()).getData();
        for (int p = width*height-1; p>=0; p-- ) b[p] = (byte) (LUT[a[p] & 0xFF]);
        imagePane.setImage(binaryImage);
        imagePane.refresh();
    }
}
