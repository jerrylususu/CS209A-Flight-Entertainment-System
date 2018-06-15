package sample.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXScrollPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.text.Text;
import javafx.util.Callback;
import sample.util.InfoExchangeUtil;
import sample.util.Movie;
import sample.util.MultiLangMovie;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class MovieListController {

    @FXML
    private Label gpdirectortext;

    @FXML
    private VBox middlevb;

    @FXML
    private HBox toprighthb;

    @FXML
    private GridPane topgp;

    @FXML
    private Label gpactor;

    @FXML
    private BorderPane bp;

    @FXML
    private Label gplength;

    @FXML
    private Label gpdirector;

    @FXML
    private Label gpcountrytext;

    @FXML
    private JFXButton langselectbtn;

    @FXML
    private Label gplengthtext;

    @FXML
    private ImageView movieim;

    @FXML
    private Label gpintrotext;

    @FXML
    private Label gpactortext;

    @FXML
    private JFXButton backbtn;

    @FXML
    private ImageView airlinelogoim;

    @FXML
    private Label gptitletext;

    @FXML
    private Label gptitle;

    @FXML
    private Label gpcountry;

    @FXML
    private Label gpintro;

    @FXML
    private Label gpgenre;

    @FXML
    private Label gpgenretext;

    @FXML
    private ListView<Movie> movielv;

    @FXML
    private JFXButton queryBTN;

    @FXML
    private HBox toplefthb;

    @FXML
    private ScrollPane infoSP;

    private List<MultiLangMovie> movieFileList;

//    private ResourceBundle rb;

    /**
     * Back to movie recommentation page.
     *
     * @throws IOException
     */
    public void backBtnClicked() throws IOException {
        ResourceBundle bundle = ResourceBundle.getBundle("language");
        Parent root = FXMLLoader.load(getClass().getResource("/sample/view/movie_recom.fxml"),bundle);
        bp.getChildren().setAll(root);
    }

    /**
     * Start to play movie.
     *
     * @throws IOException
     */
    public void goPlay() throws IOException{
        ResourceBundle bundle = ResourceBundle.getBundle("language"); // load success
        Parent root = FXMLLoader.load(getClass().getResource("/sample/view/myplayer.fxml"),bundle);
        InfoExchangeUtil.setRecomMovieSelected((MultiLangMovie) movieim.getUserData());
        bp.getChildren().setAll(root);
    }

    /**
     * Query Query the movie genre again.
     *
     * @throws IOException
     */
    public void goQueryAgain() throws IOException{
        ResourceBundle bundle = ResourceBundle.getBundle("language");
        Parent root = FXMLLoader.load(getClass().getResource("/sample/view/filter.fxml"),bundle);
        bp.getChildren().setAll(root);
    }

    /**
     * Initialize fxml file.
     */
    @FXML
    void initialize() {
        backbtn.setText("");
        backbtn.setGraphic(new ImageView(new Image("/sample/resources/back.png")));

        langselectbtn.setVisible(false);

        if(InfoExchangeUtil.getFilteredMovies()==null){
            movieFileList = InfoExchangeUtil.getAllMovieList();
        } else {
            movieFileList = InfoExchangeUtil.getFilteredMovies();
            InfoExchangeUtil.setFilteredMovies(null);
        }

        ArrayList<Movie> currentLangfilms = new ArrayList<>(movieFileList.size());
        for(MultiLangMovie onemovie:movieFileList){
            Movie currentLangMovie = onemovie.getALangMovie(Locale.getDefault().toString());
            currentLangfilms.add(currentLangMovie);
        }
        ObservableList<Movie> items = FXCollections.observableArrayList(currentLangfilms);

        airlinelogoim.setImage(InfoExchangeUtil.getAirlineLogo());

        movielv.setItems(items);
        movielv.setCellFactory(new Callback<ListView<Movie>, ListCell<Movie>>() {
            @Override
            public ListCell<Movie> call(ListView<Movie> param) {
                ListCell<Movie> cell = new ListCell<Movie>() {

                    private Text text;
                    @Override
                    protected void updateItem(Movie item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            text = new Text(String.format("%s (%d)", item.getName(), item.getYear()));
                            text.setWrappingWidth(movielv.getPrefWidth()-25);
                            setGraphic(text);
                        }
                    }

                };
                return cell;
            }
        });
        movielv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);
            gptitletext.setText(newValue.getName());
            gpdirectortext.setText(newValue.getParent().getPeopleForListView(newValue.getDirectors()));
            gpactortext.setText(newValue.getParent().getPeopleForListView(newValue.getActors()));
            gplengthtext.setText(String.valueOf(newValue.getLength()));
            gpintrotext.setText(newValue.getIntro());
            gpgenretext.setText(newValue.getParent().getGenreForListView());
            gpcountrytext.setText(newValue.getCountry());

            if(gpgenretext.getText().length()==0){
                gpgenretext.setManaged(false);
                gpgenretext.setVisible(false);
                gpgenre.setManaged(false);
                gpgenre.setVisible(false);
            } else {
                gpgenretext.setManaged(true);
                gpgenretext.setVisible(true);
                gpgenre.setManaged(true);
                gpgenre.setVisible(true);
            }

            Image posterImage = new Image(newValue.getParent().getPoster().getAbsoluteFile().toURI().toString());
            movieim.setImage(posterImage);
            movieim.setUserData(newValue.getParent());

            InfoExchangeUtil.setMovieToPlayId(newValue.getParent().getId());
            System.out.println(new Media(newValue.getParent().getVideo().getAbsoluteFile().toURI().toString()));
            InfoExchangeUtil.setMovieToPlay(new Media(newValue.getParent().getVideo().getAbsoluteFile().toURI().toString()));

        });

        if(InfoExchangeUtil.getRecomMovieSelected()!=null){
            movielv.getSelectionModel().select(InfoExchangeUtil.getRecomMovieSelected().getALangMovie("zh-cn"));
            InfoExchangeUtil.setRecomMovieSelected(null);
        } else if(items.size()>0){
            movielv.getSelectionModel().select(0);
        }


    }


}
