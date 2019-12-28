package mainObjects;

public class Music {

    private String year;
    private String name;
    private String album;
    private String author;
    private String genre;
    private double duration;
    private String path;

    public Music(String year, String name, String album, String author, String genre, double duration, String path) {
        this.year = year;
        this.name = name;
        this.album = album;
        this.author = author;
        this.genre = genre;
        this.duration = duration;
        this.path = path;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return year + "\n" + name + "\n" +album + "\n" +author + "\n" +
                genre + "\n" +duration + "\n" +path;
    }
}
