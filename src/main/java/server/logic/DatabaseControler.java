package server.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.ServerLogic;

/**
 *
 * @author a21270909
 */
public class DatabaseControler {
    
   static String BD_URL = "jdbc:mysql://localhost:3306/?useTimezone=true&serverTimezone=UTC&useSSL=false"; 
   //static final String DB_URL = "jdbc:mysql://localhost:3306/EMP?useTimezone=true&serverTimezone=UTC&useSSL=false";
   private String user = "test"; // mudar para o igual ao definido no sql.
   private String pass = "1234";
   private Connection connect;
   private Statement stmt;
   private String namedb;
   private int serverport;
   private boolean principal;
   

    /**
     * ONLY USE TO OVERRIDE USERNAME AND PASSWORD
     */
    public DatabaseControler(String user, String pass, int si,boolean pinc) {
        this.user = user;
        this.pass = pass;
        this.serverport = si;
        this.principal = pinc;
    }

    public DatabaseControler(int si,boolean pinc) {
        this.serverport = si;
        this.principal = pinc;
    }
    
    public boolean startdatabase(ServerLogic sl){
        
        System.out.println("Connecting to database...");
          
        try {
            if(connect == null)
                    connect = DriverManager.getConnection(BD_URL,user,pass);
            
            if(principal == true)
                namedb = "Principal";
            else
                namedb = numbtoletter();
                
            System.out.println("Nome da base de dados: " + namedb);
            
            stmt = connect.createStatement();
            String CreateSql = ("CREATE DATABASE IF NOT EXISTS " + namedb);
            stmt.execute(CreateSql); 

            // REVER: Funcional mas agressivo demais
            String NewString = "jdbc:mysql://localhost:3306/"+namedb+"?useTimezone=true&serverTimezone=UTC&useSSL=false";
            BD_URL = NewString;
            connect.close();stmt.close();
            connect = DriverManager.getConnection(NewString,user,pass);
            stmt = connect.createStatement();

            String tableSql = "CREATE TABLE IF NOT EXISTS users"
            + "(user_id int PRIMARY KEY AUTO_INCREMENT, name varchar(30),"
            + "username varchar(30), password TINYTEXT NOT NULL)";
            stmt.execute(tableSql);

            String tableSql2 = "CREATE TABLE IF NOT EXISTS musics"
            + "(music_id int PRIMARY KEY AUTO_INCREMENT, name varchar(30),"
            + "artist varchar(30), album varchar(30), year varchar(30), duration double, genre varchar(30), localname varchar(90))";
            stmt.execute(tableSql2);

            String tableSql3 = "CREATE TABLE IF NOT EXISTS playlist"
            + "(play_id int PRIMARY KEY AUTO_INCREMENT,"
            + "user_id int, name varchar(30))";
            stmt.execute(tableSql3);

            String tableSql4 = "CREATE TABLE IF NOT EXISTS playlistmusic"
                    + "(id int PRIMARY KEY AUTO_INCREMENT, music_id int,"
                    + "play_id int)";
            stmt.execute(tableSql4);

        } catch (SQLException ex) {
            sl.Obj().put("exception", "[ERROR] Inicializao da Base de Dados -> " + ex.getMessage());
            sl.notifyObserver(4);
            return false;
        }
        return true;
    }

    public String getNamedb() {
        return namedb;
    }

    public void setNamedb(String namedb) throws SQLException {
        this.namedb = namedb;
        String ResetString = "jdbc:mysql://localhost:3306/"+this.namedb+"?useTimezone=true&serverTimezone=UTC&useSSL=false";
        this.connect = DriverManager.getConnection(ResetString,this.user,this.pass);
        this.stmt = connect.createStatement();
        
        String tableSql = "CREATE TABLE IF NOT EXISTS users"
        + "(user_id int PRIMARY KEY AUTO_INCREMENT, name varchar(30),"
        + "username varchar(30), password TINYTEXT NOT NULL)";
        stmt.execute(tableSql);

        String tableSql2 = "CREATE TABLE IF NOT EXISTS musics"
        + "(music_id int PRIMARY KEY AUTO_INCREMENT, name varchar(30),"
        + "artist varchar(30), album varchar(30), year varchar(30), duration double, genre varchar(30), localname varchar(90))";
        stmt.execute(tableSql2);

        String tableSql3 = "CREATE TABLE IF NOT EXISTS playlist"
        + "(play_id int PRIMARY KEY AUTO_INCREMENT,"
        + "user_id int, name varchar(30))";
        stmt.execute(tableSql3);

        String tableSql4 = "CREATE TABLE IF NOT EXISTS playlistmusic"
                + "(id int PRIMARY KEY AUTO_INCREMENT, music_id int,"
                + "play_id int)";
        stmt.execute(tableSql4);
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }
    
