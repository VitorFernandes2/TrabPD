package server.logic;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import org.json.simple.JSONObject;
import server.interfaces.observerServer;

/**
 *
 * @author Luís António Moreira Ferreira da Silva
 */
public class ServerData implements observerServer{

    private String DsIP;
    private String DsPort;
    private JSONObject ObjMudance;

    public ServerData(String DsIP, String DsPort) {
        this.DsIP = DsIP;
        try {
            this.DsIP = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            ObjMudance.put("exception", e.getMessage());
            this.update(4);
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

    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(DsIP);
            
            JSONObject ObjSend =  new JSONObject();
            
            ObjSend.put("msg", "dataSubmitServer");
            ObjSend.put("Port", "9998");
            
            String StrToSend = ObjSend.toString();
            byte []buf = StrToSend.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address,
                    Integer.parseInt(DsPort));
            
            socket.send(packet);
            
            socket.close();
        } catch (SocketException ex) {
            ObjMudance.put("exception", ex.getMessage());
            this.update(4);
        } catch (UnknownHostException ex) {
            ObjMudance.put("exception", ex.getMessage());
            this.update(4);
        } catch (IOException ex) {
            ObjMudance.put("exception", ex.getMessage());
            this.update(4);
        }
    }

    @Override
    public void update(int acao) {
        switch(acao){
            case 1:
                String output = (String) ObjMudance.get("output");
                System.out.println(output);
                break;
            case 4:
                String excepcao = (String) ObjMudance.get("exception");
                System.out.println(excepcao);
                break;
            default:
                System.out.println("Default!");
        }
    }
    
}
