package music;

import author.Author;

import java.util.Date;

public class Music {

    private int id;
    private String name;
    private int author;
    private int album;
    private int year;
    private Date duration;
    private String gender;
    private String path;

    public Music(int id, String name, int author, int album, int year, Date duration, String gender, String path) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.album = album;
        this.year = year;
        this.duration = duration;
        this.gender = gender;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAuthor() {
        return author;
    }

    public void setAuthor(int author) {
        this.author = author;
    }

    public int getAlbum() {
        return album;
    }

    public void setAlbum(int album) {
        this.album = album;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Date getDuration() {
        return duration;
    }

    public void setDuration(Date duration) {
        this.duration = duration;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