    public boolean insertuser(String name, String username, String password, ServerLogic sl){
        try {  
            String insertSql = "INSERT INTO users(name, username, password)"
            + " VALUES('"+name+"', '"+username+"', '"+password+"')";
            stmt.executeUpdate(insertSql);
        } catch (SQLException ex) {
            sl.Obj().put("exception", "[ERROR] InsertUser -> " + ex.getMessage());
            sl.notifyObserver(4);
            return false;
        }
        return true;
    }
    
    public boolean removeuser(String username, String password, ServerLogic sl){
        try {
            String deleteSql = "DELETE FROM `users` WHERE `users`.`username` = \"" + username + "\"";
            stmt.executeUpdate(deleteSql);
        } catch (SQLException ex) {
            sl.Obj().put("exception", "[ERROR] Logout -> " + ex.getMessage());
            sl.notifyObserver(4);
            return false;
        }
        return true;
    }
    
    public boolean insertuserMultiCast(String name, String username, String password, ServerLogic sl){
        try {
            String insertSql = "INSERT INTO users(name, username, password)"
            + " VALUES('"+name+"', '"+username+"', '"+password.trim()+"')";
            stmt.executeUpdate(insertSql);
        } catch (SQLException ex) {
            sl.Obj().put("exception", "[ERROR] InsertUser Multicast -> " + ex.getMessage());
            sl.notifyObserver(4);
            return false;
        }
        return true;
    }
    
    public boolean insertuserMultiCast(int user_id, String name, String username, String password, ServerLogic sl){
        try {
            String insertSql = "INSERT INTO users(user_id, name, username, password)"
            + " VALUES('"+user_id+"', '"+name+"', '"+username+"', '"+password.trim()+"')";
            stmt.executeUpdate(insertSql);
        } catch (SQLException ex) {
            sl.Obj().put("exception", "[ERROR] InsertUser Multicast -> " + ex.getMessage());
            sl.notifyObserver(4);
            return false;
        }
        return true;
    }
    
    public boolean insertPlaylistMultiCast(int play_id, int user_id, String name, ServerLogic sl){
        try {
            String insertSql = "INSERT INTO playlist(play_id, user_id, name)"
            + " VALUES('"+play_id+"', '"+user_id+"', '"+name+"')";
            stmt.executeUpdate(insertSql);
        } catch (SQLException ex) {
            sl.Obj().put("exception", "[ERROR] InsertUser Multicast -> " + ex.getMessage());
            sl.notifyObserver(4);
            return false;
        }
        return true;
    }
    
    public boolean insertPlaylistMusicsMultiCast(int id, int music_id, int play_id, ServerLogic sl){
        try {
            String insertSql = "INSERT INTO playlistmusic(id, music_id, play_id)"
            + " VALUES('"+id+"', '"+music_id+"', '"+play_id+"')";
            stmt.executeUpdate(insertSql);
        } catch (SQLException ex) {
            sl.Obj().put("exception", "[ERROR] InsertUser Multicast -> " + ex.getMessage());
            sl.notifyObserver(4);
            return false;
        }
        return true;
    }
    
    public boolean contaisPlaylistMultiCast(int user_id, String name, ServerLogic sl){
        try {
            String selectSql = ("SELECT name FROM `playlist` WHERE user_id = \"" + user_id + "\"");
            ResultSet resultSet = stmt.executeQuery(selectSql);
            String procu;
            while(resultSet.next()){
                procu = resultSet.getString("name");
                if(procu.equals(name)){
                    return true;
                }
            }
        } catch (SQLException ex) {
            sl.Obj().put("exception", "[ERROR] ContainUser Multicast -> " + ex.getMessage());
            sl.notifyObserver(4);
           return false;
        }
        return false;
    }
    
