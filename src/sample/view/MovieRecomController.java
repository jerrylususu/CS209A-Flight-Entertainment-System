package sample.view;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import sample.util.InfoExchangeUtil;
import sample.util.Movie;
import sample.util.MultiLangMovie;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class MovieRecomController implements Initializable {


    private ResourceBundle rb;
    @FXML
    private BorderPane bp;
    @FXML
    private GridPane topgp;
    @FXML
    private ImageView airlineLogoIV;
    @FXML
    private Button langBtn;
    @FXML
    private HBox mainhb;
    @FXML
    private ImageView movieim1;
    @FXML
    private Label movielb1;
    @FXML
    private ImageView movieim2;
    @FXML
    private Label movielb2;
    @FXML
    private ImageView movieim3;
    @FXML
    private Label movielb3;
    @FXML
    private JFXButton downBtn;

    @FXML
    private GridPane imagegp;


    //private List<String> movielist;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GridPane.setHalignment(imagegp,HPos.CENTER);

        langBtn.setText("");
        langBtn.setGraphic(new ImageView(new Image("/sample/resources/language.png")));
        GridPane.setHalignment(topgp, HPos.CENTER);
        GridPane.setValignment(topgp,VPos.CENTER);
//        Image image = new Image(getClass().getResource("/sample/resources/airline/logo.png").toString());

//        InfoExchangeUtil.initAirlineLogo(getClass().getResource("/sample/resources/airline/logo.png").toString());
//        System.out.println(getClass().getResource("/sample/resources/airline/logo.png").toString());
//        InfoExchangeUtil.initProperties();
//        System.out.println(InfoExchangeUtil.properties.getProperty("airlineResourcePath"));

        airlineLogoIV.setImage(InfoExchangeUtil.getAirlineLogo());
        rb = ResourceBundle.getBundle("language"); // load success
        System.out.println(java.util.Locale.getDefault());
        System.out.println(rb.getString("welcome"));
//        langBtn.setText(rb.getString("welcome"));
//        InfoExchangeUtil.TempinitAllMovie();
//        InfoExchangeUtil.generateRecomMovie();

        ArrayList<PosterAndTitle> posterAndTitles = new ArrayList<>(3);
        posterAndTitles.add(new PosterAndTitle(movieim1, movielb1, null));
        posterAndTitles.add(new PosterAndTitle(movieim2, movielb2, null));
        posterAndTitles.add(new PosterAndTitle(movieim3, movielb3, null));

        for(int i=0;i<3;i++){
            PosterAndTitle posterAndTitle = posterAndTitles.get(i);
            MultiLangMovie multiLangMovie = InfoExchangeUtil.getRecomMovieList().get(i);
            posterAndTitle.mu = multiLangMovie;
            posterAndTitle.iv.requestFocus();
            posterAndTitle.iv.setImage(InfoExchangeUtil.loadPosterFromMultiLangMovie(multiLangMovie));
            posterAndTitle.lb.setText(multiLangMovie.getALangMovie(Locale.getDefault().toString()).getName());
            posterAndTitle.iv.setOnMouseClicked((e) -> {
                InfoExchangeUtil.setRecomMovieSelected(posterAndTitle.mu);
                try {
                    downBtnClicked();
                } catch (IOException e2){
                    e2.printStackTrace();
                }

            });
        }

        Gson gson = InfoExchangeUtil.gsonFactory();
        System.out.println(gson.toJson(InfoExchangeUtil.getAllMovieList().get(0)));

        System.out.println(Locale.getDefault());

    }

    public void downBtnClicked() throws IOException {

        ResourceBundle bundle = ResourceBundle.getBundle("language");
        Parent root = FXMLLoader.load(getClass().getResource("/sample/view/list.fxml"),bundle);
        bp.getChildren().setAll(root);

    }

    public void langSelected() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/sample/view/lang_select.fxml"));
        bp.getChildren().setAll(root);
    }

    public void filterBtnClicked() throws IOException {
        ResourceBundle bundle = ResourceBundle.getBundle("language");
        Parent root = FXMLLoader.load(getClass().getResource("/sample/view/filter.fxml"),bundle);
        bp.getChildren().setAll(root);
    }
}

class PosterAndTitle{
    public ImageView iv;
    public Label lb;
    public MultiLangMovie mu;

    public PosterAndTitle(ImageView iv, Label lb, MultiLangMovie mu){
        this.iv = iv;
        this.lb = lb;
        this.mu = mu;
    }

}
