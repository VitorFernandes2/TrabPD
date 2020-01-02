package remoteService;

import remoteService.ui.TextInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteService extends UnicastRemoteObject {

    public RemoteService() throws RemoteException {}

    public static void main(String[] args) {

        String value = null;

        do {

            value = TextInterface.menu();

            if (value.equals("listServers")){



            }
            else if (value.equals("endServer")){



            }
            else if (value.equals("regListener")){



            }
            else if (value.equals("delListener")){



            }

        }while (!value.equalsIgnoreCase("exit"));

    }

}
