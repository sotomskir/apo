package pl.sotomski.apoz.nodes;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.utils.FileMenuUtils;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.File;


/**
 * Created by sotomski on 10/10/15.
 */
public class ImagePane extends Pane {

    private ImageView imageView;
    private BufferedImage bufferedImage;
    private IntegerProperty imageVersion;
    private HistogramPane histogramPane;
    private CommandManager commandManager;
    private DoubleProperty zoomProperty;
    private File file;
    private ScrollPane scrollPane;
    private boolean tabbed;

    private ImagePane() {
        super();
        this.tabbed = true;
        this.commandManager = new CommandManager();
        this.zoomProperty = new SimpleDoubleProperty(1);
        this.imageVersion = new SimpleIntegerProperty(0);
        this.imageView = new ImageView();
        scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setContent(imageView);
        this.getChildren().add(scrollPane);
    }

    public ImagePane(HistogramPane histogramPane, File file) {
        this();
        BufferedImage bi = FileMenuUtils.loadImage(file);
        this.histogramPane = histogramPane;
        setImage(bi);
        this.file = file;
        refresh();
    }

    public ImagePane(HistogramPane histogramPane, BufferedImage image, String name) {
        this();
        this.histogramPane = histogramPane;
        this.bufferedImage = image;
        refresh();
    }

    public ImagePane(HistogramPane histogramPane, ImagePane imagePane) {
        this();
        setFile(new File(imagePane.getFile().getPath()));
        BufferedImage image = ImageUtils.deepCopy(imagePane.getImage());
        this.histogramPane = histogramPane;
        this.bufferedImage = image;
        refresh();
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    public void setImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public BufferedImage getImage() {
        return bufferedImage;
    }

    public void setHistogramPane(HistogramPane histogramPane) {
        this.histogramPane = histogramPane;
        histogramPane.update(bufferedImage);
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public void setFitHeight(double fitHeight) {
        this.imageView.setFitHeight(fitHeight);
    }

    public void setFitWidth(double fitWidth) {
        this.imageView.setFitWidth(fitWidth);
    }

    public void handleRevert(ActionEvent actionEvent) {
        setImage(FileMenuUtils.loadImage(file));
        refresh();
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

    public int getImageVersion() {
        return imageVersion.get();
    }

    public IntegerProperty imageVersionProperty() {
        return imageVersion;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public int getChannels() {
        return getImage().getColorModel().getColorSpace().getNumComponents();
    }

    public HistogramPane getHistogramPane() {
        return histogramPane;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void refresh() {
        long startTime;
        startTime = System.currentTimeMillis();
        imageView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
        System.out.println("SwingFXUtils.toFXImage: " + (System.currentTimeMillis()-startTime));
        startTime = System.currentTimeMillis();
//        this.histogramPane.update(bufferedImage);
        System.out.println("HistogramPane.update(): " + (System.currentTimeMillis()-startTime));
        this.imageVersionProperty().setValue(getImageVersion()+1);
    }

    public boolean isTabbed() {
        return tabbed;
    }

    public void setTabbed(boolean tabbed) {
        this.tabbed = tabbed;
    }
}
