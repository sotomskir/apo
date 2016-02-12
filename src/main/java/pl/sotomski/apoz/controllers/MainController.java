package pl.sotomski.apoz.controllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import pl.sotomski.apoz.Main;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.ConvertToGrayCommand;
import pl.sotomski.apoz.commands.NegativeCommand;
import pl.sotomski.apoz.commands.TurtleAlgorithmCommand;
import pl.sotomski.apoz.nodes.*;
import pl.sotomski.apoz.tools.*;
import pl.sotomski.apoz.utils.FileMenuUtils;
import pl.sotomski.apoz.utils.ImageUtils;
import pl.sotomski.apoz.utils.UTF8Control;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class MainController implements Initializable, ToolController {


    /***************************************************************************
     *                                                                         *
     *                               FIELDS                                    *
     *                                                                         *
     **************************************************************************/
    private ResourceBundle bundle;
    private ChartsPane histogramPane;
    private ObjectProperty<ImagePane> activePane;
    private Preferences prefs;
    @FXML public ScrollPane toolboxScrollPane;
    //    @FXML public HBox labels;
    @FXML private MenuBar menuBar;
    @FXML private VBox toolbox;
    @FXML private VBox histogramPaneContainer;
    @FXML private BorderPane rootLayout;
    //    @FXML Label labelR, labelG, labelB, labelX, labelY, labelWidth, labelHeight, labelDepth, zoomLabel;
    MenuItem labelR = new MenuItem();
    MenuItem labelG = new MenuItem();
    MenuItem labelB = new MenuItem();
    MenuItem labelX = new MenuItem();
    MenuItem labelY = new MenuItem();
    ContextMenu labels = new ContextMenu(labelX, labelY, labelR, labelG, labelB);
    @FXML Label zoomLabel;
    @FXML TabPane tabPane;
    @FXML Button pinButton;
    @FXML Button revertBtn;
    @FXML private ToggleButton pointerButton;
    @FXML private ToggleButton cropButton;
    @FXML private ToggleButton profileLineButton;
    private final BooleanProperty needsImage = new SimpleBooleanProperty(true);
    private final BooleanProperty undoUnavailable = new SimpleBooleanProperty(true);
    private final BooleanProperty redoUnavailable = new SimpleBooleanProperty(true);
    @FXML private Button toggleHistogramViewBtn;
    private ProfileLine profileLine;
    CropRectangle cropRectangle;


    /***************************************************************************
     *                                                                         *
     *                          GETTERS & SETTERS                              *
     *                                                                         *
     **************************************************************************/
    public ResourceBundle getBundle() {
        return bundle;
    }

    public boolean getNeedsImage() {
        return needsImage.get();
    }

    public BooleanProperty needsImageProperty() {
        return needsImage;
    }

    public void setNeedsImage(boolean needsImage) {
        this.needsImage.set(needsImage);
    }

    public boolean getUndoUnavailable() {
        return undoUnavailable.get();
    }

    public BooleanProperty undoUnavailableProperty() {
        return undoUnavailable;
    }

    public void setUndoUnavailable(boolean undoUnavailable) {
        this.undoUnavailable.set(undoUnavailable);
    }

    public boolean getRedoUnavailable() {
        return redoUnavailable.get();
    }

    public BooleanProperty redoUnavailableProperty() {
        return redoUnavailable;
    }

    public void setRedoUnavailable(boolean redoUnavailable) {
        this.redoUnavailable.set(redoUnavailable);
    }

    @Override public BufferedImage getBufferedImage() {
        return activePane.getValue().getImage();
    }
    public ImagePane getActivePane() {
        return activePane.getValue();
    }

    @Override
    public CropRectangle getCropRectangle() {
        return cropRectangle;
    }

    public TabPane getTabPane() {
        return tabPane;
    }

    public ToggleButton getPointerButton() {
        return pointerButton;
    }

    /***************************************************************************
     *                                                                         *
     *                               METHODS                                   *
     *                                                                         *
     **************************************************************************/
    @Override public void initialize(java.net.URL arg0, ResourceBundle resources) {
        prefs = Preferences.userNodeForPackage(Main.class);
        bundle = resources;
        activePane = new SimpleObjectProperty<>();
        histogramPane = new ChartsPane(bundle);
        histogramPaneContainer.getChildren().add(histogramPane);
        activePane.addListener(e -> {
            if (activePane.getValue() != null) {
                if (activePane.getValue().getWindow() == null) {
                    pinButton.setText(bundle.getString("mdi-pin-off"));
                } else {
                    pinButton.setText(bundle.getString("mdi-pin"));
//                    histogramPane.clear();
                }
                if(activePane.getValue().isTabbed())
                    histogramPane.update(activePane.getValue().getImage());
                zoomLabel.setText(String.format("%.0f%%", activePane.getValue().getZoomLevel()*100));
                System.out.println("Selected: " + activePane.getValue().getName());
            }
            ImagePane imagePane = activePane.getValue();
            needsImage.setValue(imagePane == null);
            revertBtn.setDisable(!(imagePane != null && imagePane.getFile() != null));
        });

        revertBtn.setDisable(true);
        menuBar.setFocusTraversable(false);

        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            ImageTab oldActiveTab = (ImageTab) oldValue;
            ImageTab newActiveTab = (ImageTab) newValue;

            if (oldActiveTab != null) {
                undoUnavailable.unbind();
                redoUnavailable.unbind();
            }
            if (newActiveTab != null) {
                activePane.setValue(newActiveTab.getPane());
                newActiveTab.getPane().imageVersionProperty().addListener(ev -> {
                    updateLabels(activePane.getValue());
                });
                updateLabels(newActiveTab.getPane());
                undoUnavailable.bind(newActiveTab.getPane().getCommandManager().undoAvailableProperty().not());
                redoUnavailable.bind(newActiveTab.getPane().getCommandManager().redoAvailableProperty().not());
            } else {
                activePane.setValue(null);
                undoUnavailable.setValue(true);
                redoUnavailable.setValue(true);
            }
        });

        tabPane.focusedProperty().addListener(e -> {
            ImageTab selectedTab = (ImageTab) tabPane.getSelectionModel().getSelectedItem();
            if (selectedTab != null) activePane.setValue(selectedTab.getPane());
        });

