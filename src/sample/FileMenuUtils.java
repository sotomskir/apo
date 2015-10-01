package sample;

import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by sotomski on 25/09/15.
 */
public class FileMenuUtils {

    public static File saveAsDialog(Pane layoutRoot, BufferedImage image) {
        Window window = layoutRoot.getScene().getWindow();
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.bmp"));
        File file = fileChooser.showSaveDialog(window);

        try {
            ImageIO.write(image, "jpg", file);
        } catch (IOException e) {
            new ExceptionDialog(e, "Wystąpił błąd podczas zapisu pliku");
            e.printStackTrace();
        }
        return file;
    }

    public static void saveDialog(BufferedImage image, File openedFile) {
        try {
            ImageIO.write(image, "jpg", openedFile);
        } catch (IOException e) {
            new ExceptionDialog(e, "Wystąpił błąd podczas zapisu pliku");
            e.printStackTrace();
        }
    }

    public static File openDialog(Pane layoutRoot) {
        Window window = layoutRoot.getScene().getWindow();
        final FileChooser fileChooser = new FileChooser();
        return fileChooser.showOpenDialog(window);
    }

    public static BufferedImage loadImage(File file) {
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
            new ExceptionDialog(e, "Error loading file");
        }
        return null;
    }


}
