package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.util.Locale;

public class Main extends Application {

    /**
     * Start.
     *
     * @param primaryStage Primary Stage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("view/lang_select.fxml"));
        primaryStage.setTitle("Onboard Movie System");
        primaryStage.setScene(new Scene(root, 1080 , 720));
        primaryStage.show();
    }


    /**
     * Lanch fxml file.
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
