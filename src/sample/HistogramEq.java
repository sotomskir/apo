package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class HistogramEq extends VBox {
    private static VBox instance;
    private ToolController toolController;
    private ChoiceBox<String> choiceBox;
    protected HistogramEq(ToolController controller) {
        this.toolController = controller;
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label("Histogram equalisation");
        this.choiceBox = new ChoiceBox<String>();
        choiceBox.getItems().addAll("method 1", "method 2", "method 3");
        Button button = new Button("Apply");
        button.setOnAction(this::handleApply);
        getChildren().addAll(separator, label, choiceBox, button);
    }

    public static VBox getInstance(ToolController controller) {
        if(instance == null) instance = new HistogramEq(controller);
        return instance;
    }

    public void handleApply(ActionEvent actionEvent) {
        String method = choiceBox.getValue();
        if("method 1".equals(method)) method1();
        else if("method 2".equals(method)) method2();
        else if("method 3".equals(method)) method3();

    }

    private void method1() {
        BufferedImage bufferedImage = toolController.getBufferedImage();
        int hR[] = toolController.getHistogram().gethR();
        int hG[] = toolController.getHistogram().gethG();
        int hB[] = toolController.getHistogram().gethB();
        double hRavg = toolController.getHistogram().gethRavg();
        double hGavg = toolController.getHistogram().gethGavg();
        double hBavg = toolController.getHistogram().gethBavg();
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

        toolController.getImageCanvas().setImage(SwingFXUtils.toFXImage(bufferedImage, null));
        toolController.getHistogram().update(bufferedImage);

    }
    private void method2() {
        BufferedImage bufferedImage = toolController.getBufferedImage();
        int hR[] = toolController.getHistogram().gethR();
        int hG[] = toolController.getHistogram().gethG();
        int hB[] = toolController.getHistogram().gethB();
        double hRavg = toolController.getHistogram().gethRavg();
        double hGavg = toolController.getHistogram().gethGavg();
        double hBavg = toolController.getHistogram().gethBavg();
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

        toolController.getImageCanvas().setImage(SwingFXUtils.toFXImage(bufferedImage, null));
        toolController.getHistogram().update(bufferedImage);

    }
    private void method3() {
        BufferedImage bufferedImage = toolController.getBufferedImage();
        int hR[] = toolController.getHistogram().gethR();
        int hG[] = toolController.getHistogram().gethG();
        int hB[] = toolController.getHistogram().gethB();
        double hRavg = toolController.getHistogram().gethRavg();
        double hGavg = toolController.getHistogram().gethGavg();
        double hBavg = toolController.getHistogram().gethBavg();
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

                if(leftR[red]==rightR[red]) red=leftR[red];
                else red=newR[red];

                if(leftG[green]==rightG[green]) green=leftG[green];
                else green=newG[green];

                if(leftB[blue]==rightB[blue]) blue=leftB[blue];
                else blue=newB[blue];

                bufferedImage.setRGB(x, y, new Color(red, green, blue).getRGB());

            }
        }

        toolController.getImageCanvas().setImage(SwingFXUtils.toFXImage(bufferedImage, null));
        toolController.getHistogram().update(bufferedImage);

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
