package pl.sotomski.apoz.commands;

import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.Histogram;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
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
        int hR[] = histogram.gethR();
        int hG[] = histogram.gethG();
        int hB[] = histogram.gethB();
        double hRavg = histogram.gethRavg();
        double hGavg = histogram.gethGavg();
        double hBavg = histogram.gethBavg();
        int rR = 0, hRint = 0;
        int rG = 0, hGint = 0;
        int rB = 0, hBint = 0;
        int leftR[] = new int[hR.length];
        int leftG[] = new int[hR.length];
        int leftB[] = new int[hR.length];
        int rightR[] = new int[hR.length];
        int rightG[] = new int[hR.length];
        int rightB[] = new int[hR.length];
        int newR[] = new int[hR.length];
        int newG[] = new int[hR.length];
        int newB[] = new int[hR.length];

        for (int z = 0; z<hR.length; ++z) {
            leftR[z] = rR;
            leftG[z] = rG;
            leftB[z] = rB;
            hRint+=hR[z];
            hGint+=hG[z];
            hBint+=hB[z];
            while (hRint>hRavg) {
                hRint-=hRavg;
                ++rR;
            }
            while (hGint>hGavg) {
                hGint-=hGavg;
                ++rG;
            }
            while (hBint>hBavg) {
                hBint-=hBavg;
                ++rB;
            }
            rightR[z]=rR;
            rightG[z]=rG;
            rightB[z]=rB;
            newR[z]=(leftR[z]+rightR[z])/2;
            newG[z]=(leftG[z]+rightG[z])/2;
            newB[z]=(leftB[z]+rightB[z])/2;
        }

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        for (int x=0;x<width;++x) {
            for (int y=0;y<height;++y) {
                Color rgb = new Color(bufferedImage.getRGB(x, y));
                int red = rgb.getRed();
                int green = rgb.getGreen();
                int blue = rgb.getBlue();

                if(leftR[red]==rightR[red]) red=leftR[red];
                else red=newR[red];

                if(leftG[green]==rightG[green]) green=leftG[green];
                else green=newG[green];

                if(leftB[blue]==rightB[blue]) blue=leftB[blue];
                else blue=newB[blue];

                bufferedImage.setRGB(x, y, new Color(red, green, blue).getRGB());

            }
        }
        return bufferedImage;
    }

    private BufferedImage method2(BufferedImage bufferedImage) {
        Histogram histogram = new Histogram(bufferedImage);
        int hR[] = histogram.gethR();
        int hG[] = histogram.gethG();
        int hB[] = histogram.gethB();
        double hRavg = histogram.gethRavg();
        double hGavg = histogram.gethGavg();
        double hBavg = histogram.gethBavg();
        int rR = 0, hRint = 0;
        int rG = 0, hGint = 0;
        int rB = 0, hBint = 0;
        int leftR[] = new int[hR.length];
        int leftG[] = new int[hR.length];
        int leftB[] = new int[hR.length];
        int rightR[] = new int[hR.length];
        int rightG[] = new int[hR.length];
        int rightB[] = new int[hR.length];
        int newR[] = new int[hR.length];
        int newG[] = new int[hR.length];
        int newB[] = new int[hR.length];

        for (int z = 0; z<hR.length; ++z) {
            leftR[z] = rR;
            leftG[z] = rG;
            leftB[z] = rB;
            hRint+=hR[z];
            hGint+=hG[z];
            hBint+=hB[z];
            while (hRint>hRavg) {
                hRint-=hRavg;
                ++rR;
            }
            while (hGint>hGavg) {
                hGint-=hGavg;
                ++rG;
            }
            while (hBint>hBavg) {
                hBint-=hBavg;
                ++rB;
            }
            rightR[z]=rR;
            rightG[z]=rG;
            rightB[z]=rB;
            newR[z]=rightR[z]-leftR[z];
            newG[z]=rightG[z]-leftG[z];
            newB[z]=rightB[z]-leftB[z];
        }

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        Random random = new Random();
        for (int x=0;x<width;++x) {
            for (int y=0;y<height;++y) {
                Color rgb = new Color(bufferedImage.getRGB(x, y));
                int red = rgb.getRed();
                int green = rgb.getGreen();
                int blue = rgb.getBlue();

                if(leftR[red]==rightR[red]) red=leftR[red];
                else red = randomInRange(0, newR[red], random) + leftR[red];

                if(leftG[green]==rightG[green]) green=leftG[green];
                else green = randomInRange(0, newG[green], random) + leftG[green];

                if(leftB[blue]==rightB[blue]) blue=leftB[blue];
                else blue = randomInRange(0, newB[blue], random) + leftB[blue];

                bufferedImage.setRGB(x, y, new Color(red, green, blue).getRGB());

            }
        }

        return bufferedImage;

    }

    private BufferedImage method3(BufferedImage bufferedImage) {
        Histogram histogram = new Histogram(bufferedImage);
        int hR[] = histogram.gethR();
        int hG[] = histogram.gethG();
        int hB[] = histogram.gethB();
        double hRavg = histogram.gethRavg();
        double hGavg = histogram.gethGavg();
        double hBavg = histogram.gethBavg();
        int rR = 0, hRint = 0;
        int rG = 0, hGint = 0;
        int rB = 0, hBint = 0;
        int leftR[] = new int[hR.length];
        int leftG[] = new int[hR.length];
        int leftB[] = new int[hR.length];
        int rightR[] = new int[hR.length];
        int rightG[] = new int[hR.length];
        int rightB[] = new int[hR.length];
        int newR[] = new int[hR.length];
        int newG[] = new int[hR.length];
        int newB[] = new int[hR.length];

        for (int z = 0; z<hR.length; ++z) {
            leftR[z] = rR;
            leftG[z] = rG;
            leftB[z] = rB;
            hRint+=hR[z];
            hGint+=hG[z];
            hBint+=hB[z];
            while (hRint>hRavg) {
                hRint-=hRavg;
                ++rR;
            }
            while (hGint>hGavg) {
                hGint-=hGavg;
                ++rG;
            }
            while (hBint>hBavg) {
                hBint-=hBavg;
                ++rB;
            }

            rightR[z]=rR;
            rightG[z]=rG;
            rightB[z]=rB;
        }

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        for (int x=0;x<width;++x) {
            for (int y=0;y<height;++y) {
                Color rgb = new Color(bufferedImage.getRGB(x, y));
                int red = rgb.getRed();
                int green = rgb.getGreen();
                int blue = rgb.getBlue();

                Color avg = getAverage(bufferedImage, x, y);

                if(avg.getRed() < leftR[red]) red=leftR[red];
                else if(avg.getRed() > rightR[red]) red = rightR[red];
                else red = avg.getRed();

                if(avg.getGreen() < leftG[green]) green=leftG[green];
                else if(avg.getGreen() > rightG[green]) green = rightG[green];
                else green = avg.getGreen();

                if(avg.getBlue() < leftB[blue]) blue=leftB[blue];
                else if(avg.getBlue() > rightB[blue]) blue = rightB[blue];
                else blue = avg.getBlue();

                bufferedImage.setRGB(x, y, new Color(red, green, blue).getRGB());

            }
        }
        return bufferedImage;
    }

    private BufferedImage method4(BufferedImage bufferedImage) {
//        http://www.generation5.org/content/2004/histogramEqualization.asp
        Histogram histogram = new Histogram(bufferedImage);
        double alpha = (double)histogram.getLevels() / (bufferedImage.getWidth()*bufferedImage.getHeight());
        int[][] cumulativeFrequency = histogram.getCumulative();
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
