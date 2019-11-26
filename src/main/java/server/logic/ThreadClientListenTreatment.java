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

/**
 *
 * @author Luís António Moreira Ferreira da Silva
 */
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

        si.getOb().put("output", "Client " + this.ID + " connected to tcp.");
        si.notifyObserver(1);

        while(!Client.isClosed()){
            try {
                in = new InputStreamReader(Client.getInputStream()); // DUMMY CODE : modificar para enviar o q é preciso
                pr = new PrintWriter(Client.getOutputStream());
                
                synchronized(in){
                    BufferedReader bf = new BufferedReader(in);

                    String str = bf.readLine();

                    JSONParser JsonParser = new JSONParser();
                    JSONObject JObj = (JSONObject) JsonParser.parse(str);

                    si.getOb().put("output", JObj.toString());
                    si.notifyObserver(1);

                    JSONObject obj = new JSONObject();

                    obj.put("Data", JObj.toString() + " Cliente de ID: " + this.ID);   //Para testes por agora//
                    obj.put("Mp3", "music");


                    pr.println(obj.toString());
                    pr.flush();
                }

            } catch (IOException ex) {
                si.getOb().put("exception", "[ERROR] Erro no ciclo de tratamento de Mensagens do Cliente.\n" + ex.getMessage());
                si.notifyObserver(4);
            } catch (ParseException ex) {
                si.getOb().put("exception", "[ERROR] Erro na tradução do Json no tratamento de Mensagens do Cliente.\n" + ex.getMessage());
                si.notifyObserver(4);
            } catch (NullPointerException ex) {
                si.getOb().put("exception", "[ERROR] Erro de Nullpointer. Provavelmente o Cliente se desconectou no tratamento de Mensagens do Cliente.\n" + ex.getMessage());
                si.notifyObserver(4);
                si.getSd().removeClient(Client);
                si.getSd().removeListenClient(this);
                return;
            }

        }

        try {
            si.getSd().desconnetClient(Client);
        } catch (IOException ex) {
            si.getOb().put("exception", "[ERROR] Erro ao tentar desconectar o Cliente.\n" + ex.getMessage());
            si.notifyObserver(4);
        }
        
        si.getSd().removeClient(Client);
        //remoção da Thread da base de dados do próprio servidor
        si.getSd().removeListenClient(this);

    }
    
}
