/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.Interfaces;

import client.logic.ConnectData;

/**
 *
 * @author Joao Coelho
 */
public interface observer {

    public void update(int acao,ConnectData data);
    
}
