package pl.sotomski.apoz;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.ConvertToGrayCommand;
import pl.sotomski.apoz.tools.HistogramEqTool;
import pl.sotomski.apoz.tools.ToolController;
import pl.sotomski.apoz.utils.FileMenuUtils;
import pl.sotomski.apoz.utils.HistogramManager;
import pl.sotomski.apoz.utils.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ResourceBundle;

public class MainController implements Initializable, ToolController {
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
    private ObjectProperty<ImagePane> activePaneProperty;
    @FXML
    private Button undoButton;
    @FXML
    private Button redoButton;
    private ResourceBundle bundle;

    @Override
    public void initialize(java.net.URL arg0, ResourceBundle resources) {
        bundle = resources;
        activePaneProperty = new SimpleObjectProperty<>();

        activePaneProperty.addListener(e -> {
            if (activePaneProperty.getValue().isTabbed()) {
                histogramPane.getChildren().clear();
                histogramPane.getChildren().add(activePaneProperty.getValue().getHistogramManager().getBarChart());
            }
            zoomLabel.setText(activePaneProperty.getValue().getZoomProperty().multiply(100).getValue().intValue() + "%");
            updateUndoRedoListeners();
        });

        menuBar.setFocusTraversable(false);
        zoomLabel.setOnInputMethodTextChanged(e -> activePaneProperty.getValue().handleZoomChange(zoomLabel.getText()));

        tabPane.getSelectionModel().selectedItemProperty().addListener(e ->
        {
            ImagePane oldActivePane = activePaneProperty.getValue();
            ImageTab newActiveTab = (ImageTab) tabPane.getSelectionModel().getSelectedItem();

            if (newActiveTab != null) {
                activePaneProperty.setValue(newActiveTab.getPane());
                newActiveTab.getPane().imageVersionProperty().addListener(ev -> {
                    updateLabels(activePaneProperty.getValue());
                });
                updateLabels(newActiveTab.getPane());
            }

        });

        tabPane.focusedProperty().addListener(e -> {
            ImageTab selectedTab = (ImageTab) tabPane.getSelectionModel().getSelectedItem();
            activePaneProperty.setValue(selectedTab.getPane());
        });

    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    private void updateLabels(ImagePane pane) {
        labelDepth.setText(bundle.getString("Depth") + ": " + (pane.getImage().getColorModel().getNumComponents()>1?"RGB":"Gray"));
        labelWidth.setText(bundle.getString("Width") + ": " + pane.getImage().getWidth());
        labelHeight.setText(bundle.getString("Heigth") + ": " + pane.getImage().getHeight());
    }

    private void updateUndoRedoListeners() {
        activePaneProperty.getValue().getCommandManager().undoAvailableProperty().addListener(e -> {
            undoButton.setDisable(!activePaneProperty.getValue().getCommandManager().getUndoAvailable());
        });
        activePaneProperty.getValue().getCommandManager().redoAvailableProperty().addListener(e -> {
            redoButton.setDisable(!activePaneProperty.getValue().getCommandManager().getRedoAvailable());
        });
        undoButton.setDisable(!activePaneProperty.getValue().getCommandManager().getUndoAvailable());
        redoButton.setDisable(!activePaneProperty.getValue().getCommandManager().getRedoAvailable());
    }

    @Override
    public void setBufferedImage(BufferedImage image) {
        this.activePaneProperty.getValue().setImage(image);
    }

    @Override
    public ImagePane getActivePaneProperty() {
        return activePaneProperty.getValue();
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
     * Handle action related to keyboard input.
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
            } else if (keyEvent.isControlDown() && keyEvent.isShiftDown() && keyEvent.getCode() == KeyCode.Z) {
                handleRedo(null);
            } else if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.Z) {
                handleUndo(null);
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
        alert.setHeaderText(bundle.getString("About"));
        alert.setContentText(bundle.getString("Author"));
        alert.showAndWait();
    }


    public void handleExit(ActionEvent actionEvent) {
        System.exit(0);
    }

    private void attachTab(ImageTab imageTab) {
        tabPane.getTabs().add(imageTab);
        imageTab.getPane().getImageView().addEventHandler(MouseEvent.MOUSE_MOVED, this::handleMouseMoved);
        tabPane.getSelectionModel().select(imageTab);
    }

