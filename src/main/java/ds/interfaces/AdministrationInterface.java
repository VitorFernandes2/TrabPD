package ds.interfaces;

import remoteService.interfaces.ObserverInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface AdministrationInterface extends Remote {

    ArrayList<String> getAliveServers() throws RemoteException;
    void endServer(int id) throws RemoteException;
    void addObserver(ObserverInterface obs) throws RemoteException;
    void removeObserver(ObserverInterface obs) throws RemoteException;

}
