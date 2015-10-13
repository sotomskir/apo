package pl.sotomski.apoz;

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
//            root = FXMLLoader.load(getClass().getResource("sample.fxml"));
            root = FXMLLoader.load(getClass().getClassLoader().getResource("sample.fxml"));
            primaryStage.setTitle("APOZ");
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(String.valueOf(getClass().getClassLoader().getResource("style.css")));
//            scene.getStylesheets().add(String.valueOf(getClass().getClassLoader().getResource("style.css")));
            primaryStage.setMaximized(true);
            primaryStage.setScene(scene);
            primaryStage.show();

//            Button button1 = new Button("1");
//            Button button2 = new Button("2");
//            Scene scene1 = new Scene(button1, 200, 200);
//            Scene scene2 = new Scene(button2, 200, 200);
//            Stage stage1 = new Stage();
//            Stage stage2 = new Stage();
//            stage1.setScene(scene1);
//            stage2.setScene(scene2);
//
//            stage1.initOwner(primaryStage);
//            stage2.initOwner(primaryStage);
//            stage1.show();
//            stage2.show();
//            stage2.toBack();
//            stage1.toFront();
//            stage1.show();
//            button1.setOnAction(event -> stage1.toFront());
//            button2.setOnAction(event -> stage2.toFront());
//
//            stage1.focusedProperty().addListener(new ChangeListener<Boolean>() {
//                @Override
//                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                    if(newValue) {
//                        stage1.toFront();
//                        System.out.println("Stage1 focus");
//                    }
//                }
//            });
//            stage2.focusedProperty().addListener(new ChangeListener<Boolean>() {
//                @Override
//                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                    if(newValue) {
//                        stage2.toFront();
//                        System.out.println("Stage2 focus");
//                    }
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
            new ExceptionDialog(e, "Exception occured");
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
