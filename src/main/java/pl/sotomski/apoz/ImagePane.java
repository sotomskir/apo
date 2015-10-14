package pl.sotomski.apoz;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import pl.sotomski.apoz.utils.FileMenuUtils;
import pl.sotomski.apoz.utils.Histogram;
import pl.sotomski.apoz.utils.HistogramManager;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.File;


/**
 * Created by sotomski on 10/10/15.
 */
public class ImagePane extends Pane {

    ImageView imageView;
    ObjectProperty<BufferedImage> bufferedImage;
    HistogramManager histogramChart;
    Histogram histogram;
    private DoubleProperty zoomProperty;
//    private ImageObservable image;
    private File file;
       private ScrollPane scrollPane;

    private ImagePane() {
        super();
        this.zoomProperty = new SimpleDoubleProperty(1);
        this.imageView = new ImageView();
        this.bufferedImage = new SimpleObjectProperty<>();
        scrollPane= new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setContent(imageView);
        this.getChildren().add(scrollPane);
    }

    public ImagePane(File file) {
        this();
        BufferedImage bi = FileMenuUtils.loadImage(file);
        this.histogramChart = new HistogramManager(bi);
        setImage(bi);
        this.file = file;
    }

    public ImagePane(BufferedImage image, String name) {
        this();
        this.histogramChart = new HistogramManager(image);
        setImage(image);
    }

    public ImagePane(ImagePane imagePane) {
        this();
        setFile(new File(imagePane.getFile().getPath()));
        BufferedImage image = imagePane.getImage();
        setImage(ImageUtils.deepCopy(image));

    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    public void setImage(BufferedImage bufferedImage) {
        this.bufferedImage.setValue(bufferedImage);
        imageView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
        this.histogram = new Histogram(bufferedImage);
        this.histogramChart.update();

    }

    public BufferedImage getImage() {
        return bufferedImage.getValue();
    }

    public Histogram getHistogram() {
        return histogram;
    }

    public void setFitHeight(double fitHeight) {
        this.imageView.setFitHeight(fitHeight);
    }

    public void setFitWidth(double fitWidth) {
        this.imageView.setFitWidth(fitWidth);
    }

    public void handleRevert(ActionEvent actionEvent) {
        setImage(FileMenuUtils.loadImage(file));
    }

    public void handleZoomIn(Label textField) {
        zoomProperty.setValue(zoomProperty.multiply(1.25).getValue());
        if(zoomProperty.getValue()>1) zoomProperty.setValue(1);
        setFitWidth(zoomProperty.getValue() * imageView.getImage().getWidth());
        setFitHeight(zoomProperty.getValue() * imageView.getImage().getHeight());
        textField.setText(zoomProperty.multiply(100).getValue().intValue() + "%");
    }

    public void handleZoomOut(Label textField) {
        zoomProperty.setValue(zoomProperty.divide(1.25).getValue());
        if(zoomProperty.getValue()>1) zoomProperty.setValue(1);
        setFitWidth(zoomProperty.getValue() * imageView.getImage().getWidth());
        setFitHeight(zoomProperty.getValue() * imageView.getImage().getHeight());
        textField.setText(zoomProperty.multiply(100).getValue().intValue() + "%");
    }

    public void handleZoomChange(String value) {
        zoomProperty.setValue(Integer.parseInt(value)/100);
        setFitWidth(zoomProperty.getValue() * imageView.getImage().getWidth());
        setFitHeight(zoomProperty.getValue() * imageView.getImage().getHeight());
    }

    public DoubleProperty getZoomProperty() {
        return zoomProperty;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public ObjectProperty<BufferedImage> getImageProperty() {
        return bufferedImage;
    }

    public ObjectProperty<BufferedImage> bufferedImageProperty() {
        return bufferedImage;
    }

    public File getFile() {
        return file;
    }

    public int getChannels() {
        return getImage().getColorModel().getColorSpace().getNumComponents();
    }

    public HistogramManager getHistogramChart() {
        return histogramChart;
    }

    public ImageView getImageView() {
        return imageView;
    }
}
