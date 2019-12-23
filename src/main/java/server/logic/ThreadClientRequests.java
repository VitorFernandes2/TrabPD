package server.logic;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import server.ServerLogic;
import server.comunicationInterface.ComunicationInterface;

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
                // set do valor do socket TCP de comunicação deste Servidor
                si.setServer(new ServerSocket(si.getServerPort()));
                //---------------------------------------------------------
                
                // notify visual do inicio da ligação TCP e o seu porto
                si.Obj().put("output", "TCP link started in port: " + si.getServerPort() + ".");
                si.notifyObserver(1);
                //-----------------------------------------------------
                
                while(!si.getServer().isClosed()){
                    s = si.getServer().accept();
                    si.addClients(s);
                    tclt = new ThreadClientListenTreatment(s, si);
                    si.addListners(tclt);
                    si.getListen(tclt).start();
                }

            } catch (IOException ex) {
                si.Obj().put("exception", "[ERROR] Não foi possivel criar o socket TCP ou Servidor forçado a parar.\n" + ex.getMessage());
                si.notifyObserver(4);
                si.desconnetAllClients();
            }
            
            si.desconnetAllClients();
            
        } catch (IOException ex) {
            si.Obj().put("exception", "[ERROR] Não foi possivel desconectar todos os Clientes e/ou as suas Threads.\n" + ex.getMessage());
            si.notifyObserver(4);
        }

    }
       
    
    public void stopthread() throws IOException{
        si.getServer().close();
    }
    
    
}
