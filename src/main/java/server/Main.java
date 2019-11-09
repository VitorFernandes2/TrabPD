package server;

import java.util.Scanner;
import server.logic.ThreadClientRequests;
import server.logic.SendDataDs;

public class Main {

    public static void main(String[] args) {

            tempserverudp();

    }

    public static void tempserverudp(){
        // TEMP -> PRA REMOVER
        SendDataDs manage = new SendDataDs("9999", "9999");
        manage.run();
        
        ThreadClientRequests threadclass = new ThreadClientRequests(); // pode ser mai pratica REVER
        Thread servethread = new Thread(threadclass);
        servethread.start();
                
        Scanner myObj = new Scanner(System.in);  // TEMP - pausa para manter a thread a correr. escreve algo pra parar thread
        String userName = myObj.nextLine();      
        
        threadclass.stopthread();
        

    }
    
    
}
