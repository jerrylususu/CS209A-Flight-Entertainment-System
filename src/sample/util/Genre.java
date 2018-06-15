package sample.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class Genre {

    private int id;
    private HashMap<String,String> name;

//    public void addLangName(String lang, String name) {
//        if (this.name == null) {
//            this.name = new HashMap<>();
//        }
//        this.name.put(lang, name);
//    }

    /**
     * Get the localized name of a certain Language.
     * @param lang input language
     * @return localized name
     */
    public String getLangName(String lang){
        if(this.name==null)
            return "";
        if(this.name.containsKey(lang)){
            return this.name.get(lang);
        } else {
            if(this.name.containsKey(Locale.getDefault().toString())){
                return this.name.get(Locale.getDefault().toString());
            } else {
                return "LANG NOT SUPPORTED";
            }
        }
    }

    /**
     * For outputing formatting Genre obejct.
     *
     * @return
     */
    @Override
    public String toString() {
        return "Genre{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }

    /**
     * Override to judge whether it is the same object.
     *
     * @param o Object.
     * @return  Critical result.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre = (Genre) o;
        return id == genre.id;
    }

    /**
     * Return object's hashcode.
     *
     * @return Object's hashcode
     */
    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    /**
     * Gets id.
     *
     * @return Value of id.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets new id.
     *
     * @param id New value of id.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets new name.
     *
     * @param name New value of name.
     */
    public void setName(HashMap<String, String> name) {
        this.name = name;
    }

    /**
     * Gets name.
     *
     * @return Value of name.
     */
    public HashMap<String, String> getName() {
        return name;
    }
}
