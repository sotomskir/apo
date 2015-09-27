package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HistogramEq implements Initializable {
    @FXML
    ChoiceBox methodChoice;
    private Pane root;

    public HistogramEq() {
        try {
            root = FXMLLoader.load(getClass().getResource("histogramEq.fxml"));
        } catch (IOException e) {
            new ExceptionDialog(e, "Error loading histogramEq.fxml");
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public Pane getRoot() {
        return root;
    }

    public void handleApply(ActionEvent actionEvent) {

    }
}
