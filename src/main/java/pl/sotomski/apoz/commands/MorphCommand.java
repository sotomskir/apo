package pl.sotomski.apoz.commands;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by sotomski on 20/11/15.
 */
public class MorphCommand extends UndoableCommand implements Command {
    private int action;
    private int neighborhood;
    private BufferedImage image;
    private Thread thread;

    /**
     *
     * @param imagePane
     * @param action
     * @param neighborhood 0 for square neighborhood 1 for diamond neighborhood
     */
    public MorphCommand(ImagePane imagePane, int action, int neighborhood) {
        super(imagePane);
        this.action = action;
        this.neighborhood = neighborhood;
        this.image = ImageUtils.deepCopy(imagePane.getImage());
        if(image.getColorModel().getNumComponents()>1) image = ImageUtils.rgbToGrayscale(image);
    }

    @Override
    public void execute() {
        //Dilatation
        if (action == 0) ImageUtils.dilate(imagePane.getImage(), neighborhood);
            //Erosion
        else if (action == 1) ImageUtils.erode(imagePane.getImage(), neighborhood);
            //Open
        else if (action == 2) ImageUtils.open(imagePane.getImage(), neighborhood);
            //Close
        else if (action == 3) ImageUtils.close(imagePane.getImage(), neighborhood);
            //Outline
        else if (action == 4) ImageUtils.outline(imagePane.getImage(), neighborhood);
            //Skeleton
        else if (action == 5) {
            BorderPane rootLayout = new BorderPane();
            rootLayout.setPadding(new Insets(10));
            Scene scene = new Scene(rootLayout, 950, 700);
            Stage stage = new Stage();
            stage.setScene(scene);
            ProgressIndicator progressBar = new ProgressIndicator();
            ImageView imageView1 = new ImageView();
            ImageView imageView2 = new ImageView();
            ImageView imageView3 = new ImageView();
            imageView1.setFitWidth(300);
            imageView2.setFitWidth(300);
            imageView3.setFitWidth(300);
            imageView1.setPreserveRatio(true);
            imageView2.setPreserveRatio(true);
            imageView3.setPreserveRatio(true);
            Slider slider = new Slider(0, 0, 0);
            slider.setMajorTickUnit(1);
            slider.setSnapToTicks(true);
            slider.setBlockIncrement(1);
            slider.setShowTickMarks(true);
            slider.setMinorTickCount(0);
            slider.setShowTickLabels(true);
            Label image1Label = new Label("img");
            Label image2Label = new Label("temp = img - close(img)");
            Label image3Label = new Label("skel = skel - temp");
            VBox vBox1 = new VBox(image1Label, imageView1);
            VBox vBox2 = new VBox(image2Label, imageView2);
            VBox vBox3 = new VBox(image3Label, imageView3);

            ResourceBundle bundle = imagePane.getHistogramPane().getBundle();
            Button applyBtn = new Button(bundle.getString("Apply"));
            applyBtn.setOnAction(event -> {
                imagePane.setImage(image);
                imagePane.refresh();
                stage.close();
            });
            Button cancelBtn = new Button(bundle.getString("Cancel"));
            cancelBtn.setOnAction(event -> {
                stage.close();
            });
            HBox applyCancelBtns = new HBox(applyBtn, cancelBtn);
            HBox hBox = new HBox(vBox1, new Separator(Orientation.VERTICAL), vBox2, new Separator(Orientation.VERTICAL), vBox3);
            VBox vBox = new VBox(progressBar, hBox, slider, applyCancelBtns);
            rootLayout.setCenter(vBox);
            stage.show();
            List<Image> imageList1 = new ArrayList<>();
            List<Image> imageList2 = new ArrayList<>();
            List<Image> imageList3 = new ArrayList<>();

            //http://felix.abecassis.me/2011/09/opencv-morphological-skeleton/
            Task task = new Task<Void>() {
                @Override public Void call() {
                    byte[] img = ImageUtils.getImageData(ImageUtils.deepCopy(image));
                    byte[] temp;
                    byte[] eroded;
                    byte[] skel = new byte[img.length];
                    Arrays.fill(skel, (byte) 255);
                    int channels = image.getColorModel().getNumComponents();
                    int width = image.getWidth();
                    int height = image.getHeight();

                    final int max = ImageUtils.countNonZero(img);
                    int maxIterations = 100;
                    int sliderMax = -1;
                    int nonZero;
                    byte[] a = ImageUtils.getImageData(image);
                    do {
                        eroded = ImageUtils.dilate(img, channels, width, height, 1);
                        temp = ImageUtils.erode(eroded, channels, width, height, 1);
                        Image image1 = ImageUtils.asImage(img,  image.getWidth(), image.getType());
                        temp = ImageUtils.substract(img, temp);
                        skel = ImageUtils.substract(skel, temp);
                        System.arraycopy(eroded, 0, img, 0, img.length);
                        nonZero = ImageUtils.countNonWhite(img);
                        System.out.println(nonZero);
                        --maxIterations;
                        updateProgress(max - nonZero, max);
                        Image image2 = ImageUtils.asImage(temp, image.getWidth(), image.getType());
                        Image image3 = ImageUtils.asImage(skel, image.getWidth(), image.getType());
                        imageList1.add(image1);
                        imageList2.add(image2);
                        imageList3.add(image3);
                        imageView1.setImage(image1);
                        imageView2.setImage(image2);
                        imageView3.setImage(image3);
                        slider.setMax(++sliderMax);
                        slider.setValue(sliderMax);
                        if (Thread.currentThread().isInterrupted()) {
                            System.out.println("Exiting gracefully");
                            return null;
                        }
                        System.arraycopy(skel, 0, a, 0, img.length);
                    } while (nonZero != 0 && maxIterations > 0);
                    slider.valueProperty().addListener((observable, oldValue, newValue) -> {
                        imageView1.setImage(imageList1.get(newValue.intValue()));
                        imageView2.setImage(imageList2.get(newValue.intValue()));
                        imageView3.setImage(imageList3.get(newValue.intValue()));
                    });
                    return null;
                }
            };
            progressBar.progressProperty().bind(task.progressProperty());
            thread = new Thread(task);
            thread.start();
        }
    }
}
