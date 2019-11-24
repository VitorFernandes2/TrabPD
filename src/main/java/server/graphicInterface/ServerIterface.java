package server.graphicInterface;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import org.json.simple.JSONObject;
import server.interfaces.subjectServer;
import server.logic.ServerMiddleLayer;
import server.logic.ThreadClientListenTreatment;

/**
 *
 * @author Luís António Moreira Ferreira da Silva
 */
public class ServerIterface implements subjectServer{

    private ServerMiddleLayer sml;
    
    public void connect(){
        
        this.notifyObserver(2);
        
        JSONObject Ob = new JSONObject();
        Ob.put("output", "Server a correr.");
        setObjMudance(Ob);
        this.notifyObserver(1);
        
        Ob.put("ServerIterface", this);
        setObjMudance(Ob);
        this.notifyObserver(3);
        
    }
    
    public int getServerPort(){
        return sml.getSd().getServerPort();
    }
    
    public ThreadClientListenTreatment getListen(ThreadClientListenTreatment Listen){
        return sml.getSd().getListen(Listen);
    }
    
    public void addListners(ThreadClientListenTreatment Listen){
        sml.getSd().addListners(Listen);
    }
    
    public boolean removeListenClient(ThreadClientListenTreatment Listen) {
        return sml.getSd().removeListenClient(Listen);
    }
    
    public void desconnetAllClientsAndClientListeners() throws IOException{
        sml.getSd().desconnetAllClientsAndClientListeners();
    }
    
    public void desconnetClient(Socket Client) throws IOException{
        sml.getSd().desconnetClient(Client);
    }
    
    public void desconnectAllClients() throws IOException{
        sml.getSd().desconnetAllClients();
    }
    
    public boolean removeClient(Socket Client) {
        return sml.getSd().removeClient(Client);
    }
    
    public String getDsIP() {
        return sml.getSd().getDsIP();
    }

    public void setDsIP(String DsIP) {
        sml.getSd().setDsIP(DsIP);
    }

    public String getDsPort() {
        return sml.getSd().getDsPort();
    }

    public void setDsPort(String DsPort) {
        sml.getSd().setDsPort(DsPort);
    }

    public JSONObject getObjMudance() {
        return sml.getSd().getObjMudance();
    }

    public void setObjMudance(JSONObject ObjMudance) {
        sml.getSd().setObjMudance(ObjMudance);
    }
    
    public void addClients(Socket Client){
        sml.getSd().addClients(Client);
    }

    public List<Socket> getClients() {
        return sml.getSd().getClients();
    }

    public ServerSocket getServer() {
        return sml.getSd().getServer();
    }

    public void setServer(ServerSocket Server) {
        sml.getSd().setServer(Server);
    }

    @Override
    public void notifyObserver(int acao) {
        sml.update(acao);
    }

    @Override
    public void addinterface(ServerMiddleLayer sml) {
        this.sml = sml;
    }
    
}
