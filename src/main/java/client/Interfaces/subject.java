/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.Interfaces;

import client.InterfaceGrafica.Interfacemain;

/**
 *
 * @author Joao Coelho
 */
public interface subject {
  
    public void addinterface(Interfacemain o);
    public void notifyObserver(int acao);
    
}
