package ds.logic.gest;

public class Server {

    private String IP;
    private String Port;
    private int numberClients;
    private boolean On;

    public Server(String IP, String port, int numberClients, boolean on) {
        this.IP = IP;
        Port = port;
        this.numberClients = numberClients;
        On = on;
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
}
