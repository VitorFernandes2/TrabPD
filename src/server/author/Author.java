package server.author;

public class Author {

    private String name;
    private int id;
    private String address;

    public Author(String name, int id, String morada) {
        this.name = name;
        this.id = id;
        this.address = morada;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