    public void handleOpen(ActionEvent actionEvent) {
        File file = FileMenuUtils.openDialog(rootLayout);
        ImagePane pane = new ImagePane(file);
        ImageTab imageTab = new ImageTab(pane);
        attachTab(imageTab);
    }

    public void handleDuplicate(ActionEvent actionEvent) {
        ImagePane activePane = activePaneProperty.getValue();
        ImagePane pane = new ImagePane(activePane);
        ImageTab tab = new ImageTab(pane);
        attachTab(tab);
    }

    public void handleRevert(ActionEvent actionEvent) {
        activePaneProperty.getValue().handleRevert(actionEvent);
    }

    public void handleSaveAs(ActionEvent actionEvent) {
        BufferedImage image = activePaneProperty.getValue().getImage();
        activePaneProperty.getValue().setFile(FileMenuUtils.saveAsDialog(rootLayout, image));
    }

    public void handleSave(ActionEvent actionEvent) {
        File file = activePaneProperty.getValue().getFile();
        FileMenuUtils.saveDialog(activePaneProperty.getValue().getImage(), file);
    }

    public void handleHistogramEqualisation() {
        toolbox.getChildren().clear();
        toolbox.getChildren().add(HistogramEqTool.getInstance(this));
    }

    public void switchHistogram(Event event) {
        getHistogramChart().switchType();
    }

    @Override
    public HistogramManager getHistogramChart() {
        return activePaneProperty.getValue().getHistogramManager();
    }

    @Override
    public BufferedImage getBufferedImage() {
        return activePaneProperty.getValue().getImage();
    }

    public void handleMouseMoved(MouseEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        x/= activePaneProperty.getValue().getZoomProperty().getValue();
        y/= activePaneProperty.getValue().getZoomProperty().getValue();
        labelX.setText("X: " + x);
        labelY.setText("Y: " + y);
        int rgb = activePaneProperty.getValue().getImage().getRGB(x, y);
        if(activePaneProperty.getValue().getChannels() == 3) {
            labelR.setText("R: " + ImageUtils.getR(rgb));
            labelG.setText("G: " + ImageUtils.getG(rgb));
            labelB.setText("B: " + ImageUtils.getB(rgb));
        } else {
            labelR.setText("K: " + ImageUtils.getB(rgb));
            labelG.setText("");
            labelB.setText("");
        }
    }

    public void handleConvertToGrayscale(ActionEvent actionEvent) {
        CommandManager manager = activePaneProperty.getValue().getCommandManager();
        manager.executeCommand(new ConvertToGrayCommand(activePaneProperty.getValue()));
        getHistogramChart().switchType();
    }

    public void handleZoomOut(Event event) {
        activePaneProperty.getValue().handleZoomOut(zoomLabel);
    }

    public void handleZoomIn(Event event) {
        activePaneProperty.getValue().handleZoomIn(zoomLabel);
    }

    public void handleScreenShot(ActionEvent actionEvent) {
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
                    return lowercaseName.endsWith(".png") && lowercaseName.startsWith("snapshot");
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

    public void handleUnpinTab(ActionEvent actionEvent) {
        //TODO
        ImageTab selectedTab = (ImageTab) tabPane.getSelectionModel().getSelectedItem();
        selectedTab.getPane().setTabbed(false);
        tabPane.getTabs().remove(selectedTab);
        Window parent = rootLayout.getScene().getWindow();
        ImageWindow window = new ImageWindow(parent, selectedTab.getPane());
        window.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue)
            {
                activePaneProperty.setValue(window.getImagePane());
                window.toFront();
                System.out.println("Window on focus: "+window.getTitle());
            }
            else
            {
                System.out.println("Window out focus: "+window.getTitle());
            }
        });
    }

    public void handleUndo(ActionEvent actionEvent) {
        CommandManager manager = activePaneProperty.getValue().getCommandManager();
        manager.undo();

    }

    public void handleRedo(ActionEvent actionEvent) {
        CommandManager manager = activePaneProperty.getValue().getCommandManager();
        manager.redo();
    }
}
