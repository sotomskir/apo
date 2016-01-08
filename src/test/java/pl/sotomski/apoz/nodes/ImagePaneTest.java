package pl.sotomski.apoz.nodes;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import pl.sotomski.apoz.utils.JavaFXThreadingRule;
import pl.sotomski.apoz.utils.UTF8Control;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.*;

/**
 * Created by sotomski on 08/01/16.
 */
public class ImagePaneTest {
    Locale locale;
    ResourceBundle bundle;
    HistogramPane hp;
    BufferedImage image;

    @Rule
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

    @Before
    public void setUp() throws Exception {
        locale = new Locale("pl");
        bundle = ResourceBundle.getBundle("bundles.ApozBundle", locale, new UTF8Control());
        hp = new HistogramPane(bundle);
        image = new BufferedImage(10, 10, BufferedImage.TYPE_BYTE_GRAY);
    }

    @Test
    public void testImagePaneCopyConstructorNameCreation() throws Exception {
        List<ImagePane> list = new ArrayList<>();
        list.add(new ImagePane(hp, image, "a.bmp"));
        list.add(new ImagePane(hp, list.get(0)));
        list.add(new ImagePane(hp, image, "a.bmp"));
        list.add(new ImagePane(hp, image, "a.bmp"));
        list.add(new ImagePane(hp, image, "a.bmp kopia"));
        assertEquals("a.bmp", list.get(0).getName());
        assertEquals("a.bmp kopia", list.get(1).getName());
        assertEquals("a.bmp 1", list.get(2).getName());
        assertEquals("a.bmp 2", list.get(3).getName());
        assertEquals("a.bmp kopia 1", list.get(4).getName());
    }

    @Test
    public void testIncrementNameIfNotDistinct() {
        ImagePane ip = new ImagePane(hp, image, "a");
        assertEquals("a 1", ip.incrementNameIfNotDistinct("a"));
        assertEquals("a 2", ip.incrementNameIfNotDistinct("a 1"));
        assertEquals("a kopia", ip.incrementNameIfNotDistinct("a kopia"));
        assertEquals("a kopia 1", ip.incrementNameIfNotDistinct("a kopia"));
        assertEquals("a kopia 2", ip.incrementNameIfNotDistinct("a kopia"));
        assertEquals("a kopia 3", ip.incrementNameIfNotDistinct("a kopia 1"));
        assertEquals("a 3", ip.incrementNameIfNotDistinct("a 1"));
        assertEquals("a 4", ip.incrementNameIfNotDistinct("a 1"));
    }
}