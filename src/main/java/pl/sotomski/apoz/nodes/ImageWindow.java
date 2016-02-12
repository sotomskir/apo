package pl.sotomski.apoz.nodes;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;


/**
 * Created by sotomski on 10/10/15.
 */
public class ImageWindow extends Stage {

    private ImagePane imagePane;
    private ChartsPane histogramPane;
    public ImageWindow(Window owner, ImagePane imagePane, ChartsPane histogramPane) {
        super();
        imagePane.setHistogramPane(histogramPane);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(imagePane);
        borderPane.setRight(histogramPane);
        Scene newScene = new Scene(borderPane);
        newScene.getStylesheets().add(String.valueOf(getClass().getClassLoader().getResource("style.css")));
        this.setScene(newScene);
        this.setTitle(imagePane.getName());
        this.setAlwaysOnTop(false);
        this.initOwner(owner);
        this.show();
        this.imagePane = imagePane;
    }

    public ImagePane getImagePane() {
        return imagePane;
    }

    public ChartsPane getHistogramPane() {
        return histogramPane;
    }
}
