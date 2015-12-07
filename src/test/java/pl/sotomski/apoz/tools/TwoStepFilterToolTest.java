package pl.sotomski.apoz.tools;

import org.junit.Test;
import pl.sotomski.apoz.utils.Mask;

import static org.junit.Assert.assertArrayEquals;

/**
 * Created by sotomski on 06/12/15.
 */
public class TwoStepFilterToolTest {
    Mask mask = new Mask(7);
    int[][] e = {
            {
                    0,0,0,
                    0,0,0,
                    0,0,1
             },
            {
                    0,0,0,
                    0,0,0,
                    0,1,2
            },
            {
                    0,0,0,
                    0,0,0,
                    1,2,1
            },
            {
                    0,0,0,
                    0,0,0,
                    2,1,0
            },
            {
                    0,0,0,
                    0,0,0,
                    1,0,0
            },
            {
                    0,0,0,
                    0,0,1,
                    0,0,2
            },
            {
                    0,0,0,
                    0,1,2,
                    0,2,4
            },
            {
                    0,0,0,
                    1,2,1,
                    2,4,2
            },
            {
                    0,0,0,
                    2,1,0,
                    4,2,0
            },
            {
                    0,0,0,
                    1,0,0,
                    2,0,0
            },
            {
                    0,0,1,
                    0,0,2,
                    0,0,1
            },
            {
                    0,1,2,
                    0,2,4,
                    0,1,2
            },
            {
                    1,2,1,
                    2,4,2,
                    1,2,1
            },
            {
                    2,1,0,
                    4,2,0,
                    2,1,0
            },
            {
                    1,0,0,
                    2,0,0,
                    1,0,0
            },
            {
                    0,0,2,
                    0,0,1,
                    0,0,0
            },
            {
                    0,2,4,
                    0,1,2,
                    0,0,0
            },
            {
                    2,4,2,
                    1,2,1,
                    0,0,0
            },
            {
                    4,2,0,
                    2,1,0,
                    0,0,0
            },
            {
                    2,0,0,
                    1,0,0,
                    0,0,0
            },
            {
                    0,0,1,
                    0,0,0,
                    0,0,0
            },
            {
                    0,1,2,
                    0,0,0,
                    0,0,0
            },
            {
                    1,2,1,
                    0,0,0,
                    0,0,0
            },
            {
                    2,1,0,
                    0,0,0,
                    0,0,0
            },
            {
                    1,0,0,
                    0,0,0,
                    0,0,0
            }
    };

    @Test
    public void testName() throws Exception {
        mask.set(2,2,1);
        mask.set(3,2,2);
        mask.set(4,2,1);
        mask.set(2,3,2);
        mask.set(3,3,4);
        mask.set(4,3,2);
        mask.set(2,4,1);
        mask.set(3,4,2);
        mask.set(4,4,1);
        int[] a;
        a = mask.get3x3Mask(0,0);
        assertArrayEquals(a, e[0]);
        a = mask.get3x3Mask(1,0);
        assertArrayEquals(a, e[1]);
        a = mask.get3x3Mask(2,0);
        assertArrayEquals(a, e[2]);
        a = mask.get3x3Mask(3,0);
        assertArrayEquals(a, e[3]);
        a = mask.get3x3Mask(4,0);
        assertArrayEquals(a, e[4]);
        a = mask.get3x3Mask(0,1);
        assertArrayEquals(a, e[5]);
        a = mask.get3x3Mask(1,1);
        assertArrayEquals(a, e[6]);
        a = mask.get3x3Mask(2,1);
        assertArrayEquals(a, e[7]);
        a = mask.get3x3Mask(3,1);
        assertArrayEquals(a, e[8]);
        a = mask.get3x3Mask(4,1);
        assertArrayEquals(a, e[9]);
        a = mask.get3x3Mask(0,2);
        assertArrayEquals(a, e[10]);
        a = mask.get3x3Mask(1,2);
        assertArrayEquals(a, e[11]);
        a = mask.get3x3Mask(2,2);
        assertArrayEquals(a, e[12]);
        a = mask.get3x3Mask(3,2);
        assertArrayEquals(a, e[13]);
        a = mask.get3x3Mask(4,2);
        assertArrayEquals(a, e[14]);
        a = mask.get3x3Mask(0,3);
        assertArrayEquals(a, e[15]);
        a = mask.get3x3Mask(1,3);
        assertArrayEquals(a, e[16]);
        a = mask.get3x3Mask(2,3);
        assertArrayEquals(a, e[17]);
        a = mask.get3x3Mask(3,3);
        assertArrayEquals(a, e[18]);
        a = mask.get3x3Mask(4,3);
        assertArrayEquals(a, e[19]);
        a = mask.get3x3Mask(0,4);
        assertArrayEquals(a, e[20]);
        a = mask.get3x3Mask(1,4);
        assertArrayEquals(a, e[21]);
        a = mask.get3x3Mask(2,4);
        assertArrayEquals(a, e[22]);
        a = mask.get3x3Mask(3,4);
        assertArrayEquals(a, e[23]);
        a = mask.get3x3Mask(4,4);
        assertArrayEquals(a, e[24]);
    }
}