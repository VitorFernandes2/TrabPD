package server.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
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
        
        //boolean isRegister = false;
        //boolean isLogin = false;
        
        Sucesso = false;
        
        String out = null;

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
                    
                    out = commandParse(cmd);
                    si.Obj().put("output", out);
                    
                    //Código de envio para os outros servidores
                    if(Sucesso){
                        byte[] b = String.valueOf(cmd).getBytes();
                        DatagramPacket packet = new DatagramPacket(b, b.length, multi.getGroup(), 3456);
                        multi.getMulticastSock().send(packet);
                    }
                    //-----------------------------------------
                    
                    si.notifyObserver(1);

                    JSONObject obj = new JSONObject();

                    obj.put("tipo", "resposta");
                    obj.put("sucesso", Sucesso);
                    obj.put("msg", out + " do user " + " Cliente de ID: " + this.ID);
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
    
    public String commandParse(String command){
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
                    Sucesso = false;
                    return "erro de comando";
                }
            }
            else if(cmd[0].equalsIgnoreCase("username") && !hasUserNameLog && !hasUserNameReg && !hasUserNameOut){
                //vai verificar se o utilizador existe na base de dados
                username = cmd[1];
                if(hasTypeLog){
                    hasUserNameLog = si.getDbaction().contaisUser(username);
                }
                else if(hasTypeReg){
                    hasUserNameReg = !si.getDbaction().contaisUser(username);
                }
                else if(hasTypeOut){
                    hasUserNameOut = si.getDbaction().contaisUser(username);
                }
                else{
                    Sucesso = false;
                    return "erro de comando";
                }
            }
            else if(cmd[0].equalsIgnoreCase("password") && !hasPasswordLog && !hasPasswordReg && !hasPasswordOut){
                //vai verificar que a password introduzida cuincide com a introduzida para aquele username
                password = cmd[1];
                if(hasTypeLog && hasUserNameLog){
                    hasPasswordLog = si.getDbaction().verifyUserPassword(username, password);
                }
                else if(hasTypeReg && hasUserNameReg){
                    hasPasswordReg = true;
                }
                else if(hasTypeOut && hasUserNameOut){
                    hasPasswordOut = si.getDbaction().verifyUserPassword(username, password);
                }
                else{
                    Sucesso = false;
                    return "erro de comando";
                }
            }
            else {
                //caso exista mais linhas de comando para além desta ou o comando introduzido tenha sido mal escrito
                //ou seja de outro tipo
                Sucesso = false;
                return "erro de comando";
            }
        }
        
        if(hasPasswordLog && hasTypeLog && hasUserNameLog){
            //Verifica se o utilizador se encontra na base de dados como logado
            Sucesso = true;
            return "Login com sucesso";
        }
        else if(hasPasswordReg && hasTypeReg && hasUserNameReg){
            Sucesso = true;
            si.getDbaction().insertuser(username, username, password);
            return "Registo com sucesso";
        }
        else if(hasPasswordOut && hasTypeOut && hasUserNameOut){
            Sucesso = true;
            si.getDbaction().removeuser(username, password);
            return "Registo com sucesso";
        }
        else{
            Sucesso = false;
            return "erro de comando";
        }
    }
    
    
}
