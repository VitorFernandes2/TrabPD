/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.InterfaceGrafica;

import client.Interfaces.observer;
import client.logic.ConnectData;
import client.logic.DsConnect;
import client.logic.ServerTCPconnect;
import java.util.Scanner;

/**
 *
 * @author Joao Coelho
 */
public class Interfacemain implements observer{
    
    
    public void inicio (){
        
        System.out.println("Bem Vindo \n Insira o seu username: \n");
        
        Scanner myObj = new Scanner(System.in);
        String userName = myObj.nextLine();
        System.out.println( userName + " insira a sua palavra passe: \n");
        String password = myObj.nextLine();
    }

    public void begin() {

        DsConnect start = new DsConnect("9999","9999"); // temp
        String returnado = start.run();

        if("error".equals(returnado)){
            System.out.println("[ERROR] exception found or unknown error connecting to ds");
        }
        else{
            System.out.println(returnado);
            ServerTCPconnect startserver = new ServerTCPconnect(returnado);
            startserver.start();

            Scanner myObj = new Scanner(System.in);  // TEMP - pausa para manter a thread a correr. escreve algo pra parar thread
            String userName = myObj.nextLine();

            startserver.stopthread();
        }
    
    }

    @Override
    public void update(int acao,ConnectData data) {
        
        switch(acao){
            
            case 1:
                System.out.println("[ERROR] Excepção lançada na conexão UDP com ds");
                break;
                
            case 2:
                this.logininter(data);
                break;
                
            case 3:
                this.tempshowreceb(data);
                break;
                
            case 4:
                Scanner myObj = new Scanner(System.in);  // TEMP - pausa para manter a thread a correr. escreve algo pra parar thread
                String userName = myObj.nextLine();
                break;
            
            case 5:
                System.out.println("[ERROR] Excepção de IO lançada. Provavelmente o servidor \n[ERROR]desligou/perdeu-se a conecção ao servidor");
                break;
                
            case 444:
                String exc = (String) data.getJObj().get("exception");
                System.out.println("[ERROR] Excepção lançada aqui no notifier: " + exc);
                break;
                
        }
        
    }

    private void logininter(ConnectData data) {

            System.out.println("\n Autenticação: ");
        
            System.out.print("\n Login:");
            Scanner myObj = new Scanner(System.in);
            data.setUsername(myObj.nextLine());
            
            System.out.print(" PassWord:");
            myObj = new Scanner(System.in);
            data.setPassword(myObj.nextLine());
            
    
    }

    private void tempshowreceb(ConnectData data) {

        System.out.println(data.getJObj().toString());
    
    }

    
}