    public boolean contaisPlaylistMusicsMultiCast(int music_id, int play_id, ServerLogic sl){
        try {
            String selectSql = ("SELECT play_id FROM `playlistmusic` WHERE music_id = \"" + music_id + "\"");
            ResultSet resultSet = stmt.executeQuery(selectSql);
            int procu;
            while(resultSet.next()){
                procu = resultSet.getInt("play_id");
                if(procu == play_id){
                    return true;
                }
            }
        } catch (SQLException ex) {
            sl.Obj().put("exception", "[ERROR] ContainUser Multicast -> " + ex.getMessage());
            sl.notifyObserver(4);
           return false;
        }
        return false;
    }
    
    public boolean removeuserMultiCast(String username, String password, ServerLogic sl){
        try {
            String deleteSql = "DELETE FROM `users` WHERE `users`.`username` = \"" + username + "\"";
            stmt.executeUpdate(deleteSql);
        } catch (SQLException ex) {
            sl.Obj().put("exception", "[ERROR] Logout Multicast -> " + ex.getMessage());
            sl.notifyObserver(4);
            return false;
        }
        return true;
    }
    
    public boolean contaisNameMultiCast(String name, ServerLogic sl){
        try {
            String selectSql = ("SELECT name FROM `users` WHERE name = \"" + name + "\"");
            ResultSet resultSet = stmt.executeQuery(selectSql);
            String procu;
            while(resultSet.next()){
                procu = resultSet.getString("name");
                if(procu.equals(name)){
                    return true;
                }
            }
        } catch (SQLException ex) {
            sl.Obj().put("exception", "[ERROR] ContainsName Multicast -> " + ex.getMessage());
            sl.notifyObserver(4);
           return false;
        }
        return false;
    }
    
    public boolean contaisUserMultiCast(String username, ServerLogic sl){
        try {
            String selectSql = ("SELECT username FROM `users` WHERE username = \"" + username + "\"");
            ResultSet resultSet = stmt.executeQuery(selectSql);
            String procu;
            while(resultSet.next()){
                procu = resultSet.getString("username");
                if(procu.equals(username)){
                    return true;
                }
            }
        } catch (SQLException ex) {
            sl.Obj().put("exception", "[ERROR] ContainsUser Multicast -> " + ex.getMessage());
            sl.notifyObserver(4);
           return false;
        }
        return false;
    }
    
    public boolean verifyUserPasswordMultiCast(String username, String Password, ServerLogic sl){
        try {
            String selectSql = ("SELECT password FROM `users` WHERE username = \"" + username + "\"");
            ResultSet resultSet = stmt.executeQuery(selectSql);
            String procu;
            while(resultSet.next()){
                procu = resultSet.getString("password");
                if(procu.equals(Password.trim())){
                    return true;
                }
            }
        } catch (SQLException ex) {
            sl.Obj().put("exception", "[ERROR] VerifyUserPassword Multicast -> " + ex.getMessage());
            sl.notifyObserver(4);
           return false;
        }
        return false;
    }
    
    public boolean insertMulticastMusic(String name,String artist,String album, String year, double duration, String genre, String localname, ServerLogic sl){
        //Comando sql para ir buscar todas as msicas
        try {
            String sql = "SELECT * FROM musics WHERE name = '" + name + "' AND artist = '" + artist + "'";
            ResultSet resultSet = stmt.executeQuery(sql);

            while (resultSet.next()){

                String nome = resultSet.getString("name");
                return false;

            }

            String insertSql = "INSERT INTO musics(name, artist, album, year, duration, genre)"
                    + " VALUES('"+name+"', '"+artist+"', '"+album+"', '"+year+"', '"+duration+"', '"+genre+"')";
            stmt.executeUpdate(insertSql);

            sql = "SELECT * FROM musics WHERE name = '" + name + "' AND artist = '" + artist + "'";
            resultSet = stmt.executeQuery(sql);

            while (resultSet.next()){

                int id = resultSet.getInt("music_id");
                String path = id +".mp3";
                String updateSql = "UPDATE musics"
                        + " SET localname = '"+ path + "'"
                        + " WHERE music_id = "+ id;
                stmt.executeUpdate(updateSql);
                return true;

            }

        } catch (SQLException e) {
             sl.Obj().put("exception", "[ERROR] InsertMulticastMusic -> " + e.getMessage());
             sl.notifyObserver(4);
             return true;
        }

        return false;
    }
    
