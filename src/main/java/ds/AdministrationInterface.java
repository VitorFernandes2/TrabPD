package ds;

import ds.logic.gest.ServerList;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface AdministrationInterface extends Remote {

    String getAliveServers() throws RemoteException;
    void endServer(int id) throws RemoteException;
    void addObserver(ObserverInterface obs) throws RemoteException;
    void removeObserver(ObserverInterface obs) throws RemoteException;

}
