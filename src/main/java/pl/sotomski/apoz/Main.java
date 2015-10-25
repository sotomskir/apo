package pl.sotomski.apoz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.sotomski.apoz.controllers.PrefsController;
import pl.sotomski.apoz.utils.ExceptionDialog;
import pl.sotomski.apoz.utils.UTF8Control;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Parent root = null;
        try {
            Preferences prefs = Preferences.userNodeForPackage(Main.class);
            String lang = prefs.get(PrefsController.LANGUAGE, Locale.getDefault().getLanguage());
            System.out.println("lang: "+lang);
            FXMLLoader loader = new FXMLLoader();
            Locale locale = new Locale(lang);
            ResourceBundle bundle = ResourceBundle.getBundle("bundles.ApozBundle", locale, new UTF8Control());
            loader.setResources(bundle);
            root = loader.load(getClass().getClassLoader().getResource("MainStage.fxml").openStream());
            primaryStage.setTitle("APOZ");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(String.valueOf(getClass().getClassLoader().getResource("style.css")));
//            scene.getStylesheets().add(String.valueOf(getClass().getClassLoader().getResource("style.css")));
            primaryStage.setMaximized(true);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            new ExceptionDialog(e, "Exception occured");
        } catch (Throwable t) {
            t.printStackTrace();
            new ExceptionDialog(t, "Exception occured");
        }

    }

    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Throwable t) {
            t.printStackTrace();
            new ExceptionDialog(t, "Exception occured");
        }
    }
}