    public boolean insertmusic(int music_id, String name,String artist,String album, String year, double duration, String genre, String localname, ServerLogic sl){
        try {
            String insertSql = "INSERT INTO musics(music_id, name, artist, album, year, duration, genre, localname)"
            + " VALUES('"+music_id+"', '"+name+"', '"+artist+"', '"+album+"', '"+year+"', '"+duration+"', '"+genre+"', '"+localname+"')";
            stmt.executeUpdate(insertSql);
        } catch (SQLException ex) {
            sl.Obj().put("exception", "[ERROR] InsertMusicPrivate -> " + ex.getMessage());
            sl.notifyObserver(4);
            return false;
        }
        return true;
    }
    
    public boolean contaismusic(String name,String artist,String album, ServerLogic sl){
        try {
            String selectSql = ("SELECT * FROM `musics`");
            ResultSet resultSet = stmt.executeQuery(selectSql);
            String namem;
            String artistm;
            String albumm;
            while(resultSet.next()){
                namem = resultSet.getString("name");
                artistm = resultSet.getString("artist");
                albumm = resultSet.getString("album");
                if(namem.equals(name) && artistm.equals(artist) && albumm.equals(album)){
                    return true;
                }
            }
        } catch (SQLException ex) {
            sl.Obj().put("exception", "[ERROR] InsertMusicPrivate -> " + ex.getMessage());
            sl.notifyObserver(4);
            return false;
        }
        return false;
    }
    
    public boolean runinsertedcode(String insert){
        
        try{
            stmt.executeUpdate(insert);
        } catch (SQLException ex){
            return false;
        }
        return true;
    }
    
    public int getIntegerfromdb(String proc){
        try {
            String selectSql = ("SELECT * FROM " + namedb);
            ResultSet resultSet = stmt.executeQuery(selectSql);
            while(resultSet.next()){
                //Retrieve by column name
                int procu  = resultSet.getInt(proc);
                return procu;
            }
        } catch (SQLException ex) {
           return -1;
        }
        return -1;
    }
    
    public String getStringfromdb(String proc){ 
        try {
            String selectSql = ("SELECT * FROM " + namedb);
            ResultSet resultSet = stmt.executeQuery(selectSql);          
            while(resultSet.next()){
                //Retrieve by column name
                String procu = resultSet.getString(proc);
                return procu;
            }
        } catch (SQLException ex) {
           return "-1";
        }
        return "-1";
    }
    
    public boolean contaisName(String name, ServerLogic sl){ 
        try {
            String selectSql = ("SELECT name FROM `users` WHERE name = \"" + name + "\"");
            ResultSet resultSet = stmt.executeQuery(selectSql);
            String procu;
            while(resultSet.next()){
                procu = resultSet.getString("name");
                if(procu.equals(name)){
                    return true;
                }
            }
        } catch (SQLException ex) {
            sl.Obj().put("exception", "[ERROR] ContainsName -> " + ex.getMessage());
            sl.notifyObserver(4);
           return false;
        }
        return false;
    }
    
    public boolean contaisUser(String username, ServerLogic sl){ 
        try {
            String selectSql = ("SELECT username FROM `users` WHERE username = \"" + username + "\"");
            ResultSet resultSet = stmt.executeQuery(selectSql);
            String procu;
            while(resultSet.next()){
                procu = resultSet.getString("username");
                if(procu.equals(username)){
                    return true;
                }
            }
        } catch (SQLException ex) {
            sl.Obj().put("exception", "[ERROR] ContainsUser -> " + ex.getMessage());
            sl.notifyObserver(4);
           return false;
        }
        return false;
    }
    
