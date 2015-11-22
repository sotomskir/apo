package pl.sotomski.apoz.utils;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.Arrays;

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

    public static int[] getPixelNeighbors(byte[] imageData, int i, int channels, int width, int height, int neighborhood) {
        if (neighborhood == 0) { // square neighborhood
            int[] pixels = new int[9];
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

        } else if (neighborhood == 1) { // diamond neighborhood
            int[] pixels = new int[5];
            try {
                pixels[0] = imageData[i - width * channels] & 0xFF;
                pixels[1] = imageData[i - channels] & 0xFF;
                pixels[2] = imageData[i] & 0xFF;
                pixels[3] = imageData[i + channels] & 0xFF;
                pixels[4] = imageData[i + width * channels] & 0xFF;
            } catch (IndexOutOfBoundsException e) {

            }
            return pixels;
        } else throw new IllegalArgumentException("Illegal neighborhood argument value");
    }

    public static void applyMask(BufferedImage bi, int[] mask) {
        int width = bi.getWidth();
        int height = bi.getHeight();
        int channels = bi.getColorModel().getNumComponents();
        byte[] a = ((DataBufferByte)bi.getRaster().getDataBuffer()).getData();
        double min = 255, max = 0;
        int multiplier = 0;
        boolean sharpening = false;
        for (int p = 0; p < 9; ++p) multiplier += mask[p];
        if (multiplier == 0) {
            sharpening = true;
            multiplier = 1;
        }

        int[] b = new int[a.length];
        for (int i = width*channels; i < width*height*channels-width*channels; ++i) {
            int[] pixels = getPixelNeighbors(a, i, channels, width, height, 0);
            double sum = 0;
            for (int p = 0; p < 9; ++p) sum += pixels[p] * mask[p];
            int v = (int) (sum / multiplier);
            if (v < min) min = v;
            if (v > max) max = v;
            b[i] = v;
        }

        if (sharpening) {
            for (int i = 0; i < a.length; ++i) {
                b[i] = (b[i] < 0 ? 0 : b[i] > 255 ? 255 : b[i]); // skalowanie przez obcinanie
                b[i] += a[i] & 0xFF;
                a[i] = (byte) (b[i] < 0 ? 0 : b[i] > 255 ? 255 : b[i]); // skalowanie przez obcinanie
//                a[i] = (byte) (((b[i] - min) / (max - min)) * 255); // skalowanie proporcjonalne
            }
        } else {
            for (int i = 0; i < a.length; ++i) {
                a[i] = (byte) (b[i] < 0 ? 0 : b[i] > 255 ? 255 : b[i]); // skalowanie przez obcinanie
            }
        }

    }

    public static void rewriteImage(BufferedImage previousImage, BufferedImage image) {
        byte[] a = ((DataBufferByte)previousImage.getRaster().getDataBuffer()).getData();
        byte[] b = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
        System.arraycopy(a, 0, b, 0, a.length);
    }

    public static void dilatation(BufferedImage image, int neighborhood) {
        byte[] a = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
        byte[] b = new byte[a.length];
        int channels = image.getColorModel().getNumComponents();
        int width    = image.getWidth();
        int height   = image.getHeight();
        int[] pixels;
        int min;
        for (int i = 0; i < a.length; ++i) {
            pixels = getPixelNeighbors(a, i, channels, width, height, neighborhood);
            min = 255;
            for (int pixel : pixels) if (pixel < min) min = pixel;
            b[i] = (byte) min;
        }
        System.arraycopy(b, 0, a, 0, a.length);
    }

    public static void erosion(BufferedImage image, int neighborhood) {
        byte[] a = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
        byte[] b = new byte[a.length];
        int channels = image.getColorModel().getNumComponents();
        int width    = image.getWidth();
        int height   = image.getHeight();
        int[] pixels;
        int max;
        for (int i = 0; i < a.length; ++i) {
            pixels = getPixelNeighbors(a, i, channels, width, height, neighborhood);
            max = 0;
            for (int pixel : pixels) if (pixel > max) max = pixel;
            b[i] = (byte) max;
        }
        System.arraycopy(b, 0, a, 0, a.length);
    }

    public static void outline(BufferedImage image, int neighborhood) {
        BufferedImage before = deepCopy(image);
        erosion(image, neighborhood);
        substract(image, before);
    }

    public static void close(BufferedImage image, int neighborhood) {
        dilatation(image, neighborhood);
        erosion(image, neighborhood);
    }

    public static void open(BufferedImage image, int neighborhood) {
        erosion(image, neighborhood);
        dilatation(image, neighborhood);
    }

    public static BufferedImage substract(BufferedImage image1, BufferedImage image2) {
        //TODO
        byte[] a = ((DataBufferByte) image1.getRaster().getDataBuffer()).getData();
        byte[] b = ((DataBufferByte) image2.getRaster().getDataBuffer()).getData();
        int channels = image1.getColorModel().getNumComponents();
        int width = image1.getWidth();
        int height = image1.getHeight();
        int valueA, valueB;
        for (int i = 0; i < a.length; ++i) {
            valueA = a[i] & 0xFF;
            valueB = b[i] & 0xFF;
            valueA = valueA - valueB;
            if (valueA < 0) valueA = 0;
            a[i] = (byte) valueA;
        }
        return image1;
    }

    public static int[] iToXY(int i, int width, int channels) {
        int[] c = new int[2];
        c[0] = (i % (width * channels)) / channels;
        c[1] =  i / (width * channels);
        return c;
    }

    public static int xyToI(int x, int y, int width, int channels) {
        return y * width * channels + (x * channels);
    }

    /**
     * Transforms image data index of image1 to index of image2
     * @param i1 index of image1
     * @param width1 width of image1
     * @param width2 width of image2
     * @param channels channels
     * @return index of image2 data with the same x,y coordinates as i1
     */
    public static int i1ToI2(int i1, int width1, int width2, int channels) {
        int y = i1 / (width1 * channels);
        return i1 + (y * (width2 - width1) * channels);
    }

    public static BufferedImage binaryOperation(BufferedImage image1, BufferedImage image2, String operation) {
        //TODO
        int channels = image1.getColorModel().getNumComponents();
        if (channels > 1) image1 = rgbToGrayscale(image1);
        channels = image2.getColorModel().getNumComponents();
        if (channels > 1) image2 = rgbToGrayscale(image2);
        channels = 1;
        byte[] a = ((DataBufferByte) image1.getRaster().getDataBuffer()).getData();
        byte[] b = ((DataBufferByte) image2.getRaster().getDataBuffer()).getData();
        int width1 = image1.getWidth();
        int width2 = image2.getWidth();
        int width  = image1.getWidth() > image2.getWidth() ? image1.getWidth() : image2.getWidth();
        int height = image1.getHeight() > image2.getHeight() ? image1.getHeight() : image2.getHeight();
        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        byte[] d = ((DataBufferByte) resultImage.getRaster().getDataBuffer()).getData();
        int[] c = new int[d.length];
        Arrays.fill(c, 255);
        int i2;
        for (int i = 0; i < a.length; ++i) {
            i2 = i1ToI2(i, width1, width, channels);
            c[i2] = a[i] & 0xFF;
        }

        switch (operation) {
            case "add":
                for (int i = 0; i < b.length; ++i) {
                    i2 = i1ToI2(i, width2, width, channels);
                    c[i2] += b[i] & 0xFF;
                }
                break;
            case "sub":
                for (int i = 0; i < b.length; ++i) {
                    i2 = i1ToI2(i, width2, width, channels);
                    c[i2] -= b[i] & 0xFF;
                }
                break;
            case "multiply":
                for (int i = 0; i < b.length; ++i) {
                    i2 = i1ToI2(i, width2, width, channels);
                    c[i2] *= b[i] & 0xFF;
                }
                break;
            case "divide":
                for (int i = 0; i < b.length; ++i) {
                    i2 = i1ToI2(i, width2, width, channels);
                    c[i2] /= ((b[i] & 0xFF) == 0 ? 1 : b[i] & 0xFF);
                }
                break;
            case "AND":
                for (int i = 0; i < b.length; ++i) {
                    i2 = i1ToI2(i, width2, width, channels);
                    c[i2] &= b[i] & 0xFF;
                }
                break;
            case "OR":
                for (int i = 0; i < b.length; ++i) {
                    i2 = i1ToI2(i, width2, width, channels);
                    c[i2] |= b[i] & 0xFF;
                }
                break;
            case "XOR":
                for (int i = 0; i < b.length; ++i) {
                    i2 = i1ToI2(i, width2, width, channels);
                    c[i2] ^= b[i] & 0xFF;
                }
                break;
        }

        for (int i = 0; i < d.length; ++i) {
            d[i] = (byte) (c[i] < 0 ? 0 : c[i] > 255 ? 255 : c[i]);
        }

        return resultImage;
    }

    public static byte[] getImageData(BufferedImage image) {
        return ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    }

    public static int[] getPixelNeighbors(byte[] imageData, int i, int channels, int width, int diameter) {
        int[] pixels = new int[diameter*diameter];
        int c = 0;
        int maxOffset = (diameter - 1) / 2;
        int minOffset = -maxOffset;
        for (int yOffset = minOffset; yOffset <= maxOffset; ++yOffset) {
            for (int xOffset = minOffset; xOffset <= maxOffset; ++xOffset) {
                pixels[c] = imageData[i + width * channels * yOffset + xOffset] & 0xFF;
                ++c;
            }
        }
        return pixels;
    }

    public static void medianOperation(BufferedImage image, int diameter) {
        byte[] a = getImageData(image);
        byte[] b = new byte[a.length];
        int width = image.getWidth();
        int channels = image.getColorModel().getNumComponents();
        int offset = (diameter - 1) / 2 * width * channels + ((diameter - 1) / 2);
        for (int i = offset; i < a.length - offset; ++i) {
            int[] pixels = getPixelNeighbors(a, i, channels, width, diameter);
            Arrays.sort(pixels);
            b[i] = (byte) pixels[diameter/2];
        }
        System.arraycopy(b, 0, a, 0, a.length);
    }

    public static void logicalFilter(BufferedImage image, int direction) {
        byte[] a = getImageData(image);
        byte[] b = new byte[a.length];
        int width = image.getWidth();
        int channels = image.getColorModel().getNumComponents();
        int offset =width * channels + channels;
        switch (direction) {
            case 0:
                for (int i = offset; i < a.length - offset; ++i) {
                    int[] pixels = getPixelNeighbors(a, i, channels, width, 0, 0);
                    b[i] = pixels[1] == pixels[7] ? (byte) pixels[1] : a[i];
                }
                break;
            case 1:
                for (int i = offset; i < a.length - offset; ++i) {
                    int[] pixels = getPixelNeighbors(a, i, channels, width, 0, 0);
                    b[i] = pixels[0] == pixels[8] ? (byte) pixels[0] : a[i];
                }
                break;
            case 2:
                for (int i = offset; i < a.length - offset; ++i) {
                    int[] pixels = getPixelNeighbors(a, i, channels, width, 0, 0);
                    b[i] = pixels[3] == pixels[5] ? (byte) pixels[3] : a[i];
                }
                break;
            case 3:
                for (int i = offset; i < a.length - offset; ++i) {
                    int[] pixels = getPixelNeighbors(a, i, channels, width, 0, 0);
                    b[i] = pixels[2] == pixels[6] ? (byte) pixels[2] : a[i];
                }
                break;
            default:
                throw new IllegalArgumentException("Illegal direction. Shoult be 0, 1, 2 or 3");
        }
        System.arraycopy(b, 0, a, 0, a.length);
    }
}

