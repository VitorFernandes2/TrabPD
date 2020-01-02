package ds;

import ds.logic.gest.ServerList;
import ds.ui.TextInterface;
import mainObjects.Readers;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RemoteService extends UnicastRemoteObject implements ObserverInterface {

    public RemoteService() throws RemoteException {}

    public static void main(String[] args) {

        try {

            //Localiza o servico remoto nomeado "piValueService"
            String objectUrl = "rmi://localhost/remoteService"; //rmiregistry on localhost

            if(args.length > 0) {
                objectUrl = "rmi://"+args[0]+"/remoteService";
            }

            AdministrationInterface service = (AdministrationInterface) Naming.lookup(objectUrl);

            String value = null;

            RemoteService observer = new RemoteService();

            do {

                value = TextInterface.menu();

                if (value.equals("listServers")){

                    System.out.println(service.getAliveServers());

                }
                else if (value.equals("endServer")){

                    int id = Readers.readInteger("Insira o id a eliminar: ");
                    service.endServer(id);

                }
                else if (value.equals("regListener")){

                    service.addObserver(observer);

                }
                else if (value.equals("delListener")){

                    service.removeObserver(observer);

                }

            }while (!value.equalsIgnoreCase("exit"));

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void notifyActivity() throws RemoteException {
        System.out.println("Notificacao");
    }
}
