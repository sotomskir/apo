package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ResourceBundle;

public class Controller implements Initializable, ToolController {
    @FXML
    private MenuBar menuBar;
    @FXML
    private VBox toolbox;
    @FXML
    private Pane histogramPane;
    @FXML
    private BorderPane rootLayout;
    @FXML
    Label labelR;
    @FXML
    Label labelG;
    @FXML
    Label labelB;
    @FXML
    Label labelX;
    @FXML
    Label labelY;
    @FXML
    Label labelWidth;
    @FXML
    Label labelHeight;
    @FXML
    Label labelDepth;
    @FXML
    TextField zoomField;
    @FXML
    Label fileName;
    @FXML
    private ImageView imageCanvas;
    private ImageObservable image;
    private File openedFile;
    private Histogram histogram;
    private double zoomProperty;

    @Override
    public void initialize(java.net.URL arg0, ResourceBundle arg1) {
        menuBar.setFocusTraversable(true);
        histogram = new Histogram();
        histogramPane.getChildren().add(histogram.getAreaChart());
        image = new ImageObservable(labelDepth, labelHeight, labelWidth, imageCanvas, histogram);
        zoomProperty = 1;
    }

    public ImageView getImageCanvas() {
        return imageCanvas;
    }

    @Override
    public void setBufferedImage(BufferedImage image) {
        this.image.setBufferedImage(image);
    }

    /**
     * Handle action related to "About" menu item.
     *
     * @param event Event on "About" menu item.
     */
    @FXML
    private void handleAboutAction(final ActionEvent event)
    {
        provideAboutFunctionality();
    }

    /**
     * Handle action related to input (in this case specifically only responds to
     * keyboard event CTRL-A).
     *
     * @param event Input event.
     */
    @FXML
    private void handleKeyInput(final InputEvent event)
    {
        if (event instanceof KeyEvent)
        {
            final KeyEvent keyEvent = (KeyEvent) event;
            if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.A) {
                provideAboutFunctionality();
            } else if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.O) {
                handleOpen(null);
            } else if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.S) {
                handleSave(null);
            } else if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.R) {
                handleRevert(null);
            } else if (keyEvent.isControlDown() && keyEvent.isShiftDown() && keyEvent.getCode() == KeyCode.S) {
                handleSaveAs(null);
            }
        }
    }

    /**
     * Perform functionality associated with "About" menu selection or CTRL-A.
     */
    private void provideAboutFunctionality()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("APOZ");
        alert.setHeaderText("O programie");
        alert.setContentText("Autor: Robert Sotomski");
        alert.showAndWait();
    }


    public void handleExit(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void handleOpen(ActionEvent actionEvent) {
        openedFile = FileMenuUtils.openDialog(rootLayout);
        fileName.setText(openedFile.getName());
        image.setBufferedImage(FileMenuUtils.loadImage(openedFile));
    }

    public void handleSaveAs(ActionEvent actionEvent) {
        openedFile = FileMenuUtils.saveAsDialog(rootLayout, image.getBufferedImage());
    }

    public void handleSave(ActionEvent actionEvent) {
        FileMenuUtils.saveDialog(image.getBufferedImage(), openedFile);
    }

    public void handleHistogramEqualisation() {
        toolbox.getChildren().clear();
        toolbox.getChildren().add(HistogramEq.getInstance(this));
    }

    public void switchHistogram(Event event) {
        if(image.getChannels()==3) histogram.switchType();
    }

    @Override
    public Histogram getHistogram() {
        return histogram;
    }

    @Override
    public BufferedImage getBufferedImage() {
        return image.getBufferedImage();
    }

    public void handleMouseMoved(MouseEvent event) throws Exception {

        int x = (int)event.getX();
        int y = (int)event.getY();
        labelX.setText("X: " + x);
        labelY.setText("Y: " + y);
        int rgb = image.getBufferedImage().getRGB(x, y);
        int r, g, b;
        r = (rgb >> 16 ) & 0xFF;
        g = (rgb >> 8 ) & 0xFF;
        b = rgb & 0xFF;
        if(image.getChannels() == 3) {
            labelR.setText("R: " + r);
            labelG.setText("G: " + g);
            labelB.setText("B: " + b);
        } else {
            labelR.setText("K: " + b);
            labelG.setText("");
            labelB.setText("");
        }
    }

    public void handleConvertToGrayscale(ActionEvent actionEvent) {
        image.setBufferedImage(ImageUtils.rgbToGrayscale(image.getBufferedImage()));
    }

    public void handleRevert(ActionEvent actionEvent) {
        image.setBufferedImage(FileMenuUtils.loadImage(openedFile));
        imageCanvas.setImage(SwingFXUtils.toFXImage(image.getBufferedImage(), null));
        histogramPane.getChildren().add(histogram.getAreaChart());
    }

    public void handleZoomIn(Event event) {
        zoomProperty*=1.25;
        zoomProperty=zoomProperty>1?1:zoomProperty;
        imageCanvas.setFitWidth(zoomProperty*image.getBufferedImage().getWidth());
        imageCanvas.setFitHeight(zoomProperty+image.getBufferedImage().getHeight());
        zoomField.setText(zoomProperty*100+"%");
    }

    public void handleZoomOut(Event event) {
        zoomProperty/=1.25;
        imageCanvas.setFitWidth(zoomProperty*image.getBufferedImage().getWidth());
        imageCanvas.setFitHeight(zoomProperty+image.getBufferedImage().getHeight());
        zoomField.setText(zoomProperty*100+"%");
    }
}
