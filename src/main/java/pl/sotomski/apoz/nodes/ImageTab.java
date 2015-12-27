package pl.sotomski.apoz.nodes;

import javafx.scene.control.Tab;

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

    public ImageTab(ImagePane imagePane, String name) {
        super();
        this.pane = imagePane;
        this.setContent(imagePane);
        this.setText(name);
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
