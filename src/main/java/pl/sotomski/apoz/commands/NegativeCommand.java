package pl.sotomski.apoz.commands;

import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.Histogram;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Created by sotomski on 21/10/15.
 */
public class NegativeCommand extends UndoableCommand implements Command {

    public NegativeCommand(ImagePane image) {
        super(image);
    }

    @Override
    public void execute() {
        BufferedImage image = imagePane.getImage();
        Histogram histogram = new Histogram(image);
        int width = image.getWidth();
        int height = image.getHeight();
        final byte[] a = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        for (int p = width*height*histogram.getChannels()-histogram.getChannels(); p>=0; p-- ) {
            a[p] = (byte) (histogram.getLevels()-1 - (a[p] & 0xFF));
        }
    }
}
