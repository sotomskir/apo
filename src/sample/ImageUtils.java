package sample;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * Created by sotomski on 01/10/15.
 */
public class ImageUtils {
    public static BufferedImage rgbToGrayscale(BufferedImage bufferedImage){
        BufferedImage grayscaleImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster wr = grayscaleImage.getRaster();
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        for(int x=0;x<width;++x) {
            for(int y=0;y<height;++y) {
                int rgb = bufferedImage.getRGB(x,y);
                wr.setSample(x, y, 0, convertPixel(rgb));
            }
        }
        return grayscaleImage;
    }

    private static int convertPixel(int rgb) {
        /*
        rPerc+gPerc+bPerc == 1
         */
        final double rPerc = 0.2;
        final double gPerc = 0.6;
        final double bPerc = 0.2;
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        int k = (int)(r*rPerc + g*gPerc + b*bPerc) ;
        return k;
    }
}
