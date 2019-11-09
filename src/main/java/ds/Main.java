package ds;

import ds.logic.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) {

        start();
        
    }

    public static void start(){
            // temp
            
        ManageClients servertemp = null;
        try{
            servertemp = new ManageClients();
        }catch (Exception e)
        {
            System.out.println("Erra ao detetar o IP");
        }
        
        servertemp.run();
        
    }
    
}
