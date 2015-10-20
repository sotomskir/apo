package pl.sotomski.apoz.commands;

import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.Histogram;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.Random;

/**
 * Created by sotomski on 17/10/15.
 */
public class HistogramEqCommand extends UndoableCommand implements Command {
    private int method;

    public HistogramEqCommand(ImagePane imagePane, int method) throws Exception {
        super(imagePane);
        if (method < 1 || method > 4) throw new IllegalArgumentException("Bad method. Eligible methods: 1, 2, 3, 4");
        this.method = method;
    }

    @Override
    public void execute() {
        if (method==1)      {
            method1(imagePane.getImage());
            imagePane.refresh();
        }
        else if (method==2) {
            method2(imagePane.getImage());
            imagePane.refresh();
        }
        else if (method == 3) {
            method3(imagePane.getImage());
            imagePane.refresh();
        }
        else if (method == 4) {
            method4(imagePane.getImage());
            imagePane.refresh();
        }
    }

    private BufferedImage method1(BufferedImage bufferedImage) {
        Histogram histogram = new Histogram(bufferedImage);
        int h[][] = histogram.getRGB();
        int r[] = new int[3], hint[] = new int[3];
        int left[][] = new int[3][histogram.getLevels()];
        int right[][] = new int[3][histogram.getLevels()];
        int newValue[][] = new int[3][histogram.getLevels()];

        for (int z = 0; z<histogram.getLevels(); ++z) {
            for (int ch = 0;ch<3;++ch) {
                left[ch][z] = r[ch];
                hint[ch] += h[ch][z];
                while (hint[ch] > histogram.getHRGBAvg()[ch]) {
                    hint[ch] -= histogram.getHRGBAvg()[ch];
                    ++r[ch];
                }
                right[ch][z] = r[ch];
                newValue[ch][z] = (left[ch][z] + right[ch][z]) / 2;
            }
        }

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        final byte[] a = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        for (int p = 0; p < width*height*histogram.getChannels(); p+=histogram.getChannels() ) {

            for (int ch = 0;ch<histogram.getChannels();++ch) {
                int chInv = histogram.getChannels()-1-ch;
                if (left[ch][a[p+chInv] & 0xFF] == right[ch][a[p+chInv] & 0xFF]) a[p+chInv] = (byte) (left[ch][a[p+chInv] & 0xFF] & 0xFF);
                else a[p+chInv] = (byte) (newValue[ch][a[p+chInv] & 0xFF] & 0xFF);
            }
        }

        return bufferedImage;
    }

    private BufferedImage method2(BufferedImage bufferedImage) {
        Histogram histogram = new Histogram(bufferedImage);
        int h[][] = histogram.getRGB();
        int r[] = new int[3], hint[] = new int[3];
        int left[][] = new int[3][histogram.getLevels()];
        int right[][] = new int[3][histogram.getLevels()];
        int newValue[][] = new int[3][histogram.getLevels()];

        for (int z = 0; z<histogram.getLevels(); ++z) {
            for (int ch = 0;ch<3;++ch) {
                left[ch][z] = r[ch];
                hint[ch] += h[ch][z];
                while (hint[ch] > histogram.getHRGBAvg()[ch]) {
                    hint[ch] -= histogram.getHRGBAvg()[ch];
                    ++r[ch];
                }
                right[ch][z] = r[ch];
                newValue[ch][z] = right[ch][z] - left[ch][z];
            }
        }

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        Random random = new Random();

        final byte[] a = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        for (int p = 0; p < width*height*histogram.getChannels(); p+=histogram.getChannels() ) {

            for (int ch = 0;ch<histogram.getChannels();++ch) {
                int chInv = histogram.getChannels()-1-ch;
                int i = a[p+chInv] & 0xFF;
                if (left[ch][i] == right[ch][i]) a[p+chInv] = (byte) (left[ch][i] & 0xFF);
                else a[p+chInv] = (byte) ((randomInRange(0, newValue[ch][i], random) + left[ch][i]) & 0xFF);
            }
        }

        return bufferedImage;
    }

