package pl.sotomski.apoz;

import javafx.scene.control.Tab;


/**
 * Created by sotomski on 10/10/15.
 */
public class ImageTab extends Tab {

    ImagePane pane;

    public ImageTab() {
    }

    public ImageTab(ImagePane imagePane) {
        super();
        this.pane = imagePane;
        this.setContent(imagePane);
        this.setText(imagePane.getFile().getName());
    }

    public ImagePane getPane() {
        return pane;
    }

    public void setPane(ImagePane imagePane) {
        this.pane = imagePane;
        this.setContent(imagePane);
        this.setText(imagePane.getFile().getName());
    }
}
