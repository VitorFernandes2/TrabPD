package server;

import server.graphicInterface.ServerIterface;
import server.logic.ServerData;
import server.logic.ServerMiddleLayer;

public class Main {

    public static void main(String[] args) {

        ServerData server = new ServerData("9999", "9999");
        
        ServerMiddleLayer sml = new ServerMiddleLayer(server);
        
        ServerIterface serverItf = new ServerIterface();
        
        serverItf.addinterface(sml);
        
        serverItf.connect();

    }
    
}
