package server;

import server.logic.SendDataDs;

public class Main {

    public static void main(String[] args) {

            tempserverudp();

    }

    public static void tempserverudp(){
        // TEMP -> PRA REMOVER
        SendDataDs manage = new SendDataDs("9999", "9999");
        manage.run();
        
    }
    
    
}