//        labels.setMinHeight(Control.USE_PREF_SIZE);
        histogramPaneContainer.setMinHeight(Control.USE_PREF_SIZE);
        toolboxScrollPane.setFitToHeight(true);

//        ToggleGroup pointersToggleGroup = new ToggleGroup();
//        pointerButton.setToggleGroup(pointersToggleGroup);
//        cropButton.setToggleGroup(pointersToggleGroup);
//        profileLineButton.setToggleGroup(pointersToggleGroup);
//        pointerButton.setSelected(true);
        final String os = System.getProperty ("os.name");
        if (os != null && os.startsWith ("Mac"))
            menuBar.useSystemMenuBarProperty ().set (true);
//        histogramPane.getMonoHistogramChart().setValueLabel(histogramValueLabel);

//        labelR.setFont(Font.font("monospace"));
//        labelG.setFont(Font.font("monospace"));
//        labelB.setFont(Font.font("monospace"));
//        labelB.setFont(Font.font("monospace"));
//        labelDepth.setFont(Font.font("monospace"));
//        labelHeight.setFont(Font.font("monospace"));
//        labelWidth.setFont(Font.font("monospace"));
//        labelX.setFont(Font.font("monospace"));
//        labelY.setFont(Font.font("monospace"));
        toggleHistogramViewBtn.setStyle("-fx-font-size:8;");
        histogramPane.setMaxHeight(0);
        labels.getStyleClass().add("dark");
    }

    private void updateLabels(ImagePane pane) {
//        labelDepth.setText(bundle.getString("Depth") + ": " + (pane.getImage().getColorModel().getNumComponents() > 1 ? "RGB" : "Gray"));
//        labelWidth.setText(bundle.getString("Width") + ": " + pane.getImage().getWidth());
//        labelHeight.setText(bundle.getString("Heigth") + ": " + pane.getImage().getHeight());
    }

    /**
     * Perform functionality associated with "About" menu selection or CTRL-A.
     */
    private void provideAboutFunctionality() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("APOZ");
        alert.setHeaderText(bundle.getString("About"));
        alert.setContentText(bundle.getString("Author") + "\nhttps://github.com/sotomskir/apo");
        alert.showAndWait();
    }

    public void attachTab(ImageTab imageTab) {
        tabPane.getTabs().add(imageTab);
        imageTab.getPane().getImageView().addEventHandler(MouseEvent.MOUSE_MOVED, this::handleMouseMoved);
        imageTab.getPane().getImageView().addEventHandler(MouseEvent.MOUSE_EXITED, this::handleMouseExited);
        imageTab.getPane().getImageView().addEventHandler(MouseEvent.MOUSE_ENTERED, this::handleMouseEntered);
        tabPane.getSelectionModel().select(imageTab);
    }

    private void addToToolbox(Tool tool) {
        disableTools();
        activePane.getValue().refresh();
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
            } else if (keyEvent.getCode() == KeyCode.ENTER) {
                Tool tool = (Tool) toolbox.getChildren().get(0);
                if(tool != null) tool.handleApply(null);
            } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                Tool tool = (Tool) toolbox.getChildren().get(0);
                if(tool != null) tool.handleCancel();
                disableTools();
                disableProfileLine();
                disableCropRectangle();
            }
        }
    }

    public void handleExit(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void handleOpen(ActionEvent actionEvent) {
        File file = FileMenuUtils.openFileDialog(rootLayout);
        if (file != null) {
            ImagePane pane = new ImagePane(histogramPane, file);
            ImageTab imageTab = new ImageTab(pane);
            attachTab(imageTab);
        }
    }

    public void handleDuplicate(ActionEvent actionEvent) {
        ImagePane activePane = this.activePane.getValue();
        ImagePane pane = new ImagePane(histogramPane, activePane);
        ImageTab tab = new ImageTab(pane);
        attachTab(tab);
    }

    public void handleRevert(ActionEvent actionEvent) {
        activePane.getValue().handleRevert(actionEvent);
    }

    public void handleSaveAs(ActionEvent actionEvent) {
        BufferedImage image = activePane.getValue().getImage();
        File file = FileMenuUtils.saveAsDialog(rootLayout, image);
        if (file != null) {
            activePane.getValue().setFile(file);
            activePane.getValue().setName(file.getName());
        }
    }

    public void handleSave(ActionEvent actionEvent) {
        File file = activePane.getValue().getFile();
        if (file != null) {
            FileMenuUtils.saveDialog(activePane.getValue().getImage(), file);
        } else handleSaveAs(null);
    }

    public void handleHistogramEqualisation() {
        toolbox.getChildren().clear();
        toolbox.getChildren().add(HistogramEqTool.getInstance(this));
    }

    public void handleMouseMoved(MouseEvent event) {
        int x = (int)(event.getX()/activePane.getValue().getZoomLevel());
        int y = (int)(event.getY()/activePane.getValue().getZoomLevel());
        labelX.setText("X: " + (x+1));
        labelY.setText("Y: " + (y+1));
        try {
            int rgb = activePane.getValue().getImage().getRGB(x, y);
            if(activePane.getValue().getChannels() == 3) {
                labelR.setText("R: " + ImageUtils.getR(rgb));
                labelG.setText("G: " + ImageUtils.getG(rgb));
                labelB.setText("B: " + ImageUtils.getB(rgb));
            } else {
                labelR.setText("K: " + ImageUtils.getB(rgb));
                labelG.setText("");
                labelB.setText("");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        labels.setX(event.getSceneX()+25);
        labels.setY(event.getSceneY()+25);
    }

    public void handleMouseEntered(MouseEvent event) {
        if(!profileLineButton.isSelected() && !cropButton.isSelected())
            labels.show(rootLayout.getScene().getWindow());
    }

    public void handleMouseExited(MouseEvent event) {
        labels.hide();
//        labelX.setText("X: -");
//        labelY.setText("Y: -");
//            if(activePane.getValue().getChannels() == 3) {
//                labelR.setText("R: -");
//                labelG.setText("G: -");
//                labelB.setText("B: -");
//            } else {
//                labelR.setText("K: -");
//                labelG.setText("");
//                labelB.setText("");
//            }
    }

    public void handleConvertToGreyscale(ActionEvent actionEvent) {
        CommandManager manager = activePane.getValue().getCommandManager();
        manager.executeCommand(new ConvertToGrayCommand(activePane.getValue()));
    }

    public void handleZoomOut(Event event) {
        activePane.getValue().handleZoomOut(zoomLabel);
    }

    public void handleZoomIn(Event event) {
        activePane.getValue().handleZoomIn(zoomLabel);
    }

    public void handleScreenShot(ActionEvent actionEvent) {
        Scene scene = tabPane.getScene();
        WritableImage image = new WritableImage((int)scene.getWidth(), (int)scene.getHeight());
        scene.snapshot(image);
        String path = prefs.get(PrefsController.SCREENSHOT_PATH, System.getProperty("user.home") + "/apoz_screenshots/");
        File apozDir = new File(path);
        try {
            apozDir.mkdir();
            String[] snapshots = apozDir.list((dir, name) -> {
                String lowercaseName = name.toLowerCase();
                return lowercaseName.endsWith(".png") && lowercaseName.startsWith("snapshot");
            });

            int currentSnapshotNumber;
            if(snapshots.length>0) {
                String lastSnapshot = snapshots[snapshots.length-1];
                String snumber = lastSnapshot.substring(8, 11);
                currentSnapshotNumber = Integer.valueOf(snumber)+1;
            } else currentSnapshotNumber = 1;
            File file = new File(path + "/snapshot" + String.format("%03d", currentSnapshotNumber) + ".png");
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleUnpinTab(ActionEvent actionEvent) {
        Window window = getActivePane().getWindow();
        // ImagePane tabbed
        if(window == null) {
            ImageTab selectedTab = (ImageTab) tabPane.getSelectionModel().getSelectedItem();
            selectedTab.getPane().setTabbed(false);
            tabPane.getTabs().remove(selectedTab);
            Window parent = rootLayout.getScene().getWindow();
            ImagePane imagePane = selectedTab.getPane();
            ImageWindow imageWindow = new ImageWindow(parent, imagePane, new ChartsPane(bundle));
            imagePane.setWindow(imageWindow);
            imageWindow.setOnCloseRequest(e -> {
                imagePane.onClose();
                activePane.setValue(null);
            });
            pinButton.setText(bundle.getString("mdi-pin"));
            imageWindow.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
                if (newPropertyValue) {
                    activePane.setValue(imageWindow.getImagePane());
                    imageWindow.toFront();
                    System.out.println("Window on focus: " + imageWindow.getTitle());
                } else {
                    System.out.println("Window out focus: " + imageWindow.getTitle());
                }
            });
            activePane.setValue(imagePane);

            //ImagePane windowed
        } else {
            ImagePane pane = activePane.getValue();
            Stage stage = (Stage) pane.getWindow();
            pane.setHistogramPane(histogramPane);
            pane.setWindow(null);
            attachTab(new ImageTab(pane));
            pinButton.setText(bundle.getString("mdi-pin-off"));
            pane.onClose();
            stage.close();
        }
    }

    public void handleUndo(ActionEvent actionEvent) {
        CommandManager manager = activePane.getValue().getCommandManager();
        manager.undo();

    }

    public void handleRedo(ActionEvent actionEvent) {
        CommandManager manager = activePane.getValue().getCommandManager();
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
        CommandManager manager = activePane.getValue().getCommandManager();
        manager.executeCommand(new NegativeCommand(activePane.getValue()));
    }

    public void handleThresholding(ActionEvent actionEvent) {
        addToToolbox(ThresholdTool.getInstance(this));
    }

    public void handleIntervalThreshloding(ActionEvent actionEvent) {
        addToToolbox(IntervalThresholdTool.getInstance(this));
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
        addToToolbox(LinearFilterTool.getInstance(this));
    }

    public void handleBinaryOperationsTool(ActionEvent actionEvent) {
        addToToolbox(BinaryOperationsTool.getInstance(this));
    }

    public void handleMorphTool(ActionEvent actionEvent) {
        addToToolbox(MorphTool.getInstance(this));
    }

    public void handleMedianTool(ActionEvent actionEvent) {
        addToToolbox(MedianTool.getInstance(this));
    }

    public void handleLogicalFilterTool(ActionEvent actionEvent) {
        addToToolbox(LogicalFilterTool.getInstance(this));
    }

    public void handleTwoStepFilterTool(ActionEvent actionEvent) {
        addToToolbox(TwoStepFilterTool.getInstance(this));
    }





    public void toggleCropTool(ActionEvent actionEvent) {
        if(cropButton.isSelected()) {
            disableTools();
            addToToolbox(CropTool.getInstance(this));
            cropButton.setSelected(true);
            rootLayout.getScene().setCursor(Cursor.CROSSHAIR);
            enableCropSelection();
            handleMouseExited(null);
        } else disableCropRectangle();
    }

    public void handleCropToolMenu(ActionEvent actionEvent) {
        cropButton.setSelected(true);
        toggleCropTool(actionEvent);
    }

    EventHandler<MouseEvent> cropDragHandler = event -> {
//        if (!cropButton.isPressed()) {
            MouseEvent mouseEvent = (MouseEvent) event;
            double newX = mouseEvent.getX();
            double newY = mouseEvent.getY();
            double maxX = getActivePane().getImage().getWidth() * getActivePane().getZoomLevel();
            double maxY = getActivePane().getImage().getHeight() * getActivePane().getZoomLevel();
            System.out.println("Mouse dragged");
            if (newX > 0 && newX < maxX && newY > 0 && newY < maxY) {
                cropRectangle.setEnd((int) (newX), (int) (newY));
            }
//        }
    };

    public void enableCropSelection() {
        ImagePane p = getActivePane();
        p.getImageStack().clear();
//        p.getImageView().setOnMousePressed(event -> {});
//        p.getImageView().setOnMouseReleased(event -> {});
//        imageView.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {});
        cropRectangle = new CropRectangle(p.zoomLevelProperty());
//        p.getImageStack().push(cropRectangle);
//        int width = p.getImage().getWidth();
//        int height = p.getImage().getHeight();
//        cropRectangle.setStart(width*0.25, height*0.25);
//        cropRectangle.setEnd(width*0.75, height*0.75);

        p.getImageView().setOnMousePressed(mouseEvent -> {
            p.getImageStack().clear();
            p.getImageStack().push(cropRectangle);
            cropRectangle.setStart(mouseEvent.getX(), mouseEvent.getY());
            cropRectangle.setEnd(mouseEvent.getX(), mouseEvent.getY());
            System.out.println("Mouse pressed X:" + cropRectangle.x1 + " Y:" + cropRectangle.y1 + " source:" + mouseEvent.getSource().hashCode());
        });

        p.getImageView().addEventFilter(MouseEvent.MOUSE_DRAGGED, cropDragHandler);

        p.getImageView().setOnMouseReleased(mouseEvent -> {
            disableCropRectangle();
//            p.getImageStack().clear();
            p.refresh();
        });
    }

    public void disableCropRectangle() {
//        cropRectangle.setSelected(false);
        rootLayout.getScene().setCursor(Cursor.DEFAULT);
        getActivePane().getImageView().removeEventFilter(MouseEvent.MOUSE_DRAGGED, cropDragHandler);
        getActivePane().getImageView().setOnMousePressed(event -> {});
    }

    public void disableTools() {
        rootLayout.getScene().setCursor(Cursor.DEFAULT);
//        disableTools();
        toolbox.getChildren().clear();
        toolbox.getChildren().add(EmptyTool.getInstance(this));
        cropButton.setSelected(false);
        profileLineButton.setSelected(false);
        histogramPane.updateProfileLineChart(null);
        activePane.getValue().getImageStack().clear();
        activePane.getValue().refresh();
    }





    public void handleProfileLineToolMenu(ActionEvent actionEvent) {
        profileLineButton.setSelected(true);
        toggleProfileLineTool(actionEvent);
    }

    public void toggleProfileLineTool(ActionEvent actionEvent) {
        if(profileLineButton.isSelected()) {
            disableTools();
            profileLineButton.setSelected(true);
            histogramPane.selectProfileLineChart();
            rootLayout.getScene().setCursor(Cursor.CROSSHAIR);
            enableProfileLineSelection();
            handleMouseExited(null);
            if (histogramPane.getHeight() == 0) handleToggleHistogramView(null);
        } else disableProfileLine();
    }

    public void disableProfileLine() {
        profileLineButton.setSelected(false);
        rootLayout.getScene().setCursor(Cursor.DEFAULT);
        getActivePane().getImageView().removeEventFilter(MouseEvent.MOUSE_DRAGGED, profileLineDragHandler);
        getActivePane().getImageView().setOnMousePressed(event -> {});
    }

    EventHandler<MouseEvent> profileLineDragHandler = event -> {
        if (!profileLine.isPressed()) {
            MouseEvent mouseEvent = (MouseEvent) event;
            double newX = mouseEvent.getX();
            double newY = mouseEvent.getY();
            double maxX = getActivePane().getImage().getWidth() * getActivePane().getZoomLevel();
            double maxY = getActivePane().getImage().getHeight() * getActivePane().getZoomLevel();
            System.out.println("Mouse dragged");
            if (newX > 0 && newX < maxX && newY > 0 && newY < maxY) {
                profileLine.setEnd(newX, newY);
            }
        }
    };

    public void enableProfileLineSelection() {
        ImagePane p = getActivePane();
        p.getImageStack().clear();
        profileLine = new ProfileLine(p.zoomLevelProperty(), getActivePane());
        histogramPane.updateProfileLineChart(profileLine);
        System.out.println("Enable mouse events on:"+hashCode());

        p.getImageView().setOnMousePressed(mouseEvent -> {
            if (!profileLine.isHoverEndpoint()) {
                if (!p.getImageStack().contains(profileLine)) p.getImageStack().push(profileLine);
                profileLine.setStart(mouseEvent.getX(), mouseEvent.getY());
                profileLine.setEnd(mouseEvent.getX(), mouseEvent.getY());
                System.out.println("Mouse pressed X:" + profileLine.getStartX() + " Y:" + profileLine.getStartY() + " source:" + mouseEvent.getSource().getClass().getName());
            }
        });

        profileLine.changedProperty().addListener(observable -> {
            histogramPane.updateProfileLineChart(profileLine);
            p.refresh();
        });

        p.getImageView().addEventFilter(MouseEvent.MOUSE_DRAGGED, profileLineDragHandler);

        p.getImageView().setOnMouseReleased(mouseEvent -> {
            histogramPane.updateProfileLineChart(profileLine);
            disableProfileLine();
            System.out.println("Mouse released");
            p.refresh();
        });
    }






    public void handleTurtleAlgorithm(ActionEvent actionEvent) {
        CommandManager manager = getActivePane().getCommandManager();
        manager.executeCommand(new TurtleAlgorithmCommand(getActivePane()));
    }

    public void handleNumberTable(ActionEvent actionEvent) {
        Window parent = rootLayout.getScene().getWindow();
        ImageTab imageTab = (ImageTab) tabPane.getSelectionModel().getSelectedItem();
        String title = imageTab.getText() + " " + bundle.getString("NumberTable");
        TableWindow tableWindow = new TableWindow(parent, getActivePane().getImage(), title);
    }

    public void handleGradientSharpening(ActionEvent actionEvent) {
        addToToolbox(GradientSharpeningTool.getInstance(this));
    }

    public void handleGradientFiltering(ActionEvent actionEvent) {
        addToToolbox(GradientEdgeDetectionTool.getInstance(this));
    }

    public void handleShowHistogramView(ActionEvent actionEvent) {
        histogramPane.setMaxHeight(300);
        histogramPane.getSelectionModel().selectFirst();
    }

    public void handleToggleHistogramView(ActionEvent actionEvent) {
        if(histogramPane.getHeight() > 0) {
            histogramPane.setMaxHeight(0);
            toggleHistogramViewBtn.setText(bundle.getString("mdi-3arrow-down"));
        }
        else {
            histogramPane.setMaxHeight(300);
            toggleHistogramViewBtn.setText(bundle.getString("mdi-3arrow-up"));
        }
    }
}
