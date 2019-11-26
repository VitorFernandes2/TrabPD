package server.logic;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.json.simple.JSONObject;
import server.comunicationInterface.ComunicationInterface;

/**
 *
 * @author a21270909
 */
public class ThreadClientRequests implements Runnable {
    
    private ComunicationInterface si;
    private JSONObject Ob;
    private Socket s;
    private ThreadClientListenTreatment tclt;
    
    public ThreadClientRequests(ComunicationInterface si) {
        this.si = si;
        this.Ob = new JSONObject();
    }
    
    public synchronized void start() {
        new Thread(this).start();
    }
    
    @Override
    public void run() {
        
        try {
            try {
                si.setServer(new ServerSocket(si.getServerPort()));
                Ob.put("output", "TCP link started in port: " + si.getServerPort() + ".");
                si.setObjMudance(Ob);
                si.notifyObserver(1);
                
                while(!si.getServer().isClosed()){
                    s = si.getServer().accept();
                    si.addClients(s);
                    tclt = new ThreadClientListenTreatment(s, si);
                    si.addListners(tclt);
                    si.getListen(tclt).start();
                }

            } catch (IOException ex) {
                Ob.put("exception", "[ERROR] Não foi possivel criar o socket TCP ou Servidor forçado a parar.\n" + ex.getMessage());
                si.setObjMudance(Ob);
                si.notifyObserver(4);
                si.desconnectAllClients();
            }
            
            si.desconnectAllClients();
            
        } catch (IOException ex) {
            Ob.put("exception", "[ERROR] Não foi possivel desconectar todos os Clientes e/ou as suas Threads.\n" + ex.getMessage());
            si.setObjMudance(Ob);
            si.notifyObserver(4);
        }

    }
       
    
    public void stopthread() throws IOException{
        si.getServer().close();
    }
    
    
}
