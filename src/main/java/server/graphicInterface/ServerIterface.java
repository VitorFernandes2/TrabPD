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

    public ServerIterface(ServerMiddleLayer sml) {
        this.sml = sml;
    }
    
    public void connect(){
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

    public ServerMiddleLayer getSml() {
        return sml;
    }

    public void setSml(ServerMiddleLayer sml) {
        this.sml = sml;
    }

    public JSONObject getObjMudance() {
        return sml.getObjMudance();
    }

    public void setObjMudance(JSONObject ObjMudance) {
        this.sml.setObjMudance(ObjMudance);
    }

    @Override
    public void notifyObserver(int acao) {
        sml.update(acao);
    }
    
}
