package pl.sotomski.apoz.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by sotomski on 23/09/15.
 */
public class Histogram {

        private int hR[];
        private int hG[];
        private int hB[];
        private int hM[];
        private double hRavg, hGavg, hBavg, Havg;
        private boolean mono;
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


    public Histogram(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();
        channels = image.getColorModel().getNumComponents();
        int bitDepth = image.getColorModel().getPixelSize()/channels;
        int levels = (int)Math.pow(2, bitDepth);

        hR = new int[levels];
        hG = new int[levels];
        hB =  new int[levels];
        hM =  new int[levels];

        double Hsum = 0, hRsum = 0, hGsum = 0, hBsum = 0;
        for (int x=0;x<width;++x)
            for (int y=0;y<height;++y) {
                Color rgb = new Color(image.getRGB(x, y));
                ++hR[rgb.getRed()];
                ++hB[rgb.getBlue()];
                ++hG[rgb.getGreen()];
            }

        for (int i=0;i<levels;++i) {
            hM[i]= hR[i]+ hG[i]+ hB[i];
            Hsum += hM[i];
            hRsum += hR[i];
            hGsum += hG[i];
            hBsum += hB[i];
        }

        hRavg=hRsum/levels;
        hGavg=hGsum/levels;
        hBavg=hBsum/levels;
        Havg=Hsum/levels;
    }
}
