package pl.sotomski.apoz.nodes;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.utils.FileMenuUtils;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;


/**
 * Created by sotomski on 10/10/15.
 */
public class ImagePane extends BorderPane {

    ImageStack imageStack = new ImageStack();
    private ZoomableImageView imageView;
    private IntegerProperty imageVersion;
    private IntegerProperty zoomIndex;
    private DoubleProperty zoomLevel = new SimpleDoubleProperty();
    private ChartsPane histogramPane;
    private CommandManager commandManager;
    private static final double[] zoomLevels = new double[]{.05, .125, .25, .50, .75, 1, 1.25, 1.5, 2, 3, 4, 6, 8, 10, 15, 20};
//    private final double zoomStep = 0.125;
//    private final double zoomMin  = 0.125;
//    private final double zoomMax  = 4.00;
    private File file;
    private ScrollPane scrollPane;
    private boolean tabbed;
    ProfileLine profileLine;
    CropRectangle cropRectangle;
    private String name = "";
    Window window;
    private static HashSet<String> names = new HashSet<>();
    Group groupWrapper;

    public ImagePane() {
        super();
        this.tabbed = true;
        this.commandManager = new CommandManager(this);
        this.imageVersion = new SimpleIntegerProperty(0);
        int indexOfOne = 5;
        this.zoomIndex = new SimpleIntegerProperty(indexOfOne);
        this.imageView = new ZoomableImageView();
        imageView.setStyle("-fx-background-color: BLACK");
        imageStack.getChildren().add(imageView);
        scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        groupWrapper = new Group(imageStack);
        groupWrapper.setStyle("-fx-background-color: BLACK");
        scrollPane.setContent(groupWrapper);
        scrollPane.setPickOnBounds(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        this.setCenter(scrollPane);
        zoomIndex.addListener((observable, oldValue, newValue) -> {
            zoomLevel.setValue(zoomLevels[zoomIndex.get()]);
        });
        zoomLevel.setValue(zoomLevels[zoomIndex.get()]);
        imageView.setOnMouseClicked(event -> refresh());
    }

    public ImagePane(ChartsPane histogramPane, File file) {
        this();
        BufferedImage bi = FileMenuUtils.loadImage(file);
        this.histogramPane = histogramPane;
        setImage(bi);
        this.file = file;
        name = incrementNameIfNotDistinct(file.getName());
        refresh();
    }

    public ImagePane(ChartsPane histogramPane, BufferedImage image, String name) {
        this();
        this.histogramPane = histogramPane;
        this.imageView.setBufferedImage(image);
        this.name = incrementNameIfNotDistinct(name);
        refresh();
    }

    public ImagePane(ChartsPane histogramPane, ImagePane imagePane) {
        this();
        if(imagePane.getFile() != null) setFile(new File(imagePane.getFile().getPath()));
        name = imagePane.getName();
        BufferedImage image = ImageUtils.deepCopy(imagePane.getImage());
        this.histogramPane = histogramPane;
        this.imageView.setBufferedImage(image);
        String copyStr = histogramPane.getBundle().getString("copy");
        int indexOfCopy = name.indexOf(copyStr);
        if (indexOfCopy < 0) name = name + " " + copyStr;
        this.name = incrementNameIfNotDistinct(this.name);
        refresh();
    }

    public String incrementNameIfNotDistinct(String name) {
        System.out.println("Name: " + name);
        while(names.contains(name)) {
            int indexOfLastSpace = name.lastIndexOf(' ');
            if (indexOfLastSpace < 0) name = name + " 1";
            else {
                int copyNumber;
                try {
                    copyNumber = Integer.valueOf(name.substring(indexOfLastSpace + 1));
                    if (copyNumber == 0) throw new NumberFormatException();
                    ++copyNumber;
                    name = name.substring(0, indexOfLastSpace+1) + copyNumber;
                } catch (NumberFormatException|StringIndexOutOfBoundsException e) {
                    copyNumber = 1;
                    name = name + " " + copyNumber;
                }
            }
            System.out.println("Incremented name: " + name);
        }
        System.out.println(names);
        names.add(name);
        return name;
    }

    public Window getWindow() {
        return window;
    }

    public DoubleProperty zoomLevelProperty() {
        return zoomLevel;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public CropRectangle getCropRectangle() {
        return cropRectangle;
    }

    public ImageStack getImageStack() {
        return imageStack;
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    public void setImage(BufferedImage bufferedImage) {
        this.imageView.setBufferedImage(bufferedImage);
    }

    public BufferedImage getImage() {
        return imageView.getBufferedImage();
    }

    public void setHistogramPane(ChartsPane histogramPane) {
        this.histogramPane = histogramPane;
        histogramPane.update(imageView.getBufferedImage());
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ProfileLine getProfileLine() {
        return profileLine;
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

    public String getName() {
        return name;
    }

    public void onClose() {
        names.remove(name);
    }

    public void setName(String name) {
        this.name = incrementNameIfNotDistinct(name);
    }

    public double getZoomLevel() {
        return zoomLevels[zoomIndex.getValue()];
    }

    public void handleZoomIn(Label textField) {
        if(zoomIndex.getValue() < zoomLevels.length - 1)
            zoomIndex.setValue(zoomIndex.getValue()+1);
        final double zoomFactor = zoomLevels[zoomIndex.getValue()];
        imageView.setZoomFactor(zoomFactor);
//        imageStack.setScaleX(zoomLevels[zoomIndex.getValue()]);
//        imageStack.setScaleY(zoomLevels[zoomIndex.getValue()]);
        textField.setText(String.format("%.0f%%", getZoomLevel()*100));
    }

    public void handleZoomOut(Label textField) {
        if(zoomIndex.getValue() > 0)
            zoomIndex.setValue(zoomIndex.getValue()-1);
        final double zoomFactor = zoomLevels[zoomIndex.getValue()];
        imageView.setZoomFactor(zoomFactor);
//        imageStack.setScaleX(zoomLevels[zoomIndex.getValue()]);
//        imageStack.setScaleY(zoomLevels[zoomIndex.getValue()]);
        textField.setText(String.format("%.0f%%", getZoomLevel()*100));
    }

    public IntegerProperty getZoomIndex() {
        return zoomIndex;
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

    public ChartsPane getHistogramPane() {
        return histogramPane;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void refresh() {
        long startTime;
        startTime = System.currentTimeMillis();
        imageView.refresh();
        this.histogramPane.update(imageView.getBufferedImage());
        this.imageVersionProperty().setValue(getImageVersion()+1);
        System.out.println("ImagePane.refresh(): " + (System.currentTimeMillis()-startTime));
    }

    public boolean isTabbed() {
        return tabbed;
    }

    public void setTabbed(boolean tabbed) {
        this.tabbed = tabbed;
    }

    public void mouseDraggedLine(MouseEvent mouseEvent, ProfileLine l) {
        if (!l.isPressed()) {
            double newX = mouseEvent.getX();
            double newY = mouseEvent.getY();
            double maxX = getImage().getWidth() * zoomLevel.getValue();
            double maxY = getImage().getHeight() * zoomLevel.getValue();
            if (newX > 0 && newX < maxX && newY > 0 && newY < maxY) {
                l.setEnd(newX, newY);
            }
        }
    }

    public void enableProfileLineSelection() {
        getImageStack().clear();
        profileLine = new ProfileLine(zoomLevel, this);
        histogramPane.updateProfileLineChart(profileLine);
        System.out.println("Enable mouse events on:"+hashCode());

        imageView.setOnMousePressed(mouseEvent -> {
            if (!profileLine.isHoverEndpoint()) {
                if (!getImageStack().contains(profileLine)) getImageStack().push(profileLine);
                profileLine.setStart(mouseEvent.getX(), mouseEvent.getY());
                profileLine.setEnd(mouseEvent.getX(), mouseEvent.getY());
                System.out.println("Mouse pressed X:" + profileLine.getStartX() + " Y:" + profileLine.getStartY() + " source:" + mouseEvent.getSource().getClass().getName());
            }
        });

        profileLine.changedProperty().addListener(observable -> {
            histogramPane.updateProfileLineChart(profileLine);
            refresh();
        });
        imageView.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> mouseDraggedLine(event, profileLine));

        imageView.setOnMouseReleased(mouseEvent -> {
            histogramPane.updateProfileLineChart(profileLine);
            getScene().setCursor(Cursor.DEFAULT);
            refresh();
        });
    }

    public void disableTools() {
        imageView.setOnMousePressed(event -> {});
        imageView.setOnMouseReleased(event -> {});
        imageView.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {});
        getImageStack().clear();
    }

    private void mouseDraggedRect(MouseEvent mouseEvent, CropRectangle r) {
        double newX = mouseEvent.getX();
        double newY = mouseEvent.getY();
        double maxX = getScene().getWidth();
        double maxY = getScene().getHeight();
        if (newX > 0 && newX < maxX && newY > 0 && newY < maxY) {
            r.setEnd((int) (newX), (int) (newY));
        }
    }

    public void enableCropSelection() {
        getImageStack().clear();
        imageView.setOnMousePressed(event -> {});
        imageView.setOnMouseReleased(event -> {});
        imageView.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {});
        cropRectangle = new CropRectangle(zoomLevel);
        getImageStack().push(cropRectangle);
        int width = getImage().getWidth();
        int height = getImage().getHeight();
        cropRectangle.setStart(width*0.25, height*0.25);
        cropRectangle.setEnd(width*0.75, height*0.75);

//        setOnMousePressed(mouseEvent -> {
//            getImageStack().clear();
//            cropRectangle.setStart(mouseEvent.getX(), mouseEvent.getY());
//            cropRectangle.setEnd(mouseEvent.getX(), mouseEvent.getY());
//            System.out.println("Mouse pressed X:" + cropRectangle.x + " Y:" + cropRectangle.y + " source:" + mouseEvent.getSource().hashCode());
//        });

//        addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> mouseDraggedRect(event, cropRectangle));

//        setOnMouseReleased(mouseEvent -> {
//            getImageStack().clear();
//        });
    }

    public class ImageStack extends AnchorPane {
        public ImageStack() {
            super();
        }

        public void push(Node node) {
            getChildren().add(node);
        }
        public void remove(Node node) { getChildren().remove(node); }
        public void clear() {
            getChildren().clear();
            getChildren().add(imageView);
        }

        public boolean contains(Node node) {
            return getChildren().contains(node);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
