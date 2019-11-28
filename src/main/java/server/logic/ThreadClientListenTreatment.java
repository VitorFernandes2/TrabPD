package server.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.comunicationInterface.ComunicationInterface;

public class ThreadClientListenTreatment implements Runnable {

    private Socket Client;
    private final ComunicationInterface si;
    private static int Counter = 0;
    private int ID;
    private InputStreamReader in;
    private PrintWriter pr;

    public ThreadClientListenTreatment(Socket Client, ComunicationInterface si) {
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

        si.getSd().getObjMudance().put("output", "Client " + this.ID + " connected to tcp.");
        si.update(1, si.getSd().getObjMudance());

        while(!Client.isClosed()){
            try {
                in = new InputStreamReader(Client.getInputStream()); // DUMMY CODE : modificar para enviar o q � preciso
                pr = new PrintWriter(Client.getOutputStream());
                
                synchronized(in){
                    BufferedReader bf = new BufferedReader(in);

                    String str = bf.readLine();

                    JSONParser JsonParser = new JSONParser();
                    JSONObject JObj = (JSONObject) JsonParser.parse(str);

                    si.getSd().getObjMudance().put("output", JObj.toString());
                    si.update(1, si.getSd().getObjMudance());

                    JSONObject obj = new JSONObject();

                    obj.put("Data", JObj.toString() + " Cliente de ID: " + this.ID);   //Para testes por agora//
                    obj.put("Mp3", "music");


                    pr.println(obj.toString());
                    pr.flush();
                }

            } catch (IOException ex) {
                si.getSd().getObjMudance().put("exception", "[ERROR] Erro no ciclo de tratamento de Mensagens do Cliente.\n" + ex.getMessage());
                si.update(4, si.getSd().getObjMudance());
            } catch (ParseException ex) {
                si.getSd().getObjMudance().put("exception", "[ERROR] Erro na tradu��o do Json no tratamento de Mensagens do Cliente.\n" + ex.getMessage());
                si.update(4, si.getSd().getObjMudance());
            } catch (NullPointerException ex) {
                si.getSd().getObjMudance().put("exception", "[ERROR] Erro de Nullpointer. Provavelmente o Cliente se desconectou no tratamento de Mensagens do Cliente.\n" + ex.getMessage());
                si.update(4, si.getSd().getObjMudance());
                si.getSd().removeClient(Client);
                si.getSd().removeListenClient(this);
                return;
            }

        }

        try {
            si.getSd().desconnetClient(Client);
        } catch (IOException ex) {
            si.getSd().getObjMudance().put("exception", "[ERROR] Erro ao tentar desconectar o Cliente.\n" + ex.getMessage());
            si.update(4, si.getSd().getObjMudance());
        }
        
        si.getSd().removeClient(Client);
        //remo��o da Thread da base de dados do pr�prio servidor
        si.getSd().removeListenClient(this);

    }
    
}
