package sample.util;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.io.*;
import java.util.*;

public class MultiLangMovie {

    private @Expose int id;
    private File video=null;
    private File poster=null;
    private List<Genre> genre=null;
    private @Expose HashMap<String,Movie> langMovie;
    private @Expose List<Integer> genreIds=null;

    public <T> String getPeopleForListView(List<T> somelist){
        if(somelist.size()>0){
            StringBuilder sb = new StringBuilder();
            for(T t:somelist){
                sb.append(t.toString()+"\n");
            }
//            sb.deleteCharAt(sb.length()-1);
            return sb.toString();
        } else {
            return "";
        }
    }

    public String getGenreForListView(){
        if(genre.size()>0){
            StringBuilder sb = new StringBuilder();
            for(Genre g:genre){
                sb.append(g.getLangName(Locale.getDefault().toString())+" ");
            }
            sb.deleteCharAt(sb.length()-1);
            return  sb.toString();
        } else {
            return "";
        }

    }

    public List<Integer> getGenreIds() {
        if(genreIds==null){
            return new ArrayList<>();
        } else {
            return this.genreIds;
        }
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public List<Genre> getGenre() {
        return (genre==null)?new ArrayList<>():genre;
    }

    public void setGenre(List<Genre> genre) {
        this.genre = genre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public File getVideo() {
        return video;
    }

    public void setVideo(File video) {
        this.video = video;
    }

    public File getPoster() {
        return poster;
    }

    public void setPoster(File poster) {
        this.poster = poster;
    }

    public HashMap<String, Movie> getLangMovie() {
        return langMovie;
    }

    public void setLangMovie(HashMap<String, Movie> langMovie) {
        this.langMovie = langMovie;
    }

    public Movie getALangMovie(String lang){
        if(this.langMovie.containsKey(lang)){
            return this.langMovie.get(lang);
        } else {
            if(this.langMovie.containsKey(InfoExchangeUtil.getProperties().getProperty("fallbackLang"))){
                return this.langMovie.get(InfoExchangeUtil.getProperties().getProperty("fallbackLang"));
            } else {
                Iterator<String> keys = this.langMovie.keySet().iterator();
                return this.langMovie.get(keys.next());
            }
        }
    }

    public void addALangMovie(String lang, Movie m){
        this.langMovie.put(lang, m);
    }

    public MultiLangMovie(int id, HashMap<String, Movie> langMovie) {
        this.id = id;
        this.langMovie = langMovie;
    }

    public MultiLangMovie(int id, File video, File poster, HashMap<String, Movie> langMovie) {
        this.id = id;
        this.video = video;
        this.poster = poster;
        this.langMovie = langMovie;
    }

    public MultiLangMovie(){
        this.langMovie = new HashMap<>();
    }

    @Override
    public String toString() {
        return "MultiLangMovie{" +
                "id=" + id +
                ", video=" + video +
                ", poster=" + poster +
                ", genre=" + genre +
                ", langMovie=" + langMovie +
                ", genreIds=" + genreIds +
                '}';
    }
}
