package server;

import server.comunicationInterface.ComunicationInterface;
import server.logic.ServerData;

public class Main {

    public static void main(String[] args) {

        ServerData server = new ServerData("9999", "9999");
        
        ServerMiddleLayer sml = new ServerMiddleLayer(server);
        
        ComunicationInterface serverItf = new ComunicationInterface();
        
        serverItf.addinterface(sml);
        
        serverItf.connect();

    }
    
}
