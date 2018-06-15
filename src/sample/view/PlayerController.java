package sample.view;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import sample.util.InfoExchangeUtil;

public class PlayerController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane ap;

    @FXML
    private VBox vb;

    @FXML
    private MediaView mainplay;

    @FXML
    private Node ctrl;

    private MediaPlayer mediaPlayer;

    /**
     * Click on the screen to display the playback control Click to hide.
     */
    public void mediactrl(){
        if(ctrl.isVisible()){
            ctrl.setVisible(false);
        } else {
            ctrl.setVisible(true);
        }
    }

    /**
     * Back to movies list.
     *
     * @throws IOException
     */
    public void goBackToList() throws IOException{
        ResourceBundle bundle = ResourceBundle.getBundle("language"); // load success
        Parent root = FXMLLoader.load(getClass().getResource("/sample/view/list.fxml"),bundle);
        ap.getChildren().setAll(root);
    }


    /**
     * Initialize fxml file.
     *
     * @throws IOException
     */
    @FXML
    void initialize() throws IOException {
        Media media = InfoExchangeUtil.getMovieToPlay();
        System.out.println(media);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true); //设置自动播放

        mainplay.setMediaPlayer(mediaPlayer);
        mediaPlayer.play();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/sample/view/playercontrol.fxml"));
        ctrl = loader.load();

        PlayerControlController playerControlController = loader.getController();
        playerControlController.initmp(mediaPlayer);
        playerControlController.initpc(this);

        if(InfoExchangeUtil.getMoviePlayTimes().containsKey(InfoExchangeUtil.getMovieToPlayId())){
            Duration d = InfoExchangeUtil.getMoviePlayTimes().get(InfoExchangeUtil.getMovieToPlayId());
            mediaPlayer.seek(d);
//            processSD.setValue(d.toMillis()*100/mediaPlayer.getTotalDuration().toMillis());
        }



        ap.getChildren().add(ctrl);
        ctrl.setVisible(false);
        ctrl.setLayoutY(720-70);

        vb.setBackground(new Background(new BackgroundFill(Color.BLACK,CornerRadii.EMPTY,Insets.EMPTY)));

        System.out.println(ctrl.getLayoutBounds());
    }
}
