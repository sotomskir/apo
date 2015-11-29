package pl.sotomski.apoz.utils;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by sotomski on 25/09/15.
 */
public class FileMenuUtils {

    public static File saveAsDialog(BorderPane layoutRoot, BufferedImage image) {
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

    public static File openFileDialog(BorderPane layoutRoot) {
        Window window = layoutRoot.getScene().getWindow();
        final FileChooser fileChooser = new FileChooser();
        return fileChooser.showOpenDialog(window);
    }

    public static File openDirDialog(Pane layoutRoot) {
        Window window = layoutRoot.getScene().getWindow();
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        return directoryChooser.showDialog(window);
    }

    public static BufferedImage loadImage(File file) {
        try {
            FileInputStream fileIs = new FileInputStream(file);
            BufferedImage image;
            String fileExtension = getFileExtension(file);
            System.out.println(fileExtension);
            if (fileExtension.equals("tiff")) {
                System.out.println("Opening tiff image");
                FileSeekableStream stream = new FileSeekableStream(file);
                TIFFDecodeParam decodeParam = new TIFFDecodeParam();
                decodeParam.setDecodePaletteAsShorts(true);
                ParameterBlock params = new ParameterBlock();
                params.add(stream);
                RenderedOp image1 = JAI.create("tiff", params);
                image = image1.getAsBufferedImage();
            } else {
                image = ImageIO.read(fileIs);
            }
            if (image.getType() != BufferedImage.TYPE_3BYTE_BGR) {
                image = ImageUtils.convertTo3Byte(image);
                System.out.println("Converting image to 3BYTE_BGR");
            }
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            new ExceptionDialog(e, "Error loading file");
        }
        return null;
    }

    public static String getFileExtension(File file) {
        int i = file.getAbsolutePath().lastIndexOf('.');
        return file.getAbsolutePath().substring(i+1).toLowerCase();
    }

}
