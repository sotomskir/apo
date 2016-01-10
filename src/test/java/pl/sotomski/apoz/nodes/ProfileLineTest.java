package pl.sotomski.apoz.nodes;

import org.junit.Before;
import org.junit.Test;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by sotomski on 13/12/15.
 */
public class ProfileLineTest {
    BufferedImage image;
    ProfileLine line = new ProfileLine();

    @Before
    public void setUp() {
        image = new BufferedImage(10, 10, BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < 10; x++)
            for (int y = 0; y < 10; y++)
                ImageUtils.setPixel(image, x, y, x*y);
    }

    @Test
    public void testGetLinePoints() throws Exception {
        ProfileLine line = new ProfileLine();

        // Test 1
        line.setStartPoint(0,0);
        line.setEndPoint(5,5);
        int[][] expecteds = new int[][] {
                {0,0},
                {1,1},
                {2,2},
                {3,3},
                {4,4},
                {5,5}
        };
        int[][] actuals = line.getLinePoints();
        for (int i = 0; i < actuals.length; ++i)
            assertArrayEquals(expecteds[i], actuals[i]);

        // Test 2
        line.setStartPoint(0,0);
        line.setEndPoint(5,10);
        expecteds = new int[][] {
                {0,0},
                {1,1},
                {1,2},
                {2,3},
                {2,4},
                {3,5},
                {3,6},
                {4,7},
                {4,8},
                {5,9},
                {5,10},
        };
        actuals = line.getLinePoints();
        for (int i = 0; i < actuals.length; ++i)
            assertArrayEquals(expecteds[i], actuals[i]);

        // Test 3
        line.setStartPoint(0,0);
        line.setEndPoint(-10,-5);
        expecteds = new int[][] {
                {-10,-5},
                {-9,-4},
                {-8,-4},
                {-7,-3},
                {-6,-3},
                {-5,-2},
                {-4,-2},
                {-3,-1},
                {-2,-1},
                {-1,0},
                {0,0}
        };
        actuals = line.getLinePoints();
        for (int i = 0; i < actuals.length; ++i)
            assertArrayEquals(expecteds[i], actuals[i]);

        // Test 4 line length
        line.setStartPoint(0,0);
        line.setEndPoint(10,0);
        actuals = line.getLinePoints();
        assertEquals(11, actuals.length);

        // Test 5 line length
        line.setStartPoint(0,0);
        line.setEndPoint(0,10);
        actuals = line.getLinePoints();
        assertEquals(11, actuals.length);
    }

    @Test
    public void testGetPixelValuesDiagonal() {
        line.setStartPoint(0, 0);
        line.setEndPoint(9, 9);
        int[][] actuals = ImageUtils.getLineProfilePixels(image, line);
        int[][] expecteds = {{0}, {1}, {4}, {9}, {16}, {25}, {36}, {49}, {64}, {81}};
        for (int i = 0; i < expecteds.length; ++i) assertArrayEquals(expecteds[i], actuals[i]);
    }

    @Test
    public void testGetPixelValuesDiagonalReversed() {
        line.setStartPoint(9, 9);
        line.setEndPoint(0, 0);
        int[][] actuals = ImageUtils.getLineProfilePixels(image, line);
        int[][] expecteds = {{0}, {1}, {4}, {9}, {16}, {25}, {36}, {49}, {64}, {81}};
        for (int i = 0; i < expecteds.length; ++i) assertArrayEquals(expecteds[i], actuals[i]);
    }

    @Test
    public void testGetPixelValuesHorizontal() {
        line.setStartPoint(0, 1);
        line.setEndPoint(9, 1);
        int[][] actuals = ImageUtils.getLineProfilePixels(image, line);
        int[][] expecteds = {{0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}};
        for (int i = 0; i < expecteds.length; ++i) assertArrayEquals(expecteds[i], actuals[i]);
    }

    @Test
    public void testGetPixelValuesHorizontalReversed() {
        line.setStartPoint(9, 1);
        line.setEndPoint(0, 1);
        int[][] actuals = ImageUtils.getLineProfilePixels(image, line);
        int[][] expecteds = {{0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}};
        for (int i = 0; i < expecteds.length; ++i) assertArrayEquals(expecteds[i], actuals[i]);
    }

    @Test
    public void testGetPixelValuesPositiveAngle() {
        line.setStartPoint(0, 0);
        line.setEndPoint(4, 9);
        int[][] actuals = ImageUtils.getLineProfilePixels(image, line);
        int[][] expecteds = {{0}, {0}, {2}, {3}, {8}, {10}, {18}, {21}, {32}, {36}};
        for (int i = 0; i < expecteds.length; ++i) assertArrayEquals(expecteds[i], actuals[i]);
    }

    @Test
    public void testGetPixelValuesPositiveAngleReversed() {
        line.setStartPoint(4, 9);
        line.setEndPoint(0, 0);
        int[][] actuals = ImageUtils.getLineProfilePixels(image, line);
        int[][] expecteds = {{0}, {0}, {2}, {3}, {8}, {10}, {18}, {21}, {32}, {36}};
        for (int i = 0; i < expecteds.length; ++i) assertArrayEquals(expecteds[i], actuals[i]);
    }

    @Test
    public void testGetPixelValuesNegativeAngle() {
        line.setStartPoint(4, 0);
        line.setEndPoint(0, 9);
        int[][] actuals = ImageUtils.getLineProfilePixels(image, line);
        int[][] expecteds = {{0}, {0}, {7}, {6}, {10}, {8}, {9}, {6}, {4}, {0}};
        for (int i = 0; i < expecteds.length; ++i) assertArrayEquals(expecteds[i], actuals[i]);
    }

    @Test
    public void testGetPixelValuesNegativeAngleReversed() {
        line.setStartPoint(0, 9);
        line.setEndPoint(4, 0);
        int[][] actuals = ImageUtils.getLineProfilePixels(image, line);
        int[][] expecteds = {{0}, {4}, {6}, {9}, {8}, {10}, {6}, {7}, {0}, {0}};
        for (int i = 0; i < expecteds.length; ++i) assertArrayEquals(expecteds[i], actuals[i]);
    }

    @Test
    public void testGetPixelValuesVertical() {
        line.setStartPoint(1, 0);
        line.setEndPoint(1, 9);
        int[][] actuals = ImageUtils.getLineProfilePixels(image, line);
        int[][] expecteds = {{0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}};
        for (int i = 0; i < expecteds.length; ++i) assertArrayEquals(expecteds[i], actuals[i]);
    }


//    @Test
//    public void testGetPixelValuesVerticalReversed() {
//        line.setStartPoint(1, 9);
//        line.setEndPoint(1, 0);
//        int[][] actuals = ImageUtils.getLineProfilePixels(image, line);
//        int[][] expecteds = {{9}, {8}, {7}, {6}, {5}, {4}, {3}, {2}, {1}, {0}};
//        for (int i = 0; i < expecteds.length; ++i) assertArrayEquals(expecteds[i], actuals[i]);
//    }
}
