package pl.sotomski.apoz.utils;

import javafx.scene.control.MenuButton;
import javafx.scene.layout.GridPane;

/**
 * Created by sotomski on 25/09/15.
 */
public class ToolsMenuUtils {

    public void histogramEqualisation() {
        GridPane pane = new GridPane();
        pane.addColumn(0);
        pane.addRow(0);
        pane.add(new MenuButton(), 0, 0);
    }
}
