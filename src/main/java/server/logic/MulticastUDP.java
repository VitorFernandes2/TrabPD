package server.logic;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.ServerLogic;

/**
 *
 * @author Joao Coelho
 */
public class MulticastUDP {

    private static MulticastSocket multicastSock;
    private static InetAddress group;
    private static ServerLogic ci;
    private static int port; // TEMPPP
    private static int uses;
    private static boolean corre;

    public MulticastUDP(ServerLogic ci) {
        this.ci = ci;
        this.port = ci.getSd().getServerPort(); // TEMPPPP
    }
    
    //public static void main(String[] args) throws UnknownHostException, IOException {
    public void comecamulticast() {
        try {
            
            group = InetAddress.getByName("225.4.5.6");
            uses = 3456;
            multicastSock = new MulticastSocket(uses);
            multicastSock.joinGroup(group);
            corre = true;
            
            new Thread(Receb).start();
            new Thread(Env).start();
            
            ci.Obj().put("output", "\nMulticastThreads iniciadas.\n");
            ci.notifyObserver(1);
            
        } catch (UnknownHostException ex) {
            ci.Obj().put("exception", "[ERROR] Lancamento de Thread Multicast [1] (UnknownHost) -> " + ex.getMessage());
            ci.notifyObserver(4);
        } catch (IOException ex) {
            ci.Obj().put("exception", "[ERROR] Lancamento de Thread Multicast [2] (IO) -> " + ex.getMessage());
            ci.notifyObserver(4);
        }
        
    }
    
    public void turnOff() {
        MulticastUDP.corre = false;
    }

    public MulticastSocket getMulticastSock() {
        return multicastSock;
    }

    public InetAddress getGroup() {
        return group;
    }

    public int getUses() {
        return uses;
    }

