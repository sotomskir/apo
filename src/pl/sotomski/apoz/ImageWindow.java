package pl.sotomski.apoz;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * Created by sotomski on 10/10/15.
 */
public class ImageWindow extends Stage {

    private ImagePane imagePane;

    public ImageWindow(ImagePane pane) {
        Stage newStage = new Stage();
        Scene newScene = new Scene(new VBox(pane));
        newStage.setScene(newScene);
        newStage.setTitle(pane.getFile().getName());
        newStage.setAlwaysOnTop(true);
        newStage.show();
        this.imagePane = pane;
    }

    public ImagePane getImagePane() {
        return imagePane;
    }
}
