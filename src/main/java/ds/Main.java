package ds;

import ds.logic.*;
import ds.logic.gest.ServerList;
import mainObjects.Readers;

import java.net.InetAddress;

public class Main {

    public static void main(String[] args) {

        start();
        
    }

    public static void start(){

        ServerList servers = new ServerList();
            
        ManageStarts servertemp = null;
        ManageServers manageServers = null;
        try{
            servertemp = new ManageStarts((InetAddress.getLocalHost()).getHostAddress(),
                    "9999", servers);
            manageServers = new ManageServers(servers);
        }catch (Exception e)
        {
            System.out.println("Erro ao detetar o IP");
        }

        if (servertemp != null) {
            servertemp.start();
        }
        if (manageServers != null) {
            manageServers.start();
        }

        String quitMessage = "";
        while (!quitMessage.equals("quit"))
            quitMessage = Readers.readString("\nInsira quit para terminar o ds: ");

        if (manageServers != null) {
            manageServers.terminate();
        }
        if (servertemp != null) {
            servertemp.terminate();
        }

    }
    
}
