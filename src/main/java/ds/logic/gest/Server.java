package ds.logic.gest;

public class Server {

    private String IP;
    private String Port;
    private int numberClients;
    private boolean On;
    private boolean Princi;

    public Server(String IP, String port, int numberClients, boolean on, boolean Princi) {
        this.IP = IP;
        Port = port;
        this.numberClients = numberClients;
        On = on;
        this.Princi = Princi;
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
}
