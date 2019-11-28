package server;

import server.comunicationInterface.ComunicationInterface;
import server.logic.ServerData;

public class Main {

    public static void main(String[] args) {

        ServerData server = new ServerData("9999", "9999");
        
        ServerLogic sml = new ServerLogic(server);
        
        ComunicationInterface serverItf = new ComunicationInterface(server);
        
        sml.addinterface(serverItf);
        
        sml.connect();

    }
    
}
