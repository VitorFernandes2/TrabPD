/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ds.logic;

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
        
        
    }
    
    new manage client mg;
    arraylist<Servidor> servidoreativos;

    servidoreativos.add(mg.run); // o manage client passa a devolver info sobre o servidor, e se se ligou bem ou não

    new manageserver ms;
    ms.runthread(servidoreativos); // dentro do manageserver inicia-se as threads de gestão dos servers

    
    
}
