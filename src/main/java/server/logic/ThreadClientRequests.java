package server.logic;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import server.comunicationInterface.ComunicationInterface;

/**
 *
 * @author a21270909
 */
public class ThreadClientRequests implements Runnable {
    
    private ComunicationInterface si;
    private Socket s;
    private ThreadClientListenTreatment tclt;
    
    public ThreadClientRequests(ComunicationInterface si) {
        this.si = si;
    }
    
    public synchronized void start() {
        new Thread(this).start();
    }
    
    @Override
    public void run() {
        
        try {
            try {
                si.getSd().setServer(new ServerSocket(si.getSd().getServerPort()));
                si.getSd().getObjMudance().put("output", "TCP link started in port: " + si.getSd().getServerPort() + ".");
                si.update(1, si.getSd().getObjMudance());
                
                while(!si.getSd().getServer().isClosed()){
                    s = si.getSd().getServer().accept();
                    si.getSd().addClients(s);
                    tclt = new ThreadClientListenTreatment(s, si);
                    si.getSd().addListners(tclt);
                    si.getSd().getListen(tclt).start();
                }

            } catch (IOException ex) {
                si.getSd().getObjMudance().put("exception", "[ERROR] Não foi possivel criar o socket TCP ou Servidor forçado a parar.\n" + ex.getMessage());
                si.update(4, si.getSd().getObjMudance());
                si.getSd().desconnetAllClients();
            }
            
            si.getSd().desconnetAllClients();
            
        } catch (IOException ex) {
            si.getSd().getObjMudance().put("exception", "[ERROR] Não foi possivel desconectar todos os Clientes e/ou as suas Threads.\n" + ex.getMessage());
            si.update(4, si.getSd().getObjMudance());
        }

    }
       
    
    public void stopthread() throws IOException{
        si.getSd().getServer().close();
    }
    
    
}
