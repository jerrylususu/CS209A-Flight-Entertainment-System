package sample.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.ObjectRowProcessor;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Scrape {

  private static HashMap<String, String> title_tconst = new HashMap<>();
  private static String[] movieslist;
  private static String moviesFilePath;

  /**
   * Read imdb data files to get tconst from movie title.
   *
   * @param tsvFile imdb data file
   * @param writeFile the path json file to write
   */
  private static void creatTitleTconstTable(String tsvFile, String writeFile) throws IOException {
    TsvParserSettings tsvParserSettings = new TsvParserSettings();
    ObjectRowProcessor rowProcessor = new ObjectRowProcessor() {
      @Override
      public void rowProcessed(Object[] row, ParsingContext context) {
        //here is the row. Let's just print it.
        if (title_tconst.get(row[2].toString()) == null) {
          title_tconst.put(row[2].toString(), row[0].toString());
        }

      }
    };

    tsvParserSettings.setRowProcessor(rowProcessor);
    TsvParser parser = new TsvParser(tsvParserSettings);
    parser.parse(new InputStreamReader(new FileInputStream(tsvFile), "UTF8") {
    });

    try (BufferedWriter writer = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(writeFile), "UTF8"))) {
      Gson gson = new GsonBuilder().create();
      gson.toJson(title_tconst, writer);
    }

  }

  /**
   * read title_tcost hashmap from json file
   *
   * @param file title_tconst Json file to read
   */

  private static void readTitleTconst(String file) throws IOException {
    Gson gson = new Gson();
    Type type = new TypeToken<HashMap<String, String>>() {
    }.getType();
    title_tconst = gson.fromJson(new InputStreamReader(new FileInputStream(file), "UTF8"), type);
  }

  /**
   * read  movieslist from moveieslist.txt
   *
   * @param file movieslist.txt to read
   */
  private static void readMovielist(String file) throws IOException {
    BufferedReader br = new BufferedReader(
            new InputStreamReader(new FileInputStream(file), "UTF8"));
    movieslist = new String[moviesNum];
    try {
      String line = br.readLine();
      int i = 0;
      while (line != null) {
        movieslist[i++] = line;
        line = br.readLine();
      }
    } finally {
      br.close();
    }
  }

  /**
   * Append movieslist with tconst for scrape movie information
   *
   * @param file the path to write new movieslist.txt with tconst
   */
  private static void getTconst(String file) throws IOException {
    PrintWriter writer = new PrintWriter(file, "UTF-8");
    for (int i = 0; i < movieslist.length; i++) {
      if (movieslist[i] != null) {
        String[] strings = movieslist[i].split(",");
        movieslist[i] += "," + title_tconst.get(strings[1]);
      }
      writer.println(movieslist[i]);
    }

    writer.close();
  }

  private static HashMap<String, Integer> Genre_To_ID;
  private static HashMap<Integer, Genre> ID_To_Genre;

  /**
   * Read Genre list file to store in hashmap
   *
   * @param file Genre file path
   */
  private static void readGenreFile(String file) throws IOException {
    Genre_To_ID = new HashMap<>();
    ID_To_Genre = new HashMap<>();
    Type genreType = new TypeToken<List<Genre>>() {
    }.getType();
    List<Genre> genreList = new Gson()
            .fromJson(new InputStreamReader(new FileInputStream(file), "UTF8"), genreType);
    for (Genre genre : genreList
            ) {
      Genre_To_ID.put(genre.getName().get("en_US"), genre.getId());
      ID_To_Genre.put(genre.getId(), genre);
    }
  }


  /**
   * Scrape movie information by moviesList file
   *
   * @param moviesFile movies lists
   */
  private static void ScrapeImdb(String moviesFile) throws IOException {
    for (int i = 0; i < moviesNum; i++) {
      if (movieslist[i] == null) {
        break;
      }
      String[] strings = movieslist[i].split(",");
      String url = "https://www.imdb.com/title/" + strings[2] + "/";

      Document document = Jsoup.connect(url).get();
      int year = 0;
      try {
        year = Integer.parseInt(document.getElementById("titleYear").text().replaceAll("[()]", ""));
      } catch (Exception e) {
        System.err.println(e.getMessage());

        System.out.println(strings[0]);
      }

      // title
      String title = strings[1];

      // intro
      String intro = document.getElementsByClass("summary_text").text();

      Elements info = document.getElementsByClass("credit_summary_item");

      // directors
      LinkedList<String> directors = new LinkedList<>();
      Elements Directors = info.get(0).select("span a");
      for (Element e : Directors
              ) {
        directors.add(e.text());
      }

      // actors
      LinkedList<String> actors = new LinkedList<>();
      Elements Actors = info.get(2).select("span a");
      for (Element e : Actors
              ) {
        actors.add(e.text());
      }

      Elements elements = document.getElementsByClass("subtext").select("a");
      String country = "";
      for (Element e : elements) {
        if (!e.select("a").attr("title").isEmpty()) {
          country = e.text();
          country = country.substring(country.indexOf("(") + 1, country.indexOf(")"));
        }
      }

      String lengthStr = document.getElementsByClass("subtext").select("time").text()
              .replace("h", "").replace("min", "");

      String[] lengthStrings = lengthStr.split(" ");

      int length = 0;
      if (lengthStrings.length == 2) {
        length = Integer.parseInt(lengthStrings[0]) * 60 + Integer.parseInt(lengthStrings[1]);
      } else {
        length = Integer.parseInt(lengthStrings[0]) * 60;
      }

      // scrape genres
      List<Genre> genre = new LinkedList<>();
      List<Integer> genreIDs = new LinkedList<>();
      Elements genreElements = elements.select("span.itemprop");
      for (Element e : genreElements
              ) {
        if (e.attr("itemprop").equals("genre")) {
          int id = Genre_To_ID.get(e.text());
          genre.add(ID_To_Genre.get(id));
          genreIDs.add(id);
        }
      }

      // cunrrent language
      String language = "en_US";

      MultiLangMovie multiLangMovie = new MultiLangMovie();
      Movie movie = new Movie();
      multiLangMovie.setId(Integer.parseInt(strings[0]));
      movie.setActors(actors);
      movie.setIntro(intro);
      movie.setDirectors(directors);
      movie.setLength(length);
      movie.setName(title);
      movie.setYear(year);
      movie.setCountry(country);
      multiLangMovie.addALangMovie(language, movie);
      multiLangMovie.setGenre(genre);
      multiLangMovie.setGenreIds(genreIDs);

      String fileWrite = moviesFile + "\\" + strings[0] + "\\info.json";
      try (BufferedWriter writer = new BufferedWriter(
              new OutputStreamWriter(new FileOutputStream(fileWrite), "UTF8"))) {
        System.out.println(multiLangMovie);
        Gson gson = InfoExchangeUtil.gsonFactory();
        gson.toJson(multiLangMovie, writer);
        System.out.println("Success to write into " + fileWrite);
      }

    }


  }


  /**
   * Read images url list and get the saved address of the image
   *
   * @param imageslist the image url lists to read
   * @param destFile the movielists file
   */
  private static void scrapeImage(String imageslist, String destFile) throws IOException {
    InputStreamReader fileReader = new InputStreamReader(new FileInputStream(imageslist), "UTF8");
    BufferedReader bufferedReader = new BufferedReader(fileReader);
    String url;
    int id = 1;
    while ((url = bufferedReader.readLine()) != null) {
      String destination = destFile + "\\" + (id++) + "\\poster.jpg";
      saveImage(url, destination);
      System.out.println("Create File : " + destination + " successfully !");
    }
    bufferedReader.close();
  }


  /**
   * Scrape image and save to file.
   *
   * @param imageUrl the imageUrl
   * @param destFile the saved address of the image
   */
  private static void saveImage(String imageUrl, String destFile) throws IOException {
    URL url = new URL(imageUrl);
    InputStream in = new BufferedInputStream(url.openStream());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] buf = new byte[1024];
    int n = 0;
    while (-1 != (n = in.read(buf))) {
      out.write(buf, 0, n);
    }
    out.close();
    in.close();
    byte[] response = out.toByteArray();
    FileUtils.writeByteArrayToFile(new File(destFile), response);

  }


  /**
   * @param movieFilePath the path of where movies file will create
   */
  private static void createMovieFile(String movieFilePath) {
    String fileName = movieFilePath + "\\movies";
    File file = new File(fileName);
    if (file.mkdir()) {
      System.out.println("Successfully create file : " + fileName);
      for (int id = 1; id <= movieslist.length; id++) {
        String movieFileName = fileName + "\\" + id;
        File moviefile = new File(movieFileName);
        if (moviefile.mkdir()) {
          System.out.println("Successfully create file : " + movieFileName);
        } else {
          System.out.println("Unable to create file : " + movieFilePath);
        }
      }
    } else {
      System.out.println("Unable to create file : " + fileName);
    }


  }

  /**
   * For separating movie files from the source directory
   *
   * @param moviesSource the source directory of movie files
   * @param moviesDest the dest   directory of movies
   */
  private static void startMoveMoviesFiles(String moviesSource, String moviesDest)
          throws IOException {
    File folder = new File(moviesSource);
    File[] listOfFiles = folder.listFiles();

    for (int i = 1; i <= moviesNum; i++) {
      String movieName = listOfFiles[i - 1].getName();
      String Source = moviesSource + "\\" + movieName;
      String Dest = moviesDest + "\\" + i;
      moveFile(Source, Dest);
    }

  }


  /**
   * Move movie from source file path to dest file path
   *
   * @param source the source file of movie file
   * @param dest the dest   file of movie file
   * @throws IOException
   */
  private static void moveFile(String source, String dest) throws IOException {
    File Source = new File(source);
    try {
      Source.renameTo(new File(dest + "\\" + "trailers.mp4"));
    }catch (Exception e){
      System.err.println(e.getMessage());
    }

    System.out.println("Remove " + Source.getName() + " to " + dest + " tralers.mp4 succesfully!");
  }

  private static String moviesListFile;
  private static String imagesListFile;
  private static String sourceMoviesFile;
  private static String genresLisyFile;
  private static int moviesNum;

  /**
   * Where the program starts to run
   *
   * @param args the arguments of the program
   */
  public static void main(String[] args) throws IOException {
    Gson gson = new Gson();
    Type type = new TypeToken<HashMap<String, String>>() {
    }.getType();
    HashMap<String, String> hm = gson
            .fromJson(new InputStreamReader(new FileInputStream(args[0]), "UTF8"), type);
    moviesNum = Integer.parseInt(hm.get("moviesNum"));
    moviesListFile = hm.get("moviesListFile");
    moviesFilePath = hm.get("moviesFilePath");
    imagesListFile = hm.get("imagesListFile");
    sourceMoviesFile = hm.get("sourceMoviesFile");
    genresLisyFile = hm.get("genresLisyFile");
    readMovielist(moviesListFile);
    readGenreFile(genresLisyFile);
    createMovieFile(moviesFilePath);
    ScrapeImdb(moviesFilePath + "\\movies");
    scrapeImage(imagesListFile, moviesFilePath + "\\movies");
    startMoveMoviesFiles(sourceMoviesFile, moviesFilePath + "\\movies");
  }

}
