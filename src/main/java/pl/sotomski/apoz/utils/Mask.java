package pl.sotomski.apoz.utils;

public class Mask {
    int[][] data;

    public Mask(int size) {
        this.data = new int[size][size];
    }

    public int[] get3x3Mask(int x, int y) {
        if (x > data.length || y > data.length) throw new IllegalArgumentException();
        int[] r = new int[9];
        r[0] = data[x  ][y  ];
        r[1] = data[x+1][y  ];
        r[2] = data[x+2][y  ];
        r[3] = data[x  ][y+1];
        r[4] = data[x+1][y+1];
        r[5] = data[x+2][y+1];
        r[6] = data[x  ][y+2];
        r[7] = data[x+1][y+2];
        r[8] = data[x+2][y+2];
//        System.out.println("["+r[0]+","+r[1]+","+r[2]+"]");
//        System.out.println("["+r[3]+","+r[4]+","+r[5]+"]");
//        System.out.println("["+r[6]+","+r[7]+","+r[8]+"]\n");
        return r;
    }

    public int[] getData() {
        int[] ret = new int[data.length * data.length];
        int i = 0;
        for (int y = 0; y < data.length; ++y)
            for (int x = 0; x < data.length; ++x) {
                ret[i++] = data[x][y];
            }
        return ret;
    }

    public void set(int x, int y, int value) {
        data[x][y] = value;
    }

    public int multiply(int[] mask) {
        if (mask.length != 9) throw new IllegalArgumentException();
//        System.out.println("*");
        int[] thisMask = get3x3Mask(0, 0);
        int ret = 0;
        for (int i = 0; i < 9; ++i) ret += mask[i] * thisMask[i];
        return ret;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < data.length; ++y)  {
            sb.append("[");
            for (int x = 0; x < data[y].length; ++x) {
                sb.append(data[y][x]);
                sb.append(",");
            }
            sb.append("]\n");
        }
        return sb.toString();
    }
}