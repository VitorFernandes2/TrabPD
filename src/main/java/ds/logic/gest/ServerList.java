package ds.logic.gest;

import java.util.ArrayList;

/**
 * Classe para proteção dos acessos ao array de servidores
 * 
 **/
public class ServerList extends ArrayList<Server> {
    
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
