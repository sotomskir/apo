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
                {1,1},
                {2,1},
                {3,2},
                {4,2},
                {5,3},
                {6,3},
                {7,4},
                {8,4},
                {9,5},
                {10,5},
        };
        actuals = line.getLinePoints();
        for (int i = 0; i < actuals.length; ++i)
            assertArrayEquals(expecteds[i], actuals[i]);

        // Test 3
        line.setStartPoint(0,0);
        line.setEndPoint(-5,-10);
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
}