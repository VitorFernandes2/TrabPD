package server.interfaces;

import server.ServerMiddleLayer;

/**
 *
 * @author Luís António Moreira Ferreira da Silva
 */
public interface subjectServer {
    
    public void addinterface(ServerMiddleLayer sml);
    public void notifyObserver(int acao);
    
}
