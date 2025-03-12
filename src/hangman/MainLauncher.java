package hangman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainLauncher extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("mainScene.fxml"));
        stage.setTitle("MediaLab Hangman");
        stage.setScene(new Scene(root, 1280, 800));
        stage.show();
    }
    
    public static void main(String[] args) {
        Application.launch(MainLauncher.class, args);
    }
}