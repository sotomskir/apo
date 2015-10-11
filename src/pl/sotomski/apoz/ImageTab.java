package pl.sotomski.apoz;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import pl.sotomski.apoz.utils.FileMenuUtils;
import pl.sotomski.apoz.utils.Histogram;
import pl.sotomski.apoz.utils.HistogramChart;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.File;


/**
 * Created by sotomski on 10/10/15.
 */
public class ImageTab extends Tab {

    ImageView imageView;
    BufferedImage bufferedImage;
    HistogramChart histogramChart;
    Histogram histogram;
    private DoubleProperty zoomProperty;
//    private ImageObservable image;
    private File file;

    public ImageTab() {
        this.zoomProperty = new SimpleDoubleProperty(1);
        this.histogramChart = new HistogramChart();
        this.imageView = new ImageView();
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setContent(imageView);
        this.setContent(scrollPane);
    }

    public ImageTab(File file) {
        this();
        setImage(FileMenuUtils.loadImage(file));
        setText(file.getName());
        this.file = file;
    }

    public ImageTab(BufferedImage image, String name) {
        this();
        setImage(image);
        setText(name);
    }

    public ImageTab(ImageTab imageTab) {
        this();
        setFile(new File(imageTab.getFile().getPath()));
        BufferedImage image = imageTab.getImage();
        setImage(ImageUtils.deepCopy(image));

    }

    public void setImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        imageView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
        this.histogram = new Histogram(bufferedImage);
        this.histogramChart.update(bufferedImage);

    }

    public BufferedImage getImage() {
        return bufferedImage;
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
        setText(file.getName());
    }

    public File getFile() {
        return file;
    }

    public int getChannels() {
        return getImage().getColorModel().getColorSpace().getNumComponents();
    }

    public HistogramChart getHistogramChart() {
        return histogramChart;
    }

    public ImageView getImageView() {
        return imageView;
    }
}
