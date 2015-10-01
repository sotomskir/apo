package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("sample.fxml"));
            primaryStage.setTitle("APOZ");

            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add("resources/style.css");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            new ExceptionDialog(e, "Exception occured");
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
