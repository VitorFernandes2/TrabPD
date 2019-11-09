package ds.logic.gest;

import java.util.ArrayList;

public class Server {

    private String IP;
    private String Port;
    private int numberClients;
    private boolean On;
    private boolean Princi;
    private ArrayList<Client> clientsServer;

    public Server(String IP, String port, int numberClients, boolean on, boolean Princi) {
        this.IP = IP;
        Port = port;
        this.numberClients = numberClients;
        On = on;
        this.Princi = Princi;
        this.clientsServer = new ArrayList<Client>();
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

    public boolean isPrinci() {
        return Princi;
    }

    public void addClient(Client client){
        clientsServer.add(client);
    }

}
