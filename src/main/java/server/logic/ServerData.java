package server.logic;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;

/**
 *
 * @author Lu�s Ant�nio Moreira Ferreira da Silva
 */
public class ServerData {

    private String DsIP;
    private String DsPort;
    private JSONObject ObjMudance;
    private ServerSocket Server;
    private List<Socket> Clients;
    private List<ThreadClientListenTreatment> Servers;

    public ServerData(String DsIP, String DsPort) {
        this.DsIP = DsIP;
        try {
            this.DsIP = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.DsPort = DsPort;
        this.Clients = new ArrayList<>();
        this.Servers = new ArrayList<>();
    }
    
    public ThreadClientListenTreatment getListen(ThreadClientListenTreatment Listen){
        return Servers.get(Servers.indexOf(Listen));
    }

    public void addListners(ThreadClientListenTreatment Listen){
        Servers.add(Listen);
    }
    
    public boolean removeListenClient(ThreadClientListenTreatment Listen) {
        return Servers.remove(Listen);
    }
    
    public void desconnetAllClientsAndClientListeners() throws IOException {
        desconnetAllClients();
        removeAllClients();
        for(ThreadClientListenTreatment Listen : Servers){
            removeListenClient(Listen);
        }
    }
    
    public List<ThreadClientListenTreatment> getServers() {
        return Servers;
    }
    
    public boolean removeClient(Socket Client) {
//        //vou colocar aqui j� a desligar//
//        Clients.get(Clients.indexOf(Client)).close();
//        //------------------------------//
        return Clients.remove(Client);
    }
    
    public void removeAllClients() {
        Clients.clear();
    }
    
    public void desconnetClient(Socket Client) throws IOException{
        Clients.get(Clients.indexOf(Client)).close();
    }
    
    public void desconnetAllClients() throws IOException{
        for(Socket Client : Clients){
            Client.close();
        }
    }
    
    public int sizeClients(){
        return Clients.size();
    }
    
    public void addClients(Socket Client){
        Clients.add(Client);
    }

    public List<Socket> getClients() {
        return Clients;
    }

    public ServerSocket getServer() {
        return Server;
    }

    public void setServer(ServerSocket Server) {
        this.Server = Server;
    }

    public String getDsIP() {
        return DsIP;
    }

    public void setDsIP(String DsIP) {
        this.DsIP = DsIP;
    }

    public String getDsPort() {
        return DsPort;
    }

    public void setDsPort(String DsPort) {
        this.DsPort = DsPort;
    }

    public JSONObject getObjMudance() {
        return ObjMudance;
    }

    public void setObjMudance(JSONObject ObjMudance) {
        this.ObjMudance = ObjMudance;
    }
    
}
