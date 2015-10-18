package pl.sotomski.apoz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader();
//            Locale locale = new Locale("pl", "PL");
            Locale locale = Locale.getDefault();
            ResourceBundle bundle = ResourceBundle.getBundle("bundles.ApozBundle", locale, new UTF8Control());

            loader.setResources(bundle);
            root = loader.load(getClass().getClassLoader().getResource("MainStage.fxml").openStream());
            primaryStage.setTitle("APOZ");
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(String.valueOf(getClass().getClassLoader().getResource("style.css")));
//            scene.getStylesheets().add(String.valueOf(getClass().getClassLoader().getResource("style.css")));
            primaryStage.setMaximized(true);
            primaryStage.setScene(scene);
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
