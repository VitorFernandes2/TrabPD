/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ds.logic;

import ds.logic.gest.Server;
import java.util.ArrayList;

/**
 *
 * @author a21270909
 */
public class Manager {
    
    ArrayList<Server> ListaServidores;
    
    public void run(){
        
            ManageClients mg=null;
        try{
            mg = new ManageClients();
        }catch (Exception e)
        {
            System.out.println("Error detecting local ip");
        }
        
        ListaServidores.add(mg.run());
        
        ManageServer ms = new ManageServer();
        ms.LoginConnectServer();
        
        
    }
    

}