    public boolean verifyUserPassword(String username, String Password, ServerLogic sl){ 
        try {
            String selectSql = ("SELECT password FROM `users` WHERE username = \"" + username + "\"");
            ResultSet resultSet = stmt.executeQuery(selectSql);
            String procu;
            while(resultSet.next()){
                procu = resultSet.getString("password");
                if(procu.equals(Password)){
                    return true;
                }
            }
        } catch (SQLException ex) {
            sl.Obj().put("exception", "[ERROR] VerifyUserPassword -> " + ex.getMessage());
            sl.notifyObserver(4);
           return false;
        }
        return false;
    }

    public boolean verifyUserPassword2(String username, String Password){
        try {
            String selectSql = ("SELECT password FROM `users` WHERE username = \"" + username + "\"");
            ResultSet resultSet = stmt.executeQuery(selectSql);
            String procu;
            while(resultSet.next()){
                procu = resultSet.getString("password");
                if(procu.equals(Password)){
                    return true;
                }
            }
        } catch (SQLException ex) {
            return false;
        }
        return false;
    }

    public ResultSet getRawDatabaseinfo(){
        try {
            String selectSql = ("SELECT * FROM " + namedb);
            return stmt.executeQuery(selectSql);          
        } catch (SQLException ex) {
           return null;
        }
    }
    
    public boolean updatedatabase(JSONObject JObjrecebido){
        
        // TODO - vai ser agressivo
        
        return false;
    }
    
    
    private String numbtoletter(){
        //tradutor de numero para uma string, para criar o nome da base de dados com o porto
        int num, temp, digit, count = 0;
        num = this.serverport;
        char[] array = { 'a', 'b', 'c', 'd', 'e', 'f' , 'g' , 'h' , 'i' , 'j' , 'k'};
        String output = "";
        
        temp = num;
        
        while(num > 0)
        {
            num = num / 10;
            count++;
        }
        while(temp > 0)
        {
            digit = temp % 10;
            output = output + array[digit];
            temp = temp / 10;
            count--;
        }
        
        //this.serverport;
        return output;
     
    }
   
    public String insertMusic(String name,String artist,String album, String year, double duration, String genre, ServerLogic sl){

        //Comando sql para ir buscar todas as msicas
        try {
            String sql = "SELECT * FROM musics WHERE name = '" + name + "' AND artist = '" + artist + "'";
            ResultSet resultSet = stmt.executeQuery(sql);

            while (resultSet.next()){

                String nome = resultSet.getString("name");
                return null;

            }

            String insertSql = "INSERT INTO musics(name, artist, album, year, duration, genre)"
                    + " VALUES('"+name+"', '"+artist+"', '"+album+"', '"+year+"', '"+duration+"', '"+genre+"')";
            stmt.executeUpdate(insertSql);

            sql = "SELECT * FROM musics WHERE name = '" + name + "' AND artist = '" + artist + "'";
            resultSet = stmt.executeQuery(sql);

            while (resultSet.next()){

                int id = resultSet.getInt("music_id");
                String path = id +".mp3";
                String updateSql = "UPDATE musics"
                        + " SET localname = '"+ path + "'"
                        + " WHERE music_id = "+ id;
                stmt.executeUpdate(updateSql);
                return path;

            }

        } catch (SQLException e) {
             sl.Obj().put("exception", "[ERROR] InsertMusic -> " + e.getMessage());
             sl.notifyObserver(4);
             return null;
        }

        return null;

    }

    public String getFileName(String name,String artist, ServerLogic sl){

        //Comando sql para ir buscar todas as msicas
        try {
            String sql = "SELECT * FROM musics WHERE name = '" + name + "' AND artist = '" + artist + "'";
            ResultSet resultSet = stmt.executeQuery(sql);

            while (resultSet.next()){

                String nome = resultSet.getString("localname");
                return nome;

            }

        } catch (SQLException e) {
            sl.Obj().put("exception", "[ERROR] InsertMusic -> " + e.getMessage());
            sl.notifyObserver(4);
            return null;
        }

        return null;

    }

