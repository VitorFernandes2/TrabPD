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
    private static boolean corre;

    public MulticastUDP(ServerLogic ci) {
        this.ci = ci;
        this.port = ci.getSd().getServerPort(); // TEMPPPP
    }
    
    //public static void main(String[] args) throws UnknownHostException, IOException {
    public void comecamulticast() {
        try {
            // TODO code application logic here
            
            group = InetAddress.getByName("225.4.5.6");
            multicastSock = new MulticastSocket(3456);
            multicastSock.joinGroup(group);
            corre = true;
            
            new Thread(Receb).start();
            new Thread(Env).start();
            
            ci.Obj().put("output", "\nMulticastThreads iniciadas.\n");
            ci.notifyObserver(1);
            
        } catch (UnknownHostException ex) {
            ci.Obj().put("exception", "[ERROR] Lançamento de Thread Multicast [1] (UnknownHost) -> " + ex.getMessage());
            ci.notifyObserver(4);
        } catch (IOException ex) {
            ci.Obj().put("exception", "[ERROR] Lançamento de Thread Multicast [2] (IO) -> " + ex.getMessage());
            ci.notifyObserver(4);
        }
        
    }
    
    public void turnOff() {
        MulticastUDP.corre = false;
    }

    public static MulticastSocket getMulticastSock() {
        return multicastSock;
    }

    public static InetAddress getGroup() {
        return group;
    }

    private static Runnable Receb = new Runnable() {
        public void run() {
            try{
                while(corre){
                    
                    byte [] buffer = new byte [2046];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    multicastSock.receive(packet);
                    
                    //Código para a atualização da base de dados, a partir do comando enviado
                    
                    String cmd = new String(buffer);
                    
                    ci.Obj().put("output", "Recebe: " + cmd.trim());
                    ci.notifyObserver(1);
                    
                    //Tratamento de cópia de musics para as playlists para a nova base de dados
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
                    //Tratamento de cópia de playlists para a nova base de dados
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
                    //Tratamento de cópia de users para a nova base de dados
                    else if(cmd.contains("createcopyUser")){
                        
                        JSONParser JsonParser = new JSONParser();
                        JSONObject JObj = (JSONObject) JsonParser.parse(cmd.trim());
                        int UserId = Integer.parseInt((String) JObj.get("UserId"));
                        String UserName = (String) JObj.get("UserName");
                        String UserPassword = (String) JObj.get("UserPassword");
                        
                        if(!ci.getDbaction().contaisUserMultiCast(UserName, ci)){
                            ci.getDbaction().insertuserMultiCast(UserId, UserName, UserName, UserPassword, ci);
                        }
                        
                    }
                    //Tratamento de cópia de músicas para a nova base de dados
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
                            ci.getDbaction().insertmusic(MusicName, MusicAuthor, MusicAlbum, MusicYear, MusicDuration, MusicGenre, MusicPath, ci);
                        }
                        
                    }
                    else if (cmd.contains("changeMusic")){



                    }
                    else if (cmd.contains("removeMusic")){



                    }
                    else if (cmd.contains("changePlaylist")){



                    }
                    else if (cmd.contains("removePlaylist")){



                    }
                    else if (cmd.contains("createPlaylist")){



                    }
                    //Tratamento de login/registo/logout
                    else{
                        String result = commandParse(cmd);
                        if(!result.contains("Sem Tipo definido [2] !\n")){
                            System.out.println("\nRecebi: " + cmd + "\nTentativa de execução de comando: " + result);
                        }
                    }
                    
                    //Código para cópia total das necessidades dos dados de cada servidor criado
                    if(cmd.trim().equals("copiar") && !ci.getDbaction().getNamedb().equals("Principal")){
                        //Código de Cópia da base de dados original
                        try {
                            String PrincipalString = "jdbc:mysql://localhost:3306/Principal?useTimezone=true&serverTimezone=UTC&useSSL=false";
                            Connection connectPrincipal = DriverManager.getConnection(PrincipalString,ci.getDbaction().getUser(),ci.getDbaction().getPass());
                            Statement stmt2 = connectPrincipal.createStatement();
                            
                            try {
                                String selectSql = ("SELECT * FROM `users`");
                                ResultSet resultSet = stmt2.executeQuery(selectSql);
                                String user_id;
                                String username;
                                String password;
                                while(resultSet.next()){
                                    user_id = String.valueOf(resultSet.getInt("user_id"));
                                    username = resultSet.getString("username");
                                    password = resultSet.getString("password");
                                    JSONObject jo = new JSONObject();
                                    jo.put("Command", "createcopyUser");
                                    jo.put("UserId", user_id);
                                    jo.put("UserName", username.trim());
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
                            //Código para a cópia das músicas
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
                            //Código para cópia das playlists
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
                            //Código para cópia das músicas associadas a cada playlist
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
                        
                        //Pedido de logins existentes à base de dados principal
                        byte[] l = String.valueOf("logins").getBytes();
                        DatagramPacket packetLogs = new DatagramPacket(l, l.length, group, 3456);
                        multicastSock.send(packetLogs);
                        //-----------------------------------------------------
                        
                    }
                    //Código de cópia de logins existentes na base de dados principal
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
                    
                    //updatedatabase(JObjrecebido);
                    
                }
            } catch (IOException ex) {
                ci.Obj().put("exception", "[ERROR] Package não foi recebida em bom estado.\n" + ex.getMessage());
                ci.notifyObserver(4);
            } catch (ParseException ex) {
                ci.Obj().put("exception", "[ERROR] Parser não foi corretamente executada.\n" + ex.getMessage());
                ci.notifyObserver(4);
            }
            ci.Obj().put("exception", "[ERROR] Servidor deixou de receber mensagens por multicast.");
            ci.notifyObserver(4);
        }
    };
    
    private static Runnable Env = new Runnable() { // TEMPPP
        public void run() {
            try{
                //Pedido de cópia de base de dados principal por parte do servidor
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
        boolean hasUserNameLog = false;
        boolean hasUserNameReg = false;
        boolean hasUserNameOut = false;
        boolean hasPasswordLog = false;
        boolean hasPasswordReg = false;
        boolean hasPasswordOut = false;
        String username = "";
        String password = "";
        command = command.replace(" ", "");
        String [] zones = command.split(";");
        for(String zone : zones){
            String parte = zone.replace("|", " ");
            String [] cmd = parte.split(" ");
            //vai verificar se o comando é inicializado corretamente
            if(cmd[0].equalsIgnoreCase("tipo") && !hasTypeLog && !hasTypeReg && !hasTypeOut){
                //vai verificar se o tipo de comando é o correto
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
                //caso exista mais linhas de comando para além desta ou o comando introduzido tenha sido mal escrito
                //ou seja de outro tipo
                return "Sem Tipo definido [2] !\n";
            }
        }
        
        if(hasPasswordLog && hasTypeLog && hasUserNameLog){
            //Verifica se o utilizador se encontra na base de dados como logado
            //Caso o servidor não tenha o nome de cliente logado, esse é adicionado
            // aos dados desse servidor
            if(!ci.getClientsLogs().contains(username.trim())){
                ci.addClientsLogs(username.trim());
                return "Login com sucesso.\n";
            }
            else{
                return "Login sem sucesso.\n";
            }
        }
        else if(hasPasswordReg && hasTypeReg && hasUserNameReg){
            if(ci.getDbaction().insertuserMultiCast(username, username, password, ci)){
                return "Registo com sucesso.\n";
            }
            else{
                return "Registo sem sucesso.\n";
            }
        }
        else if(hasPasswordOut && hasTypeOut && hasUserNameOut){
            //Caso o servidor tenha o nome de cliente logado, esse é retirado
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
