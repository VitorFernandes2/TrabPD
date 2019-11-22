package server.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.graphicInterface.ServerIterface;

/**
 *
 * @author a21270909
 */
public class ThreadClientRequests implements Runnable {

    private boolean runstatus = true;
    
    private ServerIterface si;

    //para prevenção de erro no temp da main
    public ThreadClientRequests() {
        
    }
    
    public ThreadClientRequests(ServerIterface si) {
        this.si = si;
    }
    
    public synchronized void start() {
        new Thread(this).start();
    }
    
    @Override
    public void run() {

        ServerSocket ss = null; 
        Socket s = null;
        
        try {
            ss = new ServerSocket(9998);
            s = ss.accept(); // é ma pratica meter um accept dentro de um try??
        } catch (IOException ex) {
            JSONObject Ob = new JSONObject();
            Ob.put("exception", "[ERROR] Não foi possivel criar o socket.\n" + ex.getMessage());
            si.setObjMudance(Ob);
            si.notifyObserver(4);
            runstatus = false;
            return;
        }
        
        while(runstatus){
            
            //System.out.println("client connected to tcp");
            JSONObject Ob = new JSONObject();
            Ob.put("output", "Client connected to tcp.");
            si.setObjMudance(Ob);
            si.notifyObserver(1);

            InputStreamReader in = null;

            try {
                in = new InputStreamReader(s.getInputStream()); // DUMMY CODE : modificar para enviar o q é preciso

                BufferedReader bf = new BufferedReader(in);

                String str = bf.readLine();

                JSONParser JsonParser = new JSONParser();
                JSONObject JObj = (JSONObject) JsonParser.parse(str);

                //System.out.println(JObj.toString());
                Ob.put("output", JObj.toString());
                si.setObjMudance(Ob);
                si.notifyObserver(1);

                JSONObject obj = new JSONObject();
                
                obj.put("Data", "xxx");
                obj.put("Mp3", "music");

                PrintWriter pr = new PrintWriter(s.getOutputStream());
                pr.println(obj.toString());
                pr.flush();

            } catch (IOException ex) {
                Ob.put("exception", "[ERROR] Erro no ciclo.\n" + ex.getMessage());
                si.setObjMudance(Ob);
                si.notifyObserver(4);
                runstatus = false;
                return;
            } catch (ParseException ex) {
                Ob.put("exception", "[ERROR] Erro na tradução do Json.\n" + ex.getMessage());
                si.setObjMudance(Ob);
                si.notifyObserver(4);
                runstatus = false;
                return;
            } catch (NullPointerException ex) {
                Ob.put("exception", "[ERROR] Erro de Nullpointer. Provavelmente o client se desconectou.\n" + ex.getMessage());
                si.setObjMudance(Ob);
                si.notifyObserver(4);
                runstatus = false;
                return;
            }

        }
        
    }
       
    
    public void stopthread(){ // pode ser ma pratica
        runstatus = false;
    }
    
    
}
