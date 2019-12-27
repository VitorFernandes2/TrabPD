package client;

import client.InterfaceGrafica.Interfacemain;
import client.logic.*;
import java.util.Scanner;

public class Main {
    


    public static void main(String[] args) {

        Interfacemain inter = new Interfacemain();
        
        MainConnect connt = new MainConnect(inter);
        
        connt.begin();
        
        //tempstart();

    }
    
    
    /**
    * ---OLD---- Função temporaria para teste de comunicação, com interface basica para testes
    */
    public static void tempstart(){
        
        // CLASSE TEMPORARIA
        
        
        
        DsConnect start = new DsConnect("9999","9999"); // temp
        String returnado = start.run();

        if("error".equals(returnado)){
            System.out.println("[ERROR] exception found or unknown error connecting to ds");
        }
        else{
            System.out.println(returnado);

            TalkToDS talkToDS = new TalkToDS(returnado);
            talkToDS.start();

            Scanner myObj = new Scanner(System.in);  // TEMP - pausa para manter a thread a correr. escreve algo pra parar thread
            String userName = myObj.nextLine();

            talkToDS.stopClient();


        }
                

    }
    
    

}
