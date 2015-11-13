package pl.sotomski.apoz.controllers;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
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
import javafx.stage.Stage;
import javafx.stage.Window;
import pl.sotomski.apoz.Main;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.ConvertToGrayCommand;
import pl.sotomski.apoz.commands.NegativeCommand;
import pl.sotomski.apoz.nodes.HistogramPane;
import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.nodes.ImageTab;
import pl.sotomski.apoz.nodes.ImageWindow;
import pl.sotomski.apoz.tools.*;
import pl.sotomski.apoz.utils.FileMenuUtils;
import pl.sotomski.apoz.utils.ImageUtils;
import pl.sotomski.apoz.utils.UTF8Control;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class MainController implements Initializable, ToolController {

    private ResourceBundle bundle;
    private HistogramPane histogramPane;
    private ObjectProperty<ImagePane> activePaneProperty;
    private Preferences prefs;
    @FXML private MenuBar menuBar;
    @FXML private VBox toolbox;
    @FXML private Pane histogramPaneContainer;
    @FXML private BorderPane rootLayout;
    @FXML Label labelR, labelG, labelB, labelX, labelY, labelWidth, labelHeight, labelDepth, zoomLabel;
    @FXML TabPane tabPane;
    @FXML private Button undoButton;
    @FXML private Button redoButton;

    /***************************************************************************
     *                                                                         *
     *                               METHODS                                   *
     *                                                                         *
     **************************************************************************/
    @Override public void initialize(java.net.URL arg0, ResourceBundle resources) {
        prefs = Preferences.userNodeForPackage(Main.class);

        bundle = resources;
        activePaneProperty = new SimpleObjectProperty<>();
        histogramPane = new HistogramPane(bundle);
        histogramPaneContainer.getChildren().add(histogramPane);
        activePaneProperty.addListener(e -> {
            if (activePaneProperty.getValue().isTabbed()) {
                histogramPane.update(activePaneProperty.getValue().getImage());
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

    private void updateLabels(ImagePane pane) {
        labelDepth.setText(bundle.getString("Depth") + ": " + (pane.getImage().getColorModel().getNumComponents() > 1 ? "RGB" : "Gray"));
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

    /**
     * Perform functionality associated with "About" menu selection or CTRL-A.
     */
    private void provideAboutFunctionality() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("APOZ");
        alert.setHeaderText(bundle.getString("About"));
        alert.setContentText(bundle.getString("Author"));
        alert.showAndWait();
    }

    private void attachTab(ImageTab imageTab) {
        tabPane.getTabs().add(imageTab);
        imageTab.getPane().getImageView().addEventHandler(MouseEvent.MOUSE_MOVED, this::handleMouseMoved);
        tabPane.getSelectionModel().select(imageTab);
    }

    private void addToToolbox(Pane tool) {
        toolbox.getChildren().clear();
        toolbox.getChildren().add(tool);
    }

    /***************************************************************************
     *                                                                         *
     *                  Controller Method Event Handlers                       *
     *                                                                         *
     **************************************************************************/

    /**
     * Handle action related to "About" menu item.
     *
     * @param event Event on "About" menu item.
     */
    @FXML private void handleAboutAction(final ActionEvent event) {
        provideAboutFunctionality();
    }

    /**
     * Handle action related to keyboard input.
     *
     * @param event Input event.
     */
    @FXML private void handleKeyInput(final InputEvent event) {
        if (event instanceof KeyEvent) {
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
    public void handleExit(ActionEvent actionEvent) {
        System.exit(0);
    }
    public void handleOpen(ActionEvent actionEvent) {
        File file = FileMenuUtils.openFileDialog(rootLayout);
        ImagePane pane = new ImagePane(histogramPane, file);
        ImageTab imageTab = new ImageTab(pane);
        attachTab(imageTab);
    }

    public void handleDuplicate(ActionEvent actionEvent) {
        ImagePane activePane = activePaneProperty.getValue();
        ImagePane pane = new ImagePane(histogramPane, activePane);
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

    public void handleConvertToGreyscale(ActionEvent actionEvent) {
        CommandManager manager = activePaneProperty.getValue().getCommandManager();
        manager.executeCommand(new ConvertToGrayCommand(activePaneProperty.getValue()));
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
        String path = prefs.get(PrefsController.SCREENSHOT_PATH, System.getProperty("user.home") + "/apoz_screenshots/");
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

            int currentSnapshotNumber;
            if(snapshots.length>0) {
                String lastSnapshot = snapshots[snapshots.length-1];
                String snumber = lastSnapshot.substring(8, 11);
                currentSnapshotNumber = Integer.valueOf(snumber)+1;
            } else currentSnapshotNumber = 1;
            File file = new File(path + "snapshot" + String.format("%03d", currentSnapshotNumber) + ".png");
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void handleUnpinTab(ActionEvent actionEvent) {
        ImageTab selectedTab = (ImageTab) tabPane.getSelectionModel().getSelectedItem();
        selectedTab.getPane().setTabbed(false);
        tabPane.getTabs().remove(selectedTab);
        Window parent = rootLayout.getScene().getWindow();
        ImagePane imagePane = selectedTab.getPane();
        ImageWindow window = new ImageWindow(parent, imagePane, new HistogramPane(bundle));
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
    public void handlePreferences(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader();
            String lang = prefs.get(PrefsController.LANGUAGE, Locale.getDefault().getLanguage());
            Locale locale = new Locale(lang);
            ResourceBundle bundle = ResourceBundle.getBundle("bundles.ApozBundle", locale, new UTF8Control());
            loader.setResources(bundle);
            Parent root = loader.load(getClass().getClassLoader().getResource("Preferences.fxml").openStream());
            Stage preferencesStage = new Stage();
            preferencesStage.setTitle("Preferences");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(String.valueOf(getClass().getClassLoader().getResource("style.css")));
            preferencesStage.setScene(scene);
            preferencesStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void handleNegativeConversion(ActionEvent actionEvent) {
        CommandManager manager = activePaneProperty.getValue().getCommandManager();
        manager.executeCommand(new NegativeCommand(activePaneProperty.getValue()));
    }
    public void handleThresholding(ActionEvent actionEvent) {
        addToToolbox(ThresholdTool.getInstance(this));
    }

    public void handleIntervalThreshloding(ActionEvent actionEvent) {
        addToToolbox(IntervalThresholdTool.getInstance(this));
    }

    public void handleSampleTool(ActionEvent actionEvent) {
        addToToolbox(SampleTool.getInstance(this));
    }

    public void handleLevelsReduction(ActionEvent actionEvent) {
        addToToolbox(LevelsReductionTool.getInstance(this));
    }

    public void handleBrightnessContrast(ActionEvent actionEvent) {
        addToToolbox(BrightnessContrastTool.getInstance(this));
    }

    public void handleCurves(ActionEvent actionEvent) {
        addToToolbox(CurvesTool.getInstance(this));
    }
    public void handleMaskTool(ActionEvent actionEvent) {
        addToToolbox(MaskTool.getInstance(this));
    }

    public void handleBinaryOperationsTool(ActionEvent actionEvent) {
        addToToolbox(BinaryOperationsTool.getInstance(this));
    }


    /***************************************************************************
     *                                                                         *
     *                               GETTERS                                   *
     *                                                                         *
     **************************************************************************/

    public ResourceBundle getBundle() {
        return bundle;
    }

    @Override public BufferedImage getBufferedImage() {
        return activePaneProperty.getValue().getImage();
    }
    @Override public ImagePane getActivePaneProperty() {
        return activePaneProperty.getValue();
    }

}
