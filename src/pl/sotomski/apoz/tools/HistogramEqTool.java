package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.ImagePane;
import pl.sotomski.apoz.utils.Histogram;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class HistogramEqTool extends VBox {

    private static VBox instance;
    private ToolController toolController;
    private ChoiceBox<String> choiceBox;
    private String methods[] = {"metoda średnich", "metoda losowa", "metoda sąsiedztwa", "metoda własna"};

    protected HistogramEqTool(ToolController controller) {
        this.toolController = controller;
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label("Histogram equalisation");
        this.choiceBox = new ChoiceBox<String>();
        choiceBox.getItems().addAll(methods);
        Button button = new Button("Apply");
        button.setOnAction(this::handleApply);
        getChildren().addAll(separator, label, choiceBox, button);
    }

    public static VBox getInstance(ToolController controller) {
        if(instance == null) instance = new HistogramEqTool(controller);
        return instance;
    }

    public void handleApply(ActionEvent actionEvent) {
        String method = choiceBox.getValue();
        if(methods[0].equals(method)) {
            ImagePane imageTab = toolController.getActivePaneProperty();
            imageTab.setImage(method1(imageTab.getImage(), imageTab.getHistogram()));
        }
        else if(methods[1].equals(method)) {
            ImagePane imageTab = toolController.getActivePaneProperty();
            imageTab.setImage(method2(imageTab.getImage(), imageTab.getHistogram()));      }
        else if(methods[2].equals(method)) {
            ImagePane imageTab = toolController.getActivePaneProperty();
            imageTab.setImage(method3(imageTab.getImage(), imageTab.getHistogram()));
        }
        else if(methods[3].equals(method)) {
            ImagePane imageTab = toolController.getActivePaneProperty();
            imageTab.setImage(method4(imageTab.getImage(), imageTab.getHistogram()));
        }

    }

    private static BufferedImage method1(BufferedImage bufferedImage, Histogram histogram) {
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

    private BufferedImage method2(BufferedImage bufferedImage, Histogram histogram) {
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

    private BufferedImage method3(BufferedImage bufferedImage, Histogram histogram) {
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
//            newR[z]=(leftR[z]+rightR[z])/2;
//            newG[z]=(leftG[z]+rightG[z])/2;
//            newB[z]=(leftB[z]+rightB[z])/2;
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

    private BufferedImage method4(BufferedImage bufferedImage, Histogram histogram) {

        return bufferedImage;
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
