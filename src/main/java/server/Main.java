package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import server.comunicationInterface.ComunicationInterface;
import server.logic.ServerConnectionStarter;
import server.logic.ServerData;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {

        ServerData server = new ServerData("9999", "9999");

        ServerLogic sml = new ServerLogic(server);
        
        ComunicationInterface serverItf = new ComunicationInterface();
        
        sml.addinterface(serverItf);

        SpringApplication.run(Main.class, args);

        ServerConnectionStarter scs = new ServerConnectionStarter(sml);
        scs.connect();

    }
    
}
