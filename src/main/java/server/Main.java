package server;

import java.util.Scanner;
import server.graphicInterface.ServerIterface;
import server.logic.ThreadClientRequests;
import server.logic.SendDataDs;
import server.logic.ServerData;
import server.logic.ServerMiddleLayer;

public class Main {

    public static void main(String[] args) {

        ServerData server = new ServerData("9999", "9999");
        
        ServerMiddleLayer sml = new ServerMiddleLayer(server);
        
        ServerIterface serverItf = new ServerIterface(sml);
        
        serverItf.connect();
        
        //tempserverudp();

    }

    public static void tempserverudp(){
        // TEMP -> PRA REMOVER
        
        System.out.println("Server a correr"); // temp
        
        SendDataDs manage = new SendDataDs("9999", "9999");
        manage.run();
        
        ThreadClientRequests threadclass = new ThreadClientRequests(); // pode ser mai pratica REVER //mudei -> repor se necessário
        threadclass.start();
                
        Scanner myObj = new Scanner(System.in);  // TEMP - pausa para manter a thread a correr. escreve algo pra parar thread
        String userName = myObj.nextLine();
        
        threadclass.stopthread();

    }
    
    
}
