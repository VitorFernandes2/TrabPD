/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PlayList;

import java.util.List;
import music.Music;
import user.User;

/**
 *
 * @author Luís António Moreira Ferreira da Silva
 */
public class PlayList {
    private int ID;
    
    private String name;
    
    private List<Music> musicList;
    
    private User user;

    public PlayList() {
        
    }

    public PlayList(int ID, String name, List<Music> musicList, User user) {
        this.ID = ID;
        this.name = name;
        this.musicList = musicList;
        this.user = user;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Music> getMusicList() {
        return musicList;
    }
    
//    public Music getMusicListByID(int ID) {
//        for (Music music : musicList) {
//            if(music.ID == ID){
//                return music;
//            }
//        }
//        return -1;
//    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
}
