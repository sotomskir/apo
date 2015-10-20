package pl.sotomski.apoz;

/**
 * Created by sotomski on 20/10/15.
 */
public class Test {
    public static void main(String[] args) {
        int a = 129;
        byte b = (byte) (a & 0xFF);
        int c = b &0xFF;
        System.out.println(c);
    }
}
