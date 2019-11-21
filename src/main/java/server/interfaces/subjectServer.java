/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.interfaces;

import server.graphicInterface.ServerIterface;

/**
 *
 * @author Luís António Moreira Ferreira da Silva
 */
public interface subjectServer {
    
    public void addinterface(ServerIterface o);
    public void notifyObserver(int acao);
    
}
