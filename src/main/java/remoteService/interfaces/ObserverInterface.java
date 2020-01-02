package remoteService.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ObserverInterface extends Remote {

    void notifyActivity() throws RemoteException;

}
