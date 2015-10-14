package pl.sotomski.apoz;

import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;


/**
 * Created by sotomski on 10/10/15.
 */
public class ImageWindow extends Stage {

    private ImagePane imagePane;
    public ImageWindow(Window owner, ImagePane pane) {
        super();
        Pane histogramPane = new Pane();
        TabPane tabPane = new TabPane();
        Tab tab1 = new Tab("histogram");
        Tab tab2 = new Tab("linia profilu");
        tabPane.getTabs().addAll(tab1, tab2);
        tab1.setContent(histogramPane);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(pane);
        borderPane.setRight(tabPane);
        histogramPane.getChildren().add(pane.getHistogramChart().getBarChart());
        Scene newScene = new Scene(borderPane);
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
