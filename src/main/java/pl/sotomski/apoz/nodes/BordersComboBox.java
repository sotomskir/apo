package pl.sotomski.apoz.nodes;

import javafx.scene.control.ComboBox;

import java.util.ResourceBundle;

/**
 * Created by sotomski on 10/12/15.
 */
public class BordersComboBox extends ComboBox<String> {
    public BordersComboBox(ResourceBundle bundle) {
        getItems().addAll(
                bundle.getString("blackBorder"),
                bundle.getString("whiteBorder"),
                bundle.getString("copyBorders"),
                bundle.getString("useExistingPixels"),
                bundle.getString("dontChangeExtremePixels")
        );
        getSelectionModel().selectFirst();
    }

    public int getMethod() {
        return this.getSelectionModel().getSelectedIndex();
    }
}
