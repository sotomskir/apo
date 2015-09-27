package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class SampleTool extends Pane {
    @FXML
    ChoiceBox methodChoice;

    public SampleTool() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sampleTool.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void handleApply(ActionEvent actionEvent) {

    }
}
