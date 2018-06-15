package sample.view;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import sample.Main;
import sample.util.InfoExchangeUtil;

import javax.sound.sampled.Line;
import java.io.IOException;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * This class of player controller is adapted from Al-assad's Simple-Media-Player @ Github.
 * Github link: https://github.com/Al-assad/Simple-Media-Player
 * Made some change to use it in this project.
 */
public class PlayerControlController {

  @FXML
  private Button volumeBT;

  @FXML
  private Slider volumeSD;

  @FXML
  private Button playBT;

  @FXML
  private BorderPane controlBorderPane;

  @FXML
  private VBox controlBar;

  @FXML
  private Button maxBT;

  @FXML
  private Slider processSD;

  @FXML
  private Label timeLB;

  @FXML
  private Button speedBTN;

  private Duration duration;
  private double volumeValue;
  private boolean muted = false;

  public PlayerController playerController;

  public MediaPlayer mediaPlayer;

  /**
   * Change Change the playback speed.
   */
  public void speedChange() {
    if (speedBTN.getText().equals("1x")) {
      mediaPlayer.setRate(2);
      speedBTN.setText("2x");
    } else if (speedBTN.getText().equals("2x")) {
      mediaPlayer.setRate(4);
      speedBTN.setText("4x");
    } else if (speedBTN.getText().equals("4x")) {
      mediaPlayer.setRate(1);
      speedBTN.setText("1x");
    }
  }

  /**
   * Return to the page before playing.
   *
   */
  public void playbtnc() {

//        if(mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)){
//            mediaPlayer.pause();
//            playBT.setText(">");
//        } else {
//            mediaPlayer.play();
//            playBT.setText("||");
//        }

    if (mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
      mediaPlayer.pause();
      playBT.setGraphic(new ImageView(new Image("/sample/resources/play.png")));
    } else {
      mediaPlayer.play();
      playBT.setGraphic(new ImageView(new Image("/sample/resources/stop.png")));
    }
  }

