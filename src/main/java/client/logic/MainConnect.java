/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.logic;

import client.InterfaceGrafica.Interfacemain;
import client.Interfaces.subject;

/**
 *
 * @author Joao Coelho
 */
public class MainConnect implements subject{

    Interfacemain inter;
    ConnectData datacomun;
    
    public MainConnect(Interfacemain inter) {
        this.inter = inter;
    
    }

    @Override
    public void addinterface(Interfacemain o) {
        this.inter = o;
    }

    @Override
    public void notifyObserver(int acao) {

        switch(acao){

            case 1:
                // caso de erro no ds
                inter.update(1,datacomun);
                break;
            case 2:
                // pede info de login
                inter.update(2,datacomun);
                break;
                
            case 3:
                // mostra o q recebe
                inter.update(3,datacomun);
                break;
                
            case 4:
                // pede para ficar bloqueado - TEMP
                inter.update(4,datacomun);
                break;    
                
            case 444:
                // Envio a interface erro desconhecido
                inter.update(444,datacomun);
                break;
                
        }
        
    }

    public void begin() {

        DsConnect start = new DsConnect("9999","9999",inter,datacomun); // temp
        String returnado = start.run();

        if("error".equalsIgnoreCase(returnado)){
            this.notifyObserver(1);
        }
        else{
            
            datacomun = new ConnectData();

            //System.out.println(returnado);
            ServerTCPconnect startserver = new ServerTCPconnect(returnado,datacomun,inter);
            startserver.start();
            
            //Wait por comando de saida
            while(!startserver.getCommand().equalsIgnoreCase("exit")){}
            //-------------------------

            startserver.stopthread();

        }
    
    }
    
}
