package pl.sotomski.apoz;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.InputStream;

public class Test extends Application {

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        TextArea textArea = new TextArea();
        Label label = new Label();
        label.setStyle("-fx-font-family: 'Material Icons'; -fx-font-size: 24");
//        label.setStyle("-fx-font-family: Gafata; -fx-font-size: 80;");

        try {
            InputStream is = new FileInputStream("/Users/sotomski/Documents/workspace/POB2/src/main/resources/iconfont/MaterialIcons-Regular.ttf");
            Font font = Font.loadFont(is, 24);
//            label.setFont(font);
            System.out.println(font.getFamily());
            System.out.println(font.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        textArea.setText(String.valueOf((char)0xE869));
        label.textProperty().bind(textArea.textProperty());
        root.getChildren().add(new VBox(textArea, label));
        Scene scene = new Scene(root, 300, 250);
        primaryStage.setScene(scene);
//        scene.getStylesheets().add("http://cdn.materialdesignicons.com/1.3.41/css/materialdesignicons.min.css");
        scene.getStylesheets().add(String.valueOf(getClass().getClassLoader().getResource("style.css")));

        primaryStage.show();
    }
}