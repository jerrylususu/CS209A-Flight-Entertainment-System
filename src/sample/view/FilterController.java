package sample.view;

import com.jfoenix.controls.JFXButton;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import sample.util.Genre;
import sample.util.InfoExchangeUtil;
import sample.util.MultiLangMovie;

public class FilterController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private JFXButton resetBTN;

    @FXML
    private ListView yearLV;

    @FXML
    private ListView genreLV;

    @FXML
    private JFXButton backBTN;

    @FXML
    private JFXButton confirmBTN;

    @FXML
    private ListView lengthLV;

    @FXML
    private ImageView airlineLogoIV;

    @FXML
    private BorderPane bp;

    @FXML
    private Label warnLabel;

    @FXML
    /**
     * Initialize fxml file
     */
    void initialize() {

        backBTN.setText("");
        backBTN.setGraphic(new ImageView(new Image("/sample/resources/back.png")));

        airlineLogoIV.setImage(InfoExchangeUtil.getAirlineLogo());

        ArrayList<Genre> genreli = new ArrayList<>(InfoExchangeUtil.getPossibleGenres());
        ObservableList<Genre> genreObservableList = FXCollections.observableArrayList(genreli);

        genreLV.setItems(genreObservableList);
        genreLV.setCellFactory(new Callback<ListView<Genre>, ListCell<Genre>>() {
            @Override
            public ListCell<Genre> call(ListView<Genre> param) {
                ListCell<Genre> cell = new ListCell<Genre>() {
                    @Override
                    protected void updateItem(Genre item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getLangName(Locale.getDefault().toString()));
                        }
                    }
                };
                return cell;
            }
        });

        ArrayList<Integer> yearli = new ArrayList<>(InfoExchangeUtil.getPossibleYears());
        Collections.sort(yearli, Comparator.reverseOrder());
        ObservableList<Integer> yearObservableList = FXCollections.observableArrayList(yearli);
        yearLV.setItems(yearObservableList);

        List<String> lengthli = Arrays.asList("<30","30~60","60~90","90~120",">120");
        ObservableList<String> lengthObservableList = FXCollections.observableList(lengthli);
        lengthLV.setItems(lengthObservableList);

        yearLV.getSelectionModel().clearSelection();
        genreLV.getSelectionModel().clearSelection();
        lengthLV.getSelectionModel().clearSelection();

        genreLV.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
            warnLabel.setText("");
        });
        yearLV.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
            warnLabel.setText("");
        });
        lengthLV.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
            warnLabel.setText("");
        });
    }

    /**
     * Press button to trigger the event.
     */
    public void setResetBTN(){
        yearLV.getSelectionModel().clearSelection();
        genreLV.getSelectionModel().clearSelection();
        lengthLV.getSelectionModel().clearSelection();
        initialize();
    }


    /**
     * Query movie genre
     * @throws IOException
     */
    public void doQuery() throws IOException{
        List<Predicate<MultiLangMovie>> predicateList = new ArrayList<>(3);
        Genre g;
        Integer i;

        if((g = (Genre) genreLV.getSelectionModel().getSelectedItem())!=null){
            Predicate<MultiLangMovie> p = m -> m.getGenre().contains(g);
            predicateList.add(p);
        }
        if((i = (Integer) yearLV.getSelectionModel().getSelectedItem())!=null){
            Predicate<MultiLangMovie> p = m -> m.getALangMovie(Locale.getDefault().toString()).getYear()==i;
            predicateList.add(p);
        }

        if(lengthLV.getSelectionModel().getSelectedItem()!=null){
            int no = lengthLV.getSelectionModel().getSelectedIndex();
            Predicate<MultiLangMovie> p = null;
            Predicate<MultiLangMovie> p2 = null;
            switch (no){
                case 0: p = m -> m.getALangMovie(Locale.getDefault().toString()).getLength() <= 30; break;
                case 1: p = m -> m.getALangMovie(Locale.getDefault().toString()).getLength() >= 30;
                        p2 = m -> m.getALangMovie(Locale.getDefault().toString()).getLength() <= 60;
                        break;
                case 2: p = m -> m.getALangMovie(Locale.getDefault().toString()).getLength() >= 60;
                    p2 = m -> m.getALangMovie(Locale.getDefault().toString()).getLength() <= 90;
                    break;
                case 3: p = m -> m.getALangMovie(Locale.getDefault().toString()).getLength() >= 90;
                    p2 = m -> m.getALangMovie(Locale.getDefault().toString()).getLength() <= 120;
                    break;
                case 4: p = m -> m.getALangMovie(Locale.getDefault().toString()).getLength() >= 120;
                    break;
                default:
                    System.out.println("No selection of length made"); break;
            }
            if(p!=null){
                predicateList.add(p);
            }
            if(p2!=null){
                predicateList.add(p2);
            }


        }

        boolean doFilter = true;

        if(predicateList.size()==0){
            doFilter = false;
            warnLabel.setText(resources.getString("no_filter_req"));
        }

        if(doFilter){
            Predicate<MultiLangMovie> compPredicate = predicateList.stream().reduce(w -> true, Predicate::and);
            InfoExchangeUtil.filterMoives(compPredicate);
        }

        if(InfoExchangeUtil.getFilteredMovies()==null||InfoExchangeUtil.getFilteredMovies().size()==0){
            warnLabel.setText(resources.getString("no_movie_remain"));
        } else {
            ResourceBundle bundle = ResourceBundle.getBundle("language");
            Parent root = FXMLLoader.load(getClass().getResource("/sample/view/list.fxml"),bundle);
            bp.getChildren().setAll(root);
        }


    }

    /**
     * Return to movie recommendation page
     *
     * @throws IOException
     */
    public void setBackBTN() throws IOException {
        ResourceBundle bundle = ResourceBundle.getBundle("language");
        Parent root = FXMLLoader.load(getClass().getResource("/sample/view/movie_recom.fxml"),bundle);
        bp.getChildren().setAll(root);


    }

}