    public String getFileName2(String name,String artist){

        //Comando sql para ir buscar todas as msicas
        try {
            String sql = "SELECT * FROM musics WHERE name = '" + name + "' AND artist = '" + artist + "'";
            ResultSet resultSet = stmt.executeQuery(sql);

            while (resultSet.next()){

                String nome = resultSet.getString("localname");
                return nome;

            }

        } catch (SQLException e) {
            return null;
        }

        return null;

    }

    public boolean removeMusic(String name,String artist, ServerLogic sl){

        String filename = getFileName(name, artist, sl);
        if (filename != null){
            File file = new File(filename);

            if(file.delete())
            {
                System.out.println("File deleted successfully");
            }

        }

        try {

            int id = getMusicId(name,artist,sl);
            if (id != -1){
                String deleteSql = "DELETE FROM playlistmusic WHERE music_id = '" + id + "'";
                stmt.executeUpdate(deleteSql);
            }

            String deleteSql = "DELETE FROM musics WHERE name = '" + name +"' And artist = '" + artist + "'";
            stmt.executeUpdate(deleteSql);
        } catch (SQLException ex) {
            sl.Obj().put("exception", "[ERROR] Logout -> " + ex.getMessage());
            sl.notifyObserver(4);
            return false;
        }
        return true;

    }

    public boolean changeMusic(String name,String artist, String newName,
                               String newArtist,String album, String year,
                               double duration, String genre, ServerLogic sl){

        try {
            String deleteSql = "UPDATE musics" +
                    " SET name = '" + newName + "', artist = '" + newArtist + "', album = '" + album
                    + "', year = '" + year + "', duration = '" + duration +"', genre = '" + genre +"'" +
                    " WHERE name = '" + name +"' AND artist = '" + artist + "'";
            stmt.executeUpdate(deleteSql);
        } catch (SQLException ex) {
            sl.Obj().put("exception", "[ERROR] ChangeMusic -> " + ex.getMessage());
            sl.notifyObserver(4);
            return false;
        }
        return true;

    }

    public JSONObject listMusics(){

        JSONObject Jobj = new JSONObject();
        Jobj.put("message", "musicsList");
        try {

            String selectSql = "SELECT * FROM musics" ;
            ResultSet resultSet = stmt.executeQuery(selectSql);
            int i = 0;
            while(resultSet.next()){

                i++;
                String nome = "music" + i;
                JSONArray array = new JSONArray();
                array.add(resultSet.getString("name"));
                array.add(resultSet.getString("artist"));
                array.add(resultSet.getString("album"));
                array.add(resultSet.getString("year"));
                array.add(resultSet.getDouble("duration"));
                array.add(resultSet.getString("genre"));
                Jobj.put(nome, array);

            }

            Jobj.put("numberOfMusics", i);

        } catch (SQLException ex) {
            return null;
        }

        return Jobj;

    }
    
    public JSONObject listMusics(String search){
            
        boolean filt = false;
        String[] parts = null;
        if(search.contains(":")){
            parts = search.split(" : ");
            filt = true;
            if(parts.length < 3){
                return null;
            }
        }
            
        JSONObject Jobj = new JSONObject();
        Jobj.put("message", "musicsList");
        try {
            
            String selectSql = "";
            if(filt == false){
                selectSql = "SELECT * FROM musics WHERE name = '" + search + "'" ;
            }else{
                if(parts[1].equals("artist") || parts[1].equals("album") || parts[1].equals("year")  || parts[1].equals("duration")  || parts[1].equals("genre")){
                    selectSql = "SELECT * FROM musics WHERE name = '" + parts[0] + "' AND " +parts[1] + " = '" + parts[2] + "'";
                }else{
                    return null;
                }

            }
            
            ResultSet resultSet = stmt.executeQuery(selectSql);
            int i = 0;
            while(resultSet.next()){

                i++;
                String nome = "music" + i;
                JSONArray array = new JSONArray();
                array.add(resultSet.getString("name"));
                array.add(resultSet.getString("artist"));
                array.add(resultSet.getString("album"));
                array.add(resultSet.getString("year"));
                array.add(resultSet.getDouble("duration"));
                array.add(resultSet.getString("genre"));
                Jobj.put(nome, array);

            }

            Jobj.put("numberOfMusics", i);

        } catch (SQLException ex) {
            return null;
        } catch (Exception e){
            return null;
        }

        return Jobj;

    }

