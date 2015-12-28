package pl.sotomski.apoz.nodes;

import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by sotomski on 10/12/15.
 */
public class BordersMethodToggles extends VBox {
    ToggleGroup toggleGroup = new ToggleGroup();
    List<RadioButton> toggles = new ArrayList<>();

    public BordersMethodToggles(ResourceBundle bundle) {
        toggles.add(new RadioButton(bundle.getString("blackBorder")));
        toggles.add(new RadioButton(bundle.getString("whiteBorder")));
        toggles.add(new RadioButton(bundle.getString("copyBorders")));
        toggles.add(new RadioButton(bundle.getString("useExistingPixels")));
        toggles.add(new RadioButton(bundle.getString("dontChangeExtremePixels")));
        for (RadioButton r : toggles) r.setToggleGroup(toggleGroup);
        getChildren().addAll(toggles);
        toggleGroup.selectToggle(toggles.get(2));
    }

    public int getMethod() {
        return toggles.indexOf((RadioButton)toggleGroup.getSelectedToggle());
    }
}
