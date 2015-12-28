package pl.sotomski.apoz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import pl.sotomski.apoz.controllers.PrefsController;
import pl.sotomski.apoz.utils.ExceptionDialog;
import pl.sotomski.apoz.utils.UTF8Control;

import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Parent root = null;
        try {
            Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
                throwable.printStackTrace();
                new ExceptionDialog(throwable, throwable.getMessage());
            });
            Preferences prefs = Preferences.userNodeForPackage(Main.class);
            String lang = prefs.get(PrefsController.LANGUAGE, Locale.getDefault().getLanguage());
            System.out.println("lang: "+lang);
            FXMLLoader loader = new FXMLLoader();
            Locale locale = new Locale(lang);
            ResourceBundle bundle = ResourceBundle.getBundle("bundles.ApozBundle", locale, new UTF8Control());
            loader.setResources(bundle);
            root = loader.load(getClass().getClassLoader().getResource("MainStage.fxml").openStream());
//            root = FXMLLoader.load(getClass().getResource("MainStage.fxml"));
//            root = loader.load(getClass().getResource("MainStage.fxml").openStream());
            primaryStage.setTitle("APOZ");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(String.valueOf(getClass().getClassLoader().getResource("style.css")));
//            scene.getStylesheets().add(String.valueOf(getClass().getClassLoader().getResource("mdi.css")));
            InputStream is = getClass().getClassLoader().getResource("MaterialDesign-Webfont-master/fonts/materialdesignicons-webfont.ttf").openStream();
            Font font = Font.loadFont(is, 24);
            System.out.println(font.getName());
            primaryStage.setMaximized(true);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            new ExceptionDialog(e, "Exception occured");
        }

    }

//    public static void main(String[] args) {
//        try {
//            Thread.setDefaultUncaughtExceptionHandler(Thread.UncaughtExceptionHandler);
//            launch(args);
//        } catch (Throwable t) {
//            t.printStackTrace();
//            new ExceptionDialog(t, "Exception occured");
//        }
//    }
}