  /**
   * Initialize movie player
   *
   * @param mp  Mediaplayer before loading the path
   */
  public void initmp(MediaPlayer mp) {
    mediaPlayer = mp;
    duration = mp.getTotalDuration();

//        System.out.println(InfoExchangeUtil.moviePlayTimes);

    mediaPlayer.setOnReady(new Runnable() {
      @Override
      public void run() {

        duration = mediaPlayer.getMedia().getDuration();
        volumeValue = mediaPlayer.getVolume();

        if (InfoExchangeUtil.getMoviePlayTimes().containsKey(InfoExchangeUtil.getMovieToPlayId())) {
          Duration d = InfoExchangeUtil.getMoviePlayTimes()
              .get(InfoExchangeUtil.getMovieToPlayId());
          System.out.println(d);
          mediaPlayer.seek(d);
//            processSD.setValue(d.toMillis()*100/mediaPlayer.getTotalDuration().toMillis());
        }

        updateValues();
      }
    });

    mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
      @Override
      public void changed(ObservableValue<? extends Duration> observable, Duration oldValue,
          Duration newValue) {
        updateValues();
      }
    });

    processSD.valueProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> observable, Number oldValue,
          Number newValue) {
        if (processSD.isValueChanging()) {     //加入Slider正在改变的判定，否则由于update线程的存在，mediaPlayer会不停地回绕
          mediaPlayer.seek(duration.multiply(processSD.getValue() / 100.0));
        }
      }
    });

    volumeSD.valueProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> observable, Number oldValue,
          Number newValue) {
        if (!muted) {
          mediaPlayer.setVolume(newValue.doubleValue() / 100);
//                volumeValue = (Double) newValue;
          if ((Double) newValue == 0) {
            volumeBT.setGraphic(new ImageView(new Image("/sample/resources/volume-x.png")));
          } else {
            double vol = (double) newValue;
            if(vol<30){
              volumeBT.setGraphic(new ImageView(new Image("/sample/resources/volume0.png")));
            } else if(30<=vol && vol<=60){
              volumeBT.setGraphic(new ImageView(new Image("/sample/resources/volume1.png")));
            } else {
              volumeBT.setGraphic(new ImageView(new Image("/sample/resources/volume2.png")));
            }
          }
        }
      }
    });

    volumeBT.setOnMouseClicked(event -> {
      if (mediaPlayer.getVolume() != 0) {
        volumeValue = mediaPlayer.getVolume();
        mediaPlayer.setVolume(0);
        muted = true;
        volumeSD.setValue(volumeValue * 100);
        volumeBT.setGraphic(new ImageView(new Image("/sample/resources/volume-x.png")));
      } else {
        muted = false;
        volumeSD.setValue(volumeValue * 100);
        mediaPlayer.setVolume(volumeValue);
        System.out.println(volumeValue);
        if (volumeValue == 0) {
          volumeBT.setGraphic(new ImageView(new Image("/sample/resources/volume-x.png")));
        } else {
          double vol = volumeValue*100;
          if(vol<30){
            volumeBT.setGraphic(new ImageView(new Image("/sample/resources/volume0.png")));
          } else if(30<=vol && vol<=60){
            volumeBT.setGraphic(new ImageView(new Image("/sample/resources/volume1.png")));
          } else {
            volumeBT.setGraphic(new ImageView(new Image("/sample/resources/volume2.png")));
          }
        }
      }
    });


  }

  /**
   * Initialize movie player controller.
   *
   * @param p  Movie player controller
   */
  public void initpc(PlayerController p) {
    this.playerController = p;
  }

  /**
   * Back to movie player page.
   * @throws IOException
   */
  public void goback() throws IOException {
    HashMap<Integer, Duration> hm = InfoExchangeUtil.getMoviePlayTimes();
    hm.put(InfoExchangeUtil.getMovieToPlayId(), mediaPlayer.getCurrentTime());
    mediaPlayer.pause();
    this.playerController.goBackToList();
  }

  /**
   * Initialize fxml page.
   */
  @FXML
  void initialize() {
    playBT.setText("");
    volumeBT.setText("");
    speedBTN.setText("1x");
    maxBT.setText("");
    playBT.setGraphic(new ImageView(new Image("/sample/resources/stop.png")));
    maxBT.setGraphic(new ImageView(new Image("/sample/resources/back.png")));
  }

  //更新视频数据（进度条 、时间标签、音量条数据）

  /**
   * Update video data (progress bar, time stamp, volume bar data)
   *
   */
  protected void updateValues() {
    if (processSD != null && timeLB != null && volumeSD != null && volumeBT != null) {
      Platform.runLater(new Runnable() {
        @Override
        public void run() {

          Duration currentTime = mediaPlayer.getCurrentTime();
//                    System.out.println(currentTime);
          timeLB.setText(formatTime(currentTime, duration));    //设置时间标签
          processSD.setDisable(duration.isUnknown());   //无法读取时间时隐藏进度条
          if (!processSD.isDisabled() && duration.greaterThan(Duration.ZERO) && !processSD
              .isValueChanging()) {
            processSD.setValue(currentTime.toMillis() / duration.toMillis() * 100);   //设置进度条
          }
          if (!volumeSD.isValueChanging()) {
            if (!muted) {
              volumeSD.setValue((int) Math.round(mediaPlayer.getVolume() * 100));   //设置音量条
//                        if(mediaPlayer.getVolume() == 0){        //设置音量按钮
//                            setIcon(volumeBT,volOffIcon,20);
//                        }else{
//                            setIcon(volumeBT,volOnIcon,20);
//                        }
            }
          }
        }
      });
    }
  }

  //将Duration数据格式化，用于播放时间标签

  /**
   * Format Duration data for playing time stamps
   *
   * @param elapsed  Already played (current time)
   * @param duration Total time
   * @return         Fomatted time
   */
  protected String formatTime(Duration elapsed, Duration duration) {
    //将两个Duartion参数转化为 hh：mm：ss的形式后输出
    int intElapsed = (int) Math.floor(elapsed.toSeconds());
    int elapsedHours = intElapsed / (60 * 60);
    int elapsedMinutes = (intElapsed - elapsedHours * 60 * 60) / 60;
    int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;
    if (duration.greaterThan(Duration.ZERO)) {
      int intDuration = (int) Math.floor(duration.toSeconds());
      int durationHours = intDuration / (60 * 60);
      int durationMinutes = (intDuration - durationHours * 60 * 60) / 60;
      int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;

      if (durationHours > 0) {
        return String
            .format("%02d:%02d:%02d / %02d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds,
                durationHours, durationMinutes, durationSeconds);
      } else {
        return String
            .format("%02d:%02d / %02d:%02d", elapsedMinutes, elapsedSeconds, durationMinutes,
                durationSeconds);
      }
    } else {
      if (elapsedHours > 0) {
        return String.format("%02d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
      } else {
        return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds);
      }
    }
  }
}
