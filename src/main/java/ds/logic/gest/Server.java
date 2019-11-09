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
    
    public String getIP() {
        return IP;
    }

    public String getPort() {
        return Port;
    }

    public int getNumberClients() {
        return numberClients;
    }
    
    public boolean isOn() {      
        return On;
    }

    public void turnOn() {      
        On = true;
    }
        
    public void turnOff() {      
        On = false;
    }
    
    public boolean isPrinci() {
        return Princi;
    }

    public void addClient(Client client){
        this.clients.add(client);
        this.numberClients++;
    }

}
