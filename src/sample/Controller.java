package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private MenuBar menuBar;

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
            if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.A)
            {
                provideAboutFunctionality();
            }
        }
    }

    /**
     * Perform functionality associated with "About" menu selection or CTRL-A.
     */
    private void provideAboutFunctionality()
    {
        System.out.println("You clicked on About!");
    }

    @Override
    public void initialize(java.net.URL arg0, ResourceBundle arg1) {
        menuBar.setFocusTraversable(true);

    }

    public void handleExit(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void handleOpen(ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        Window window = menuBar.getScene().getWindow();
        File file = fileChooser.showOpenDialog(window);
        Image image = new Image(file.toURI().toString());
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        HBox pictureRegion = new HBox();
        pictureRegion.getChildren().add(imageView);
        BorderPane root = (BorderPane) window.getScene().getRoot();
        root.setCenter(imageView);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        Histogram histogram = new Histogram(bufferedImage);
        root.setLeft(histogram);
    }

}
