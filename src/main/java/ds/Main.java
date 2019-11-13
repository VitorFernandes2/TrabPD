package ds;

import ds.logic.*;
import ds.logic.gest.ServerList;

import java.net.InetAddress;

public class Main {

    public static void main(String[] args) {

        start();
        
    }

    public static void start(){
            // temp

        ServerList servers = new ServerList();
            
        ManageStarts servertemp = null;
        try{
            servertemp = new ManageStarts((InetAddress.getLocalHost()).getHostAddress(),
                    "9999", servers);
        }catch (Exception e)
        {
            System.out.println("Erra ao detetar o IP");
        }
        
        servertemp.run();
        
    }
    
}
