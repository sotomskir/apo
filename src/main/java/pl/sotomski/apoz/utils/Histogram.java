package pl.sotomski.apoz.utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Created by sotomski on 23/09/15.
 */
public class Histogram {

    private final int levels;
    private int hR[];
        private int hG[];
        private int hB[];
        private int hM[];
    private int rgb[][];
    private int cumulative[][];
        private double hRavg, hGavg, hBavg, Havg;
        private int channels;
        public int[] gethM() {
            return hM;
        }

        public double gethBavg() {
            return hBavg;
        }

        public double gethGavg() {
            return hGavg;
        }

        public int[] gethR() {
            return hR;
        }

        public int[] gethG() {
            return hG;
        }

        public int[] gethB() {
            return hB;
        }

        public double gethRavg() {

            return hRavg;
        }

        public double getHavg() {
            return Havg;
        }

    public int getLevels() {
        return levels;
    }

    public int[][] getCumulative() {
        return cumulative;
    }

    public int getChannels() {
        return channels;
    }

    public Histogram(BufferedImage image) {
        long startTime1 = System.currentTimeMillis();

        int height = image.getHeight();
        int width = image.getWidth();
        channels = image.getColorModel().getNumComponents();
        int bitDepth = image.getColorModel().getPixelSize()/channels;
        levels = (int)Math.pow(2, bitDepth);

        hR = new int[levels];
        hG = new int[levels];
        hB =  new int[levels];
        hM =  new int[levels];
        rgb =  new int[channels][levels];
        cumulative =  new int[3][levels];
        int rgb;
        double Hsum = 0, hRsum = 0, hGsum = 0, hBsum = 0;
        final byte[] a = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        for (int p = 0; p < width*height*3; p+=3 ) {
                ++hR[a[p+2] & 0xFF];
                ++hG[a[p+1] & 0xFF];
                ++hB[a[p] & 0xFF];
        }

        for (int i=0;i<levels;++i) {
            hM[i]= hR[i]+ hG[i]+ hB[i];
            Hsum += hM[i];
            hRsum += hR[i];
            hGsum += hG[i];
            hBsum += hB[i];
            cumulative[0][i] = (int) hRsum;
            cumulative[1][i] = (int) hGsum;
            cumulative[2][i] = (int) hBsum;
        }

        hRavg=hRsum/levels;
        hGavg=hGsum/levels;
        hBavg=hBsum/levels;
        Havg=Hsum/levels;
        System.out.println(this.getClass() + ": " + (System.currentTimeMillis()-startTime1));
    }
}
