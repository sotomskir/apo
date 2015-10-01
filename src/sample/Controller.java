package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
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
    private ImageView imageCanvas;
    private BufferedImage bufferedImage;
    private File openedFile;
    private Histogram histogram;
    private int depth;
    private int channels;

    @Override
    public void initialize(java.net.URL arg0, ResourceBundle arg1) {
        menuBar.setFocusTraversable(true);
    }

    public ImageView getImageCanvas() {
        return imageCanvas;
    }

    @Override
    public void setBufferedImage(BufferedImage image) {
        this.bufferedImage = image;
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
        bufferedImage = FileMenuUtils.loadImage(openedFile);
        imageCanvas.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
        histogram = new Histogram(bufferedImage);
        channels = +bufferedImage.getColorModel().getColorSpace().getNumComponents();
        depth = bufferedImage.getColorModel().getPixelSize();
        labelDepth.setText("Depth: " + depth + "bit, "+channels+" channel");
        labelWidth.setText("Width: " +bufferedImage.getWidth());
        labelHeight.setText("Heigth: " +bufferedImage.getHeight());
        histogramPane.getChildren().add(histogram.getAreaChart());
    }

    public void handleSaveAs(ActionEvent actionEvent) {
        openedFile = FileMenuUtils.saveAsDialog(rootLayout, bufferedImage);
    }

    public void handleSave(ActionEvent actionEvent) {
        FileMenuUtils.saveDialog(bufferedImage, openedFile);
    }

    public void handleHistogramEqualisation() {
        toolbox.getChildren().clear();
        toolbox.getChildren().add(HistogramEq.getInstance(this));
    }

    public void switchHistogram(Event event) {
        if(channels==3) histogram.switchType();
    }

    public void handleSampleTool(ActionEvent actionEvent) {
        toolbox.getChildren().clear();
        toolbox.getChildren().add(SampleTool.getInstance(this));
    }

    @Override
    public Histogram getHistogram() {
        return histogram;
    }

    @Override
    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void handleMouseMoved(MouseEvent event) {

        int x = (int)event.getX();
        int y = (int)event.getY();
        labelX.setText("X: "+x);
        labelY.setText("Y: "+y);
        int rgb = bufferedImage.getRGB(x, y);
        int r, g, b;
        r = (rgb >> 16 ) & 0xFF;
        g = (rgb >> 8 ) & 0xFF;
        b = rgb & 0xFF;
        if(channels==3) {
            labelR.setText("R: "+r);
            labelG.setText("G: "+g);
            labelB.setText("B: "+b);
        } else {
            labelR.setText("K: "+b);
            labelG.setText("");
            labelB.setText("");
        }
    }
}
