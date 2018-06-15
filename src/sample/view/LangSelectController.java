package sample.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import sample.Main;
import sample.util.InfoExchangeUtil;

import java.io.*;
import java.util.*;

import java.net.URL;
import java.util.Locale;

public class LangSelectController implements Initializable {


    @FXML
    private BorderPane bp;
    @FXML
    private VBox vbox;
    @FXML
    private JFXListView<String> langlist;
    @FXML
    private JFXButton langConfimbtn;
    @FXML
    private Label filghtNoLb;
    @FXML
    private ImageView airlineLogoIV;


    /**
     * Initialize fxml file
     *
     * @param location   the pathe to fxml file
     * @param resources  ResourceBundle file
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InfoExchangeUtil.initAll();

        airlineLogoIV.setImage(InfoExchangeUtil.getAirlineLogo());

        ObservableList<String> langli =FXCollections.observableArrayList("简体中文","繁体中文","English");

        langlist.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            String s =null;
            switch (newValue.intValue()){
                case 0: Locale.setDefault(Locale.SIMPLIFIED_CHINESE);break;
                case 1: Locale.setDefault(Locale.TRADITIONAL_CHINESE);break;
                case 2: Locale.setDefault(Locale.US);break;
            }
            ResourceBundle bundle = ResourceBundle.getBundle("language");
            s = bundle.getString("welcomeOnBoard");
            filghtNoLb.setText(String.format(s,InfoExchangeUtil.getFilghtNo()));
        });
        langlist.setItems(langli);
        langlist.getSelectionModel().selectFirst();
    }


    /**
     * Select language.
     *
     * @throws IOException
     */
    public void langSelected() throws IOException {
        System.out.println(Locale.getDefault());
        System.out.println(langlist.getSelectionModel().getSelectedIndices());

//        System.out.println(Main.class.getPackage().toString());

//        System.out.println(new File(String.valueOf(Main.class.getResource("/sample/resources/lang/language_en_US.properties"))).length());

        ResourceBundle bundle = ResourceBundle.getBundle("language"); // load success
        System.out.println(bundle.keySet());
        Parent root = FXMLLoader.load(getClass().getResource("/sample/view/movie_recom.fxml"),bundle);
        bp.getChildren().setAll(root);
    }

}
