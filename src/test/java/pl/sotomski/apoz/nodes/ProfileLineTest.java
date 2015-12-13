package pl.sotomski.apoz.nodes;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by sotomski on 13/12/15.
 */
public class ProfileLineTest {

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
                {1,2},
                {2,4},
                {3,6},
                {4,8},
                {5,10}
        };
        actuals = line.getLinePoints();
        for (int i = 0; i < actuals.length; ++i)
            assertArrayEquals(expecteds[i], actuals[i]);

        // Test 3
        line.setStartPoint(0,0);
        line.setEndPoint(-5,-10);
        expecteds = new int[][] {
                {-5,-10},
                {-4,-8},
                {-3,-6},
                {-2,-4},
                {-1,-2},
                {0,0}
        };
        actuals = line.getLinePoints();
        for (int i = 0; i < actuals.length; ++i)
            assertArrayEquals(expecteds[i], actuals[i]);
    }
}