package pl.sotomski.apoz.commands;

import pl.sotomski.apoz.nodes.ImagePane;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Created by sotomski on 17/10/15.
 */
public class BinaryOperationCommand extends UndoableCommand implements Command {
    private String method;
    private BufferedImage secondImage;

    public BinaryOperationCommand(ImagePane image, BufferedImage secondImage, String method) throws Exception {
        super(image);
        this.method = method;
        this.secondImage = secondImage;
    }

    @Override
    public void execute() {
        BufferedImage image = imagePane.getImage();
        System.out.println(method);
        if ("add".equals(method)) add(image, secondImage);
        else if ("sub".equals(method)) sub(image, secondImage);
        else if ("multiply".equals(method)) multiply(image, secondImage);
        else if ("divide".equals(method)) divide(image, secondImage);
        else if ("AND".equals(method)) and(image, secondImage);
        else if("OR".equals(method)) or(image, secondImage);
        else if("XOR".equals(method)) xor(image, secondImage);
    }

    private void divide(BufferedImage image, BufferedImage secondImage) {
        //TODO

    }

    private void and(BufferedImage image, BufferedImage secondImage) {
        //TODO

    }

    private void or(BufferedImage image, BufferedImage secondImage) {
        //TODO
    }

    private void xor(BufferedImage image, BufferedImage secondImage) {
        //TODO
    }

    private void multiply(BufferedImage image, BufferedImage secondImage) {
        //TODO
    }

    private void sub(BufferedImage image, BufferedImage secondImage) {
        //TODO
    }

    private void add(BufferedImage image, BufferedImage secondImage) {
        //TODO
        int width = image.getWidth();
        int height = image.getHeight();
        final byte[] a = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final byte[] b = ((DataBufferByte) secondImage.getRaster().getDataBuffer()).getData();
        final int channels = image.getColorModel().getNumComponents();
        for (int p = 0; p < width*height*channels; p+=channels) {
            for (int ch = 0; ch < channels; ++ch) a[p] = (byte) (((a[p] & 0xFF) + (b[p] & 0xFF)) / 2);
        }
    }
}