    private BufferedImage method3(BufferedImage bufferedImage) {
        Histogram histogram = new Histogram(bufferedImage);
        int h[][] = histogram.getRGB();
        int r[] = new int[3], hint[] = new int[3];
        int left[][] = new int[3][histogram.getLevels()];
        int right[][] = new int[3][histogram.getLevels()];
        int newValue[][] = new int[3][histogram.getLevels()];

        for (int z = 0; z<histogram.getLevels(); ++z) {
            for (int ch = 0;ch<3;++ch) {
                left[ch][z] = r[ch];
                hint[ch] += h[ch][z];
                while (hint[ch] > histogram.getHRGBAvg()[ch]) {
                    hint[ch] -= histogram.getHRGBAvg()[ch];
                    ++r[ch];
                }
                right[ch][z] = r[ch];
                newValue[ch][z] = right[ch][z] - left[ch][z];
            }
        }

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        Random random = new Random();

        final byte[] a = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        for (int p = 0; p < width*height*histogram.getChannels(); p+=histogram.getChannels() ) {

            for (int ch = 0;ch<histogram.getChannels();++ch) {
                int chInv = histogram.getChannels()-1-ch;
                int i = a[p+chInv] & 0xFF;
                int avg = getAverage(a, width, height, histogram.getChannels(), p);
                if(avg < left[ch][i]) a[p+chInv] = (byte) (left[ch][i] & 0xFF);
                else if(avg > right[ch][i]) a[p+chInv] = (byte) (right[ch][i] & 0xFF);
                else a[p+chInv] = (byte) (avg & 0xFF);
            }
        }

        return bufferedImage;
    }

    private BufferedImage method4(BufferedImage bufferedImage) {
//        http://www.generation5.org/content/2004/histogramEqualization.asp
        Histogram histogram = new Histogram(bufferedImage);
        double alpha = (double)histogram.getLevels() / (bufferedImage.getWidth()*bufferedImage.getHeight());
        int[][] cumulativeFrequency = histogram.getCumulativeRGB();
        WritableRaster raster = bufferedImage.getRaster();
        int rgb2;
        int[] rgb = new int[3];

        for (int x=0;x<bufferedImage.getWidth();++x) {
            for (int y=0;y<bufferedImage.getHeight();++y) {
                rgb2 = bufferedImage.getRGB(x, y);
                rgb[0] = ImageUtils.getR(rgb2);
                rgb[1] = ImageUtils.getG(rgb2);
                rgb[2] = ImageUtils.getB(rgb2);
                rgb[0] = (int) (cumulativeFrequency[0][rgb[0]] * alpha);
                rgb[1] = (int) (cumulativeFrequency[1][rgb[1]] * alpha);
                rgb[2] = (int) (cumulativeFrequency[2][rgb[2]] * alpha);
                for (int i=0;i<3;++i) if (rgb[i]>255) rgb[i]=255;
                raster.setPixel(x, y, rgb);
            }
        }

        return bufferedImage;
    }

    private Color getAverage(BufferedImage bufferedImage, int px, int py) {
        int avg[] = new int[3];
        for (int x=-1;x<2;++x) {
            for (int y=-1;y<2;++y) {
                int xc = px+x;
                int yc = py+y;
                if(xc<0) xc=0;
                if(yc<0) yc=0;
                if(xc>=bufferedImage.getWidth()) xc = bufferedImage.getWidth()-1;
                if(yc>=bufferedImage.getHeight()) yc = bufferedImage.getHeight()-1;
                int rgb = bufferedImage.getRGB(xc, yc);
                for(int i=0;i<3;++i) avg[i]+=getRGB(rgb)[i];
            }
        }
        int rgb = bufferedImage.getRGB(px, py);
        for(int i=0;i<3;++i) {
            avg[i]-=getRGB(rgb)[i];
            avg[i]/=8;
        }
        return new Color(avg[0], avg[1], avg[2]);
    }


    private int getAverage(byte[] a, int width, int height, int ch ,int p) {
        //TODO dodać obsługę skrajnych pikseli
        if (p<width*ch || p>a.length-width*ch-3 || p%width*ch <ch || p%width*ch>width*ch-ch) return a[p] & 0xFF;
        int sum;
        try {
            sum = a[p-((width+1)*ch)] & 0xFF + a[p-((width)*ch)] & 0xFF + a[p-((width-1)*ch)] & 0xFF +
                    a[p-ch] & 0xFF + a[p+ch] & 0xFF +
                    a[p+((width+1)*ch)] & 0xFF + a[p+((width)*ch)] & 0xFF + a[p+((width-1)*ch)] & 0xFF;
        } catch (ArrayIndexOutOfBoundsException e) {
            return a[p] & 0xFF;
        }
        return sum/8;
    }

    private int[] getRGB(int rgb) {
        int a[] = new int[3];
        a[0] = (rgb >> 16) & 0xff;
        a[1] = (rgb >>  8) & 0xff;
        a[2] = rgb & 0xff;
        return a;
    }

    private static int randomInRange(int aStart, int aEnd, Random aRandom){
        if (aStart > aEnd) {
            throw new IllegalArgumentException("Start cannot exceed End.");
        }
        //get the range, casting to long to avoid overflow problems
        long range = (long)aEnd - (long)aStart + 1;
        // compute a fraction of the range, 0 <= frac < range
        long fraction = (long)(range * aRandom.nextDouble());
        return (int)(fraction + aStart);
    }
}
