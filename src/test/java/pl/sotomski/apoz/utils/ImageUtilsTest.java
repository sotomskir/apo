package pl.sotomski.apoz.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static pl.sotomski.apoz.utils.ImageUtils.*;

/**
 * Created by sotomski on 03/11/15.
 */
public class ImageUtilsTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void rewriteImageTest() {
        BufferedImage testImage = new BufferedImage(5, 5, BufferedImage.TYPE_BYTE_GRAY);
        //TODO
        byte[] a = ImageUtils.getImageData(testImage);
        Arrays.fill(a, (byte) 1);
        BufferedImage growedImage = new BufferedImage(testImage.getWidth()+2, testImage.getHeight()+2, testImage.getType());
        ImageUtils.rewriteImage(testImage, growedImage, 1, 1);
        byte[] expecteds = new byte[] {
                0,0,0,0,0,0,0,
                0,1,1,1,1,1,0,
                0,1,1,1,1,1,0,
                0,1,1,1,1,1,0,
                0,1,1,1,1,1,0,
                0,1,1,1,1,1,0,
                0,0,0,0,0,0,0
        };
        byte[] actuals = ImageUtils.getImageData(growedImage);
        assertArrayEquals(expecteds, actuals);

        growedImage = new BufferedImage(testImage.getWidth()+2, testImage.getHeight()+2, testImage.getType());
        ImageUtils.rewriteImage(testImage, growedImage, 0, 0);
        expecteds = new byte[] {
                1,1,1,1,1,0,0,
                1,1,1,1,1,0,0,
                1,1,1,1,1,0,0,
                1,1,1,1,1,0,0,
                1,1,1,1,1,0,0,
                0,0,0,0,0,0,0,
                0,0,0,0,0,0,0
        };
        actuals = ImageUtils.getImageData(growedImage);
        assertArrayEquals(expecteds, actuals);
    }

