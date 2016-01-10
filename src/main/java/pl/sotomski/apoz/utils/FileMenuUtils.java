package pl.sotomski.apoz.utils;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import pl.sotomski.apoz.Main;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.*;
import java.util.prefs.Preferences;

import static pl.sotomski.apoz.controllers.PrefsController.LAST_DIRECTORY;

public class FileMenuUtils {

    private static Preferences prefs = Preferences.userNodeForPackage(Main.class);

    public static File saveAsDialog(BorderPane layoutRoot, BufferedImage image) {
        Window window = layoutRoot.getScene().getWindow();
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.bmp"));
        File file = new File(prefs.get(LAST_DIRECTORY, System.getProperty("user.dir") + File.separator));
        fileChooser.setInitialDirectory(file);
        file = fileChooser.showSaveDialog(window);
        if(file != null) {
            prefs.put(LAST_DIRECTORY, file.getParent());
            try {
                ImageIO.write(image, "jpg", file);
            } catch (IOException e) {
                new ExceptionDialog(e, "Wystąpił błąd podczas zapisu pliku");
                e.printStackTrace();
            }
        }
        return file;
    }

    public static File saveAsCSVDialog(Parent layoutRoot, BufferedImage image) {
        Window window = layoutRoot.getScene().getWindow();
        final FileChooser fileChooser = new FileChooser();
        String path = prefs.get(LAST_DIRECTORY, null);
        File initialDirectory = new File(path);
        fileChooser.setInitialDirectory(initialDirectory);
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("CSV", "csv"));
        File file = fileChooser.showSaveDialog(window);
        if(file != null) {
            prefs.put(LAST_DIRECTORY, file.getParent());
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(file));
                byte[] a = ImageUtils.getImageData(image);
                int width = image.getWidth();
                for (int i = 0; i < a.length; ++i) {
                    int value = a[i] & 0xFF;
                    bw.write(String.valueOf(value));
                    if (i % width == width-1) {
                        bw.write('\n');
                    } else bw.write(";");
                }
            } catch (IOException e) {
                new ExceptionDialog(e, "Wystąpił błąd podczas zapisu pliku");
                e.printStackTrace();
            } finally {
                if (bw != null) try {
                    bw.close();
                } catch (IOException e) {
                    new ExceptionDialog(e, "Wystąpił błąd podczas zapisu pliku");
                    e.printStackTrace();
                }
            }
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
        String path = prefs.get(LAST_DIRECTORY, System.getProperty("user.dir") + File.separator);
        File initialDirectory = new File(path);
        fileChooser.setInitialDirectory(initialDirectory);
        File choosenFile = fileChooser.showOpenDialog(window);
        if(choosenFile != null) prefs.put(LAST_DIRECTORY, choosenFile.getParent());
        return choosenFile;
    }

    public static File openDirDialog(Pane layoutRoot) {
        Window window = layoutRoot.getScene().getWindow();
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        String path = prefs.get(LAST_DIRECTORY, null);
        File initialDirectory = new File(path);
        directoryChooser.setInitialDirectory(initialDirectory);
        File choosenFile = directoryChooser.showDialog(window);
        if(choosenFile != null) prefs.put(LAST_DIRECTORY, choosenFile.getParent());
        System.out.println(choosenFile.getParent());
        return choosenFile;
    }

    public static BufferedImage loadImage(File file) {
        try {
            FileInputStream fileIs = new FileInputStream(file);
            BufferedImage image;
            String fileExtension = getFileExtension(file);
            System.out.println(fileExtension);
            if (fileExtension.equals("tiff") || fileExtension.equals("tif")) {
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
