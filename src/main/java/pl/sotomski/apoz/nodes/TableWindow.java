package pl.sotomski.apoz.nodes;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import pl.sotomski.apoz.utils.FileMenuUtils;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class TableWindow extends Stage {

    BufferedImage image;
    TableView<Integer[]> tableView = new TableView<>();

    public TableWindow(Window owner, BufferedImage image, String title) {
        super();
        this.image = image;
        if (image.getColorModel().getNumComponents() > 1) this.image = ImageUtils.rgbToGrayscale(image);
        ToolBar toolbar = new ToolBar();
        Button csvButton = new Button("CSV");
        toolbar.getItems().addAll(csvButton);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(tableView);
        borderPane.setTop(toolbar);
        Scene newScene = new Scene(borderPane);
        this.setScene(newScene);
        this.setTitle(title);
        this.setAlwaysOnTop(false);
        this.initOwner(owner);
        this.show();
        fillTable(tableView, image);
        csvButton.setOnAction(event -> FileMenuUtils.saveAsCSVDialog(tableView.getScene().getRoot(), this.image));
    }

    private void fillTable(TableView<Integer[]> table, BufferedImage image) {
        Integer[][] imageArray = ImageUtils.asArray(image);
        ObservableList<Integer[]> data = FXCollections.observableArrayList();
        data.addAll(Arrays.asList(imageArray));
        for (int i = 0; i < imageArray[0].length; i++) {
            TableColumn<Integer[], String> tc = new TableColumn<>(String.valueOf(i));
            final int colNo = i;
            tc.setCellValueFactory(p -> new SimpleStringProperty((p.getValue()[colNo]).toString()));
            tc.setPrefWidth(35);
            table.getColumns().add(tc);
        }
        table.setItems(data);
    }
}
