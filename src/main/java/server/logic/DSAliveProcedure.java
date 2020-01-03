package server.logic;

import mainObjects.Contants;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.ServerLogic;

import java.io.IOException;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DSAliveProcedure extends Thread {

    private boolean finished;
    private MulticastUDP mudp;
    private ServerLogic serverLogic;
    private DatagramSocket socket;

    public DSAliveProcedure(ServerLogic serverLogic, MulticastUDP mudp) {
        this.finished = false;
        this.mudp = mudp;
        this.serverLogic = serverLogic;
    }

    @Override
    public void run() {

        try {

            //Socket que vai receber a mensagem para saber se est vivo
            socket = new DatagramSocket(serverLogic.getSd().getServerPort());

            while (!finished){

                byte[] buf = new byte[Contants.TOTALBYTES];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String msg = new String(packet.getData(), 0 , packet.getLength());
                
                serverLogic.getSd().getObjMudance().put("output", msg);
                serverLogic.notifyObserver(1);

                //get the json message
                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject)parser.parse(msg);

                if (obj.get("message").equals("isAlive")){

                    long numberOfServers = (long) obj.get("numberOfServers");
                    serverLogic.getSd().setNumberOfServers(numberOfServers);
                    
                    serverLogic.getSd().getObjMudance().put("output", "O numero de servidores e: " + numberOfServers);
                    serverLogic.notifyObserver(1);

                    int Port = packet.getPort();
                    InetAddress IP = packet.getAddress();

                    String reply = "Greetings!";
                    buf = reply.getBytes();
                    packet = new DatagramPacket(buf, buf.length, IP, Port);
                    socket.send(packet);

                }
                else if (obj.get("message").equals("ServerDown")){

                    long numberOfServers = (long) obj.get("numberOfServers");
                    serverLogic.getSd().setNumberOfServers(numberOfServers);
                    
                    serverLogic.getSd().getObjMudance().put("output", "Um servidor foi abaixo o numero de servidores e: " + numberOfServers);
                    serverLogic.notifyObserver(1);

                }
                else if (obj.get("message").equals("giveadmin")){
                    
                    String namebd = (String) obj.get("namebd");
                    
                    serverLogic.getSd().getObjMudance().put("output", "Este servidor passou a ser o servidor principal chamado: " + namebd);
                    serverLogic.notifyObserver(1);
                    
                    String oldNamebd = serverLogic.getDbaction().getNamedb();
                    
                    serverLogic.getSd().getObjMudance().put("output", "A copiar do servidor antigo: " + oldNamebd);
                    serverLogic.notifyObserver(1);
                    
                    serverLogic.getDbaction().setNamedb(namebd);
                    
                    //Cpia dos dados mais atualizados da base de dados anterior para a Principal
                    
                    try {
                        String OldString = "jdbc:mysql://localhost:3306/" + oldNamebd + "?useTimezone=true&serverTimezone=UTC&useSSL=false";
                        Connection connectPrinserverLogicpal = DriverManager.getConnection(OldString,serverLogic.getDbaction().getUser(),serverLogic.getDbaction().getPass());
                        Statement stmt2 = connectPrinserverLogicpal.createStatement();

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
                                DatagramPacket packetUser = new DatagramPacket(b, b.length, mudp.getGroup(), mudp.getUses());
                                mudp.getMulticastSock().send(packetUser);
                            }
                        } catch (SQLException ex) {
                            serverLogic.Obj().put("exception", "[ERROR] Copia da Users para outras bases de dados -> " + ex.getMessage());
                            serverLogic.notifyObserver(4);
                            break;
                        }
                        //Cdigo para a cpia das msicas
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
                                DatagramPacket packetUser = new DatagramPacket(b, b.length, mudp.getGroup(), mudp.getUses());
                                mudp.getMulticastSock().send(packetUser);
                            }
                        } catch (SQLException ex) {
                            serverLogic.Obj().put("exception", "[ERROR] Copia da Musics -> " + ex.getMessage());
                            serverLogic.notifyObserver(4);
                            break;
                        }
                        //Cdigo para cpia das playlists
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
                                DatagramPacket packetUser = new DatagramPacket(b, b.length, mudp.getGroup(), mudp.getUses());
                                mudp.getMulticastSock().send(packetUser);
                            }
                        } catch (SQLException ex) {
                            serverLogic.Obj().put("exception", "[ERROR] Copia da Playlist -> " + ex.getMessage());
                            serverLogic.notifyObserver(4);
                            break;
                        }
                        //Cdigo para cpia das msicas assoserverLogicadas a cada playlist
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
                                DatagramPacket packetUser = new DatagramPacket(b, b.length, mudp.getGroup(), mudp.getUses());
                                mudp.getMulticastSock().send(packetUser);
                            }
                        } catch (SQLException ex) {
                            serverLogic.Obj().put("exception", "[ERROR] Copia das Musicas assoserverLogicadas a cada Playlist -> " + ex.getMessage());
                            serverLogic.notifyObserver(4);
                            break;
                        }

                        connectPrinserverLogicpal.close();
                        stmt2.close();

                    } catch (SQLException ex) {
                        serverLogic.Obj().put("exception", "[ERROR] Copia da Base de Dados -> " + ex.getMessage());
                        serverLogic.notifyObserver(4);
                        return;
                    }
                    //---------------------------------------------------------------------------
                    
                }

            }

        } catch (SocketException e) {
            serverLogic.getSd().getObjMudance().put("exception", "[ERROR] Erro no socket de pings de ligao.\n" + e.getMessage());
            serverLogic.notifyObserver(4);
        } catch (IOException e) {
            serverLogic.getSd().getObjMudance().put("exception", "[ERROR] Erro nos pings de ligao.\n" + e.getMessage());
            serverLogic.notifyObserver(4);
        } catch (ParseException e) {
            serverLogic.getSd().getObjMudance().put("exception", "[ERROR] Erro no Parse do objeto JSON nos pings de ligao.\n" + e.getMessage());
            serverLogic.notifyObserver(4);
        } catch (SQLException e) {
            serverLogic.getSd().getObjMudance().put("exception", "[ERROR] Erro no Cdigo de ligao  base de dados.\n" + e.getMessage());
            serverLogic.notifyObserver(4);
        }
        
    }

    public void terminate(){
        this.finished = true;
    }

}
