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
            
            this.data = datacomun;
            this.upperclass = man;
            
            JObjE = new JSONObject();
            JObjE.put("output", "Ligacao ao Servidor de IP: " + ip + "\nCom Porto: " + port);
            data.setJObj(JObjE);
            upperclass.update(86, data);
            
        }
        catch (ParseException e){
            JObjE = new JSONObject();
            JObjE.put("exception", "[ERROR] unable to parse json from tcp connect in ServerTCPconnect thread " + e.toString());
            data.setJObj(JObjE);
            upperclass.update(444, data);
        }
    }
    
    public void resetTCP(String ippp, String porttt) throws IOException{
        if(ippp != null && porttt != null){
            s = new Socket(ippp, Integer.parseInt(porttt));
        }
        else{
            throw new IOException();
        }
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
            s = new Socket(this.ip, Integer.parseInt(this.port)); // DUMMY CODE : modificar para enviar o q é preciso
            PrintWriter pr = null;
            InputStreamReader in = null;
            StringBuilder sb;
            BufferedReader bf;
            String str = new String();
            JSONParser JsonParser;
            JSONObject JObj;
            JSONObject obj = this.obj;
            while(stopthread){
                
                try {
                    
                    pr = new PrintWriter(s.getOutputStream());
                    
                    synchronized(s.getOutputStream()){
                        
                        //Reset do guardaMenu
                        guardaMenu = 7;
                        //-------------------
                        
                        didPush = false;
                        
                        //Mostra consoante o menu em que se encontra
                        switch (data.getMenu()){
                            
                            case 2:
                                guardaMenu = 2;
                                this.upperclass.update(2, data);
                                
                                sb = new StringBuilder();
                                sb.append("tipo|login;username|").append(data.getUsername()).append(";password|").append(data.getPassword());
                                
                                obj = new JSONObject();
                                obj.put("Command", sb.toString());
                                
                                //ponto provavel de rutura
                                didPush = true;
                                pr.println(obj.toString());
                                pr.flush();
                                //------------------------
                                
                                break;
                                
                            case 3:
                                guardaMenu = 3;
                                this.upperclass.update(10, data);
                                
                                sb = new StringBuilder();
                                sb.append("tipo|registo;name|").append(data.getName()).append(";username|").append(data.getUsername()).append(";password|").append(data.getPassword());
                                
                                obj = new JSONObject();
                                obj.put("Command", sb.toString());
                                
                                //ponto provavel de rutura
                                pr.println(obj.toString());
                                pr.flush();
                                didPush = true;
                                //------------------------
                                
                                break;
                                
                            case 7:
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
                                    guardaMenu = 7;
                                    obj = new JSONObject();
                                    //Flag de saida de programa
                                    obj.put("Command", "exit");
                                    
                                    //ponto provavel de rutura
                                    pr = new PrintWriter(s.getOutputStream());
                                    pr.println(obj.toString());
                                    pr.flush();
                                    close = true;
                                    //------------------------
                                    
                                    break;
                                }
                                
                                break;
                                
                            case 8:
                                this.upperclass.update(8, data);
                                
                                if (data.getCommand().equals("gotoMusics")){
                                    data.setMenu(6);
                                }
                                else if(data.getCommand().equals("logout")){
                                    guardaMenu = 8;
                                    sb = new StringBuilder();
                                    sb.append("tipo|logout;username|").append(data.getUsername()).append(";password|").append(data.getPassword());
                                    
                                    obj = new JSONObject();
                                    obj.put("Command", sb.toString());
                                    
                                    //ponto provavel de rutura
                                    pr = new PrintWriter(s.getOutputStream());
                                    pr.println(obj.toString());
                                    pr.flush();
                                    didPush = true;
                                    data.setMenu(7);
                                    //------------------------
                                    
                                }
                                else if (data.getCommand().equals("gotoPlaylists")){
                                    data.setMenu(15);
                                }
                                
                                break;
                                
                            case 6:
                                //Nao precisa de guardaMenu pois ele não envia nenhuns comandos
                                // para o servidor
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
                                else if (data.getCommand().equals("searchMusics")){
                                    data.setMenu(21);
                                }
                                
                                break;
                                
                                //Criar musica
                            case 9:
                                guardaMenu = 9;
                                this.upperclass.update(9, data);
                                
                                Music music = data.getMusic();
                                
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
                                
                                //ponto provavel de rutura
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
                                //------------------------
                                
                                break;
                                
                                //Tocar musica
                            case 11:
                                guardaMenu = 11;
                                this.upperclass.update(11, data);
                                
                                obj = new JSONObject();
                                obj.put("Command", "playMusic");
                                obj.put("name", data.getNome());
                                obj.put("author", data.getAutor());
                                
                                //ponto provavel de rutura
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
                                    PlayMusic.playMusic("tmp.mp3");
                                }
                                //------------------------
                                
                                break;
                                
                                //Remover musica
                            case 12:
                                guardaMenu = 12;
                                this.upperclass.update(12, data);
                                
                                obj = new JSONObject();
                                obj.put("Command", "removeMusic");
                                obj.put("name", data.getNome());
                                obj.put("author", data.getAutor());
                                
                                //ponto provavel de rutura
                                //Envia os dados para remover a musica para o servidor
                                pr = new PrintWriter(s.getOutputStream());
                                pr.println(obj.toString());
                                pr.flush();
                                didPush = true;
                                //------------------------
                                
                                break;
                                
                                //Listar musicas
                            case 13:
                                guardaMenu = 13;
                                obj = new JSONObject();
                                obj.put("Command", "listMusics");
                                
                                //ponto provavel de rutura
                                //Envia os dados para listar as musicas
                                pr = new PrintWriter(s.getOutputStream());
                                pr.println(obj.toString());
                                pr.flush();
                                
                                //Mensagem de confirmacao ou negacao
                                in = new InputStreamReader(s.getInputStream());
                                BufferedReader bf3 = new BufferedReader(in);
                                
                                String str3 = bf3.readLine();
                                
                                if(str3 == null){
                                    JObjE.put("output","Nao existe lista ou comando inválido");
                                    data.setJObj(JObjE);
                                    upperclass.update(86, data);
                                    data.setMenu(6);
                                }
                                else{
                                    JSONParser JsonParser3 = new JSONParser();
                                    JSONObject JObj3 = (JSONObject) JsonParser3.parse(str3);

                                    data.setJObj(JObj3);

                                    this.upperclass.update(13, data);
                                }
                                //------------------------
                                
                                break;
                                
                                //Alterar musica
                            case 14:
                                guardaMenu = 14;
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
                                
                                //ponto provavel de rutura
                                //Envia os dados para alterar a musica para o servidor
                                pr = new PrintWriter(s.getOutputStream());
                                pr.println(obj.toString());
                                pr.flush();
                                //------------------------
                                
                                break;
                                
                                //Menu de Playlists
                            case 15:
                                //Nao precisa de guardaMenu pois ele não envia nenhuns comandos
                                // para o servidor
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
                                else if (data.getCommand().equals("searchPlaylists")){
                                    data.setMenu(22);
                                }
                                
                                break;
                                
                                //Criacao de Playlist
                            case 16:
                                guardaMenu = 16;
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
                                
                                //ponto provavel de rutura
                                //Envia os dados para criar as playlists
                                pr = new PrintWriter(s.getOutputStream());
                                pr.println(obj.toString());
                                pr.flush();
                                didPush = true;
                                //------------------------
                                
                                break;
                                
                                //Remove de Playlist
                            case 17:
                                guardaMenu = 17;
                                this.upperclass.update(17, data);
                                
                                obj = new JSONObject();
                                obj.put("Command", "removePlaylist");
                                obj.put("username", this.data.getUsername());
                                obj.put("name", data.getNome());
                                
                                //ponto provavel de rutura
                                //Envia os dados para alterar as playlists
                                pr = new PrintWriter(s.getOutputStream());
                                pr.println(obj.toString());
                                pr.flush();
                                didPush = true;
                                //------------------------
                                
                                break;
                                
                                //Change Playlist
                            case 18:
                                guardaMenu = 18;
                                this.upperclass.update(18, data);
                                
                                obj = new JSONObject();
                                obj.put("Command", "changePlaylist");
                                obj.put("username", this.data.getUsername());
                                obj.put("name", this.data.getNome());
                                obj.put("newName", this.data.getAutor());
                                
                                //ponto provavel de rutura
                                //Envia os dados para alterar as playlists
                                pr = new PrintWriter(s.getOutputStream());
                                pr.println(obj.toString());
                                pr.flush();
                                didPush = true;
                                //------------------------
                                
                                break;
                                
                                //List Playlist
                            case 19:
                                guardaMenu = 19;
                                obj = new JSONObject();
                                obj.put("Command", "listPlaylist");
                                obj.put("username", this.data.getUsername());
                                
                                //ponto provavel de rutura
                                //Envia os dados para listar as playlists
                                pr = new PrintWriter(s.getOutputStream());
                                pr.println(obj.toString());
                                pr.flush();
                                
                                //Mensagem de confirmacao ou negacao
                                in = new InputStreamReader(s.getInputStream());
                                bf = new BufferedReader(in);
                                
                                str = bf.readLine();
                                
                                if(str == null){
                                    JObjE.put("output", "Nao existe playlist ou comando invalido");
                                    data.setJObj(JObjE);
                                    upperclass.update(86, data);
                                    data.setMenu(15);
                                } 
                                else{
                                
                                    JsonParser = new JSONParser();
                                    JObj = (JSONObject) JsonParser.parse(str);

                                    data.setJObj(JObj);

                                    this.upperclass.update(19, data);
                                }
                                //------------------------
                                
                                break;
                                
                                //Play Playlist
                            case 20:
                                guardaMenu = 20;
                                this.upperclass.update(20, data);
                                
                                String nome = data.getNome();
                                obj = new JSONObject();
                                obj.put("Command", "playPlaylist");
                                obj.put("username", this.data.getUsername());
                                obj.put("nome", this.data.getNome());
                                
                                //ponto provavel de rutura
                                //Envia os dados para listar as playlists
                                pr = new PrintWriter(s.getOutputStream());
                                pr.println(obj.toString());
                                pr.flush();

                                //Recebe as músicas
                                in = new InputStreamReader(s.getInputStream());
                                bf = new BufferedReader(in);

                                str = bf.readLine();

                                JsonParser = new JSONParser();
                                JObj = (JSONObject) JsonParser.parse(str);

                                if (JObj.get("message").equals("receiveFiles")){

                                    JObjE = new JSONObject();
                                    JObjE.put("output", JObj.toString());
                                    data.setJObj(JObjE);
                                    upperclass.update(86, data);

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

                                        PlayMusic.playMusic(filename);

                                    }

                                }
                                //------------------------

                                break;
                            //Procurar musica
                            case 21:
                                guardaMenu = 21;
                                this.upperclass.update(21, data);
                                obj = new JSONObject();
                                obj.put("Command", "searchMusic");
                                obj.put("Commandextra", data.getCommand());
                                
                                //Envia os dados
                                pr = new PrintWriter(s.getOutputStream());
                                pr.println(obj.toString());
                                pr.flush();
                                
                                //Mensagem com a resposta (estilo list text)
                                in = new InputStreamReader(s.getInputStream());
                                BufferedReader bf5 = new BufferedReader(in);
                                
                                String str5 = bf5.readLine();
                                
                                if(str5 == null){
                                    JObjE.put("output", "Nao foi encontrado musicas ou comando invalido");
                                    data.setJObj(JObjE);
                                    upperclass.update(86, data);
                                    //data.setMenu(6);
                                }
                                else{

                                    JSONParser JsonParser5 = new JSONParser();
                                    JSONObject JObj5 = (JSONObject) JsonParser5.parse(str5);

                                    data.setJObj(JObj5);

                                    this.upperclass.update(13, data);
                                }
                                data.setMenu(6);
                                
                                
                                break;
                            // procura playlist
                            case 22:
                                guardaMenu = 22;
                                this.upperclass.update(22, data);
                                obj = new JSONObject();
                                obj.put("Command", "searchPlaylist");
                                obj.put("Commandextra", data.getCommand());
                                obj.put("username", this.data.getUsername());
                                
                                //Envia os dados
                                pr = new PrintWriter(s.getOutputStream());
                                pr.println(obj.toString());
                                pr.flush();
                                
                                //Mensagem com a resposta (estilo list text)
                                in = new InputStreamReader(s.getInputStream());
                                BufferedReader bf6 = new BufferedReader(in);
                                
                                String str6 = bf6.readLine();
                                
                                if(str6 == null){
                                    JObjE.put("output", "Nao foi encontrado musicas ou comando invalido");
                                    data.setJObj(JObjE);
                                    upperclass.update(86, data);
                                }
                                else{
                                    JSONParser JsonParser6 = new JSONParser();
                                    JSONObject JObj6 = (JSONObject) JsonParser6.parse(str6);

                                    data.setJObj(JObj6);

                                    this.upperclass.update(19, data);
                                }
                                data.setMenu(15);
                                
                                break;
                                
                        }
                        
                        //Envio para tratamento
                        if(didPush){
                            didPush = false;
                            in = new InputStreamReader(s.getInputStream());
                            bf = new BufferedReader(in);
                            
                            String str4 = bf.readLine();
                            
                            JsonParser = new JSONParser();
                            
                            JObj = new JSONObject();
                            JObj = (JSONObject) JsonParser.parse(str4);
                            
                            boolean Sucesso = (boolean) JObj.get("sucesso");
                            
                            if(!Sucesso){
                                switch(guardaMenu){
                                    case 2:
                                        data.setMenu(7);
                                        break;
                                    case 3:
                                        data.setMenu(7);
                                        break;
                                    case 12:
                                        data.setMenu(7);
                                        break;
                                }
                            }
                            
                            data.getJObj().put("output", JObj.toString());
                            upperclass.update(86, data);
                        }
                        //Fecho da Aplicação
                        else if(close){
                            break;
                        }
                        
                    }
                } catch (SocketException e) {
                    didPush = false;
                    data.getJObj().put("output", "[ERROR] Redirecionamento do utilizador para outro servidor.");
                    upperclass.update(86, data);
                    
                    DsConnect start = new DsConnect("9999","9999",upperclass,data);
                    String returnado = start.run();

                    if(returnado.equalsIgnoreCase("error")){
                        break;
                    }
                    
                    JSONParser JsonParserReset = new JSONParser();
                    JSONObject JObjReset = (JSONObject) JsonParserReset.parse(returnado);
                    
                    String ipReset = (String) JObjReset.get("IP");
                    String portReset = (String) JObjReset.get("Port");

                    data.getJObj().put("output", "Foi redirecionado para o porto: " + portReset + ".");
                    upperclass.update(86, data);

                    port = portReset;
                    
                    this.resetTCP(ipReset, portReset);
                    
                    //Tratamento do ultimo comando inserido
                    pr = new PrintWriter(s.getOutputStream());
                    in = new InputStreamReader(s.getInputStream());
                    switch(guardaMenu){
                        case 2:
                            pr.println(obj.toString());
                            pr.flush();
                            didPush = true;
                            break;
                        case 3:
                            pr.println(obj.toString());
                            pr.flush();
                            didPush = true;
                            break;
                        case 7:
                            pr.println(obj.toString());
                            pr.flush();
                            close = true;
                            break;
                        case 8:
                            pr.println(obj.toString());
                            pr.flush();
                            didPush = true;
                            data.setMenu(7);
                            break;
                        case 9:
                            pr.println(obj.toString());
                            pr.flush();

                            bf = new BufferedReader(in);

                            str = bf.readLine();

                            JsonParser = new JSONParser();
                            JObj = (JSONObject) JsonParser.parse(str);
                            data.setJObj(JObj);

                            this.upperclass.update(3, data);

                            //Se puder enviar o ficheiro
                            if (JObj.get("message").equals("sendFile")){
                                SendFileToServer sendFileToServer = new SendFileToServer(data.getMusic().getPath(),
                                        s.getOutputStream());
                                sendFileToServer.start();
                            }

                            data.setMenu(6);
                            break;
                        case 11:
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
                                try {
                                    receiveFileFromServer.join();
                                } catch (InterruptedException ex) {
                                    JObjE = new JSONObject();
                                    JObjE.put("exception", "Excepcao no join(1) do tratamento de comando quando vai a passar para outro servidor. " + ex.toString());
                                    data.setJObj(JObjE);
                                    upperclass.update(404, data);
                                    //Provisório para fechar aplicação quando o servidor for abaixo
                                    this.setCommand("exit");
                                    //-------------------------------------------------------------
                                }
                                PlayMusic.playMusic("tmp.mp3");
                            }
                            break;
                        case 12:
                            pr.println(obj.toString());
                            pr.flush();
                            didPush = true;
                            break;
                        case 13:
                            pr.println(obj.toString());
                            pr.flush();

                            //Mensagem de confirmacao ou negacao
                            in = new InputStreamReader(s.getInputStream());
                            BufferedReader bf3 = new BufferedReader(in);

                            String str3 = bf3.readLine();

                            if(str3 == null){
                                JObjE.put("output","Nao existe lista ou comando inválido");
                                data.setJObj(JObjE);
                                upperclass.update(86, data);
                                data.setMenu(6);
                            }
                            else{
                                JSONParser JsonParser3 = new JSONParser();
                                JSONObject JObj3 = (JSONObject) JsonParser3.parse(str3);

                                data.setJObj(JObj3);

                                this.upperclass.update(13, data);
                            }
                            break;
                        case 14:
                            pr.println(obj.toString());
                            pr.flush();
                            break;
                        case 16:
                            pr.println(obj.toString());
                            pr.flush();
                            didPush = true;
                            break;
                        case 17:
                            pr.println(obj.toString());
                            pr.flush();
                            didPush = true;
                            break;
                        case 18:
                            pr.println(obj.toString());
                            pr.flush();
                            didPush = true;
                            break;
                        case 19:
                            pr = new PrintWriter(s.getOutputStream());
                            pr.println(obj.toString());
                            pr.flush();

                            //Mensagem de confirmacao ou negacao
                            in = new InputStreamReader(s.getInputStream());
                            bf = new BufferedReader(in);

                            str = bf.readLine();

                            if(str == null){
                                JObjE.put("output", "Nao existe playlist ou comando invalido");
                                data.setJObj(JObjE);
                                upperclass.update(86, data);
                                data.setMenu(15);
                            } 
                            else{

                                JsonParser = new JSONParser();
                                JObj = (JSONObject) JsonParser.parse(str);

                                data.setJObj(JObj);

                                this.upperclass.update(19, data);
                            }
                            break;
                        case 20:
                            pr.println(obj.toString());
                            pr.flush();

                            //Recebe as músicas
                            in = new InputStreamReader(s.getInputStream());
                            bf = new BufferedReader(in);

                            str = bf.readLine();

                            JsonParser = new JSONParser();
                            JObj = (JSONObject) JsonParser.parse(str);

                            if (JObj.get("message").equals("receiveFiles")){

                                JObjE = new JSONObject();
                                JObjE.put("output", JObj.toString());
                                data.setJObj(JObjE);
                                upperclass.update(86, data);

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
                                    try {
                                        receiveFileFromServer.join();
                                    } catch (InterruptedException ex) {
                                        JObjE = new JSONObject();
                                        JObjE.put("exception", "Excepcao no join(2) do tratamento de comando quando vai a passar para outro servidor. " + ex.toString());
                                        data.setJObj(JObjE);
                                        upperclass.update(404, data);
                                        //Provisório para fechar aplicação quando o servidor for abaixo
                                        this.setCommand("exit");
                                        //-------------------------------------------------------------
                                    }

                                    PlayMusic.playMusic(filename);

                                }

                            }
                            break;
                            case 21:
                            pr.println(obj.toString());
                            pr.flush();

                            //Mensagem com a resposta (estilo list text)
                            in = new InputStreamReader(s.getInputStream());
                            BufferedReader bf5 = new BufferedReader(in);

                            String str5 = bf5.readLine();

                            JSONParser JsonParser5 = new JSONParser();
                            JSONObject JObj5 = (JSONObject) JsonParser5.parse(str5);

                            data.setJObj(JObj5);

                            this.upperclass.update(13, data);

                            data.setMenu(6);
                            break;
                        case 22:
                            pr.println(obj.toString());
                            pr.flush();

                            //Mensagem com a resposta (estilo list text)
                            in = new InputStreamReader(s.getInputStream());
                            BufferedReader bf6 = new BufferedReader(in);

                            String str6 = bf6.readLine();

                            JSONParser JsonParser6 = new JSONParser();
                            JSONObject JObj6 = (JSONObject) JsonParser6.parse(str6);

                            data.setJObj(JObj6);

                            this.upperclass.update(19, data);

                            data.setMenu(15);
                            break;
                    }

                    //Tratamento dos didPush
                    if(didPush){
                        didPush = false;
                        bf = new BufferedReader(in);

                        str = bf.readLine();

                        JsonParser = new JSONParser();
                        JObj = (JSONObject) JsonParser.parse(str);
                        boolean Sucesso = (boolean) JObj.get("sucesso");

                        if(!Sucesso){
                            switch(guardaMenu){
                                case 2:
                                    data.setMenu(7);
                                    break;
                                case 3:
                                    data.setMenu(7);
                                    break;
                            }
                        }

                        data.getJObj().put("output", JObj.toString());
                        upperclass.update(86, data);
                    }
                    else if(close){
                        break;
                    }
                    
                } catch (InterruptedException e) {
                    JObjE = new JSONObject();
                    JObjE.put("exception", e.toString());
                    data.setJObj(JObjE);
                    upperclass.update(404, data);
                    //Provisório para fechar aplicação quando o servidor for abaixo
                    this.setCommand("exit");
                    //-------------------------------------------------------------
                }
                
            }
            
            //Provisório para fechar aplicação quando o servidor for abaixo
            this.setCommand("exit");
            //-------------------------------------------------------------
            
            pr.println(obj.toString());
            pr.flush();
            
            s.close();

        } catch (SocketException e) {
            JObjE = new JSONObject();
            JObjE.put("exception", "[ERROR] Nao foi possivel ligar ao servidor: " + e.toString());
            data.setJObj(JObjE);
            upperclass.update(5, data);
            //Provisório para fechar aplicação quando o servidor for abaixo
            this.setCommand("exit");
            //-------------------------------------------------------------
        } catch (ParseException e) {
            JObjE = new JSONObject();
            JObjE.put("exception", "[ERROR] Parse de ServerTCPconnect " + e.toString());
            data.setJObj(JObjE);
            upperclass.update(444, data);
        } catch (NullPointerException | IOException e) {
            JObjE = new JSONObject();
            JObjE.put("exception", "[ERROR] NullPointer ou outro de ServerTCPconnect " + e.toString() + "\nLocal do ERRO:  " + e.toString());
            data.setJObj(JObjE);
            upperclass.update(444, data);
        }

        JObjE = new JSONObject();
        JObjE.put("output", "Desligando o Cliente.");
        data.setJObj(JObjE);
        upperclass.update(86, data);
        //Provisório para fechar aplicação quando o servidor for abaixo
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