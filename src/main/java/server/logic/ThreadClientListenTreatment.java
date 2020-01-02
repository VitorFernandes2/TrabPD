package server.logic;

import java.io.*;
import java.net.DatagramPacket;
import java.net.Socket;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.ServerLogic;

public class ThreadClientListenTreatment implements Runnable {

    private Socket Client;
    private final ServerLogic si;
    private static int Counter = 0;
    private int ID;
    private InputStreamReader in;
    private PrintWriter pr;
    private boolean Sucesso;
    private MulticastUDP multi;

    public ThreadClientListenTreatment(Socket Client, ServerLogic si, MulticastUDP multi) {
        this.Client = Client;
        this.si = si;
        this.ID = Counter++;
        this.multi = multi;
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        //Tratamento de mensagens
        String out = null;
        boolean log = false;
        boolean ped = false;

        si.Obj().put("output", "Client " + this.ID + " connected to tcp.");
        si.notifyObserver(1);

        //Enquanto n�o se fechar o client
        while(!Client.isClosed()){
            Sucesso = false;
            ped = false;
            try {
                in = new InputStreamReader(Client.getInputStream()); // DUMMY CODE : modificar para enviar o q � preciso
                pr = new PrintWriter(Client.getOutputStream());

                //Recebe o que o Cliente enviou ao servidor
                synchronized(in){

                    BufferedReader bf = new BufferedReader(in);

                    String str = bf.readLine();

                    JSONParser JsonParser = new JSONParser();
                    JSONObject JObj = (JSONObject) JsonParser.parse(str);

                    String cmd = (String) JObj.get("Command");

                    if (cmd.equals("createMusic")){

                        ped = true;

                        si.getSd().getObjMudance().put("output", "\nMandou a musica " + JObj.get("MusicName"));
                        si.notifyObserver(1);

                        String MusicName = (String) JObj.get("MusicName");
                        String MusicAuthor = (String) JObj.get("MusicAuthor");
                        String MusicYear = (String) JObj.get("MusicYear");
                        String MusicAlbum = (String) JObj.get("MusicAlbum");
                        double MusicDuration = (double) JObj.get("MusicDuration");
                        String MusicGenre = (String) JObj.get("MusicGenre");
                        String MusicPath = (String) JObj.get("MusicPath");
                        long fileSize = (long)JObj.get("size");

                        String fileName = si.getDbaction().insertMusic(MusicName, MusicAuthor, MusicAlbum,
                                MusicYear, MusicDuration, MusicGenre, si);

                        //Se puder criar o ficheiro inicia a thread de leitura
                        if (fileName != null){

                            //Envia Mensagem para enviar o ficheiro
                            JSONObject obj = new JSONObject();

                            obj.put("message", "sendFile");
                            pr.println(obj.toString());
                            pr.flush();

                            //Inicia a Thread para receber os ficheiros
                            ReadFileFromClient readFileFromClient =
                                    new ReadFileFromClient(Client.getInputStream(), Client.getOutputStream()
                                            , fileName, fileSize);
                            readFileFromClient.start();
                            readFileFromClient.join();
                            System.out.printf("Sai");

                        }
                        else{

                            //Envia Mensagem para enviar o ficheiro
                            JSONObject obj = new JSONObject();

                            obj.put("message", "fileDenied");
                            pr.println(obj.toString());
                            pr.flush();

                        }

                    }
                    else if (cmd.equals("playMusic")){

                        ped = true;

                        String name = (String)JObj.get("name");
                        String author = (String)JObj.get("author");

                        String path = si.getDbaction().getFileName(name, author, si);
                        System.out.println(path);

                        JSONObject obj = new JSONObject();
                        obj.put("message", "receiveFile");
                        File file = new File(path);
                        obj.put("size", file.length());

                        pr.println(obj.toString());
                        pr.flush();

                        SendFileToClient sendFileToClient =
                                new SendFileToClient(path, Client.getOutputStream());
                        sendFileToClient.start();

                    }
                    else if(cmd.equals("changeMusic")){

                        ped = true;

                        String name = (String)JObj.get("name");
                        String author = (String)JObj.get("author");

                        String MusicName = (String) JObj.get("MusicName");
                        String MusicAuthor = (String) JObj.get("MusicAuthor");
                        String MusicYear = (String) JObj.get("MusicYear");
                        String MusicAlbum = (String) JObj.get("MusicAlbum");
                        double MusicDuration = (double) JObj.get("MusicDuration");
                        String MusicGenre = (String) JObj.get("MusicGenre");
                        String MusicPath = (String) JObj.get("MusicPath");

                        //Sucesso na altera��o
                        if (si.getDbaction().changeMusic(name, author, MusicName, MusicAuthor,
                                MusicAlbum, MusicYear, MusicDuration, MusicGenre, si)){

                            JSONObject obj = new JSONObject();
                            obj.put("message", "changeDone");

                            pr.println(obj.toString());
                            pr.flush();

                        } //Se n�o teve sucesso
                        else{

                            JSONObject obj = new JSONObject();
                            obj.put("message", "changeNotDone");

                            pr.println(obj.toString());
                            pr.flush();

                        }

                    }
                    else if(cmd.equals("removeMusic")){

                        ped = true;

                        String name = (String)JObj.get("name");
                        String author = (String)JObj.get("author");

                        if (si.getDbaction().removeMusic(name, author, si)){

                            JSONObject obj = new JSONObject();
                            obj.put("message", "removeDone");

                            pr.println(obj.toString());
                            pr.flush();

                        }
                        else {

                            JSONObject obj = new JSONObject();
                            obj.put("message", "removeNotDone");

                            pr.println(obj.toString());
                            pr.flush();

                        }

                    }
                    else if(cmd.equals("listMusics")){

                        ped = true;

                        JSONObject obj = new JSONObject();
                        obj = si.getDbaction().listMusics();

                        if (obj != null){

                            pr.println(obj.toString());
                            pr.flush();

                        }
                        else {

                            obj.put("message", "noMusicList");
                            pr.println(obj.toString());
                            pr.flush();

                        }

                    }
                    else if(cmd.equals("listPlaylist")){

                        ped = true;

                        JSONObject obj = new JSONObject();
                        String username = (String)JObj.get("username");
                        obj = si.getDbaction().listPlaylist(username, si);

                        if (obj != null){

                            pr.println(obj.toString());
                            pr.flush();

                        }
                        else {

                            obj.put("message", "noPlaylistList");
                            pr.println(obj.toString());
                            pr.flush();

                        }

                    }
                    else if(cmd.equals("changePlaylist")){

                        String username = (String)JObj.get("username");
                        String oldName = (String)JObj.get("name");
                        String newName = (String)JObj.get("newName");
                        si.getDbaction().changePlaylist(oldName,newName,username, si);

                    }
                    else if(cmd.equals("removePlaylist")){

                        String username = (String)JObj.get("username");
                        String name = (String)JObj.get("name");
                        si.getDbaction().removePlaylist(name,username,si);

                    }
                    else if(cmd.equals("createPlaylist")){

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

                        si.getDbaction().createPlaylist(name, username, musics, autors, si);

                    }
                    else if(cmd.equals("playPlaylist")){

                        ped = true;

                        String username = (String)JObj.get("username");
                        String nome = (String)JObj.get("nome");

                        ArrayList<String> list = si.getDbaction().playPlaylist(username, nome, si);

                        //Tem dados para reproduzir
                        if (list != null && list.size() > 0){

                            SendPlaylistToClient t = new SendPlaylistToClient(pr, Client.getOutputStream(), list);
                            t.start();
                            t.join();

                        }
                        else {

                            JSONObject obj = new JSONObject();
                            obj.put("message", "noPlaylistMusics");
                            pr.println(obj.toString());
                            pr.flush();

                        }

                    }
                    else{
                        //Tratamento do login/registo/logout
                        out = commandParse(cmd);
                        si.Obj().put("output", out);
                        log = true;
                        //----------------------------------
                    }

                    //Codigo de envio para os outros servidores
                    // mesmo que o comando nao seja um sucesso
                    // neste servidor os dados dele podem ser diferentes
                    // por isso enviados para o para todos
                    if(log){
                        byte[] b = String.valueOf(cmd).getBytes();
                        DatagramPacket packet = new DatagramPacket(b, b.length, multi.getGroup(), 3456);
                        multi.getMulticastSock().send(packet);
                        log = false;
                    }
                    else{
                        byte[] b = String.valueOf(JObj.toString()).getBytes();
                        DatagramPacket packet = new DatagramPacket(b, b.length, multi.getGroup(), 3456);
                        multi.getMulticastSock().send(packet);
                    }
                    //-----------------------------------------

                    si.notifyObserver(1);

                    if(!ped){
                        JSONObject obj = new JSONObject();

                        obj.put("tipo", "resposta");
                        obj.put("sucesso", Sucesso);
                        obj.put("msg", out + " do user " + " Cliente de ID: " + this.ID);
                        pr.println(obj.toString());
                        pr.flush();
                    }

                }
                //------------------------------------------
            } catch (IOException ex) {
                si.Obj().put("exception", "[ERROR] Erro no ciclo de tratamento de Mensagens do Cliente.\n" + ex.getMessage());
                si.notifyObserver(4);
                try {
                    si.desconnetClient(Client);
                } catch (IOException ex2) {
                    si.Obj().put("exception", "[ERROR] Erro ao tentar desconectar o Cliente.\n" + ex2.getMessage());
                    si.notifyObserver(4);
                }
                si.removeClient(Client);
                si.removeListenClient(this);
                return;
            } catch (ParseException ex) {
                si.Obj().put("exception", "[ERROR] Erro na tradu��o do Json no tratamento de Mensagens do Cliente.\n" + ex.getMessage());
                si.notifyObserver(4);
                try {
                    si.desconnetClient(Client);
                } catch (IOException ex2) {
                    si.Obj().put("exception", "[ERROR] Erro ao tentar desconectar o Cliente.\n" + ex2.getMessage());
                    si.notifyObserver(4);
                }
                si.removeClient(Client);
                si.removeListenClient(this);
                return;
            } catch (NullPointerException ex) {
                si.getSd().getObjMudance().put("exception", "[ERROR] Erro de Nullpointer. Provavelmente o Cliente se desconectou no tratamento de Mensagens do Cliente.\n" + ex.getMessage());
                si.notifyObserver(4);
                try {
                    si.desconnetClient(Client);
                } catch (IOException ex2) {
                    si.Obj().put("exception", "[ERROR] Erro ao tentar desconectar o Cliente.\n" + ex2.getMessage());
                    si.notifyObserver(4);
                }
                si.removeClient(Client);
                si.removeListenClient(this);
                return;
            } catch (InterruptedException ex) {
                si.getSd().getObjMudance().put("exception", "[ERROR] Erro de InterruptedException. Provavelmente o Cliente foi interrumpido.\n" + ex.getMessage());
                si.notifyObserver(4);
                try {
                    si.desconnetClient(Client);
                } catch (IOException ex2) {
                    si.Obj().put("exception", "[ERROR] Erro ao tentar desconectar o Cliente.\n" + ex2.getMessage());
                    si.notifyObserver(4);
                }
                si.removeClient(Client);
                si.removeListenClient(this);
                return;
            }

        }

        try {
            si.desconnetClient(Client);
        } catch (IOException ex) {
            si.Obj().put("exception", "[ERROR] Erro ao tentar desconectar o Cliente.\n" + ex.getMessage());
            si.notifyObserver(4);
        }

        si.removeClient(Client);
        //remocao da Thread da base de dados do pr�prio servidor
        si.removeListenClient(this);

    }

