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
public class HistogramEqCommand extends UndoableCommand implements Command {
    private int method;

    public HistogramEqCommand(ImagePane image, int method) {
        super(image);
        if (method < 1 || method > 5) throw new IllegalArgumentException("Bad method. Eligible methods: 1, 2, 3, 4, 5");
        this.method = method;
    }

    @Override
    public void execute() {
        if (method == 1) method1(imagePane.getImage());
        else if (method==2) method2(imagePane.getImage());
        else if (method == 3) method3(imagePane.getImage());
        else if (method == 4) method4(imagePane.getImage());
        else if (method == 5) method5(imagePane.getImage());
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

    private BufferedImage method5(BufferedImage bufferedImage) {
        final double t = 0.05;
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
                double dif = ((double)(avg>a[p]?avg-a[p]:a[p]-avg)) / (double)a[p];
                if (left[ch][i] == right[ch][i]) a[p+chInv] = (byte) (left[ch][i] & 0xFF);
//                else if (dif>t) a[p+chInv] = (byte) ((randomInRange(0, newValue[ch][i], random) + left[ch][i]) & 0xFF);
                else a[p+chInv] = (byte) ((randomInRange(0, newValue[ch][i], random) + left[ch][i]) & 0xFF);
//                else a[p+chInv] = (byte) (newValue[ch][i] /2);
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

    private BufferedImage method4(BufferedImage bi) {
//        http://www.generation5.org/content/2004/histogramEqualization.asp
        Histogram histogram = new Histogram(bi);
        double alpha = (double)histogram.getLevels() / (bi.getWidth()*bi.getHeight());
        int[][] cumulativeFrequency = histogram.getCumulativeRGB();

        final byte[] a = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        for (int p = bi.getWidth()*bi.getHeight()*histogram.getChannels()-histogram.getChannels(); p>=0; p-=histogram.getChannels() ) {
            for (int ch = 0;ch<histogram.getChannels();++ch) {
                int chInv = histogram.getChannels() - 1 - ch;
                int i = a[p + chInv] & 0xFF;
                a[p+chInv] = (byte) ((cumulativeFrequency[ch][i] * alpha)-1);
            }
        }

        return bi;
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
