package client;

import client.InterfaceGrafica.Interfacemain;
import client.logic.*;
import java.util.Scanner;

public class Main {
    


    public static void main(String[] args) {

        Interfacemain inter = new Interfacemain();
        
        MainConnect connt = new MainConnect(inter);
        
        connt.begin();

    }
    

}
