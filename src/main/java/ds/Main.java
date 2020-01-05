package ds;

import ds.logic.*;
import ds.logic.gest.Server;
import ds.logic.gest.ServerList;
import mainObjects.Readers;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main extends UnicastRemoteObject implements AdministrationInterface {

    protected List<ObserverInterface> observers;
    private static ServerList servers = new ServerList();

    protected Main() throws RemoteException {

        observers = new ArrayList<>();

    }

    public static void main(String[] args) {

        start();
        
    }

    public static void start(){

        ManageStarts servertemp = null;
        ManageServers manageServers = null;
        try{
            servertemp = new ManageStarts((InetAddress.getLocalHost()).getHostAddress(),
                    "9999", servers);
            manageServers = new ManageServers(servers);
        }catch (Exception e)
        {
            System.out.println("Erro ao detetar o IP: " + e.getMessage());
        }

        if (servertemp != null) {
            servertemp.start();
        }
        if (manageServers != null) {
            manageServers.start();
        }

        try{

            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            Main service = new Main();
            Naming.rebind("rmi://localhost/remoteService", service);

        }catch(RemoteException | MalformedURLException e){

        }
        
        String quitMessage = "";
        Scanner sc = new Scanner(System.in);
        while (!quitMessage.equals("quit")){
            System.out.println("\nInsira quit para terminar o ds: ");
            quitMessage = sc.nextLine();
        }

        if (manageServers != null) {
            manageServers.terminate();
        }
        if (servertemp != null) {
            servertemp.terminate();
        }
        
        System.exit(0);
        
    }

    @Override
    public String getAliveServers() throws RemoteException {

        StringBuffer str = new StringBuffer();

        for (Server item : servers) {
            str.append(item.getIP() + " " + item.getPort() + "\n");
        }

        return str.toString();

    }

    @Override
    public void endServer(int id) throws RemoteException {

        if (id >= 0 && id < servers.size()){

            servers.remove(id);

        }

    }

    @Override
    public void addObserver(ObserverInterface obs) throws RemoteException {
        observers.add(obs);
    }

    @Override
    public void removeObserver(ObserverInterface obs) throws RemoteException {
        observers.remove(obs);
    }

}