//    @Test
//    public void testGet5x5Pixels() {
//        byte[] imageData1 = new byte[100];
//        byte[] imageData3 = new byte[300];
//        for (int i = 0; i < 100; ++i) imageData1[i] = (byte) i;
//        for (int i = 0; i < 300; ++i) imageData3[i] = (byte) i;
//
//        int[] actuals = ImageUtils.get5x5Pixels(imageData1, 22, 1, 10, 10, 0);
//        int[] expecteds = new int[] {
//                 0, 1, 2, 3, 4,
//                10,11,12,13,14,
//                20,21,22,23,24,
//                30,31,32,33,34,
//                40,41,42,43,44
//        };
//        assertArrayEquals(expecteds, actuals);
//
//        actuals = ImageUtils.get5x5Pixels(imageData1, 11, 1, 10, 10, 0);
//        expecteds = new int[] {
//                 0, 0, 1, 2, 3,
//                 0, 0, 1, 2, 3,
//                10,10,11,12,13,
//                20,20,21,22,23,
//                30,30,31,32,33
//        };
//        assertArrayEquals(expecteds, actuals);
//
//        actuals = ImageUtils.get5x5Pixels(imageData1, 0, 1, 10, 10, 0);
//        expecteds = new int[] {
//                 0, 0, 0, 1, 2,
//                 0, 0, 0, 1, 2,
//                 0, 0, 0, 1, 2,
//                10,10,10,11,12,
//                20,20,20,21,22
//        };
//        assertArrayEquals(expecteds, actuals);
//
//        actuals = ImageUtils.get5x5Pixels(imageData1, 77, 1, 10, 10, 0);
//        expecteds = new int[] {
//                55,56,57,58,59,
//                65,66,67,68,69,
//                75,76,77,78,79,
//                85,86,87,88,89,
//                95,96,97,98,99
//        };
//        assertArrayEquals(expecteds, actuals);
//
//        actuals = ImageUtils.get5x5Pixels(imageData1, 88, 1, 10, 10, 0);
//        expecteds = new int[] {
//                66,67,68,69,69,
//                76,77,78,79,79,
//                86,87,88,89,89,
//                96,97,98,99,99,
//                96,97,98,99,99
//        };
//        assertArrayEquals(expecteds, actuals);
//
//        actuals = ImageUtils.get5x5Pixels(imageData1, 99, 1, 10, 10, 0);
//        expecteds = new int[] {
//                77,78,79,79,79,
//                87,88,89,89,89,
//                97,98,99,99,99,
//                97,98,99,99,99,
//                97,98,99,99,99
//        };
//        assertArrayEquals(expecteds, actuals);
//    }

    @Test
    public void testIToXY() throws Exception {
        final int width = 10;
        int[] xy;
        xy = ImageUtils.iToXY(101, width, 1);
        assertArrayEquals(new int[]{1, 10}, xy);

        xy = ImageUtils.iToXY(100, width, 1);
        assertArrayEquals(new int[]{0, 10}, xy);

        xy = ImageUtils.iToXY(90, width, 3);
        assertArrayEquals(new int[]{0, 3}, xy);

        xy = ImageUtils.iToXY(119, width, 3);
        assertArrayEquals(new int[]{9, 3}, xy);

        xy = ImageUtils.iToXY(303, width, 3);
        assertArrayEquals(new int[]{1, 10}, xy);
    }

    @Test
    public void testXyToI() throws Exception {
        final int width = 10;
        int i;
        i = ImageUtils.xyToI(0, 0, width, 1);
        assertEquals(0, i);

        i = ImageUtils.xyToI(9, 10, width, 1);
        assertEquals(109, i);

        i = ImageUtils.xyToI(1, 10, width, 1);
        assertEquals(101, i);

        i = ImageUtils.xyToI(0, 10, width, 1);
        assertEquals(100, i);

        i = ImageUtils.xyToI(0, 3, width, 3);
        assertEquals(90, i);

        i = ImageUtils.xyToI(9, 3, width, 3);
        assertEquals(117, i);

        i = ImageUtils.xyToI(1, 10, width, 3);
        assertEquals(303, i);
    }

    @Test
    public void testI1ToI2() throws Exception {
        final int width1 = 10;
        final int width2 = 15;
        int i2;

        i2 = ImageUtils.i1ToI2(13, width1, width2, 1);
        assertEquals(18, i2);

        i2 = ImageUtils.i1ToI2(10, width1, width2, 1);
        assertEquals(15, i2);

        i2 = ImageUtils.i1ToI2(9, width1, width2, 1);
        assertEquals(9, i2);

        i2 = ImageUtils.i1ToI2(27, width1, width2, 3);
        assertEquals(27, i2);

        i2 = ImageUtils.i1ToI2(33, width1, width2, 3);
        assertEquals(48, i2);
    }


    @Test
    public void testBitwise_not() throws Exception {
        byte[] c = new byte[2];
        byte[] e = new byte[2];
        c[0] = (byte)0;
        c[1] = (byte)255;

        e[0] = (byte)255;
        e[1] = (byte)0;

        byte[] a;
        a = bitwise_not(c);
        assertArrayEquals(e, a);
    }

    @Test
    public void testBitwise_and() throws Exception {
        byte[] c = new byte[4];
        byte[] d = new byte[4];
        c[0] = (byte)0;
        c[1] = (byte)0;
        c[2] = (byte)255;
        c[3] = (byte)255;

        d[0] = (byte)0;
        d[1] = (byte)255;
        d[2] = (byte)0;
        d[3] = (byte)255;

        byte[] e = new byte[4];
        e[0] = (byte)0;
        e[1] = (byte)0;
        e[2] = (byte)0;
        e[3] = (byte)255;


        byte[] a;
        a = bitwise_and(c, d);
        assertArrayEquals(e, a);
    }

    @Test
    public void testBitwise_or() throws Exception {
        byte[] c = new byte[4];
        byte[] d = new byte[4];
        c[0] = (byte)0;
        c[1] = (byte)0;
        c[2] = (byte)255;
        c[3] = (byte)255;

        d[0] = (byte)0;
        d[1] = (byte)255;
        d[2] = (byte)0;
        d[3] = (byte)255;

        byte[] e = new byte[4];
        e[0] = (byte)0;
        e[1] = (byte)255;
        e[2] = (byte)255;
        e[3] = (byte)255;


        byte[] a;
        a = bitwise_or(c, d);
        assertArrayEquals(e, a);
    }

    @Test
    public void testMax() throws Exception {
        byte[] c = new byte[4];
        c[0] = (byte)255;
        c[1] = (byte)0;
        c[2] = (byte)0;
        c[3] = (byte)0;


        int e = 255;
        int a = max(c);
        assertEquals(e, a);
    }

    @Test
    public void testMin() throws Exception {
        byte[] c = new byte[4];
        c[0] = (byte)255;
        c[1] = (byte)0;
        c[2] = (byte)0;
        c[3] = (byte)253;


        int e = 0;
        int a = min(c);
        assertEquals(e, a);
    }
}