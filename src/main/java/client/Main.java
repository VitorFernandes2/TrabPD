package client;

import client.logic.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    
    private static ClientStartServer CThread;

    public static void main(String[] args) {

        tempstart();
        //CThread.start();

    }
    
    
    /**
    * Fun��o temporaria para teste de comunica��o, com interface basica para testes
    */
    public static void tempstart(){
        // CLASSE TEMPORARIA
        
                ClientStart start = new ClientStart("9999","9999"); // temp
                start.run();
        
    }
    
    

}
