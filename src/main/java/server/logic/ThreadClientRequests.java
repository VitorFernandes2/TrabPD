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
    private MulticastUDP multi;
    
    public ThreadClientRequests(ServerLogic si, MulticastUDP multi) {
        this.si = si;
        this.multi = multi;
    }
    
    public synchronized void start() {
        new Thread(this).start();
    }
    
    @Override
    public void run() {
        
        try {
            try {
                // set do valor do socket TCP de comunicao deste Servidor
                si.setServer(new ServerSocket(si.getServerPort()));
                //---------------------------------------------------------
                
                // notify visual do inicio da ligao TCP e o seu porto
                si.Obj().put("output", "TCP link started in port: " + si.getServerPort() + ".");
                si.notifyObserver(1);
                //-----------------------------------------------------
                
                while(!si.getServer().isClosed() && multi.isCorre()){
                    s = si.getServer().accept();
                    si.addClients(s);
                    tclt = new ThreadClientListenTreatment(s, si, multi);
                    si.addListners(tclt);
                    si.getListen(tclt).start();
                }

            } catch (IOException ex) {
                si.Obj().put("exception", "[ERROR] No foi possivel criar o socket TCP ou Servidor forcado a parar. " + ex.getMessage());
                si.notifyObserver(4);
                si.desconnetAllClients();
                return;
            }
            
            si.desconnetAllClients();
            
        } catch (IOException ex) {
            si.Obj().put("exception", "[ERROR] No foi possivel desconectar todos os Clientes e/ou as suas Threads. " + ex.getMessage());
            si.notifyObserver(4);
        }

    }
       
    
    public void stopthread() throws IOException{
        si.getServer().close();
    }
    
    
}
