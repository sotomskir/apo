package pl.sotomski.apoz;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TabPane;
import javafx.scene.image.WritableImage;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.tools.HistogramEqTool;
import pl.sotomski.apoz.tools.ToolController;
import pl.sotomski.apoz.utils.FileMenuUtils;
import pl.sotomski.apoz.utils.HistogramChart;
import pl.sotomski.apoz.utils.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FilenameFilter;
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
    Label labelR, labelG, labelB, labelX, labelY, labelWidth, labelHeight, labelDepth, zoomLabel;
    @FXML
    TabPane tabPane;
    private ObjectProperty<ImageTab> activeTabProperty;

    @Override
    public void initialize(java.net.URL arg0, ResourceBundle arg1) {
        activeTabProperty = new SimpleObjectProperty<>();
        activeTabProperty.addListener(e -> handleActiveTabChanged());
        menuBar.setFocusTraversable(true);
        zoomLabel.setOnInputMethodTextChanged(e -> activeTabProperty.getValue().handleZoomChange(zoomLabel.getText()));

        tabPane.getSelectionModel().selectedItemProperty().addListener(e ->
        {
            ImageTab oldActiveTab = activeTabProperty.getValue();
            ImageTab newActiveTab = (ImageTab) tabPane.getSelectionModel().getSelectedItem();
            activeTabProperty.setValue(newActiveTab);
            ColorModel cm = newActiveTab.getImage().getColorModel();
            labelDepth.setText("Depth: " + cm.getPixelSize()/cm.getNumComponents());
            labelWidth.setText("Width: " + newActiveTab.getImage().getWidth());
            labelHeight.setText("Heigth: " + newActiveTab.getImage().getHeight());
        });

    }

    private void handleActiveTabChanged() {
        histogramPane.getChildren().clear();
        histogramPane.getChildren().add(activeTabProperty.getValue().getHistogramChart().getBarChart());
        zoomLabel.setText(activeTabProperty.getValue().getZoomProperty().multiply(100).getValue().intValue() + "%");
    }

    @Override
    public void setBufferedImage(BufferedImage image) {
        this.activeTabProperty.getValue().setImage(image);
    }

    @Override
    public ImageTab getActiveTabProperty() {
        return activeTabProperty.getValue();
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
            } else if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.D) {
                handleDuplicate(null);
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

    private void attachTab(ImageTab imageTab) {
        tabPane.getTabs().add(imageTab);

//        imageTab.setOnSelectionChanged(e -> {
//            ImageTab tab = (ImageTab) e.getSource();
//            if (activeTabProperty.getValue() != tab) activeTabProperty.setValue(tab);
//        });
        imageTab.getImageView().addEventHandler(MouseEvent.MOUSE_MOVED, this::handleMouseMoved);
        tabPane.getSelectionModel().select(imageTab);
    }

    public void handleOpen(ActionEvent actionEvent) {
        File file = FileMenuUtils.openDialog(rootLayout);
        ImageTab tab = new ImageTab(file);
        attachTab(tab);
    }

    public void handleDuplicate(ActionEvent actionEvent) {
        ImageTab activeTab = activeTabProperty.getValue();
        ImageTab tab = new ImageTab(activeTab);
        attachTab(tab);
    }

    public void handleRevert(ActionEvent actionEvent) {
        activeTabProperty.getValue().handleRevert(actionEvent);
    }

    public void handleSaveAs(ActionEvent actionEvent) {
        BufferedImage image = activeTabProperty.getValue().getImage();
        activeTabProperty.getValue().setFile(FileMenuUtils.saveAsDialog(rootLayout,image));
    }

    public void handleSave(ActionEvent actionEvent) {
        File file = activeTabProperty.getValue().getFile();
        FileMenuUtils.saveDialog(activeTabProperty.getValue().getImage(), file);
    }

    public void handleHistogramEqualisation() {
        toolbox.getChildren().clear();
        toolbox.getChildren().add(HistogramEqTool.getInstance(this));
    }

    public void switchHistogram(Event event) {
        getHistogramChart().switchType();
    }

    @Override
    public HistogramChart getHistogramChart() {
        return activeTabProperty.getValue().getHistogramChart();
    }

    @Override
    public BufferedImage getBufferedImage() {
        return activeTabProperty.getValue().getImage();
    }

    public void handleMouseMoved(MouseEvent event) {

        int x = (int)event.getX();
        int y = (int)event.getY();
        x/= activeTabProperty.getValue().getZoomProperty().getValue();
        y/= activeTabProperty.getValue().getZoomProperty().getValue();
        labelX.setText("X: " + x);
        labelY.setText("Y: " + y);
        int rgb = activeTabProperty.getValue().getImage().getRGB(x, y);
        int r, g, b;
        r = (rgb >> 16 ) & 0xFF;
        g = (rgb >> 8 ) & 0xFF;
        b = rgb & 0xFF;
        if(activeTabProperty.getValue().getChannels() == 3) {
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
        activeTabProperty.getValue().setImage(ImageUtils.rgbToGrayscale(activeTabProperty.getValue().getImage()));
        getHistogramChart().switchType();
    }

    public void handleZoomOut(Event event) {
        activeTabProperty.getValue().handleZoomOut(zoomLabel);
    }

    public void handleZoomIn(Event event) {
        activeTabProperty.getValue().handleZoomIn(zoomLabel);
    }

    public void handleScreenShot(ActionEvent actionEvent) {
        //TODO
        Scene scene = tabPane.getScene();
        WritableImage image = new WritableImage((int)scene.getWidth(), (int)scene.getHeight());
        scene.snapshot(image);
        String path = System.getProperty("user.home") + "/apoz_screenshots/";
        File apozDir = new File(path);
        try {
            apozDir.mkdir();
            String[] snapshots = apozDir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    String lowercaseName = name.toLowerCase();
                    if (lowercaseName.endsWith(".png") && lowercaseName.startsWith("snapshot")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            String lastSnapshot = snapshots[snapshots.length-1];
            String snumber = lastSnapshot.substring(8, 11);
            int number = Integer.valueOf(snumber)+1;
            File file = new File(path + "snapshot" + String.format("%03d", number) + ".png");
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
