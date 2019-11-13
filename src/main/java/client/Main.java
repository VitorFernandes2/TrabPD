package client;

import client.logic.*;
import client.observer.ClientObserver;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {
    
    private static ClientStartServer_OLD CThread;
    
    //Teste
    private static ClientObserver obs;

    public static void main(String[] args) {

        tempstart();
        //CThread.start();

    }
    
    
    /**
    * Função temporaria para teste de comunicação, com interface basica para testes
    */
    public static void tempstart(){
        
        //Teste temporario
        obs = new ClientObserver();
        
        // CLASSE TEMPORARIA
        
        DsConnect start = new DsConnect("9999","9999"); // temp
        String returnado = start.run();

        if("error".equals(returnado)){
            System.out.println("[ERROR] exception found or unknown error connecting to ds");
        }
        else{
            System.out.println(returnado);
            ServerTCPconnect startserver = new ServerTCPconnect(returnado);
            startserver.addObserver(obs);
            startserver.start();

            Scanner myObj = new Scanner(System.in);  // TEMP - pausa para manter a thread a correr. escreve algo pra parar thread
            String userName = myObj.nextLine();

            startserver.stopthread();
        }
                

    }
    
    

}
