package server.logic;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import server.ServerLogic;

/**
 *
 * @author a21270909
 */
public class ThreadClientRequests implements Runnable {
    
    private ServerLogic si;
    private Socket s;
    private ThreadClientListenTreatment tclt;
    
    public ThreadClientRequests(ServerLogic si) {
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
                si.getOb().put("output", "TCP link started in port: " + si.getSd().getServerPort() + ".");
                si.notifyObserver(1);
                
                while(!si.getSd().getServer().isClosed()){
                    s = si.getSd().getServer().accept();
                    si.getSd().addClients(s);
                    tclt = new ThreadClientListenTreatment(s, si);
                    si.getSd().addListners(tclt);
                    si.getSd().getListen(tclt).start();
                }

            } catch (IOException ex) {
                si.getOb().put("exception", "[ERROR] Não foi possivel criar o socket TCP ou Servidor forçado a parar.\n" + ex.getMessage());
                si.notifyObserver(4);
                si.getSd().desconnetAllClients();
            }
            
            si.getSd().desconnetAllClients();
            
        } catch (IOException ex) {
            si.getOb().put("exception", "[ERROR] Não foi possivel desconectar todos os Clientes e/ou as suas Threads.\n" + ex.getMessage());
            si.notifyObserver(4);
        }

    }
       
    
    public void stopthread() throws IOException{
        si.getSd().getServer().close();
    }
    
    
}
