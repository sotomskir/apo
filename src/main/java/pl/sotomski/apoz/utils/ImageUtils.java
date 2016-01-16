package pl.sotomski.apoz.utils;

import com.google.common.primitives.Ints;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import pl.sotomski.apoz.nodes.CropRectangle;
import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.nodes.ProfileLine;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageUtils {
    private static final double rPerc = 0.2;
    private static final double gPerc = 0.6;
    private static final double bPerc = 0.2;

    public static int[][] getLineProfilePixels(BufferedImage image, ProfileLine line) {
        int channels = image.getColorModel().getNumComponents();
        int[][] points = line.getLinePoints();
        int[][] pixels = new int[points.length][channels];
        for (int i = 0; i < points.length; ++i) pixels[i] = getPixel(image, points[i][0], points[i][1]);
        return pixels;
    }

    public static BufferedImage rgbToGrayscale(BufferedImage bufferedImage){
        int channels = bufferedImage.getColorModel().getNumComponents();
        if (channels==1) return bufferedImage;

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        BufferedImage grayscaleImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        final byte[] a = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        final byte[] gray = ((DataBufferByte) grayscaleImage.getRaster().getDataBuffer()).getData();
        for (int p = 0; p < width * height * channels; p += channels) {
            double r = a[p+2] & 0xFF;
            double g = a[p+1] & 0xFF;
            double b = a[p] & 0xFF;
            gray[p/channels] = (byte) Math.round((r * rPerc) + (g * gPerc) + (b * bPerc));

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
        final byte[] a = getImageData(grayBI);
        final byte[] b = getImageData(binaryImage);
        for (int p = width*height-1; p>=0; p-- ) b[p] = (byte) (lut[a[p] & 0xFF]);
        return binaryImage;
    }

    /**
     * @deprecated
     */
    public static int[] get3x3Pixels(byte[] imageData, int i, int channels, int width, int diameter) {
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

    /**
     * @deprecated
     * @param bordersMethod    0 fill black
     *                         1 fill white
     *                         2 copy borders
     *                         3 use existing pixels
     *                         4 dont't change extreme pixels
     */
    public static int[] get3x3Pixels(byte[] imageData, int i, int channels, int width, int height, int bordersMethod, int neighborhood) {
        if (neighborhood == 0) { // square neighborhood
            int[] pixels = new int[9];
            try {
                int wdth = channels * width;
                int n = 0, w = 0, s = 0, e = 0, x = i % wdth;
                if (i < wdth) n = wdth;
                if (i > imageData.length - wdth - channels) s = -wdth;
                if (x <= channels) w = channels;
                if (x >= wdth - channels) e = -channels;
                pixels[0] = imageData[i + n + w - width * channels - channels] & 0xFF;
                pixels[1] = imageData[i + n - width * channels] & 0xFF;
                pixels[2] = imageData[i + n + e - width * channels + channels] & 0xFF;
                pixels[3] = imageData[i + w - channels] & 0xFF;
                pixels[4] = imageData[i] & 0xFF;
                pixels[5] = imageData[i + e + channels] & 0xFF;
                pixels[6] = imageData[i + s + w + width * channels - channels] & 0xFF;
                pixels[7] = imageData[i + s + width * channels] & 0xFF;
                pixels[8] = imageData[i + s + e + width * channels + channels] & 0xFF;
                return pixels;
            } catch (IndexOutOfBoundsException e) {
                return pixels;
            }

        } else if (neighborhood == 1) { // diamond neighborhood
            int[] pixels = new int[5];
            try {
                int wdth = channels * width;
                int n = 0, w = 0, s = 0, e = 0, x = i % wdth;
                if (i < wdth) n = wdth;
                if (i > imageData.length - wdth) s = -wdth;
                if (x <= channels) w = channels;
                if (x >= wdth - channels) e = -channels;
                pixels[0] = imageData[i + n - width * channels] & 0xFF;
                pixels[1] = imageData[i + e - channels] & 0xFF;
                pixels[2] = imageData[i] & 0xFF;
                pixels[3] = imageData[i + w + channels] & 0xFF;
                pixels[4] = imageData[i + s + width * channels] & 0xFF;
                return pixels;
            } catch (IndexOutOfBoundsException e) {
                return pixels;
            }
        } else throw new IllegalArgumentException("Illegal neighborhood argument value");

    }


    /**
     * @deprecated
     * @param bordersMethod    0 fill black
     *                         1 fill white
     *                         2 copy borders
     *                         3 use existing pixels
     *                         4 dont't change extreme pixels

     */
    public static int[] get5x5Pixels(byte[] imageData, int i, int channels, int width, int height, int bordersMethod) {
        int[] pixels = new int[25];
        int wdth = channels * width;
        int[] xy = iToXY(i, width, channels);
        int n = 0, w = 0, s = 0, e = 0, x = i % wdth;
        if (i < wdth * 2) n = (i / wdth); //north
        if (i > imageData.length - 2 * wdth - channels) s = ((imageData.length - i) / wdth + 1); //south
        if (x < channels * 2) w = x; //west
        if (x > wdth - channels * 2) e = (wdth - x); //east
        final int closerFactor = 2 * channels;
        final int yShift = wdth * channels;
        final int xShift = channels;
        pixels[0] =  imageData[i + n + w - yShift*2 - xShift*2] & 0xFF;
        pixels[1] =  imageData[i + n + w / closerFactor - yShift*2 - xShift] & 0xFF;
        pixels[2] =  imageData[i + n - yShift*2] & 0xFF;
        pixels[3] =  imageData[i + n + e / closerFactor - yShift*2 + xShift] & 0xFF;
        pixels[4] =  imageData[i + n + e - yShift*2 + xShift*2] & 0xFF;
        pixels[5] =  imageData[i + n / closerFactor + w - yShift - xShift*2] & 0xFF;
        pixels[6] =  imageData[i + n / closerFactor + w / closerFactor - yShift - xShift] & 0xFF;
        pixels[7] =  imageData[i + n / closerFactor - yShift] & 0xFF;
        pixels[8] =  imageData[i + n / closerFactor + e / closerFactor - yShift + xShift] & 0xFF;
        pixels[9] =  imageData[i + n / closerFactor + e - yShift + xShift*2] & 0xFF;
        pixels[10] = imageData[i + w - xShift*2] & 0xFF;
        pixels[11] = imageData[i + w / closerFactor - xShift] & 0xFF;
        pixels[12] = imageData[i] & 0xFF;
        pixels[13] = imageData[i + e / closerFactor + xShift] & 0xFF;
        pixels[14] = imageData[i + e + xShift*2] & 0xFF;
        pixels[15] = imageData[i + s / closerFactor + w + yShift - xShift*2] & 0xFF;
        pixels[16] = imageData[i + s / closerFactor + w / closerFactor + yShift - xShift] & 0xFF;
        pixels[17] = imageData[i + s / closerFactor + yShift] & 0xFF;
        pixels[18] = imageData[i + s / closerFactor + e / closerFactor + yShift + xShift] & 0xFF;
        pixels[19] = imageData[i + s / closerFactor + e  + yShift + xShift*2] & 0xFF;
        pixels[20] = imageData[i + s + w + yShift*2 - xShift*2] & 0xFF;
        pixels[21] = imageData[i + s + w / closerFactor + yShift*2 - xShift] & 0xFF;
        pixels[22] = imageData[i + s + yShift*2] & 0xFF;
        pixels[23] = imageData[i + s + e / closerFactor + yShift*2 + xShift] & 0xFF;
        pixels[24] = imageData[i + s + e + yShift*2 + xShift*2] & 0xFF;
        return pixels;
    }

    private static int[] arrayAbsSum(int[] gx, int[] gy) {
        int[] r = new int[gx.length];
        for (int i = 0; i < gx.length; ++i) {
            r[i] = Math.round(Math.abs(gx[i]) + Math.abs(gy[i]));
        }
        return r;    }

    public static void linearFilter(BufferedImage bi, int[] mask, int bordersMethod) {
        byte[] a = getImageData(bi);

        //calculate mask multiplier
        int multiplier = arraySum(mask);
        boolean sharpening = false;
        if (multiplier == 0) {
            sharpening = true;
        }

        //filter image
        int[] b = filterImage(bi, mask, bordersMethod);

        //scale image levels
        scaleImage(a, b, sharpening);
    }

    /**
     *
     * @param bi
     * @param mask
     * @param bordersMethod    0 fill black
     *                         1 fill white
     *                         2 copy borders
     *                         3 use existing pixels
     *                         4 dont't change extreme pixels
     * @param scaleMethod 0 - bez skalowania, 3 - obcinanie, 1 - skalowanie proporcjonalne
     */
    public static void linearFilterWithScaling(BufferedImage bi, int[] mask, int bordersMethod, int scaleMethod) {
        byte[] a = getImageData(bi);

        //filter image
        int[] b = filterImage(bi, mask, bordersMethod);

        //scale image levels
        scaleImage(a, b, scaleMethod);
    }

    /**
     *
     * @param bi
     * @param masks
     * @param bordersMethod    0 fill black
     *                         1 fill white
     *                         2 copy borders
     *                         3 use existing pixels
     *                         4 dont't change extreme pixels
     * @param scalingMethod 0 - bez skalowania, 3 - obcinanie, 1 - skalowanie proporcjonalne
     * @param calcMethod
     */
    public static void gradientFilter(BufferedImage bi, int[][] masks, int bordersMethod, int scalingMethod, int calcMethod) {
        byte[] a = getImageData(bi);

        //filter image
        int[] Gx = filterImage(bi, masks[0], bordersMethod);
        int[] Gy = filterImage(bi, masks[1], bordersMethod);
        int[] b = calcMethod == 1 ? arrayAbsSum(Gx, Gy) : arraySqrtSumOfSquares(Gx, Gy);

        //scale image levels
        scaleImage(a, b, scalingMethod);
    }

    private static int[] arraySqrtSumOfSquares(int[] gx, int[] gy) {
        int[] r = new int[gx.length];
        for (int i = 0; i < gx.length; ++i) {
            r[i] = (int) Math.round(Math.sqrt(Math.pow(gx[i], 2) + Math.pow(gy[i], 2)));
        }
        return r;
    }

    /**
     * @param bi
     * @param mask
     * @param bordersMethod    0 fill black
     *                         1 fill white
     *                         2 copy borders
     *                         3 use existing pixels
     *                         4 dont't change extreme pixels

     * @return
     */

    public static int[] filterImage(BufferedImage bi, int[] mask, int bordersMethod) {
        int width = bi.getWidth();
        int channels = bi.getColorModel().getNumComponents();
        byte[] a = getImageData(bi);
        final int borderWidth = (int) (Math.sqrt(mask.length)/2);

        //create temporary image with extended borders
        ExtendedBordersImage tmpImage = new ExtendedBordersImage(bi, borderWidth, bordersMethod);

        //calculate mask multiplier
        int multiplier = arraySum(mask);
        if (multiplier == 0) multiplier = 1;

        //filter image
        int[] b = new int[a.length];
        for (int y = 0; y < bi.getHeight(); ++y)
            for (int x = 0; x < bi.getWidth(); ++x) {
                int[][] pixels;
                int diameter = (int) Math.sqrt(mask.length);
                pixels = getPixels(tmpImage, diameter, x, y, bordersMethod);
                double[] sum = new double[channels];
                int tmpMultiplier = multiplier;
                for (int p = 0; p < mask.length; ++p) {
                    if (pixels[p][0] == -1) {
                        tmpMultiplier -= mask[p];
                    } else {
                        for (int i = 0; i < channels; ++i)
                            sum[i] += pixels[p][i] * mask[p];
                    }
                }

                for (int i = 0; i < channels; ++i) {
                    double tmp = sum[i] / tmpMultiplier;
                    b[xyToI(x, y, width, channels) + i] = (int) Math.round(tmp);
                }

            }
        return b;
    }

    private static byte[] scaleImage(byte[] imageData, int[] inputArray, boolean sharpening) {
        //scale image levels
        if (sharpening) {
            for (int i = 0; i < imageData.length; ++i) {
                inputArray[i] = (inputArray[i] < 0 ? 0 : inputArray[i] > 255 ? 255 : inputArray[i]); // skalowanie przez obcinanie
                inputArray[i] += imageData[i] & 0xFF;
                imageData[i] = (byte) (inputArray[i] < 0 ? 0 : inputArray[i] > 255 ? 255 : inputArray[i]); // skalowanie przez obcinanie
            }
        } else {
            for (int i = 0; i < imageData.length; ++i) {
                imageData[i] = (byte) (inputArray[i] < 0 ? 0 : inputArray[i] > 255 ? 255 : inputArray[i]); // skalowanie przez obcinanie
            }
        }
        return imageData;
    }


    /**
     * scale image levels
     * @param imageData
     * @param inputArray
     * @param scaleMethod 0 - bez skalowania, 3 - obcinanie, 1 - skalowanie proporcjonalne
     * @return
     */
    public static byte[] scaleImage(byte[] imageData, int[] inputArray, int scaleMethod) {
        double min = Ints.min(inputArray);
        double max = Ints.max(inputArray);
        System.out.printf("MIN:\t%f MAX:\t%f", min, max);

        if (scaleMethod == 3) { // cut scaling
            for (int i = 0; i < imageData.length; ++i) {
                inputArray[i] = (inputArray[i] < 0 ? 0 : inputArray[i] > 255 ? 255 : inputArray[i]); // skalowanie przez obcinanie
                inputArray[i] += imageData[i] & 0xFF;
                imageData[i] = (byte) (inputArray[i] < 0 ? 0 : inputArray[i] > 255 ? 255 : inputArray[i]); // skalowanie przez obcinanie
            }

        } else if (scaleMethod == 1) { // proportional scaling
            for (int i = 0; i < imageData.length; ++i)
                imageData[i] = (byte) ((((double)inputArray[i] - min) / (max - min)) * 255); // skalowanie proporcjonalne

        } else if (scaleMethod == 2) { // 3 value scaling
            for (int i = 0; i < imageData.length; ++i)
                imageData[i] = (byte)(inputArray[i] < 0 ? 0 : inputArray[i] == 0 ? 128 : 255);

        } else if(scaleMethod == 0){ // no scaling
            for (int i = 0; i < imageData.length; ++i)
                imageData[i] = (byte)inputArray[i];
        }
        return imageData;
    }

    private static int arraySum(int[] mask) {
        int multiplier = 0;
        for (int aMask : mask) multiplier += aMask;
        return multiplier;
    }

    public static int[] getPixel(BufferedImage image, int x, int y) {
        byte[] data = getImageData(image);
        int channels = getImageChannels(image);
        int i = xyToI(x, y, image.getWidth(), channels);
        int[] ret = new int[channels];
        for (int c = 0; c < channels; ++c) ret[c] = data[i + c] & 0xFF;
        return ret;
    }

    public static void setPixel(BufferedImage image, int x, int y, int[] pixel) {
        byte[] data = getImageData(image);
        int channels = getImageChannels(image);
        int i = xyToI(x, y, image.getWidth(), channels);
        for (int c = 0; c < channels; ++c) data[i + c] = (byte) pixel[c];
    }

    public static void setPixel(BufferedImage image, int x, int y, int value) {
        int[] pixel = new int[image.getColorModel().getNumComponents()];
        Arrays.fill(pixel, value);
        byte[] data = getImageData(image);
        int channels = getImageChannels(image);
        int i = xyToI(x, y, image.getWidth(), channels);
        for (int c = 0; c < channels; ++c) data[i + c] = (byte) pixel[c];
    }

    public static int getImageChannels(BufferedImage image) {
        return image.getColorModel().getNumComponents();
    }

    /**
     *
     * @param image
     * @param diameter
     * @param x
     * @param y
     * @param bordersMethod 0 fill black
     *                      1 fill white
     *                      2 copy borders
     *                      3 use existing pixels
     *                      4 don't change extreme pixels
     * @return
     */
    private static int[][] getPixels(ExtendedBordersImage image, int diameter, int x, int y, int bordersMethod) {
        x+=diameter/2;
        y+=diameter/2;
        int[][] ret = new int[diameter*diameter][image.getColorModel().getNumComponents()];
        int i = 0;
        if (bordersMethod == 2 || bordersMethod == 1 || bordersMethod == 0) {
            for (int yi = -diameter/2; yi <= diameter/2; ++yi)
                for (int xi = -diameter/2; xi <= diameter/2; ++xi)
                    ret[i++] = getPixel(image, xi+x, yi+y);
            // 3 use existing pixels
        } else if (bordersMethod == 3) {
            for (int yi = -diameter/2; yi <= diameter/2; ++yi)
                for (int xi = -diameter/2; xi <= diameter/2; ++xi) {
                    int xx = xi + x;
                    int yy = yi + y;
                    if (xx == 0 || xx == image.getWidth()-1 || yy == 0 || yy == image.getHeight()-1) {
                        Arrays.fill(ret[i++], -1);
                    } else {
                        ret[i++] = getPixel(image, xx, yy);
                    }
                }
            // 4 don't change extreme pixels
        } else if (bordersMethod == 4) {
            for (int yi = -diameter/2; yi <= diameter/2; ++yi)
                for (int xi = -diameter/2; xi <= diameter/2; ++xi) {
                    if (x <= 1 || x >= image.getWidth()-2 || y <= 1 || y >= image.getHeight()-2) {
                        for (int[] array : ret) Arrays.fill(array, -1);
                        ret[ret.length/2] = getPixel(image, x, y);
                        return ret;
                    } else {
                        ret[i++] = getPixel(image, xi + x, yi + y);
                    }
                }
        }
        return ret;
    }

    public static void rewriteImage(BufferedImage srcImage, BufferedImage destImage, int xShift, int yShift) {
        byte[] a = getImageData(srcImage);
        byte[] b = getImageData(destImage);
        int channels = srcImage.getColorModel().getNumComponents();
        if (xShift == 0 && yShift == 0 && srcImage.getHeight() == destImage.getHeight() &&
                srcImage.getWidth() == destImage.getWidth()) System.arraycopy(a, 0, b, 0, a.length);
        else {
            int[] xy;
            int srcWidth = srcImage.getWidth();
            int dstWidth = destImage.getWidth();
            for (int i = 0; i < a.length; i+=channels) {
                xy = iToXY(i, srcWidth, channels);
                int i2 = xyToI(xy[0] + xShift, xy[1] + yShift, dstWidth, channels);
                for (int ch = 0; ch < channels; ++ch) b[i2+ch] = a[i+ch];
            }
        }
    }

    public static byte[] erode(byte[] a, int channels, int width, int height, int neighborhood) {
        int[] pixels;
        int min;
        byte[] b = new byte[a.length];
        for (int i = 0; i < a.length; ++i) {
            pixels = get3x3Pixels(a, i, channels, width, height, 9, neighborhood);
            min = 255;
            for (int pixel : pixels) if (pixel < min) min = pixel;
            b[i] = (byte) min;
        }
        return b;
    }

    public static void erode(BufferedImage image, int neighborhood) {
        byte[] a = getImageData(image);
        int channels = image.getColorModel().getNumComponents();
        int width    = image.getWidth();
        int height   = image.getHeight();
        byte[] b = erode(a, channels, width, height, neighborhood);
        System.arraycopy(b, 0, a, 0, a.length);
    }

    public static void dilate(BufferedImage image, int neighborhood) {
        byte[] a = getImageData(image);
        int channels = image.getColorModel().getNumComponents();
        int width    = image.getWidth();
        int height   = image.getHeight();
        byte[] b = dilate(a, channels, width, height, neighborhood);
        System.arraycopy(b, 0, a, 0, a.length);
    }

    public static byte[] dilate(byte[] a, int channels, int width, int height, int neighborhood) {
        byte[] b = new byte[a.length];
        int[] pixels;
        int max;
        for (int i = 0; i < a.length; ++i) {
            pixels = get3x3Pixels(a, i, channels, width, height, 9, neighborhood);
            max = 0;
            for (int pixel : pixels) if (pixel > max) max = pixel;
            b[i] = (byte) max;
        }
        return b;
    }

    public static void outline(BufferedImage image, int neighborhood) {
        BufferedImage before = deepCopy(image);
        erode(image, neighborhood);
        byte[] a = getImageData(image);
        byte[] b = getImageData(before);
        b = substract(b, a);
        System.arraycopy(b, 0, a, 0, b.length);
    }

    public static void close(BufferedImage image, int neighborhood) {
        dilate(image, neighborhood);
        erode(image, neighborhood);
    }

    public static byte[] open(byte[] a, int channels, int width, int height, int neighborhood) {
        byte[] b = erode(a, channels, width, height, neighborhood);
        return dilate(b, channels, width, height, neighborhood);
    }

    public static void open(BufferedImage image, int neighborhood) {
        erode(image, neighborhood);
        dilate(image, neighborhood);
    }


    public static byte[] bitwise_not(byte[] image1) {
        byte[] b = new byte[image1.length];
        for (int i = 0; i < image1.length; ++i) {
            b[i] = (byte) ~(image1[i] & 0xFF);
        }
        return b;
    }

    public static byte[] bitwise_and(byte[] image1, byte[] image2) {
        byte[] b = new byte[image1.length];
        for (int i = 0; i < image1.length; ++i) {
            b[i] = (byte) (image1[i] & image2[i]);
        }
        return b;
    }

    public static byte[] bitwise_or(byte[] image1, byte[] image2){
        byte[] b = new byte[image1.length];
        for (int i = 0; i < image1.length; ++i) {
            b[i] = (byte) (image1[i] | image2[i]);
        }
        return b;
    }

    public static byte[] bitwise_xor(byte[] image1, byte[] image2){
        byte[] b = new byte[image1.length];
        for (int i = 0; i < image1.length; ++i) {
            b[i] = (byte) (image1[i] ^ image2[i]);
        }
        return b;
    }

    public static int max(byte[] a) {
        int max = 0, v;
        for (byte anA : a) {
            v = anA & 0xFF;
            if (v > max) max = v;
        }
        return max;
    }

    public static int min(byte[] a) {
        int min = 255, v;
        for (byte anA : a) {
            v = anA & 0xFF;
            if (v < min) min = v;
        }
        return min;
    }

    public static int countNonZero(byte[] a) {
        int c = 0;
        for (byte anA : a) if((anA & 0xFF) > 0) ++c;
        return c;
    }

    public static int countNonWhite(byte[] a) {
        int c = 0;
        for (byte anA : a) if((anA & 0xFF) < 255) ++c;
        return c;
    }
    /*
        public static void skeleton(ImagePane imagePane, int neighborhood) {
            //http://felix.abecassis.me/2011/09/opencv-morphological-skeleton/
            BufferedImage image = imagePane.getImage();
            byte[] img = getImageData(deepCopy(image));
            byte[] temp;
            byte[] eroded;
            byte[] ret = new byte[0];
            byte[] skel = new byte[img.length];
            int channels = image.getColorModel().getNumComponents();
            int width = image.getWidth();
            int height = image.getHeight();

            int maxLoopCount = 100;
            int nonZero;
            do {
                eroded = erode(img, channels, width, height, 0);
                temp = dilate(eroded, channels, width, height, 0);
    //            eroded = dilate(img, channels, width, height, 0);
    //            temp = erode(eroded, channels, width, height, 0);
                temp = substract(img, temp);
                skel = bitwise_or(skel, temp);
                System.arraycopy(eroded, 0, img, 0, img.length);
                nonZero = countNonZero(img);
                System.out.println(nonZero);
    //            ret = appendVertical(ret, temp);
    //            ret = appendVertical(ret, temp);
    //            System.arraycopy(skel, 0, temp, 0, skel.length);
                --maxLoopCount;
            } while (nonZero != 0 && maxLoopCount > 0);
    //        BufferedImage r = new BufferedImage(image.getWidth(), ret.length / image.getWidth(), image.getType());
    //        byte[] a = getImageData(r);
    //        System.arraycopy(ret, 0, a, 0, ret.length);
            byte[] a = getImageData(image);
            System.arraycopy(skel, 0, a, 0, ret.length);
    //        imagePane.setImage(r);
            imagePane.refresh();
        }
        */
//    public static void skeleton(ImagePane imagePane, ProgressBar bar, int neighborhood) {
        //http://felix.abecassis.me/2011/09/opencv-morphological-skeleton/
//        Task task = new Task<Void>() {
//            @Override public Void call() {
//                BufferedImage image = imagePane.getImage();
//                byte[] img = getImageData(image);
//                byte[] temp;
//                byte[] eroded;
//                byte[] skel = new byte[img.length];
//                int channels = image.getColorModel().getNumComponents();
//                int width = image.getWidth();
//                int height = image.getHeight();
//
//                final int max = countNonZero(img);
//                int maxIterations = 1000;
//                int nonZero;
//                do {
//                    eroded = erode(img, channels, width, height, neighborhood);
//                    temp = dilate(eroded, channels, width, height, neighborhood);
//                    temp = substract(img, temp);
//                    skel = bitwise_or(skel, temp);
//                    System.arraycopy(eroded, 0, img, 0, img.length);
//                    nonZero = countNonZero(img);
//                    System.out.println(nonZero);
//                    --maxIterations;
//                    updateProgress(max - nonZero, max);
//                } while (nonZero != 0 && maxIterations > 0);
//                byte[] a = getImageData(image);
//                System.arraycopy(skel, 0, a, 0, img.length);
//                return null;
//            }
//        };
//        bar.progressProperty().bind(task.progressProperty());
//        new Thread(task).start();
//    }
//
    public static byte[] appendVertical(byte[] base, byte[] image) {
        byte[] tmp = new byte[base.length + image.length];
        System.arraycopy(base, 0, tmp, 0, base.length);
        System.arraycopy(image, 0, tmp, base.length, image.length);
        return tmp;
    }

    public static byte[] substract(byte[] a, byte[] b) {
        byte[] c = new byte[a.length];
        for (int i = 0; i < a.length; ++i) c[i] = (byte) Math.abs((a[i] & 0xFF) - (b[i] & 0xFF));
        return c;
    }

    private static void printimg(byte[] a, int width) {
        for (int i = 0; i < a.length; ++i) {
            if (i % width == 0) System.out.println();
            System.out.print((a[i] & 0xFF) + ",");
        }
        System.out.println();
    }

    public static BufferedImage substract(BufferedImage image1, BufferedImage image2) {
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
            d[i] = (byte) (Math.abs(c[i]));
        }

        return resultImage;
    }

    public static byte[] getImageData(BufferedImage image) {
        return ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    }

    public static void medianOperation(BufferedImage image, int diameter) {
        byte[] a = getImageData(image);
        byte[] b = new byte[a.length];
        int width = image.getWidth();
        int channels = image.getColorModel().getNumComponents();
        int offset = (diameter - 1) / 2 * width * channels + ((diameter - 1) / 2);
        for (int i = offset; i < a.length - offset; ++i) {
            int[] pixels = get3x3Pixels(a, i, channels, width, diameter);
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
        int offset = width * channels + channels;
        switch (direction) {
            case 0:
                for (int i = offset; i < a.length - offset; ++i) {
                    int[] pixels = get3x3Pixels(a, i, channels, width, 0, 9, 0);
                    b[i] = pixels[1] == pixels[7] ? (byte) pixels[1] : a[i];
                }
                break;
            case 1:
                for (int i = offset; i < a.length - offset; ++i) {
                    int[] pixels = get3x3Pixels(a, i, channels, width, 0, 9, 0);
                    b[i] = pixels[0] == pixels[8] ? (byte) pixels[0] : a[i];
                }
                break;
            case 2:
                for (int i = offset; i < a.length - offset; ++i) {
                    int[] pixels = get3x3Pixels(a, i, channels, width, 0, 9, 0);
                    b[i] = pixels[3] == pixels[5] ? (byte) pixels[3] : a[i];
                }
                break;
            case 3:
                for (int i = offset; i < a.length - offset; ++i) {
                    int[] pixels = get3x3Pixels(a, i, channels, width, 0, 9, 0);
                    b[i] = pixels[2] == pixels[6] ? (byte) pixels[2] : a[i];
                }
                break;
            default:
                throw new IllegalArgumentException("Illegal direction. Shoult be 0, 1, 2 or 3");
        }
        System.arraycopy(b, 0, a, 0, a.length);
    }

    public static BufferedImage convertTo3Byte(BufferedImage image) {
        BufferedImage ret = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        int rgb;
        for (int x = 0; x < ret.getWidth(); ++x)
            for (int y = 0; y < ret.getHeight(); ++y) {
                rgb = image.getRGB(x, y);
                ret.setRGB(x, y, rgb);
            }
        return ret;
    }


    public static BufferedImage grayToRGB(BufferedImage image) {
        BufferedImage ret = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        int[] rgb;
        for (int x = 0; x < ret.getWidth(); ++x)
            for (int y = 0; y < ret.getHeight(); ++y) {
                rgb = getPixel(image, x, y);
                setPixel(ret, x, y, rgb[0]);
            }
        return ret;
    }


    public static int turtleAlgorithm(ImagePane imagePane) {
        BufferedImage image = imagePane.getImage();
        int objectColor = getPixel(image, 0, 0)[0] < 128 ? 255 : 0;
        // Subroutine to perform chain coding
        int xStart = -1, yStart = -1;
        List<Integer> chainCode = new ArrayList<>();
        final int TMAX = 20000;
        int N1 = 0;
        int M1 = 0;
        int N2 = image.getWidth();
        int M2 = image.getHeight();

        int x,y,c;
        int d=0;

        //find starting point
        outerloop:
        for(y=M1;y<M2;++y)
            for(x=N1;x<N2;++x)
                if (getPixel(image, x, y)[0] == objectColor) {
                    xStart = x; yStart = y;
                    break outerloop;
                }

        // Initialization
        x = xStart; y = yStart;
        //create chain head and tail


        //Main loop. Border is followed.
        c = 0;
        do { //Follow border
            if (getPixel(image, x, y)[0] == objectColor) d=(modulus((d+1), 4)); else d=(modulus((d-1), 4));
            switch (d) { //translate direction to pixel
                case 0: x++; break;
                case 1: y--; break;
                case 2: x--; break;
                case 3: y++; break;
            }
            //Borders of ROI have been violated
            if (x<N1 || x>N2 || y<M1 || y>M2) return -93;
            //Add direction to end of chain
            if(d>-1)chainCode.add(d);
//            printChainCode(chainCode);
            //check if turtle follower is running wild
            if (c++ > TMAX) return -94;
            // while not back at start point, continue
        } while (x != xStart || y != yStart);
        drawLine(image, xStart, yStart, chainCode, imagePane);
        return 0;
    }

    private static void printChainCode(List<Integer> chainCode) {
        for (Integer d : chainCode) {
            System.out.print((int)d + ":");
        }
        System.out.println();

    }

    private static void drawLine(BufferedImage image, int xStart, int yStart, List<Integer> chainCode, ImagePane imagePane) {
        int x = xStart, y = yStart;
        if (image.getColorModel().getNumComponents() < 3) image = grayToRGB(image);
        for (Integer d : chainCode) {
            setPixel(image, x, y, new int[] {0,0,255});
            switch (d) { //translate direction to pixel
                case 0: x++; break;
                case 1: y--; break;
                case 2: x--; break;
                case 3: y++; break;
            }
        }
        imagePane.setImage(image);
    }

    public static void printImage(BufferedImage image) {
        byte[] a = getImageData(image);
        int width = image.getWidth();
        for (int i = 0; i < a.length; ++i) {
            int value = a[i] & 0xFF;
            System.out.print(value);
            if (i % width == width-1) {
                System.out.println();
            } else System.out.print(";");
        }
    }

    public static int modulus(int a, int b) {
        return (a % b + b) % b;
    }


    public static BufferedImage crop(BufferedImage image, CropRectangle cropRectangle) {
        int width = (int) cropRectangle.getWidth();
        int height = (int) cropRectangle.getHeight();
        int x = (int) cropRectangle.getX();
        int y = (int) cropRectangle.getY();
        BufferedImage croppedImage = getSubimage(image, x, y, width, height);
        System.out.println("cropped image WxH: " + croppedImage.getWidth() + "x" + croppedImage.getHeight());
        return croppedImage;
    }

    private static BufferedImage getSubimage(BufferedImage image, int x, int y, int width, int height) {
        byte[] a = getImageData(image);
        int channels = image.getColorModel().getNumComponents();
        BufferedImage croppedImage = new BufferedImage(width, height, image.getType());
        byte[] b = getImageData(croppedImage);
        int imageWidth = image.getWidth();
        int srcPos, destPos;
        int rowLength = width * channels;
        for(int yi = 0; yi < height; ++yi) {
            srcPos = xyToI(x, y + yi, imageWidth, channels);
            destPos = yi * rowLength;
            System.arraycopy(a, srcPos, b, destPos, rowLength);
        }
        return croppedImage;
    }

    public static int[] asArray(BufferedImage image) {
        byte[] a = getImageData(image);
        int[] r = new int[a.length];
        for (int i = 0; i < a.length; ++i) r[i] = a[i] & 0xFF;
        return r;
    }

    public static BufferedImage toImage(int[] array, int width) {
        if(array.length % width != 0) throw new IllegalArgumentException();
        BufferedImage image = new BufferedImage(width, array.length/width, BufferedImage.TYPE_BYTE_GRAY);
        byte[] a = getImageData(image);
        for(int i = 0; i < array.length; ++i) a[i] = (byte) array[i];
        return image;
    }

    public static BufferedImage toImage(byte[] array, int width, int imageType) {
//        if(array.length % (width* != 0) throw new IllegalArgumentException();
        BufferedImage image = new BufferedImage(width, array.length/width, imageType);
        byte[] a = getImageData(image);
        System.arraycopy(array, 0, a, 0, a.length);
        return image;
    }

    public static Integer[][] asTwoDimensionalArray(BufferedImage image) {
        if (image.getColorModel().getNumComponents() > 1) image = rgbToGrayscale(image);
        int width = image.getWidth();
        int height = image.getHeight();
        Integer[][] array = new Integer[height][width];
        for (int y = 0; y < height; ++y)
            for (int x = 0; x < width; ++x)
                array[y][x] = getPixel(image, x, y)[0];
        return array;

    }

    public static Image asImage(byte[] img, int width, int imageType) {
        BufferedImage image = toImage(img, width, imageType);
        return SwingFXUtils.toFXImage(image, null);
    }

    public static byte[] negative(byte[] temp) {
        for (int i = 0; i < temp.length; ++i) temp[i] = (byte) (255 - temp[i]);
        return temp;
    }

    public static class ExtendedBordersImage extends BufferedImage {

        /**
         *
         * @param image
         * @param borderWidth
         * @param borderFillMethod 0 fill black
         *                         1 fill white
         *                         2 copy borders
         *                         3 use existing pixels
         *                         4 dont't change extreme pixels
         */
        public ExtendedBordersImage(BufferedImage image, int borderWidth, int borderFillMethod) {
            super(image.getWidth() + borderWidth * 2, image.getHeight() + borderWidth * 2, image.getType());
            rewriteImage(image, this, borderWidth, borderWidth);
            fillBorders(this, borderWidth, borderFillMethod);
        }

        /**
         * Fills borders of image.
         * @param image
         * @param borderWidth
         * @param borderFillMethod 0 fill black
         *                         1 fill white
         *                         2 copy borders
         *                         3 use existing pixels
         *                         4 dont't change extreme pixels
         */
        private void fillBorders(ExtendedBordersImage image, int borderWidth, int borderFillMethod) {
            switch (borderFillMethod) {
                case 0:
                    fillBordersWithValue(image, borderWidth, 0);
                    break;
                case 1:
                    fillBordersWithValue(image, borderWidth, 255);
                    break;
                case 2:
                    int width = image.getWidth();
                    int height = image.getHeight();
                    // upper border
                    for (int y = 0; y < borderWidth; ++y)
                        for (int x = 0; x < width; ++x) setPixel(image, x, y, getPixel(image, x, borderWidth));
                    // lower border
                    for (int y = height-borderWidth; y < height; ++y)
                        for (int x = 0; x < width; ++x) setPixel(image, x, y, getPixel(image, x, height-borderWidth-1));
                    // left border
                    for (int x = 0; x < borderWidth; ++x)
                        for (int y = 0; y < height; ++y) setPixel(image, x, y, getPixel(image, borderWidth, y));
                    // right border
                    for (int x = width-borderWidth; x < width; ++x)
                        for (int y = 0; y < height; ++y) setPixel(image, x, y, getPixel(image, width-borderWidth-1, y));
                    // NW corner
                    for (int x = 0; x < borderWidth; ++x)
                        for (int y = 0; y < borderWidth; ++y) setPixel(image, x, y, getPixel(image, borderWidth, borderWidth));
                    // NE corner
                    for (int x = width-borderWidth; x < width; ++x)
                        for (int y = 0; y < borderWidth; ++y) setPixel(image, x, y, getPixel(image, width - 1 - borderWidth, borderWidth));
                    // SE corner
                    for (int x = width-borderWidth; x < width; ++x)
                        for (int y = height-borderWidth; y < height; ++y) setPixel(image, x, y, getPixel(image, width - 1 - borderWidth, height - 1 - borderWidth));
                    // SW corner
                    for (int x = 0; x < borderWidth; ++x)
                        for (int y = height-borderWidth; y < height; ++y) setPixel(image, x, y, getPixel(image, borderWidth, height - 1 - borderWidth));
            }
        }

        /**
         *
         * @param image
         * @param borderWidth single border width
         * @param value
         */
        public void fillBordersWithValue(ExtendedBordersImage image, int borderWidth, int value) {
            for (int y = 0; y < borderWidth; ++y)
                for (int x = 0; x < image.getWidth(); ++x)
                    setPixel(image, x, y, value);

            for (int y = image.getHeight()-borderWidth; y < image.getHeight(); ++y)
                for (int x = 0; x < image.getWidth(); ++x)
                    setPixel(image, x, y, value);

            for (int x = 0; x < borderWidth; ++x)
                for (int y = 0; y < image.getHeight(); ++y)
                    setPixel(image, x, y, value);

            for (int x = image.getWidth()-borderWidth; x < image.getWidth(); ++x)
                for (int y = 0; y < image.getHeight(); ++y)
                    setPixel(image, x, y, value);
        }


    }
}
