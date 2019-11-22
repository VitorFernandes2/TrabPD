package server.logic;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.json.simple.JSONObject;

/**
 *
 * @author Luís António Moreira Ferreira da Silva
 */
public class ServerData {

    private String DsIP;
    private String DsPort;
    private JSONObject ObjMudance;

    public ServerData(String DsIP, String DsPort) {
        this.DsIP = DsIP;
        try {
            this.DsIP = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.DsPort = DsPort;
    }

    public String getDsIP() {
        return DsIP;
    }

    public void setDsIP(String DsIP) {
        this.DsIP = DsIP;
    }

    public String getDsPort() {
        return DsPort;
    }

    public void setDsPort(String DsPort) {
        this.DsPort = DsPort;
    }

    public JSONObject getObjMudance() {
        return ObjMudance;
    }

    public void setObjMudance(JSONObject ObjMudance) {
        this.ObjMudance = ObjMudance;
    }
    
}
