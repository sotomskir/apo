package pl.sotomski.apoz.commands;

import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.Histogram;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Created by sotomski on 21/10/15.
 */
public class TresholdCommand extends UndoableCommand implements Command {
    private int treshold;

    public TresholdCommand(ImagePane imagePane, int treshold) {
        super(imagePane);
        this.treshold = treshold;
    }

    @Override
    public void execute() {
        BufferedImage bi = imagePane.getImage();
        if(bi.getColorModel().getNumComponents()>1) bi = ImageUtils.rgbToGrayscale(bi);
        Histogram histogram = new Histogram(bi);
        int width = bi.getWidth();
        int height = bi.getHeight();
        BufferedImage binaryImage = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        final byte[] a = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        final byte[] b = ((DataBufferByte) binaryImage.getRaster().getDataBuffer()).getData();
//        int bp = b.length-1;
        for (int p = width*height-1; p>=0; p-- ) {
            //TODO
            b[p] = (byte) ((a[p] & 0xFF) > treshold?255:0);
//            b[bp] = 0x00;
//            for (int i=0; i<8; ++i ) {
//                if ((a[p-i] & 0xFF) > treshold) b[bp] = (byte) (b[bp] & 0x01);
//                b[bp]= (byte) (b[bp] >> 1);
//            }
//            --bp;

        }
        imagePane.setImage(binaryImage);
        imagePane.refresh();
    }
}
