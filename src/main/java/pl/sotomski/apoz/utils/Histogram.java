package pl.sotomski.apoz.utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Created by sotomski on 23/09/15.
 */
public class Histogram {

    private final int levels;
    private int rgb[][];
    private int mono[]; //Only for drawing charts
    private int cumulativeRGB[][];
    private int cumulativeMono[]; //Only for drawing charts
    private double hRGBAvg[], hMonoAvg;
    private int channels;

    public Histogram(BufferedImage image) {
        long startTime1 = System.currentTimeMillis();
        hRGBAvg = new double[3];
        int height = image.getHeight();
        int width = image.getWidth();
        channels = image.getColorModel().getNumComponents();
        int bitDepth = image.getColorModel().getPixelSize()/channels;
        levels = (int)Math.pow(2, bitDepth);
        rgb =  new int[3][levels];
        mono =  new int[levels];
        cumulativeRGB =  new int[3][levels];
        cumulativeMono =  new int[levels];
        double hSum = 0, hRGBSum[] = new double[3];
        final byte[] a = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        if (channels==3) {
            for (int p = 0; p < width*height*3; p+=3 ) {
                ++rgb[0][a[p+2] & 0xFF];
                ++rgb[1][a[p+1] & 0xFF];
                ++rgb[2][a[p] & 0xFF];
            }
            for (int i=0;i<levels;++i) mono[i] = rgb[0][i]+rgb[1][i]+rgb[2][i];
        } else {
            for (int p = 0; p < width*height; p++ ) {
                ++mono[a[p] & 0xFF];
                ++rgb[0][a[p] & 0xFF];
                ++rgb[1][a[p] & 0xFF];
                ++rgb[2][a[p] & 0xFF];

            }
        }

        for (int i=0;i<levels;++i) {
            hSum += mono[i];
            hRGBSum[0] += rgb[0][i];
            hRGBSum[1] += rgb[1][i];
            hRGBSum[2] += rgb[2][i];
            cumulativeRGB[0][i] = (int) hRGBSum[0];
            cumulativeRGB[1][i] = (int) hRGBSum[1];
            cumulativeRGB[2][i] = (int) hRGBSum[2];
            cumulativeMono[i] = (int) hSum;
        }

        hRGBAvg[0]=hRGBSum[0]/levels;
        hRGBAvg[1]=hRGBSum[1]/levels;
        hRGBAvg[2]=hRGBSum[2]/levels;
        hMonoAvg =hSum/levels;
        System.out.println(this.getClass() + ": " + (System.currentTimeMillis()-startTime1));
    }

    public int getLevels() {
        return levels;
    }

    public int[][] getRGB() {
        return rgb;
    }

    public int[] getMono() {
        return mono;
    }

    public int[][] getCumulativeRGB() {
        return cumulativeRGB;
    }

    public int[] getCumulativeMono() {
        return cumulativeMono;
    }

    public double[] getHRGBAvg() {
        return hRGBAvg;
    }

    public double gethMonoAvg() {
        return hMonoAvg;
    }

    public int getChannels() {
        return channels;
    }
}
