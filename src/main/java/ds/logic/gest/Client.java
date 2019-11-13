package ds.logic.gest;

public class Client {

    private String ip;
    private String port;
    private String loginid;

    public Client(String ip, String port) {
        this.ip = ip;
        this.port = port;
    }

    public Client(String ip, String port, String loginid) {
        this.ip = ip;
        this.port = port;
        this.loginid = loginid;
    }

    public synchronized String getIp() {
        return ip;
    }

    public synchronized void setIp(String ip) {
        this.ip = ip;
    }

    public synchronized String getPort() {
        return port;
    }

    public synchronized void setPort(String port) {
        this.port = port;
    }

    public synchronized String getLoginid() {
        return loginid;
    }

    public synchronized void setLoginid(String loginid) {
        this.loginid = loginid;
    }

    
    
}
