package sample.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.util.Duration;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InfoExchangeUtil {

    private static List<String> moviePathList;
    private static List<MultiLangMovie> recomMovieList;
    private static List<MultiLangMovie> allMovieList;
    private static Media movieToPlay;
    private static int movieToPlayId;
    private static Image airlineLogo;
    private static MultiLangMovie recomMovieSelected;
    private static Properties properties;
    private static ArrayList<Genre> genreList;
    private static Set<Genre> possibleGenres;
    private static Set<Integer> possibleYears;
    private static List<MultiLangMovie> filteredMovies;
    private static HashMap<Integer, Duration> moviePlayTimes = new HashMap<>();
    private static String filghtNo;


    private static void initGenreToMovie(){
        for(MultiLangMovie multiLangMovie:allMovieList){
            ArrayList<Genre> genreArrayList = new ArrayList<>();
            for(int i:multiLangMovie.getGenreIds()){
                genreList.stream().filter(g -> g.getId()==i).forEach(genreArrayList::add);
            }
            multiLangMovie.setGenre(genreArrayList);
        }
    }

    private static void initPossibleGenresAndYears(){
        possibleGenres = new HashSet<>();
        possibleYears = new HashSet<>();
        for(MultiLangMovie multiLangMovie:allMovieList){
            if(multiLangMovie.getGenreIds()!=null){
                for(int i:multiLangMovie.getGenreIds()){
                    genreList.stream().filter((g)->g.getId()==i).forEach(possibleGenres::add);
                }
            }
            if(multiLangMovie.getALangMovie(Locale.getDefault().toString())!=null){
                possibleYears.add(multiLangMovie.getALangMovie(Locale.getDefault().toString()).getYear());
            }
        }
        if(possibleYears.contains(0)){
            possibleYears.remove(0);
        }
    }

    /**
     * Provide Gson object with @Expose annotation working.
     *
     * @return Gson object with @Expose annotation working
     */
    public static Gson gsonFactory(){
        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        return builder.create();
    }

    private static void setGenreFromIds(MultiLangMovie mlmovie){
        List<Genre> genres = InfoExchangeUtil.genreList;
        List<Integer> genreIds = mlmovie.getGenreIds();
        List<Genre> thisgenre = new ArrayList<>();
        for(int genreId:genreIds){
            for(Genre g:genres){
                if(genreId == g.getId())
                    thisgenre.add(g);
            }
        }
        mlmovie.setGenre(thisgenre);
    }

    private static void putGenreIntoIds(MultiLangMovie mlmovie){
        List<Integer> generIds = new ArrayList<>();
        List<Genre> genres = mlmovie.getGenre();
        for(Genre g:genres){
            generIds.add(g.getId());
        }
        mlmovie.setGenreIds(generIds);
    }

    private static void initGenre(String genreJsonPath){
        File genreJsonFile = new File(genreJsonPath);
        
        StringBuilder sb = new StringBuilder();
        try{
            FileInputStream fileInputStream = new FileInputStream(genreJsonFile);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,"utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            while((line = bufferedReader.readLine())!=null){
                sb.append(line);
            }

            bufferedReader.close();
            inputStreamReader.close();
        }catch (IOException e){
            e.printStackTrace();
        }

        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Genre>>(){}.getType();
        genreList = gson.fromJson(sb.toString(),listType);
//        System.out.println(genreList);
//        System.out.println(genreList.get(1).getId());
    }

    private static void initProperties(){

        properties = new Properties();

        Map<String, String> sysEnvMap = System.getenv();
        String loadpath;
        if(sysEnvMap.containsKey("movieconf")){
            loadpath = sysEnvMap.get("movieconf");
            System.out.println("FOUND");
        } else {
            System.out.println("WARNING: USING DEFAULT CONFIG FILE");
            loadpath = InfoExchangeUtil.class.getClassLoader().getResource("").getFile() + "sample/resources/conf.properties";
        }

//        System.out.println(sysEnvMap);

        System.out.println(loadpath);
//        loadpath = loadpath.substring(1);

        try {
            FileInputStream fileInputStream = new FileInputStream(loadpath);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,"utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

//            BufferedReader bufferedReader = new BufferedReader(new FileReader(loadpath));
            properties.load(bufferedReader);
            System.out.println(properties);
            bufferedReader.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        System.out.println(properties.getProperty("airlineLogo"));
    }

    private static void initAirlineLogo(String imageurl){
        Image image = new Image(imageurl);
        airlineLogo = image;
    }

    /**
     * Initiate all things that will be used.
     * Including properties, filghtNo, airlineLogo, genreList, MovieList, RecomMovieList and more.
     */
    public static void initAll(){
        initProperties();
        filghtNo = properties.getProperty("flightNo");
        System.out.println(filghtNo);
        initAirlineLogo("file:///" +properties.getProperty("airlineLogo"));
        initGenre(properties.getProperty("genrePath"));
        initAllMovie(properties.getProperty("moviePath"));
        initGenreToMovie();
        initRecomMovie(properties.getProperty("recomPath"));
        initPossibleGenresAndYears();
        System.out.println("movie loaded:"+allMovieList.size());
        System.out.println(allMovieList.get(0).getLangMovie().size());
    }

    private static void initAllMovie(String movieFolderPath){
        File moviedir = new File(movieFolderPath);
        File[] moviefolders = new File[0];
        try{
            moviefolders = moviedir.listFiles();
            if(moviefolders==null){
                throw new NullPointerException("Null pointer for movie folder");
            }
            if(moviefolders.length==0){
                throw new Exception("Empty movie folder");
            }
            InfoExchangeUtil.allMovieList = new ArrayList<>(moviefolders.length);
            for(File onemovie:moviefolders){
                if(onemovie.isDirectory()){
                    allMovieList.add(initOneMultiLangMovie(onemovie));
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

//    public static void TempinitAllMovie(){
//        File moviedir = new File("C:/Users/jerry/Downloads/Temp/movies");
//        File[] moviefolders = moviedir.listFiles();
//        InfoExchangeUtil.allMovieList = new ArrayList<>(moviefolders.length);
//        for(File onemovie:moviefolders){
//            if(onemovie.isDirectory()){
//                allMovieList.add(initOneMultiLangMovie(onemovie));
//            }
//        }
//    }

    private static void initRecomMovie(String recomJsonPath){
        File recomJsonFile = new File(recomJsonPath);
        StringBuilder sb = new StringBuilder();
        try{
            FileInputStream fileInputStream = new FileInputStream(recomJsonFile);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,"utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            while((line = bufferedReader.readLine())!=null){
                sb.append(line);
            }

            bufferedReader.close();
            inputStreamReader.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        ArrayList<Integer> RecomMovieIds = new ArrayList<>();

        Gson gson = new Gson();
        RecomMovieIds = gson.fromJson(sb.toString(),RecomMovieIds.getClass());

        getRecomMovie(RecomMovieIds);
    }

    private static void generateRecomMovie(){

        ArrayList<Integer> RecommendedMovieID = new ArrayList<>();

        RecommendedMovieID.add(1);
        RecommendedMovieID.add(2);
        RecommendedMovieID.add(3);

        Gson gson = new Gson();

        String s = gson.toJson(RecommendedMovieID);
        System.out.println("jsonhere");
        System.out.println(s);

        ArrayList<Integer> recomMovieIds = gson.fromJson(s,ArrayList.class);
        System.out.println(recomMovieIds);
        getRecomMovie(recomMovieIds);
    }

    /**
     * Load poster from multiLangMovie into an Image object
     * @param multiLangMovie input multiLangMovie
     * @return image object with valid url
     */
    public static Image loadPosterFromMultiLangMovie(MultiLangMovie multiLangMovie){
        String posterURL = multiLangMovie.getPoster().getAbsoluteFile().toURI().toString();
        return new Image(posterURL);
    }

    private static void getRecomMovie(List<Integer> recomMovieIds){
        recomMovieList = allMovieList.stream().filter((m) -> recomMovieIds.contains((double)m.getId())).collect(Collectors.toList());

    }

    /**
     * Filter movie according to inputted composited predicate.
     * @param compPredicate composited predicate
     */
    public static void filterMoives(Predicate compPredicate){
        filteredMovies = (List<MultiLangMovie>) allMovieList.stream().filter(compPredicate).collect(Collectors.toList());
        System.out.println(filteredMovies.size());
//        for (MultiLangMovie m:filteredMovies){
//            System.out.println(m.getGenre());
//        }
    }

    /*
    main test method here
     */
//    public static void main(String[] args) {
//        initProperties();
//        initGenre(properties.getProperty("genrePath"));
//        initAllMovie(properties.getProperty("moviePath"));
//        initPossibleGenresAndYears();
//        System.out.println(possibleGenres);
//        Predicate<MultiLangMovie> p = m -> m.getGenreIds().contains(1);
//        filterMoives(p);
//        System.out.println(filteredMovies);
//        System.out.println(possibleYears);
//    }

    private static MultiLangMovie initOneMultiLangMovie(File dir){
        File[] files = dir.listFiles();

        File infojson = null;

        try{
            for(File f:files){
                if(f.getName().endsWith(".json")){
                    infojson = f;
                }
            }
            if(infojson==null){
                throw new NullPointerException("No json found");
            }
        } catch (NullPointerException e){
            e.printStackTrace();
        }



        StringBuilder sb = new StringBuilder();

        try {
            FileInputStream fileInputStream = new FileInputStream(infojson);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,"utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            while((line = bufferedReader.readLine())!=null){
                sb.append(line);
            }

            bufferedReader.close();
            inputStreamReader.close();

        } catch (IOException e){
            e.printStackTrace();
        }

        Gson gson = new Gson();

        MultiLangMovie one = gson.fromJson(sb.toString(),MultiLangMovie.class);

        one.getLangMovie().forEach((k,v)->v.setParent(one));

        for(File f:files){
            if(f.getName().endsWith(".mp4")){
                one.setVideo(f);
            } else if(f.getName().startsWith("poster")){
                one.setPoster(f);
            }
        }

        return one;
    }


    /**
     * Sets new airlineLogo.
     *
     * @param airlineLogo New value of airlineLogo.
     */
    public static void setAirlineLogo(Image airlineLogo) {
        InfoExchangeUtil.airlineLogo = airlineLogo;
    }

    /**
     * Gets airlineLogo.
     *
     * @return Value of airlineLogo.
     */
    public static Image getAirlineLogo() {
        return airlineLogo;
    }

    /**
     * Gets possibleYears.
     *
     * @return Value of possibleYears.
     */
    public static Set<Integer> getPossibleYears() {
        return possibleYears;
    }

    /**
     * Sets new moviePathList.
     *
     * @param moviePathList New value of moviePathList.
     */
    public static void setMoviePathList(List<String> moviePathList) {
        InfoExchangeUtil.moviePathList = moviePathList;
    }

    /**
     * Gets genreList.
     *
     * @return Value of genreList.
     */
    public static ArrayList<Genre> getGenreList() {
        return genreList;
    }

    /**
     * Gets allMovieList.
     *
     * @return Value of allMovieList.
     */
    public static List<MultiLangMovie> getAllMovieList() {
        return allMovieList;
    }

    /**
     * Sets new moviePlayTimes.
     *
     * @param moviePlayTimes New value of moviePlayTimes.
     */
    public static void setMoviePlayTimes(HashMap<Integer, Duration> moviePlayTimes) {
        InfoExchangeUtil.moviePlayTimes = moviePlayTimes;
    }

    /**
     * Gets recomMovieList.
     *
     * @return Value of recomMovieList.
     */
    public static List<MultiLangMovie> getRecomMovieList() {
        return recomMovieList;
    }

    /**
     * Sets new genreList.
     *
     * @param genreList New value of genreList.
     */
    public static void setGenreList(ArrayList<Genre> genreList) {
        InfoExchangeUtil.genreList = genreList;
    }

    /**
     * Sets new possibleGenres.
     *
     * @param possibleGenres New value of possibleGenres.
     */
    public static void setPossibleGenres(Set<Genre> possibleGenres) {
        InfoExchangeUtil.possibleGenres = possibleGenres;
    }

    /**
     * Sets new filteredMovies.
     *
     * @param filteredMovies New value of filteredMovies.
     */
    public static void setFilteredMovies(List<MultiLangMovie> filteredMovies) {
        InfoExchangeUtil.filteredMovies = filteredMovies;
    }

    /**
     * Gets filghtNo.
     *
     * @return Value of filghtNo.
     */
    public static String getFilghtNo() {
        return filghtNo;
    }

    /**
     * Sets new filghtNo.
     *
     * @param filghtNo New value of filghtNo.
     */
    public static void setFilghtNo(String filghtNo) {
        InfoExchangeUtil.filghtNo = filghtNo;
    }

    /**
     * Sets new allMovieList.
     *
     * @param allMovieList New value of allMovieList.
     */
    public static void setAllMovieList(List<MultiLangMovie> allMovieList) {
        InfoExchangeUtil.allMovieList = allMovieList;
    }

    /**
     * Gets possibleGenres.
     *
     * @return Value of possibleGenres.
     */
    public static Set<Genre> getPossibleGenres() {
        return possibleGenres;
    }

    /**
     * Gets properties.
     *
     * @return Value of properties.
     */
    public static Properties getProperties() {
        return properties;
    }

    /**
     * Sets new movieToPlayId.
     *
     * @param movieToPlayId New value of movieToPlayId.
     */
    public static void setMovieToPlayId(int movieToPlayId) {
        InfoExchangeUtil.movieToPlayId = movieToPlayId;
    }

    /**
     * Sets new recomMovieList.
     *
     * @param recomMovieList New value of recomMovieList.
     */
    public static void setRecomMovieList(List<MultiLangMovie> recomMovieList) {
        InfoExchangeUtil.recomMovieList = recomMovieList;
    }

    /**
     * Gets filteredMovies.
     *
     * @return Value of filteredMovies.
     */
    public static List<MultiLangMovie> getFilteredMovies() {
        return filteredMovies;
    }

    /**
     * Gets moviePlayTimes.
     *
     * @return Value of moviePlayTimes.
     */
    public static HashMap<Integer, Duration> getMoviePlayTimes() {
        return moviePlayTimes;
    }

    /**
     * Gets recomMovieSelected.
     *
     * @return Value of recomMovieSelected.
     */
    public static MultiLangMovie getRecomMovieSelected() {
        return recomMovieSelected;
    }

    /**
     * Sets new recomMovieSelected.
     *
     * @param recomMovieSelected New value of recomMovieSelected.
     */
    public static void setRecomMovieSelected(MultiLangMovie recomMovieSelected) {
        InfoExchangeUtil.recomMovieSelected = recomMovieSelected;
    }

    /**
     * Sets new possibleYears.
     *
     * @param possibleYears New value of possibleYears.
     */
    public static void setPossibleYears(Set<Integer> possibleYears) {
        InfoExchangeUtil.possibleYears = possibleYears;
    }

    /**
     * Gets moviePathList.
     *
     * @return Value of moviePathList.
     */
    public static List<String> getMoviePathList() {
        return moviePathList;
    }

    /**
     * Sets new properties.
     *
     * @param properties New value of properties.
     */
    public static void setProperties(Properties properties) {
        InfoExchangeUtil.properties = properties;
    }

    /**
     * Gets movieToPlayId.
     *
     * @return Value of movieToPlayId.
     */
    public static int getMovieToPlayId() {
        return movieToPlayId;
    }

    /**
     * Gets movieToPlay.
     *
     * @return Value of movieToPlay.
     */
    public static Media getMovieToPlay() {
        return movieToPlay;
    }

    /**
     * Sets new movieToPlay.
     *
     * @param movieToPlay2 New value of movieToPlay.
     */
    public static void setMovieToPlay(Media movieToPlay2) {
        InfoExchangeUtil.movieToPlay = movieToPlay2;
    }
}