    public int getMusicId(String nome, String autor, ServerLogic sl){

        int id = -1;

        try {

            String sql = "SELECT * FROM musics WHERE name = '" + nome + "' AND artist = '" + autor + "'";
            ResultSet resultSet = stmt.executeQuery(sql);

            while (resultSet.next()){

                id = resultSet.getInt("music_id");
                return id;

            }

        } catch (SQLException e) {
            return -1;
        }

        return id;

    }

    public boolean createPlaylist(String nome, String username, ArrayList<String> musics,
                                  ArrayList<String> autores, ServerLogic sl){

        int id = 0;
        id = getUserID(username);
        if (id == -1)
            return false;

        String sql = null;
        ResultSet resultSet = null;

        try {

            sql = "SELECT * FROM playlist WHERE name = '" + nome + "' AND user_id = '" + id +"'";
            resultSet = stmt.executeQuery(sql);

            //Se existir retorna falso
            while (resultSet.next()){
                return false;
            }

            String insertSql = "INSERT INTO playlist(user_id, name)"
                    + " VALUES('"+id+"', '"+nome+"')";
            int resultSet2 = stmt.executeUpdate(insertSql);

            String sql2 = "SELECT * FROM playlist WHERE name = '" + nome + "' AND user_id = '" + id +"'";
            resultSet = stmt.executeQuery(sql2);

            int playId = 0;
            while (resultSet.next()){
                playId = resultSet.getInt("play_id");
            }

            for (int i = 0; i < musics.size(); i++) {

                int musicId = getMusicId(musics.get(i), autores.get(i), sl);

                if (musicId != -1){

                    sql = "INSERT INTO playlistmusic(music_id, play_id) " +
                            "VALUES('" + musicId +"','" + playId + "')";
                    stmt.executeUpdate(sql);

                }

            }

        } catch (SQLException e) {
            return false;
        }

        return true;

    }

    public boolean changePlaylist(String oldname, String nome, String username,ServerLogic sl){

        int id = 0;
        id = getUserID(username);
        if (id == -1)
            return false;

        try {
            String sql = "UPDATE playlist " +
                    "SET name = '" + nome + "'" +
                    "WHERE user_id = '" + id +"' AND name = '" + oldname + "'";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            return false;
        }

        return true;

    }

    public boolean removePlaylist(String nome, String username, ServerLogic sl){

        int id = 0;
        id = getUserID(username);
        if (id == -1)
            return false;

        ResultSet resultSet = null;
        int playlistId = 0;
        try{

            String sqlSelect = "SELECT * FROM playlist WHERE name = '" + nome + "' AND user_id = '" + id + "'";
            resultSet = stmt.executeQuery(sqlSelect);

            int i = 0;
            while (resultSet.next()){
                i++;
                playlistId = resultSet.getInt("play_id");
                break;
            }

            if (i == 0)
                return false;

        } catch (SQLException e) {
            return false;
        }

        try{

            String deleteSql = "DELETE FROM playlist WHERE name = '" + nome +"' And user_id = '" + id + "'";
            stmt.executeUpdate(deleteSql);

            deleteSql = "DELETE FROM playlistmusic WHERE play_id = '" + playlistId + "'";
            stmt.executeUpdate(deleteSql);

        }catch (SQLException e) {
            return false;
        }

        return true;

    }

    public int getUserID(String username){

        try {

            String selectSql = "SELECT * FROM users WHERE username = '" + username + "'";
            ResultSet resultSet = stmt.executeQuery(selectSql);

            while (resultSet.next()){

                int id = resultSet.getInt("user_id");
                return id;

            }

        } catch (SQLException e) {
            return -1;
        }

        return -1;

    }

