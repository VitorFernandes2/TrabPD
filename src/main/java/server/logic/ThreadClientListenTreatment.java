package server.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.graphicInterface.ServerIterface;

/**
 *
 * @author Luís António Moreira Ferreira da Silva
 */
public class ThreadClientListenTreatment implements Runnable {

    private Socket Client;
    private final ServerIterface si;
    private JSONObject Ob;
    private static int Counter = 0;
    private int ID;
    private InputStreamReader in;
    private PrintWriter pr;

    public ThreadClientListenTreatment(Socket Client, ServerIterface si) {
        this.Client = Client;
        this.si = si;
        this.Ob = new JSONObject();
        this.ID = Counter++;
    }
    
    public void start() {
        new Thread(this).start();
    }
    
    @Override
    public void run() {

        //Tratamento de mensagens

        Ob.put("output", "Client " + this.ID + " connected to tcp.");
        si.setObjMudance(Ob);
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

                    Ob.put("output", JObj.toString());
                    si.setObjMudance(Ob);
                    si.notifyObserver(1);

                    JSONObject obj = new JSONObject();

                    obj.put("Data", JObj.toString() + " Cliente de ID: " + this.ID);   //Para testes por agora//
                    obj.put("Mp3", "music");


                    pr.println(obj.toString());
                    pr.flush();
                }

            } catch (IOException ex) {
                Ob.put("exception", "[ERROR] Erro no ciclo de tratamento de Mensagens do Cliente.\n" + ex.getMessage());
                si.setObjMudance(Ob);
                si.notifyObserver(4);
            } catch (ParseException ex) {
                Ob.put("exception", "[ERROR] Erro na tradução do Json no tratamento de Mensagens do Cliente.\n" + ex.getMessage());
                si.setObjMudance(Ob);
                si.notifyObserver(4);
            } catch (NullPointerException ex) {
                Ob.put("exception", "[ERROR] Erro de Nullpointer. Provavelmente o Cliente se desconectou no tratamento de Mensagens do Cliente.\n" + ex.getMessage());
                si.setObjMudance(Ob);
                si.notifyObserver(4);
                si.removeClient(Client);
                si.removeListenClient(this);
                return;
            }

        }

        try {
            si.desconnetClient(Client);
        } catch (IOException ex) {
            Ob.put("exception", "[ERROR] Erro ao tentar desconectar o Cliente.\n" + ex.getMessage());
            si.setObjMudance(Ob);
            si.notifyObserver(4);
        }
        
        si.removeClient(Client);
        //remoção da Thread da base de dados do próprio servidor
        si.removeListenClient(this);

    }
    
}
