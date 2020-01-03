package client.logic;

import mainObjects.Music;
import org.json.simple.JSONObject;

import java.util.List;

/**
 *
 * @author Joao Coelho
 * 
 * REVER!!! Pode ser mau implmentato... serve para fazer a conceo de datos entre observer e observable
 */
public class ConnectData {
    
    private String name;
    private String username;
    private String password;
    private JSONObject JObj = new JSONObject();
    private String command;
    private int menu = 7;
    private Music music = null;
    private String nome = null;
    private String autor = null;
    private List<String> musicNameList = null;
    private List<String> musicAuthorList = null;
    
    public JSONObject getJObj() {
        return JObj;
    }

    public void setJObj(JSONObject JObj) {
        this.JObj = JObj;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getMenu() {
        return menu;
    }

    public void setMenu(int menu) {
        this.menu = menu;
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(String year, String name, String album, String author, String genre, double duration, String path) {
        this.music = new Music(year, name, album, author, genre, duration, path);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public List<String> getMusicNameList() {
        return musicNameList;
    }

    public void setMusicNameList(List<String> musicNameList) {
        this.musicNameList = musicNameList;
    }

    public List<String> getMusicAuthorList() {
        return musicAuthorList;
    }

    public void setMusicAuthorList(List<String> musicAuthorList) {
        this.musicAuthorList = musicAuthorList;
    }

}
