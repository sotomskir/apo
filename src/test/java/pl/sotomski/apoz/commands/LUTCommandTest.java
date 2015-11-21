package pl.sotomski.apoz.commands;

import pl.sotomski.apoz.nodes.ImagePane;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;

/**
 * Created by sotomski on 03/11/15.
 */
public class LUTCommandTest  {
    CommandManager manager;
    BufferedImage image;
    ImagePane imagePane;
    int[] lut;

    @org.junit.Before
    public void setUp() throws Exception {
//        manager = new CommandManager();
//        image = new BufferedImage(256, 1, BufferedImage.TYPE_3BYTE_BGR);
//        for (int i = 0; i < 256; ++i) image.setRGB(i, 0, new Color(i, i, i).getRGB());
//        Locale locale = new Locale("en");
//        ResourceBundle bundle = ResourceBundle.getBundle("bundles.ApozBundle", locale, new UTF8Control());
//        imagePane = new ImagePane(new HistogramPane(bundle), image, "test");
//        lut = new int[256];
//        for (int i = 0; i < 256; ++i) lut[i] = 255 - i;
//        manager.executeCommand(new LUTCommand(imagePane, lut));
    }

    @org.junit.After
    public void tearDown() throws Exception {

    }

    @org.junit.Test
    public void testExecuteExtremeValues() throws Exception {
        BufferedImage image = imagePane.getImage();
        assertEquals(new Color(255, 255, 255).getRGB(), image.getRGB(0  , 0));
        assertEquals(new Color(  0,   0,   0).getRGB(), image.getRGB(255, 0));
    }

}