    private static Runnable Receb = new Runnable() {
        public void run() {
            try{
                while(corre){
                    
                    byte [] buffer = new byte [2046];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    multicastSock.receive(packet);
                    
                    //Codigo para a atualizacao da base de dados, a partir do comando enviado
                    
                    String cmd = new String(packet.getData(), 0, packet.getLength());
                    
                    ci.Obj().put("output", "Recebe: " + cmd.trim());
                    ci.notifyObserver(1);
                    
                    //Tratamento de copia de musics para as playlists para a nova base de dados
                    if(cmd.contains("createcopyPlayListMusics")){
                        
                        JSONParser JsonParser = new JSONParser();
                        JSONObject JObj = (JSONObject) JsonParser.parse(cmd.trim());
                        int Id = Integer.parseInt((String) JObj.get("Id"));
                        int PlayId = Integer.parseInt((String) JObj.get("PlayId"));
                        int MusicId = Integer.parseInt((String) JObj.get("MusicId"));
                        
                        if(!ci.getDbaction().contaisPlaylistMusicsMultiCast(MusicId, PlayId, ci)){
                            ci.getDbaction().insertPlaylistMusicsMultiCast(Id, MusicId, PlayId, ci);
                        }
                        
                    }
                    //Tratamento de copia de playlists para a nova base de dados
                    else if(cmd.contains("createcopyPlayLists")){
                        
                        JSONParser JsonParser = new JSONParser();
                        JSONObject JObj = (JSONObject) JsonParser.parse(cmd.trim());
                        int PlayId = Integer.parseInt((String) JObj.get("PlayId"));
                        int UserId = Integer.parseInt((String) JObj.get("UserId"));
                        String Name = (String) JObj.get("Name");
                        
                        if(!ci.getDbaction().contaisPlaylistMultiCast(UserId, Name, ci)){
                            ci.getDbaction().insertPlaylistMultiCast(PlayId, UserId, Name, ci);
                        }
                        
                    }
                    //Tratamento de copia de users para a nova base de dados
                    else if(cmd.contains("createcopyUser")){
                        
                        JSONParser JsonParser = new JSONParser();
                        JSONObject JObj = (JSONObject) JsonParser.parse(cmd.trim());
                        int UserId = Integer.parseInt((String) JObj.get("UserId"));
                        String UserName = (String) JObj.get("UserName");
                        String UserUserName = (String) JObj.get("UserUserName");
                        String UserPassword = (String) JObj.get("UserPassword");
                        
                        if(!ci.getDbaction().contaisUserMultiCast(UserName, ci)){
                            ci.getDbaction().insertuserMultiCast(UserId, UserName, UserUserName, UserPassword, ci);
                        }
                        
                    }
                    //Tratamento de copia de msicas para a nova base de dados
                    else if(cmd.contains("createcopyMusic")){
                        
                        JSONParser JsonParser = new JSONParser();
                        JSONObject JObj = (JSONObject) JsonParser.parse(cmd.trim());
                        int MusicId = Integer.parseInt((String) JObj.get("MusicId"));
                        String MusicName = (String) JObj.get("MusicName");
                        String MusicAuthor = (String) JObj.get("MusicAuthor");
                        String MusicYear = (String) JObj.get("MusicYear");
                        String MusicAlbum = (String) JObj.get("MusicAlbum");
                        double MusicDuration = (double) JObj.get("MusicDuration");
                        String MusicGenre = (String) JObj.get("MusicGenre");
                        String MusicPath = (String) JObj.get("MusicPath");
                        
                        if(!ci.getDbaction().contaismusic(MusicName, MusicAuthor, MusicAlbum, ci)){
                            ci.getDbaction().insertmusic(MusicId, MusicName, MusicAuthor, MusicAlbum, MusicYear, MusicDuration, MusicGenre, MusicPath, ci);
                        }
                        
                    }
                    else if(cmd.contains("createMusic")){
                        
                        JSONParser JsonParser = new JSONParser();
                        JSONObject JObj = (JSONObject) JsonParser.parse(cmd.trim());
                        String MusicName = (String) JObj.get("MusicName");
                        String MusicAuthor = (String) JObj.get("MusicAuthor");
                        String MusicYear = (String) JObj.get("MusicYear");
                        String MusicAlbum = (String) JObj.get("MusicAlbum");
                        double MusicDuration = (double) JObj.get("MusicDuration");
                        String MusicGenre = (String) JObj.get("MusicGenre");
                        String MusicPath = (String) JObj.get("MusicPath");
                        
                        if(!ci.getDbaction().contaismusic(MusicName, MusicAuthor, MusicAlbum, ci)){
                            ci.getDbaction().insertMulticastMusic(MusicName, MusicAuthor, MusicAlbum, MusicYear, MusicDuration, MusicGenre, MusicPath, ci);
                        }
                        
                    }
                    else if (cmd.contains("changeMusic")){

                        JSONParser JsonParser = new JSONParser();
                        JSONObject JObj = (JSONObject) JsonParser.parse(cmd);

                        String name = (String)JObj.get("name");
                        String author = (String)JObj.get("author");

                        String MusicName = (String) JObj.get("MusicName");
                        String MusicAuthor = (String) JObj.get("MusicAuthor");
                        String MusicYear = (String) JObj.get("MusicYear");
                        String MusicAlbum = (String) JObj.get("MusicAlbum");
                        double MusicDuration = (double) JObj.get("MusicDuration");
                        String MusicGenre = (String) JObj.get("MusicGenre");

                        ci.getDbaction().changeMusic(name, author, MusicName, MusicAuthor,
                                MusicAlbum, MusicYear, MusicDuration, MusicGenre, ci);

                    }
                    else if (cmd.contains("removeMusic")){

                        JSONParser JsonParser = new JSONParser();
                        JSONObject JObj = (JSONObject) JsonParser.parse(cmd);

                        String name = (String)JObj.get("name");
                        String author = (String)JObj.get("author");

                        ci.getDbaction().removeMusic(name, author, ci);

                    }
                    else if (cmd.contains("changePlaylist")){

                        JSONParser JsonParser = new JSONParser();
                        JSONObject JObj = (JSONObject) JsonParser.parse(cmd);

                        String username = (String)JObj.get("username");
                        String oldName = (String)JObj.get("name");
                        String newName = (String)JObj.get("newName");
                        
                        ci.getDbaction().changePlaylist(oldName,newName,username, ci);

                    }
                    else if (cmd.contains("removePlaylist")){

                        JSONParser JsonParser = new JSONParser();
                        JSONObject JObj = (JSONObject) JsonParser.parse(cmd);

                        String username = (String)JObj.get("username");
                        String name = (String)JObj.get("name");
                        ci.getDbaction().removePlaylist(name,username,ci);

                    }
                    else if (cmd.contains("createPlaylist")){

                        JSONParser JsonParser = new JSONParser();
                        JSONObject JObj = (JSONObject) JsonParser.parse(cmd);

                        String username = (String)JObj.get("username");
                        String name = (String)JObj.get("name");
                        long size = (long)JObj.get("numberOfMusics");

                        ArrayList<String> musics = new ArrayList<>();
                        ArrayList<String> autors = new ArrayList<>();
                        for (long i = 0; i < size; i++) {

                            String MusicName = "nomeMusica" + i;
                            String autor = "autorMusica" + i;

                            String MusicNameInput = (String)JObj.get(MusicName);
                            String MusicAutorInput = (String)JObj.get(autor);

                            musics.add(MusicNameInput);
                            autors.add(MusicAutorInput);

                        }
                        
                        ci.getDbaction().createPlaylist(name, username, musics, autors, ci);

                    }
                    //Tratamento de login/registo/logout
                    else{
                        String result = commandParse(cmd);
                        if(!result.contains("Sem Tipo definido [2] !\n")){
                            System.out.println("\nRecebi: " + cmd + "\nTentativa de execucao de comando: " + result);
                        }
                    }
                    
                    //Cdigo para copia total das necessidades dos dados de cada servidor criado
                    if(cmd.trim().equals("copiar") && !ci.getDbaction().getNamedb().equals("Principal")){
                        //Cdigo de Cpia da base de dados original
                        try {
                            String PrincipalString = "jdbc:mysql://localhost:3306/Principal?useTimezone=true&serverTimezone=UTC&useSSL=false";
                            Connection connectPrincipal = DriverManager.getConnection(PrincipalString,ci.getDbaction().getUser(),ci.getDbaction().getPass());
                            Statement stmt2 = connectPrincipal.createStatement();
                            
                            try {
                                String selectSql = ("SELECT * FROM `users`");
                                ResultSet resultSet = stmt2.executeQuery(selectSql);
                                String user_id;
                                String name;
                                String username;
                                String password;
                                while(resultSet.next()){
                                    user_id = String.valueOf(resultSet.getInt("user_id"));
                                    name = resultSet.getString("name");
                                    username = resultSet.getString("username");
                                    password = resultSet.getString("password");
                                    JSONObject jo = new JSONObject();
                                    jo.put("Command", "createcopyUser");
                                    jo.put("UserId", user_id);
                                    jo.put("UserName", name.trim());
                                    jo.put("UserUserName", username.trim());
                                    jo.put("UserPassword", password.trim());
                                    byte[] b = String.valueOf(jo.toString()).getBytes();
                                    DatagramPacket packetUser = new DatagramPacket(b, b.length, group, 3456);
                                    multicastSock.send(packetUser);
                                }
                            } catch (SQLException ex) {
                                ci.Obj().put("exception", "[ERROR] Copia da Users para outras bases de dados -> " + ex.getMessage());
                                ci.notifyObserver(4);
                                break;
                            }
                            //Cdigo para a copia das msicas
                            try {
                                String selectSql = ("SELECT * FROM `musics`");
                                ResultSet resultSet = stmt2.executeQuery(selectSql);
                                String music_id;
                                String name;
                                String artist;
                                String album;
                                String year;
                                double duration;
                                String genre;
                                String localname;
                                while(resultSet.next()){
                                    music_id = String.valueOf(resultSet.getInt("music_id"));
                                    name = resultSet.getString("name");
                                    artist = resultSet.getString("artist");
                                    album = resultSet.getString("album");
                                    year = resultSet.getString("year");
                                    duration = resultSet.getDouble("duration");
                                    genre = resultSet.getString("genre");
                                    localname = resultSet.getString("localname");
                                    JSONObject jo = new JSONObject();
                                    jo.put("Command", "createcopyMusic");
                                    jo.put("MusicId", music_id);
                                    jo.put("MusicName", name.trim());
                                    jo.put("MusicAuthor", artist.trim());
                                    jo.put("MusicYear", year.trim());
                                    jo.put("MusicAlbum", album.trim());
                                    jo.put("MusicDuration", duration);
                                    jo.put("MusicGenre", genre.trim());
                                    jo.put("MusicPath", localname.trim());
                                    byte[] b = String.valueOf(jo.toString()).getBytes();
                                    DatagramPacket packetUser = new DatagramPacket(b, b.length, group, 3456);
                                    multicastSock.send(packetUser);
                                }
                            } catch (SQLException ex) {
                                ci.Obj().put("exception", "[ERROR] Copia da Musics -> " + ex.getMessage());
                                ci.notifyObserver(4);
                                break;
                            }
                            //Cdigo para copia das playlists
                            try {
                                String selectSql = ("SELECT * FROM `playlist`");
                                ResultSet resultSet = stmt2.executeQuery(selectSql);
                                String play_id;
                                String user_id;
                                String name;
                                while(resultSet.next()){
                                    play_id = String.valueOf(resultSet.getInt("play_id"));
                                    user_id = String.valueOf(resultSet.getInt("user_id"));
                                    name = resultSet.getString("name");
                                    JSONObject jo = new JSONObject();
                                    jo.put("Command", "createcopyPlayLists");
                                    jo.put("PlayId", play_id);
                                    jo.put("UserId", user_id);
                                    jo.put("Name", name);
                                    byte[] b = String.valueOf(jo.toString()).getBytes();
                                    DatagramPacket packetUser = new DatagramPacket(b, b.length, group, 3456);
                                    multicastSock.send(packetUser);
                                }
                            } catch (SQLException ex) {
                                ci.Obj().put("exception", "[ERROR] Copia da Playlist -> " + ex.getMessage());
                                ci.notifyObserver(4);
                                break;
                            }
                            //Cdigo para copia das msicas associadas a cada playlist
                            try {
                                String selectSql = ("SELECT * FROM `playlistmusic`");
                                ResultSet resultSet = stmt2.executeQuery(selectSql);
                                String play_id;
                                String music_id;
                                String id;
                                while(resultSet.next()){
                                    play_id = String.valueOf(resultSet.getInt("play_id"));
                                    music_id = String.valueOf(resultSet.getInt("music_id"));
                                    id = String.valueOf(resultSet.getInt("id"));
                                    JSONObject jo = new JSONObject();
                                    jo.put("Command", "createcopyPlayListMusics");
                                    jo.put("PlayId", play_id);
                                    jo.put("MusicId", music_id);
                                    jo.put("Id", id);
                                    byte[] b = String.valueOf(jo.toString()).getBytes();
                                    DatagramPacket packetUser = new DatagramPacket(b, b.length, group, 3456);
                                    multicastSock.send(packetUser);
                                }
                            } catch (SQLException ex) {
                                ci.Obj().put("exception", "[ERROR] Copia das Musicas associadas a cada Playlist -> " + ex.getMessage());
                                ci.notifyObserver(4);
                                break;
                            }
                            
                            connectPrincipal.close();
                            stmt2.close();

                        } catch (SQLException ex) {
                            ci.Obj().put("exception", "[ERROR] Copia da Base de Dados -> " + ex.getMessage());
                            ci.notifyObserver(4);
                            return;
                        }
                        //-----------------------------------------
                        
                        //Pedido de logins existentes  base de dados principal
                        byte[] l = String.valueOf("logins").getBytes();
                        DatagramPacket packetLogs = new DatagramPacket(l, l.length, group, 3456);
                        multicastSock.send(packetLogs);
                        //-----------------------------------------------------
                        
                    }
                    //Cdigo de copia de logins existentes na base de dados principal
                    else if(cmd.trim().equals("logins") && ci.getDbaction().getNamedb().equals("Principal")){
                        try {
                            String PrincipalString = "jdbc:mysql://localhost:3306/Principal?useTimezone=true&serverTimezone=UTC&useSSL=false";
                            Connection connectPrincipal = DriverManager.getConnection(PrincipalString,ci.getDbaction().getUser(),ci.getDbaction().getPass());
                            Statement stmt2 = connectPrincipal.createStatement();
                            try {
                                String selectSql = ("SELECT * FROM `users`");
                                ResultSet resultSet = stmt2.executeQuery(selectSql);
                                String username;
                                String password;
                                while(resultSet.next()){
                                    username = resultSet.getString("username");
                                    password = resultSet.getString("password");
                                    if(ci.getClientsLogs().contains(username.trim())){
                                        String userLogcopy = "tipo|login;username|" + username + ";password|" + password;
                                        byte[] c = String.valueOf(userLogcopy).getBytes();
                                        DatagramPacket packetUserLog = new DatagramPacket(c, c.length, group, 3456);
                                        multicastSock.send(packetUserLog);
                                    }
                                }
                            } catch (SQLException ex) {
                                ci.Obj().put("exception", "[ERROR] Copia da Logs para outras bases de dados -> " + ex.getMessage());
                                ci.notifyObserver(4);
                                break;
                            }
                            
                            connectPrincipal.close();
                            stmt2.close();
                            
                        } catch (SQLException ex) {
                            ci.Obj().put("exception", "[ERROR] Copia da Base de Dados do Logins -> " + ex.getMessage());
                            ci.notifyObserver(4);
                            return;
                        }
                        
                    }
                    
                    //-----------------------------------------------------------------------
                    
                }
            } catch (IOException ex) {
                ci.Obj().put("exception", "[ERROR] Package nao foi recebida em bom estado.\n" + ex.getMessage());
                ci.notifyObserver(4);
            } catch (ParseException ex) {
                ci.Obj().put("exception", "[ERROR] Parser nao foi corretamente executada.\n" + ex.getMessage());
                ci.notifyObserver(4);
            }
            ci.Obj().put("exception", "[ERROR] Servidor deixou de receber mensagens por multicast.");
            ci.notifyObserver(4);
        }
    };
    
