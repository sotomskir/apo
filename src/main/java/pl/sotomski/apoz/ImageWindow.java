package pl.sotomski.apoz;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;


/**
 * Created by sotomski on 10/10/15.
 */
public class ImageWindow extends Stage {

    private ImagePane imagePane;

    public ImageWindow(Window owner, ImagePane pane) {
        super();
        Scene newScene = new Scene(new VBox(pane));
        this.setScene(newScene);
        this.setTitle(pane.getFile().getName());
        this.setAlwaysOnTop(false);
        this.initOwner(owner);
        this.show();
        this.imagePane = pane;
    }

    public ImagePane getImagePane() {
        return imagePane;
    }

}
