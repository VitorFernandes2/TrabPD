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
public class ServerMiddleLayer implements observerServer{

    private ServerData sd;

    public ServerMiddleLayer(ServerData sd) {
        this.sd = sd;
    }

    public ServerData getSd() {
        return sd;
    }

    public void setSd(ServerData sd) {
        this.sd = sd;
    }
    
    @Override
    public void update(int acao) {
        switch(acao){
            case 1:
                String output = (String) sd.getObjMudance().get("output");
                System.out.println(output);
                break;
            case 2:
                System.out.println("Pedido de Ligação ao DS.");
                DatagramSocket socket;
                try {
                    socket = new DatagramSocket();
                
                    InetAddress address = InetAddress.getByName(sd.getDsIP());

                    JSONObject ObjSend =  new JSONObject();

                    ObjSend.put("msg", "dataSubmitServer");
                    ObjSend.put("Port", "9998");

                    String StrToSend = ObjSend.toString();
                    byte []buf = StrToSend.getBytes();
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, address,
                            Integer.parseInt(sd.getDsPort()));

                    socket.send(packet);

                    socket.close();
                } catch (SocketException ex) {
                    ex.printStackTrace();
                } catch (UnknownHostException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            case 3:
                
                break;
            case 4:
                String excepcao = (String) sd.getObjMudance().get("exception");
                System.out.println(excepcao);
                break;
            default:
                System.out.println("Default!");
        }
    }
    
}
