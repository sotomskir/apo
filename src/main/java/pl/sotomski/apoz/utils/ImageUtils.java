package pl.sotomski.apoz.utils;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

/**
 * Created by sotomski on 01/10/15.
 */
public class ImageUtils {
    private static final double rPerc = 0.2;
    private static final double gPerc = 0.6;
    private static final double bPerc = 0.2;

    public static BufferedImage rgbToGrayscale(BufferedImage bufferedImage){
        int channels = bufferedImage.getColorModel().getNumComponents();
        if (channels==1) return bufferedImage;

        BufferedImage grayscaleImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        final byte[] a = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        final byte[] gray = ((DataBufferByte) grayscaleImage.getRaster().getDataBuffer()).getData();
        for (int p = 0; p < width * height * channels; p += channels) {

            int r = a[p+2] & 0xFF;
            int g = a[p+1] & 0xFF;
            int b = a[p] & 0xFF;
            gray[p/channels] = (byte) ((r * rPerc) + (g * gPerc) + (b * bPerc));

        }
        return grayscaleImage;
    }

    public static int getR(int rgb) {
        return (rgb >> 16) & 0xFF;
    }
    public static int getG(int rgb) {
        return (rgb >> 8) & 0xFF;
    }
    public static int getB(int rgb) {
        return rgb & 0xFF;
    }

    public static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public static BufferedImage applyLUT(BufferedImage image, int[] lut) {
        BufferedImage grayBI;
        if(image.getColorModel().getNumComponents()>1) grayBI = ImageUtils.rgbToGrayscale(image);
        else grayBI = ImageUtils.deepCopy(image);
        int width = grayBI.getWidth();
        int height = grayBI.getHeight();
        BufferedImage binaryImage = new BufferedImage(grayBI.getWidth(), grayBI.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        final byte[] a = ((DataBufferByte) grayBI.getRaster().getDataBuffer()).getData();
        final byte[] b = ((DataBufferByte) binaryImage.getRaster().getDataBuffer()).getData();
        for (int p = width*height-1; p>=0; p-- ) b[p] = (byte) (lut[a[p] & 0xFF]);
        return binaryImage;
    }

    public static int[] getPixelNeighbors(byte[] imageData, int i, int channels, int width, int height) {
        int[] pixels = new int[9*channels];
        try {
            pixels[0] = imageData[i - width * channels - channels] & 0xFF;
            pixels[1] = imageData[i - width * channels] & 0xFF;
            pixels[2] = imageData[i - width * channels + channels] & 0xFF;
            pixels[3] = imageData[i - channels] & 0xFF;
            pixels[4] = imageData[i] & 0xFF;
            pixels[5] = imageData[i + channels] & 0xFF;
            pixels[6] = imageData[i + width * channels - channels] & 0xFF;
            pixels[7] = imageData[i + width * channels] & 0xFF;
            pixels[8] = imageData[i + width * channels + channels] & 0xFF;
        } catch (IndexOutOfBoundsException e) {

        }
        return pixels;
    }

    public static void applyMask(BufferedImage bi, int[] mask) {
        int width = bi.getWidth();
        int height = bi.getHeight();
        int channels = bi.getColorModel().getNumComponents();
        byte[] a = ((DataBufferByte)bi.getRaster().getDataBuffer()).getData();
        double min = 255, max = 0;
        int multiplier = 0;
        for (int p = 0; p < 9; ++p) multiplier += mask[p];
        if (multiplier == 0) multiplier = 1;

        int[] b = new int[a.length];
        for (int i = width*channels; i < width*height*channels-width*channels; ++i) {
            int[] pixels = getPixelNeighbors(a, i, channels, width, height);
            int sum = 0;
            for (int p = 0; p < 9; ++p) sum += pixels[p] * mask[p];
            int v = (sum / multiplier);
            if (v < min) min = v;
            if (v > max) max = v;
            b[i] = v;
        }

        // skalowanie przy wyostrzaniu
        if (multiplier == 1) {
            for (int i = 0; i < width * height * channels; ++i)
                a[i] = (byte) (((b[i] - min) / (max - min)) * 255);
        }
    }
}
