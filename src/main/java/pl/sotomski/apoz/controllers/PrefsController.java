package pl.sotomski.apoz.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pl.sotomski.apoz.Main;
import pl.sotomski.apoz.utils.FileMenuUtils;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class PrefsController implements Initializable {
    /**
     * Preferences keys
     */
    public final static String SCREENSHOT_PATH = "screenshot_path";
    public final static String LANGUAGE = "locale";

    /**
     * Available languages
     */
    public static final String[] LANGUAGES = new String[] { "pl", "en" };

    private ResourceBundle bundle;
    private Preferences prefs;

    @FXML private GridPane rootLayout;
    @FXML private TextField screenshotPath;
    @FXML private ComboBox languageBox;

    @Override
    public void initialize(java.net.URL arg0, ResourceBundle resources) {
        bundle = resources;
        prefs = Preferences.userNodeForPackage(Main.class);
        String lang = prefs.get(LANGUAGE, Locale.getDefault().getLanguage());
        languageBox.getItems().addAll(LANGUAGES);
        languageBox.getSelectionModel().select(lang);
        screenshotPath.setText(prefs.get(SCREENSHOT_PATH, System.getProperty("user.home") + "/apoz_screenshots/"));
    }

    public void handleSave(ActionEvent actionEvent) {
        prefs.put(SCREENSHOT_PATH, screenshotPath.getText());
        prefs.put(LANGUAGE, (String) languageBox.getSelectionModel().getSelectedItem());
        Stage stage = (Stage) rootLayout.getScene().getWindow();
        stage.close();
    }

    public void selectFile(ActionEvent actionEvent) {
        File file = FileMenuUtils.openDirDialog(rootLayout);
        if (file!=null) screenshotPath.setText(file.getAbsolutePath());
    }
}
