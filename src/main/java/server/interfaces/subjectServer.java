package server.interfaces;

import server.comunicationInterface.ComunicationInterface;

public interface subjectServer {
    
    public void addinterface(ComunicationInterface sml);
    public void notifyObserver(int acao);
    
}