    public String commandParse(String command){
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
            //vai verificar se o comando � inicializado corretamente
            if(cmd[0].equalsIgnoreCase("tipo") && !hasTypeLog && !hasTypeReg && !hasTypeOut){
                //vai verificar se o tipo de comando � o correto
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
                    Sucesso = false;
                    return "Erro de comando";
                }
            }
            else if(cmd[0].equalsIgnoreCase("name") && !hasNameReg){
                if(cmd[1] == null || cmd[1].length() == 0){
                    Sucesso = false;
                    return "Sem Name defenido";
                }
                //vai verificar se o nome de utilizador existe na base de dados
                name = cmd[1];
                if(hasTypeReg){
                    hasNameReg = !si.getDbaction().contaisName(name, si);
                }
                else{
                    Sucesso = false;
                    return "Erro de comando";
                }
            }
            else if(cmd[0].equalsIgnoreCase("username") && !hasUserNameLog && !hasUserNameReg && !hasUserNameOut){
                if(cmd[1] == null || cmd[1].length() == 0){
                    Sucesso = false;
                    return "Sem Username defenido";
                }
                //vai verificar se o utilizador existe na base de dados
                username = cmd[1];
                if(hasTypeLog){
                    hasUserNameLog = si.getDbaction().contaisUser(username, si);
                }
                else if(hasTypeReg){
                    hasUserNameReg = !si.getDbaction().contaisUser(username, si);
                }
                else if(hasTypeOut){
                    hasUserNameOut = si.getDbaction().contaisUser(username, si);
                }
                else{
                    Sucesso = false;
                    return "Erro de comando";
                }
            }
            else if(cmd[0].equalsIgnoreCase("password") && !hasPasswordLog && !hasPasswordReg && !hasPasswordOut){
                if(cmd[1] == null || cmd[1].length() == 0){
                    Sucesso = false;
                    return "Sem Password defenida";
                }
                //vai verificar que a password introduzida cuincide com a introduzida para aquele username
                password = cmd[1];
                if(hasTypeLog && hasUserNameLog){
                    hasPasswordLog = si.getDbaction().verifyUserPassword(username, password, si);
                }
                else if(hasTypeReg && hasUserNameReg){
                    hasPasswordReg = true;
                }
                else if(hasTypeOut && hasUserNameOut){
                    hasPasswordOut = si.getDbaction().verifyUserPassword(username, password, si);
                }
                else{
                    Sucesso = false;
                    return "Erro de comando";
                }
            }
            else {
                //caso exista mais linhas de comando para al�m desta ou o comando introduzido tenha sido mal escrito
                //ou seja de outro tipo
                Sucesso = false;
                return "Erro de comando";
            }
        }

        if(hasPasswordLog && hasTypeLog && hasUserNameLog){
            //Verifica se o utilizador se encontra nos dados do servidor como logado
            if(si.getClientsLogs().contains(username.trim())){
                Sucesso = false;
                return "Login sem sucesso";
            }
            else{
                Sucesso = true;
                si.addClientsLogs(username.trim());
                return "Login com sucesso";
            }
        }
        else if(hasPasswordReg && hasTypeReg && hasUserNameReg && hasNameReg){
            //Verifica se o utilizador � registado de forma correta
            if(si.getDbaction().insertuser(name, username, password, si)){
                Sucesso = true;
                return "Registo com sucesso";
            }
            else{
                Sucesso = false;
                return "Registo sem sucesso";
            }
        }
        else if(hasPasswordOut && hasTypeOut && hasUserNameOut){
            //Verifica se o utilizador se encontra nos dados do servidor como logado
            // e retira-o
            if(si.getClientsLogs().contains(username.trim())){
                Sucesso = true;
                si.removeClientsLogs(username.trim());
                return "Logout com sucesso";
            }
            else{
                Sucesso = false;
                return "Logout sem sucesso";
            }
        }
        else{
            Sucesso = false;
            return "Erro total de comando";
        }
    }

}
