package client.observer;

import client.logic.ServerTCPconnect;
import java.util.Observable;
import java.util.Observer;

public class ClientObserver implements Observer {

    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof ServerTCPconnect){
            System.out.println("Changes in ServerTCPconnect: " + arg.toString());
        }
        else{
            System.out.println("Changes inother object.");
        }
    }
    
}
