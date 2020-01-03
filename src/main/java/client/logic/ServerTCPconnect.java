package client.logic;

import client.InterfaceGrafica.Interfacemain;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

import mainObjects.Music;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ServerTCPconnect implements Runnable{
    
    private String ip;
    private String port;
    private boolean stopthread = true;
    private Socket s;

    ConnectData data;
    Interfacemain upperclass;
    JSONObject JObjE;
    JSONObject obj;
    
    public ServerTCPconnect(String json){
        
        try{

            JSONParser JsonParser = new JSONParser();
            JSONObject JObj = (JSONObject) JsonParser.parse(json);

            ip = (String) JObj.get("IP");
            port = (String) JObj.get("Port");
            
            data.getJObj().put("output", "Iniciou a aplicao no porto: " + port + ".\nCom o IP: " + ip + ".\n");
            upperclass.update(86, data);
            
            s = new Socket(this.ip, Integer.parseInt(this.port));
            
        }
        catch (ParseException e){
            JObjE = new JSONObject();
            JObjE.put("exception", "[ERROR] Parse de JSON com erros no Reset de Socket. " + e.toString());
            data.setJObj(JObjE);
            upperclass.update(444, data);
        } catch (IOException e) {
            JObjE = new JSONObject();
            JObjE.put("exception", "[ERROR] Reset de Socket com erros. " + e.toString());
            data.setJObj(JObjE);
            upperclass.update(444, data);
        }
        
    }

    public ServerTCPconnect(String json, ConnectData datacomun,Interfacemain man) {

        try{
            //Inicializacao do objeto de comando
            obj = new JSONObject();
            obj.put("Command", "");
            //----------------------------------
            
            JSONParser JsonParser = new JSONParser();
            JSONObject JObj = (JSONObject) JsonParser.parse(json);

            ip = (String) JObj.get("IP");
            port = (String) JObj.get("Port");

            System.out.println(ip);
            System.out.println(port);
            
            this.data = datacomun;
            this.upperclass = man;
            
        }
        catch (ParseException e){
            JObjE = new JSONObject();
            JObjE.put("exception", "[ERROR] unable to parse json from tcp connect in ServerTCPconnect thread " + e.toString());
            data.setJObj(JObjE);
            upperclass.update(444, data);
        }
    }
    
    public void resetTCP(String ip, String port) throws IOException{
        s = new Socket(ip, Integer.parseInt(port));
    }
    
    public synchronized void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            int guardaMenu = 7;
            boolean didPush = false;
            boolean close = false;
            s = new Socket(this.ip, Integer.parseInt(this.port)); // DUMMY CODE : modificar para enviar o q  preciso
            PrintWriter pr;
            InputStreamReader in;
            StringBuilder sb;
            BufferedReader bf;
            String str;
            JSONParser JsonParser;
            JSONObject JObj;
            JSONObject obj;
            while(stopthread){
                
                try {
                    synchronized(s.getOutputStream()){
                        
                        //Mostra consoante o menu em que se encontra
                        switch (data.getMenu()){
                            
                            case 2:
                                guardaMenu = 7;
                                this.upperclass.update(2, data);
                                
                                sb = new StringBuilder();
                                sb.append("tipo|login;username|").append(data.getUsername()).append(";password|").append(data.getPassword());
                                
                                obj = new JSONObject();
                                obj.put("Command", sb.toString());
                                
                                pr = new PrintWriter(s.getOutputStream());
                                pr.println(obj.toString());
                                pr.flush();
                                didPush = true;
                                
                                break;
                                
                            case 3:
                                guardaMenu = 7;
                                this.upperclass.update(10, data);
                                
                                sb = new StringBuilder();
                                sb.append("tipo|registo;name|").append(data.getName()).append(";username|").append(data.getUsername()).append(";password|").append(data.getPassword());
                                
                                obj = new JSONObject();
                                obj.put("Command", sb.toString());
                                
                                pr = new PrintWriter(s.getOutputStream());
                                pr.println(obj.toString());
                                pr.flush();
                                didPush = true;
                                
                                break;
                                
                            case 7:
                                guardaMenu = 7;
                                this.upperclass.update(7, data);
                                
                                if (data.getCommand().equals("entrar")){
                                    data.setMenu(8);
                                }
                                else if(data.getCommand().equals("entraLogin")){
                                    data.setMenu(2);
                                }
                                else if(data.getCommand().equals("entraRegisto")){
                                    data.setMenu(3);
                                }
                                else if(data.getCommand().equals("sair")){
                                    obj = new JSONObject();
                                    //Flag de saida de programa
                                    obj.put("Command", "exit");
                                    pr = new PrintWriter(s.getOutputStream());
                                    pr.println(obj.toString());
                                    pr.flush();
                                    close = true;
                                    break;
                                }
                                
                                break;
                                
                            case 8:
                                guardaMenu = 7;
                                this.upperclass.update(8, data);
                                
                                if (data.getCommand().equals("gotoMusics")){
                                    data.setMenu(6);
                                }
                                else if(data.getCommand().equals("logout")){
                                    sb = new StringBuilder();
                                    sb.append("tipo|logout;username|").append(data.getUsername()).append(";password|").append(data.getPassword());
                                    
                                    obj = new JSONObject();
                                    obj.put("Command", sb.toString());
                                    
                                    pr = new PrintWriter(s.getOutputStream());
                                    pr.println(obj.toString());
                                    pr.flush();
                                    didPush = true;
                                    data.setMenu(7);
                                }
                                else if (data.getCommand().equals("gotoPlaylists")){
                                    data.setMenu(15);
                                }
                                
                                break;
                                
                            case 6:
                                guardaMenu = 8;
                                this.upperclass.update(6, data);
                                
                                if (data.getCommand().equals("createMusic")){
                                    data.setMenu(9);
                                }
                                else if (data.getCommand().equals("playMusic")){
                                    data.setMenu(11);
                                }
                                else if (data.getCommand().equals("removeMusic")){
                                    data.setMenu(12);
                                }
                                else if (data.getCommand().equals("changeMusic")){
                                    data.setMenu(14);
                                }
                                else if (data.getCommand().equals("listMusics")){
                                    data.setMenu(13);
                                }
                                else if (data.getCommand().equals("voltar")){
                                    data.setMenu(8);
                                }
                                
                                break;
                                
                                //Criar musica
                            case 9:
                                guardaMenu = 6;
                                this.upperclass.update(9, data);
                                
                                Music music = data.getMusic();
                                System.out.printf(music.toString());
                                
                                obj = new JSONObject();
                                obj.put("Command", "createMusic");
                                obj.put("MusicName", music.getName());
                                obj.put("MusicAuthor", music.getAuthor());
                                obj.put("MusicYear", music.getYear());
                                obj.put("MusicAlbum", music.getAlbum());
                                obj.put("MusicDuration", music.getDuration());
                                obj.put("MusicGenre", music.getGenre());
                                obj.put("MusicPath", music.getPath());
                                
                                File file = new File(music.getPath());
                                obj.put("size", file.length());
                                
                                pr = new PrintWriter(s.getOutputStream());
                                pr.println(obj.toString());
                                pr.flush();
                                
                                in = new InputStreamReader(s.getInputStream());
                                bf = new BufferedReader(in);
                                
                                str = bf.readLine();
                                
                                JsonParser = new JSONParser();
                                JObj = (JSONObject) JsonParser.parse(str);
                                data.setJObj(JObj);
                                
                                this.upperclass.update(3, data);
                                
                                //Se puder enviar o ficheiro
                                if (JObj.get("message").equals("sendFile")){
                                    SendFileToServer sendFileToServer = new SendFileToServer(music.getPath(),
                                            s.getOutputStream());
                                    sendFileToServer.start();
                                }
                                
                                data.setMenu(6);
                                
                                break;
                                
                                //Tocar musica
                            case 11:
                                guardaMenu = 6;
                                this.upperclass.update(11, data);
                                
                                obj = new JSONObject();
                                obj.put("Command", "playMusic");
                                obj.put("name", data.getNome());
                                obj.put("author", data.getAutor());
                                
                                //Envia os dados para tocar a musica para o servidor
                                pr = new PrintWriter(s.getOutputStream());
                                pr.println(obj.toString());
                                pr.flush();
                                
                                //Mensagem de confirmacao ou negacao
                                in = new InputStreamReader(s.getInputStream());
                                BufferedReader bf2 = new BufferedReader(in);
                                
                                String str2 = bf2.readLine();
                                
                                JSONParser JsonParser2 = new JSONParser();
                                JSONObject JObj2 = (JSONObject) JsonParser2.parse(str2);
                                
                                if (JObj2.get("message").equals("receiveFile")){
                                    
                                    long fileSize = (long)JObj2.get("size");
                                    ReceiveFileFromServer receiveFileFromServer =
                                            new ReceiveFileFromServer(s.getInputStream(), s.getOutputStream(),
                                                    "tmp.mp3", fileSize);
                                    receiveFileFromServer.start();
                                    receiveFileFromServer.join();
                                    
                                }
                                
                                break;
                                
                                //Remover musica
                            case 12:
                                guardaMenu = 6;
                                this.upperclass.update(12, data);
                                
                                obj = new JSONObject();
                                obj.put("Command", "removeMusic");
                                obj.put("name", data.getNome());
                                obj.put("author", data.getAutor());
                                
                                //Envia os dados para tocar a musica para o servidor
                                pr = new PrintWriter(s.getOutputStream());
                                pr.println(obj.toString());
                                pr.flush();
                                didPush = true;
                                
                                break;
                                
                                //Listar musicas
                            case 13:
                                guardaMenu = 6;
                                obj = new JSONObject();
                                obj.put("Command", "listMusics");
                                
                                //Envia os dados para listar as musicas
                                pr = new PrintWriter(s.getOutputStream());
                                pr.println(obj.toString());
                                pr.flush();
                                
                                //Mensagem de confirmacao ou negacao
                                in = new InputStreamReader(s.getInputStream());
                                BufferedReader bf3 = new BufferedReader(in);
                                
                                String str3 = bf3.readLine();
                                
                                JSONParser JsonParser3 = new JSONParser();
                                JSONObject JObj3 = (JSONObject) JsonParser3.parse(str3);
                                
                                data.setJObj(JObj3);
                                
                                this.upperclass.update(13, data);
                                
                                break;
                                
                                //Alterar musica
                            case 14:
                                guardaMenu = 6;
                                this.upperclass.update(14, data);
                                
                                Music music2 = data.getMusic();
                                
                                obj = new JSONObject();
                                obj.put("Command", "changeMusic");
                                obj.put("name", data.getNome());
                                obj.put("author", data.getAutor());
                                obj.put("MusicName", music2.getName());
                                obj.put("MusicAuthor", music2.getAuthor());
                                obj.put("MusicYear", music2.getYear());
                                obj.put("MusicAlbum", music2.getAlbum());
                                obj.put("MusicDuration", music2.getDuration());
                                obj.put("MusicGenre", music2.getGenre());
                                obj.put("MusicPath", music2.getPath());
                                
                                //Envia os dados para alterar a musica para o servidor
                                pr = new PrintWriter(s.getOutputStream());
                                pr.println(obj.toString());
                                pr.flush();
                                
                                //Mensagem de confirmacao ou negacao
                                in = new InputStreamReader(s.getInputStream());
                                BufferedReader bf4 = new BufferedReader(in);
                                
                                String str4 = bf4.readLine();
                                
                                JSONParser JsonParser4 = new JSONParser();
                                JSONObject JObj4 = (JSONObject) JsonParser4.parse(str4);
                                
                                break;
                                
                                //Menu de Playlists
                            case 15:
                                guardaMenu = 8;
                                this.upperclass.update(15, data);
                                
                                if (data.getCommand().equals("listPlaylists")){
                                    this.data.setMenu(19);
                                }
                                else if(data.getCommand().equals("createPlaylist")){
                                    this.data.setMenu(16);
                                }
                                else if(data.getCommand().equals("removePlaylist")){
                                    this.data.setMenu(17);
                                }
                                else if(data.getCommand().equals("changePlaylist")){
                                    this.data.setMenu(18);
                                }
                                else if(data.getCommand().equals("playPlaylist")){
                                    this.data.setMenu(20);
                                }
                                else if(data.getCommand().equals("voltar")){
                                    this.data.setMenu(8);
                                }
                                
                                break;
                                
                                //Criacao de Playlist
                            case 16:
                                guardaMenu = 15;
                                this.upperclass.update(16, data);
                                
                                obj = new JSONObject();
                                obj.put("Command", "createPlaylist");
                                obj.put("username", this.data.getUsername());
                                obj.put("name", this.data.getNome());
                                
                                for (int i = 0; i < data.getMusicNameList().size(); i++) {
                                    
                                    String name = "nomeMusica" + i;
                                    String autor = "autorMusica" + i;
                                    obj.put(name, data.getMusicNameList().get(i));
                                    obj.put(autor, data.getMusicAuthorList().get(i));
                                    
                                }
                                
                                obj.put("numberOfMusics", data.getMusicAuthorList().size());
                                
                                //Envia os dados para criar as playlists
                                pr = new PrintWriter(s.getOutputStream());
                                pr.println(obj.toString());
                                pr.flush();
                                
                                break;
                                
                                //Remove de Playlist
                            case 17:
                                guardaMenu = 15;
                                this.upperclass.update(17, data);
                                
                                obj = new JSONObject();
                                obj.put("Command", "removePlaylist");
                                obj.put("username", this.data.getUsername());
                                obj.put("name", data.getNome());
                                
                                //Envia os dados para alterar as playlists
                                pr = new PrintWriter(s.getOutputStream());
                                pr.println(obj.toString());
                                pr.flush();
                                
                                break;
                                
                                //Change Playlist
                            case 18:
                                guardaMenu = 15;
                                this.upperclass.update(18, data);
                                
                                obj = new JSONObject();
                                obj.put("Command", "changePlaylist");
                                obj.put("username", this.data.getUsername());
                                obj.put("name", this.data.getNome());
                                obj.put("newName", this.data.getAutor());
                                
                                //Envia os dados para alterar as playlists
                                pr = new PrintWriter(s.getOutputStream());
                                pr.println(obj.toString());
                                pr.flush();
                                
                                break;
                                
                                //List Playlist
                            case 19:
                                guardaMenu = 15;
                                obj = new JSONObject();
                                obj.put("Command", "listPlaylist");
                                obj.put("username", this.data.getUsername());
                                
                                //Envia os dados para listar as playlists
                                pr = new PrintWriter(s.getOutputStream());
                                pr.println(obj.toString());
                                pr.flush();
                                
                                //Mensagem de confirmacao ou negacao
                                in = new InputStreamReader(s.getInputStream());
                                bf = new BufferedReader(in);
                                
                                str = bf.readLine();
                                
                                JsonParser = new JSONParser();
                                JObj = (JSONObject) JsonParser.parse(str);
                                
                                data.setJObj(JObj);
                                
                                this.upperclass.update(19, data);
                                
                                break;
                                
                                //Play Playlist
                            case 20:
                                guardaMenu = 15;
                                this.upperclass.update(20, data);
                                
                                String nome = data.getNome();
                                obj = new JSONObject();
                                obj.put("Command", "playPlaylist");
                                obj.put("username", this.data.getUsername());
                                obj.put("nome", this.data.getNome());
                                
                                //Envia os dados para listar as playlists
                                pr = new PrintWriter(s.getOutputStream());
                                pr.println(obj.toString());
                                pr.flush();

                                //Recebe as msicas
                                in = new InputStreamReader(s.getInputStream());
                                bf = new BufferedReader(in);

                                str = bf.readLine();

                                JsonParser = new JSONParser();
                                JObj = (JSONObject) JsonParser.parse(str);

                                if (JObj.get("message").equals("receiveFiles")){

                                    System.out.println(JObj.toString());

                                    long numberOfFiles = (long)JObj.get("numberOfFiles");

                                    for (int i = 0; i < numberOfFiles; i++) {

                                        String playNome = "Nome" + i;
                                        String name = (String)JObj.get(playNome);

                                        String MusicSize = "Size" + i;
                                        long fileSize = (long)JObj.get(MusicSize);

                                        String filename = "tmp" + i + ".mp3";

                                        ReceiveFileFromServer receiveFileFromServer =
                                                new ReceiveFileFromServer(s.getInputStream(), s.getOutputStream(),
                                                        filename, fileSize);
                                        receiveFileFromServer.start();
                                        receiveFileFromServer.join();

                                    }

                                }

                                break;
                                
                        }
                        
                        //Envio para tratamento
                        if(didPush){
                            didPush = false;
                            in = new InputStreamReader(s.getInputStream());
                            bf = new BufferedReader(in);

                            str = bf.readLine();

                            JsonParser = new JSONParser();
                            JObj = (JSONObject) JsonParser.parse(str);
                            boolean Sucesso = (boolean) JObj.get("sucesso");
                            
                            if(!Sucesso){
                                data.setMenu(guardaMenu);
                            }

                            data.getJObj().put("output", JObj.toString());
                            upperclass.update(86, data);
                        }
                        //Fecho da Aplicao
                        else if(close){
                            break;
                        }

                    }
                } catch (SocketException e) {
                    if(data.getMenu() != 8){
                        data.setMenu(guardaMenu);
                    }
                    data.getJObj().put("output", "[ERROR] Redirecionamento do utilizador para outro servidor.");
                    upperclass.update(86, data);
                    
                    DsConnect start = new DsConnect("9999","9999",upperclass,data);
                    String returnado = start.run();

                    if(returnado.equalsIgnoreCase("error")){
                        break;
                    }

                    JSONParser JsonParserReset = new JSONParser();
                    JSONObject JObjReset = (JSONObject) JsonParserReset.parse(returnado);

                    String ip = (String) JObjReset.get("IP");
                    String port = (String) JObjReset.get("Port");

                    data.getJObj().put("output", "Foi redirecionado para o porto: " + port + ".");
                    upperclass.update(86, data);

                    this.resetTCP(ip, port);
                } catch (InterruptedException e) {
                    JObjE = new JSONObject();
                    JObjE.put("exception", e.toString());
                    data.setJObj(JObjE);
                    upperclass.update(404, data);
                    //Provisrio para fechar aplicao quando o servidor for abaixo
                    this.setCommand("exit");
                    //-------------------------------------------------------------
                }
                
            }
            
            s.close();

        } catch (SocketException e) {
            JObjE = new JSONObject();
            JObjE.put("exception", e.toString());
            data.setJObj(JObjE);
            upperclass.update(5, data);
            //Provisrio para fechar aplicao quando o servidor for abaixo
            this.setCommand("exit");
            //-------------------------------------------------------------
        } catch (ParseException e) {
            JObjE = new JSONObject();
            JObjE.put("exception", e.toString());
            data.setJObj(JObjE);
            upperclass.update(444, data);
        } catch (NullPointerException | IOException e) {
            JObjE = new JSONObject();
            JObjE.put("exception", e.toString());
            data.setJObj(JObjE);
            upperclass.update(444, data);
        }

        JObjE = new JSONObject();
        JObjE.put("exception", "Desligando o Cliente.");
        data.setJObj(JObjE);
        upperclass.update(5, data);
        //Provisrio para fechar aplicao quando o servidor for abaixo
        this.setCommand("exit");
        //-------------------------------------------------------------

    }
    
    public String getCommand(){
        return (String) obj.get("Command");
    }
    
    public void setCommand(String cmd){
        obj.put("Command",cmd);
    }
    
    public void stopthread(){ // pode ser ma pratica
        stopthread = false;
    }
    
}