    private static Runnable Env = new Runnable() { // TEMPPP
        public void run() {
            try{
                //Pedido de copia de base de dados principal por parte do servidor
                byte[] c = String.valueOf("copiar").getBytes();
                DatagramPacket packetUser = new DatagramPacket(c, c.length, group, 3456);
                multicastSock.send(packetUser);
                //----------------------------------------------------------------
                while(corre){
                    
                    TimeUnit.SECONDS.sleep(2);
                    int msg = port;
                    byte[] b = String.valueOf(msg).getBytes(); 
                    DatagramPacket packet = new DatagramPacket(b, b.length,group,3456);
                    multicastSock.send(packet);
                    //System.out.println("Enviei val: " + msg);
                    
                }
            } catch (IOException | InterruptedException e){
                ci.Obj().put("exception", "[ERROR] Servidor deixou de enviar mensagens por multicast." + e.getMessage());
                ci.notifyObserver(4);
            }
            
        }
        
    };
    
    private static String commandParse(String command){
        boolean hasTypeLog = false;
        boolean hasTypeReg = false;
        boolean hasTypeOut = false;
        boolean hasNameReg = false;
        boolean hasUserNameLog = false;
        boolean hasUserNameReg = false;
        boolean hasUserNameOut = false;
        boolean hasPasswordLog = false;
        boolean hasPasswordReg = false;
        boolean hasPasswordOut = false;
        String name = "";
        String username = "";
        String password = "";
        command = command.replace(" ", "");
        String [] zones = command.split(";");
        for(String zone : zones){
            String parte = zone.replace("|", " ");
            String [] cmd = parte.split(" ");
            //vai verificar se o comando  inicializado corretamente
            if(cmd[0].equalsIgnoreCase("tipo") && !hasTypeLog && !hasTypeReg && !hasTypeOut){
                //vai verificar se o tipo de comando  o correto
                if(cmd[1].equalsIgnoreCase("login")){
                    hasTypeLog = true;
                }
                else if(cmd[1].equalsIgnoreCase("registo")){
                    hasTypeReg = true;
                }
                else if(cmd[1].equalsIgnoreCase("logout")){
                    hasTypeOut = true;
                }
                else{
                    return "Sem tipo definido [1] !\n";
                }
            }
            else if(cmd[0].equalsIgnoreCase("name") && !hasNameReg){
                if(cmd[1] == null || cmd[1].length() == 0){
                    return "Sem Name defenido!\n";
                }
                //vai verificar se o nome do utilizador existe na base de dados
                name = cmd[1];
                if(hasTypeReg){
                    hasUserNameReg = !ci.getDbaction().contaisNameMultiCast(name, ci);
                }
                else{
                    return "Erro de comando";
                }
            }
            else if(cmd[0].equalsIgnoreCase("username") && !hasUserNameLog && !hasUserNameReg && !hasUserNameOut){
                if(cmd[1] == null || cmd[1].length() == 0){
                    return "Sem Username defenido!\n";
                }
                //vai verificar se o utilizador existe na base de dados
                username = cmd[1];
                if(hasTypeLog){
                    hasUserNameLog = ci.getDbaction().contaisUserMultiCast(username, ci);
                }
                else if(hasTypeReg){
                    hasUserNameReg = !ci.getDbaction().contaisUserMultiCast(username, ci);
                }
                else if(hasTypeOut){
                    hasUserNameOut = ci.getDbaction().contaisUserMultiCast(username, ci);
                }
            }
            else if(cmd[0].equalsIgnoreCase("password") && !hasPasswordLog && !hasPasswordReg && !hasPasswordOut){
                if(cmd[1] == null || cmd[1].length() == 0){
                    return "Sem Password defenida!\n";
                }
                //vai verificar que a password introduzida cuincide com a introduzida para aquele username
                password = cmd[1];
                if(hasTypeLog && hasUserNameLog){
                    hasPasswordLog = ci.getDbaction().verifyUserPasswordMultiCast(username, password, ci);
                }
                else if(hasTypeReg && hasUserNameReg){
                    hasPasswordReg = true;
                }
                else if(hasTypeOut && hasUserNameOut){
                    hasPasswordOut = ci.getDbaction().verifyUserPasswordMultiCast(username, password, ci);
                }
            }
            else {
                //caso exista mais linhas de comando para alm desta ou o comando introduzido tenha sido mal escrito
                //ou seja de outro tipo
                return "Sem Tipo definido [2] !\n";
            }
        }
        
        if(hasPasswordLog && hasTypeLog && hasUserNameLog){
            //Verifica se o utilizador se encontra na base de dados como logado
            //Caso o servidor nao tenha o nome de cliente logado, esse  adicionado
            // aos dados desse servidor
            if(!ci.getClientsLogs().contains(username.trim())){
                ci.addClientsLogs(username.trim());
                return "Login com sucesso.\n";
            }
            else{
                return "Login sem sucesso.\n";
            }
        }
        else if(hasPasswordReg && hasTypeReg && hasUserNameReg && hasNameReg){
            if(ci.getDbaction().insertuserMultiCast(name, username, password, ci)){
                return "Registo com sucesso.\n";
            }
            else{
                return "Registo sem sucesso.\n";
            }
        }
        else if(hasPasswordOut && hasTypeOut && hasUserNameOut){
            //Caso o servidor tenha o nome de cliente logado, esse  retirado
            // dos dados desse servidor
            if(ci.getClientsLogs().contains(username.trim())){
                ci.removeClientsLogs(username.trim());
                return "Logout com sucesso.\n";
            }
            else{
                return "Logout sem sucesso.\n";
            }
        }
        else{
            return "Erro de comando total.\n";
        }
    }
    
}