    public JSONObject listPlaylist(String username, ServerLogic sl){

        JSONObject obj = new JSONObject();
        int id = 0;
        String selectSql = null;
        ResultSet resultSet = null;

        id = getUserID(username);
        if (id == -1)
            return null;

        try {

            selectSql = "SELECT * FROM playlist WHERE user_id = '" + id +"'";
            resultSet = stmt.executeQuery(selectSql);
            int i = 0;
            while(resultSet.next()){

                i++;
                String nome = "playlist" + (i - 1);
                obj.put(nome, resultSet.getString("name"));

            }

            obj.put("numberOfPlaylists", i);

        } catch (SQLException ex) {
            return null;
        }
        return obj;

    }
    
    public JSONObject listPlaylist(String username, ServerLogic sl, String search){
            
        boolean filt = false;
        String[] parts = null;
        if(search.contains(":")){
            parts = search.split(" : ");
            filt = true;
            if(parts.length < 3){
                return null;
            }
        }
            
        JSONObject obj = new JSONObject();
        int id = 0;
        String selectSql = null;
        ResultSet resultSet = null;

        id = getUserID(username);
        if (id == -1)
            return null;

        try {

            if(filt == false){
                selectSql = "SELECT name FROM playlist WHERE user_id = '" + id +"'" + " AND name = '" + search + "'";
            }else{
                if(parts[1].equals("artist") || parts[1].equals("name") || parts[1].equals("album") || parts[1].equals("year")){
                    //SELECT playlist.name FROM playlist,playlistmusic,musics WHERE user_id = '1' AND playlist.name = 'playcorre' AND playlistmusic.music_id = musics.music_id AND playlistmusic.play_id = playlist.play_id AND musics.artist = 'me'
                    selectSql = "SELECT playlist.name FROM playlist,playlistmusic,musics WHERE user_id = '" + id + "'" 
                            + " AND playlist.name = '" + parts[0] + "'" + " AND playlistmusic.music_id = musics.music_id" 
                            + " AND playlistmusic.play_id = playlist.play_id" + " AND musics." + parts[1] +" = '"+ parts[2] +"'";
                }else{
                    return null;
                }

            }
            resultSet = stmt.executeQuery(selectSql);
            int i = 0;
            while(resultSet.next()){

                i++;
                String nome = "playlist" + (i - 1);
                obj.put(nome, resultSet.getString("name"));

            }

            obj.put("numberOfPlaylists", i);

        } catch (SQLException ex) {
            return null;
        }catch (Exception e){
            return null;
        }

        return obj;

    }

    public int getPlaylistID(String nome, int id){

        try{

            String sql = "SELECT * FROM playlist WHERE name = '" + nome + "' AND user_id = '" + id +"'";
            ResultSet resultSet = stmt.executeQuery(sql);

            //Se existir retorna falso
            while (resultSet.next()){
                int playid = resultSet.getInt("play_id");
                return playid;
            }

        } catch (SQLException e) {
            return -1;
        }

        return -1;

    }

    public ArrayList<String> playPlaylist(String username, String nome,ServerLogic sl){

        int id = 0;
        id = getUserID(username);
        if (id == -1)
            return null;

        int playid = 0;
        playid = getPlaylistID(nome, id);
        if (playid == -1)
            return null;

        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> aux = new ArrayList<>();

        try{

            String sql = "SELECT * FROM playlistmusic WHERE play_id = '" + playid + "'";
            ResultSet resultSet = stmt.executeQuery(sql);

            while (resultSet.next()){

                int musicId = resultSet.getInt("music_id");
                aux.add(String.valueOf(musicId));

            }

            for(String path : aux){
                String ppp = getMusicPath(Integer.parseInt(path));
                list.add(ppp);
            }

        } catch (SQLException e) {
            sl.Obj().put("exception", "[ERROR] playPlaylist -> " + e.getMessage());
            sl.notifyObserver(4);
            return null;
        }

        return list;

    }

    public String getMusicPath(int musicId) {

        try {

            String sql = "SELECT * FROM musics WHERE music_id = '" + musicId + "'";
            ResultSet resultSet = stmt.executeQuery(sql);

            while (resultSet.next()){

                String path = resultSet.getString("localname");
                return path;

            }

        } catch (SQLException e) {
            return null;
        }

        return null;

    }

}
