package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketImpl;
import org.json.simple.JSONObject;
import server.comunicationInterface.ComunicationInterface;
import server.interfaces.subjectServer;
import server.logic.DatabaseControler;
import server.logic.ServerData;
import server.logic.ThreadClientListenTreatment;

public class ServerLogic implements subjectServer{

    private ServerData sd;
    private ComunicationInterface sml;

    public ServerLogic(ServerData sd) {
        this.sd = sd;
    }

    public ServerData getSd() {
        return sd;
    }

    public void setSd(ServerData sd) {
        this.sd = sd;
    }

    @Override
    public void addinterface(ComunicationInterface sml) {
        this.sml = sml;
    }

    public String getDsIP() {
        return sd.getDsIP();
    }
    
    public JSONObject Obj(){
        return sd.getObjMudance();
    }

    public void setServerPort(int localPort) {
        sd.setServerPort(localPort);
    }

    public String getDsPort() {
        return sd.getDsPort();
    }

    public void startdatabase(boolean b) {
        sd.startdatabase(b);
    }

    public DatabaseControler getDbaction() {
        return sd.getDbaction();
    }

    public void removeClient(Socket Client) {
        sd.removeClient(Client);
    }

    public ServerSocket getServer() {
        return sd.getServer();
    }

    public void addClients(Socket s) {
        sd.addClients(s);
    }

    public void addListners(ThreadClientListenTreatment tclt) {
        sd.addListners(tclt);
    }

    public ThreadClientListenTreatment getListen(ThreadClientListenTreatment tclt) {
        return sd.getListen(tclt);
    }

    public void desconnetAllClients() throws IOException {
        sd.desconnetAllClients();
    }

    public void setServer(ServerSocket serverSocket) {
        sd.setServer(serverSocket);
    }

    public int getServerPort() {
        return sd.getServerPort();
    }

    public void removeListenClient(ThreadClientListenTreatment aThis) {
        sd.removeListenClient(aThis);
    }

    public void desconnetClient(Socket Client) throws IOException {
        sd.desconnetClient(Client);
    }

    @Override
    public void notifyObserver(int acao) {
        sml.update(acao, sd.getObjMudance());
    }
    
}
