package server.interfaces;

import server.ServerMiddleLayer;

/**
 *
 * @author Lu�s Ant�nio Moreira Ferreira da Silva
 */
public interface subjectServer {
    
    public void addinterface(ServerMiddleLayer sml);
    public void notifyObserver(int acao);
    
}
