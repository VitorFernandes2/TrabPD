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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getLoginid() {
        return loginid;
    }

    public void setLoginid(String loginid) {
        this.loginid = loginid;
    }

    
    
}
