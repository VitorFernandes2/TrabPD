package ds;

import ds.logic.*;
import ds.logic.gest.Server;

import java.net.InetAddress;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        start();
        
    }

    public static void start(){
            // temp

        ArrayList<Server> servers = new ArrayList<Server>();
            
        ManageStarts servertemp = null;
        try{
            servertemp = new ManageStarts((InetAddress.getLocalHost()).getHostAddress(),
                    "9999", servers);
        }catch (Exception e)
        {
            System.out.println("Erra ao detetar o IP");
        }
        
        servertemp.start();
        
    }
    
}
