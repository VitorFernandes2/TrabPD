package ds.observer;

import java.util.Observable;
import java.util.Observer;

public class ObserverDs implements Observer {

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("Change: " + (String) arg);
    }
    
}
