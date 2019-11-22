package server.graphicInterface;

import java.util.Scanner;
import org.json.simple.JSONObject;
import server.interfaces.subjectServer;
import server.logic.ServerMiddleLayer;
import server.logic.ThreadClientRequests;

/**
 *
 * @author Luís António Moreira Ferreira da Silva
 */
public class ServerIterface implements subjectServer{

    private ServerMiddleLayer sml;
    
    public void connect(){
        
        this.notifyObserver(2);
        
        //System.out.println("Server a correr"); // temp
        JSONObject Ob = new JSONObject();
        Ob.put("output", "Server a correr.");
        setObjMudance(Ob);
        this.notifyObserver(1);
        
        ThreadClientRequests threadclass = new ThreadClientRequests(this); // pode ser mai pratica REVER
        threadclass.start();
        
        Scanner myObj = new Scanner(System.in);  // TEMP - pausa para manter a thread a correr. escreve algo pra parar thread
        String userName = myObj.nextLine();
        
        threadclass.stopthread();
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

    @Override
    public void notifyObserver(int acao) {
        sml.update(acao);
    }

    @Override
    public void addinterface(ServerMiddleLayer sml) {
        this.sml = sml;
    }
    
}
