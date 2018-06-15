package sample.util;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.io.*;
import java.util.HashMap;
import java.util.List;

public class Movie {

    private @Expose String name;
    private @Expose int length=0;
    private @Expose List<String> directors;
    private @Expose List<String> actors;
    private @Expose String intro;
    private @Expose int year=0;
    private @Expose String country;
    private MultiLangMovie parent;


    /**
     * For output formatting Movie object.
     *
     * @return
     */
    @Override
    public String toString() {
        return "Movie{" +
                "name='" + name + '\'' +
                ", length=" + length +
                ", directors=" + directors +
                ", actors=" + actors +
                ", intro='" + intro + '\'' +
                ", year=" + year +
                ", country='" + country + '\'' +
                '}';
    }

    /**
     * Sets new country.
     *
     * @param country New value of country.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Sets new length.
     *
     * @param length New value of length.
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * Sets new name.
     *
     * @param name New value of name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets actors.
     *
     * @return Value of actors.
     */
    public List<String> getActors() {
        return actors;
    }

    /**
     * Gets directors.
     *
     * @return Value of directors.
     */
    public List<String> getDirectors() {
        return directors;
    }

    /**
     * Gets parent.
     *
     * @return Value of parent.
     */
    public MultiLangMovie getParent() {
        return parent;
    }

    /**
     * Sets new year.
     *
     * @param year New value of year.
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Sets new intro.
     *
     * @param intro New value of intro.
     */
    public void setIntro(String intro) {
        this.intro = intro;
    }

    /**
     * Sets new parent.
     *
     * @param parent New value of parent.
     */
    public void setParent(MultiLangMovie parent) {
        this.parent = parent;
    }

    /**
     * Gets intro.
     *
     * @return Value of intro.
     */
    public String getIntro() {
        return intro;
    }

    /**
     * Sets new actors.
     *
     * @param actors New value of actors.
     */
    public void setActors(List<String> actors) {
        this.actors = actors;
    }

    /**
     * Gets name.
     *
     * @return Value of name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets year.
     *
     * @return Value of year.
     */
    public int getYear() {
        return year;
    }

    /**
     * Sets new directors.
     *
     * @param directors New value of directors.
     */
    public void setDirectors(List<String> directors) {
        this.directors = directors;
    }

    /**
     * Gets length.
     *
     * @return Value of length.
     */
    public int getLength() {
        return length;
    }

    /**
     * Gets country.
     *
     * @return Value of country.
     */
    public String getCountry() {
        return country;
    }
}