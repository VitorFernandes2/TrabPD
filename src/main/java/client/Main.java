package client;

import client.logic.comunication.server.ClientStart;

public class Main {
    
    private static ClientStart CThread;

    public static void main(String[] args) {


        CThread.start();
        
    }

}
