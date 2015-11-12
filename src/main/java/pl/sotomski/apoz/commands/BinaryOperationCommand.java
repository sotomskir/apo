package pl.sotomski.apoz.commands;

import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.Histogram;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Random;

/**
 * Created by sotomski on 17/10/15.
 */
public class BinaryOperationCommand extends UndoableCommand implements Command {
    private String method;
    private BufferedImage secondImage;

    public BinaryOperationCommand(ImagePane imagePane, BufferedImage secondImage, String method) throws Exception {
        super(imagePane);
        this.method = method;
        this.secondImage = secondImage;
    }

    @Override
    public void execute() {
        System.out.println(method);
        if ("add".equals(method)) {
            add(imagePane.getImage(), secondImage);
            imagePane.refresh();
        } else if ("sub".equals(method)) {
            sub(imagePane.getImage(), secondImage);
            imagePane.refresh();
        } else if ("multiply".equals(method)) {
            multiply(imagePane.getImage(), secondImage);
            imagePane.refresh();
        } else if ("divide".equals(method)) {
            divide(imagePane.getImage(), secondImage);
            imagePane.refresh();
        } else if ("AND".equals(method)) {
            and(imagePane.getImage(), secondImage);
            imagePane.refresh();
        } else if("OR".equals(method)) {
            or(imagePane.getImage(), secondImage);
            imagePane.refresh();
        } else if("XOR".equals(method)) {
            xor(imagePane.getImage(), secondImage);
            imagePane.refresh();
        }
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
