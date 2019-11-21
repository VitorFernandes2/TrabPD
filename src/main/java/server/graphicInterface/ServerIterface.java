/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.graphicInterface;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import server.interfaces.observerServer;
import server.interfaces.subjectServer;
import server.logic.SendDataDs;
import server.logic.ServerData;
import server.logic.ThreadClientRequests;

/**
 *
 * @author Luís António Moreira Ferreira da Silva
 */
public class ServerIterface implements subjectServer{

    private ServerData sd;

    public ServerIterface(ServerData sd) {
        this.sd = sd;
    }
    
    public void connect(){
        //System.out.println("Server a correr"); // temp
        JSONObject Ob = new JSONObject();
        Ob.put("output", "Server a correr.");
        this.getSd().setObjMudance(Ob);
        this.notifyObserver(1);
        
        sd.run();
        
        ThreadClientRequests threadclass = new ThreadClientRequests(this); // pode ser mai pratica REVER
        threadclass.start();
        
        Scanner myObj = new Scanner(System.in);  // TEMP - pausa para manter a thread a correr. escreve algo pra parar thread
        String userName = myObj.nextLine();
        
        threadclass.stopthread();
    }

    public ServerData getSd() {
        return sd;
    }

    public void setSd(ServerData sd) {
        this.sd = sd;
    }

    @Override
    public void notifyObserver(int acao) {
        sd.update(acao);
    }
    
}
