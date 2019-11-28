package server;

import server.comunicationInterface.ComunicationInterface;
import server.interfaces.subjectServer;
import server.logic.ServerData;

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
    
    public void connect(){
        
        this.notifyObserver(2);
        
        this.notifyObserver(5);
        
    }

    @Override
    public void addinterface(ComunicationInterface sml) {
        
        this.sml = sml;
        
    }

    @Override
    public void notifyObserver(int acao) {
        
        sml.update(acao, sd.getObjMudance());
        
    }
    
}
