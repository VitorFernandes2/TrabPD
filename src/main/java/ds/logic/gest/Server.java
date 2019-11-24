package ds.logic.gest;

import java.util.ArrayList;

public class Server {

    private String IP; // ip do servidor
    private String Port; // porta do servidor
    private int numberClients; // numero de clientes
    private boolean On; // estado do servidor
    private boolean Princi; // servidor 
    private ArrayList<Client> clients; // client ligado ao servidor
    
    public Server(String IP, String port,boolean on, boolean Princi) {
        this.IP = IP;
        Port = port;
        this.numberClients = 0;
        On = on;
        this.Princi = Princi;
        clients = new ArrayList<Client>();
    }
    
    public synchronized String getIP() {
        return IP;
    }

    public synchronized String getPort() {
        return Port;
    }

    public synchronized int getNumberClients() {
        return numberClients;
    }
    
    public synchronized boolean isOn() {      
        return On;
    }

    public synchronized void turnOn() {      
        On = true;
    }
        
    public synchronized void turnOff() {      
        On = false;
    }
    
    public synchronized boolean isPrinci() {
        return Princi;
    }

    public synchronized void addClient(Client client){
        this.clients.add(client);
        this.numberClients++;
    }

    public synchronized void removeClient(Client client){
        this.clients.remove(client);
        this.numberClients--;
    }

    public synchronized void setPrinci(){
        this.Princi = true;
    }

    public synchronized void unsetPrinci(){
        this.Princi = false;
    }

    public synchronized ArrayList<Client> getClients() {
        return clients;
    }
}
