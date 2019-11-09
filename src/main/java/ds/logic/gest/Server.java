package ds.logic.gest;

import java.util.ArrayList;

public class Server {

    private String IP; // ip do servidor
    private String Port; // porta do servidor
    private boolean isbeingused; // numero de clientes
    private boolean On; // estado do servidor
    private boolean Princi; // servidor 
    private Client connectedclient; // client ligado ao servidor
    
    
    public Server(String IP, String port, boolean on, boolean Princi) {
        this.IP = IP;
        Port = port;
        this.isbeingused = false;
        On = on;
        this.Princi = Princi;
    }

    public Server(String IP, String Port, boolean On,Client cli) {
        this.IP = IP;
        this.Port = Port;
        this.isbeingused = false;
        this.On = false;
        this.connectedclient = cli;
    }
    
    public String getIP() {
        return IP;
    }

    public String getPort() {
        return Port;
    }

    public boolean isbeingused() {
        return isbeingused;
    }
    
    public void exitClient(){
        isbeingused = false;
        connectedclient = null;
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
        this.connectedclient = client;
        isbeingused = true;
    }

}
