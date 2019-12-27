package server.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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

    public ThreadClientListenTreatment(Socket Client, ServerLogic si) {
        this.Client = Client;
        this.si = si;
        this.ID = Counter++;
    }
    
    public void start() {
        new Thread(this).start();
    }
    
    @Override
    public void run() {
        //Tratamento de mensagens
        
        //boolean isRegister = false;
        //boolean isLogin = false;

        si.Obj().put("output", "Client " + this.ID + " connected to tcp.");
        si.notifyObserver(1);

        while(!Client.isClosed()){
            try {
                in = new InputStreamReader(Client.getInputStream()); // DUMMY CODE : modificar para enviar o q é preciso
                pr = new PrintWriter(Client.getOutputStream());
                //Recebe o que o Cliente enviou ao servidor
                synchronized(in){
                    //Reset de validações para os comandos
                    boolean isLogg = true;
                    boolean isRegg = true;
                    boolean isList = true;
                    //------------------------------------
                    
                    BufferedReader bf = new BufferedReader(in);

                    String str = bf.readLine();

                    JSONParser JsonParser = new JSONParser();
                    JSONObject JObj = (JSONObject) JsonParser.parse(str);
                    
                    String user = (String) JObj.get("Username");
                    String pass = (String) JObj.get("Password");
                    String cmd = (String) JObj.get("Command");
                    
                    //Código temporário de Registo
                    //if(!si.getDbaction().contaisUser(user)){
                    //    si.getDbaction().insertuser(user, user, pass, true);
                    //}
                    //----------------------------
                    
                    //if(isLogg && !isLogin){
                    if(isLogg){
                        switch(commandLogin(cmd)){
                            case 1:
                                si.Obj().put("output", "[ERROR] User " + user + " logado com sucesso.");
                                //isLogin = true;
                                break;
                            case -1:
                                si.Obj().put("output", "[ERROR] User " + user + " já se encontra online.");
                                break;
                            case -2:
                                //Quando o commando não é do tipo login
                                isLogg = false;
                                break;
                            default:
                                si.Obj().put("output", "[ERROR] User " + user + " introduziu dados incorretos.");
                                break;
                        }
                    }
                    else if(!isLogg && isRegg){
                        switch(commandRegisto(cmd)){
                            case 1:
                                si.Obj().put("output", "[ERROR] User " + user + " registado com sucesso.");
                                break;
                            case -1:
                                si.Obj().put("output", "[ERROR] User " + user + " já se encontra na base de dados.");
                                break;
                            default:
                                //Quando o commando não é do tipo registo
                                isRegg = false;
                                break;
                        }
                    }

                    if(isLogg){
                        si.Obj().put("output", "Objeto enviado com sucesso no login: " + JObj.toString());
                    }
                    else if(!isLogg && isRegg){
                        si.Obj().put("output", "Objeto registado com sucesso: " + JObj.toString());
                    }
                    else{
                        si.Obj().put("output", "Objeto enviado com erros de comando: " + JObj.toString());
                    }
                    
                    si.notifyObserver(1);

                    JSONObject obj = new JSONObject();

                    obj.put("Data", si.Obj().toString() + " Cliente de ID: " + this.ID);   //Para testes por agora//
                    obj.put("Mp3", "music");
                    pr.println(obj.toString());
                    pr.flush();
                    
                }
                //------------------------------------------
            } catch (IOException ex) {
                si.Obj().put("exception", "[ERROR] Erro no ciclo de tratamento de Mensagens do Cliente.\n" + ex.getMessage());
                si.notifyObserver(4);
            } catch (ParseException ex) {
                si.Obj().put("exception", "[ERROR] Erro na tradução do Json no tratamento de Mensagens do Cliente.\n" + ex.getMessage());
                si.notifyObserver(4);
            } catch (NullPointerException ex) {
                si.getSd().getObjMudance().put("exception", "[ERROR] Erro de Nullpointer. Provavelmente o Cliente se desconectou no tratamento de Mensagens do Cliente.\n" + ex.getMessage());
                si.notifyObserver(4);
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
        //remoção da Thread da base de dados do próprio servidor
        si.removeListenClient(this);

    }
    
    public int commandLogin(String command){
        boolean hasType = false;
        boolean hasUserName = false;
        boolean hasPassword = false;
        String username = "";
        String password = "";
        command = command.replace(" ", "");
        String [] zones = command.split(";");
        for(String zone : zones){
            String parte = zone.replace("|", " ");
            String [] cmd = parte.split(" ");
            //vai verificar se o comando é inicializado corretamente
            if(cmd[0].equalsIgnoreCase("tipo") && !hasType){
                //vai verificar se o tipo de comando é o correto
                if(cmd[1].equalsIgnoreCase("login")){
                    hasType = true;
                }
                else{
                    //o comando introduzido é de outro tipo
                    return -2;
                }
            }
            else if(cmd[0].equalsIgnoreCase("username") && !hasUserName && hasType){
                //vai verificar se o utilizador existe na base de dados
                username = cmd[1];
                hasUserName = si.getDbaction().contaisUser(username);
            }
            else if(cmd[0].equalsIgnoreCase("password") && !hasPassword && hasUserName && hasType){
                //vai verificar que a password introduzida cuincide com a introduzida para aquele username
                password = cmd[1];
                hasPassword = si.getDbaction().verifyUserPassword(username, password);
            }
            else {
                //caso exista mais linhas de comando para além desta ou o comando introduzido tenha sido mal escrito
                //ou seja de outro tipo
                return -2;
            }
        }
        
        if(hasPassword && hasType && hasUserName){
            //Verifica se o utilizador se encontra na base de dados como logado
            if(!si.getDbaction().contaisUserOnline(username)){
                //caso não esteja logado, é efetuado o seu login
                si.getDbaction().UserLogin(username);
                return 1;
            }
            else{
                return -1;
            }
        }
        else{
            if(!hasType){
                //caso o user não existe na base de dados
                return -2;
            }
            else{
                //caso o user não existe na base de dados
                return -3;
            }
            //return -3;
        }
    }
    
    public int commandRegisto(String command){
        boolean hasType = false;
        boolean hasUserName = false;
        boolean hasPassword = false;
        String username = "";
        String password = "";
        command = command.replace(" ", "");
        String [] zones = command.split(";");
        for(String zone : zones){
            String parte = zone.replace("|", " ");
            String [] cmd = parte.split(" ");
            //vai verificar se o comando é inicializado corretamente
            if(cmd[0].equalsIgnoreCase("tipo") && !hasType){
                //vai verificar se o tipo de comando é o correto
                if(cmd[1].equalsIgnoreCase("registo")){
                    hasType = true;
                }
                else{
                    //o comando introduzido é de outro tipo
                    return -2;
                }
            }
            else if(cmd[0].equalsIgnoreCase("username") && !hasUserName && hasType){
                //vai verificar se o utilizador ainda não existe na base de dados
                username = cmd[1];
                hasUserName = !si.getDbaction().contaisUser(username);
            }
            else if(cmd[0].equalsIgnoreCase("password") && hasUserName && hasType){
                //vai guardar a password do novo utilizador
                password = cmd[1];
                hasPassword = true;
            }
            else{
                //caso exista mais linhas de comando para além desta ou o comando introduzido tenha sido mal escrito
                //ou seja de outro tipo
                return -2;
            }
        }
        
        if(hasType && hasUserName && hasPassword){
            //user é inserido na base de dados e logado
            if(si.getDbaction().insertuser(username, username, password, true)){
                return 1;
            }
            else{
                return -3;
            }
        }
        else{
            if(!hasUserName){
                return -1;
            }
            else{
                return -2;
            }
        }
    }
    
}
