package ds.logic.gest;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe para proteo dos acessos ao array de servidores
 * 
 **/
public class ServerList extends ArrayList<Server> implements Serializable {
    public static final long serialVersionUID = 1;
    @Override
    public synchronized Server remove(int index) {
        return super.remove(index);
    }

    @Override
    public synchronized boolean add(Server e) {
        return super.add(e);
    }

    @Override
    public synchronized Server get(int index) {
        return super.get(index);
    }
    
}
