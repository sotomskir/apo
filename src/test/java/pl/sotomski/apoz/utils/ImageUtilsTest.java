package pl.sotomski.apoz.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

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

}