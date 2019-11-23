package server.logic;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;
import org.json.simple.JSONObject;
import server.graphicInterface.ServerIterface;
import server.interfaces.observerServer;

/**
 *
 * @author Luís António Moreira Ferreira da Silva
 */
public class ServerMiddleLayer implements observerServer{

    private ServerData sd;
    private PingReply pingReply;

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
                String output = (String) this.sd.getObjMudance().get("output");
                System.out.println(output);
                break;
            case 2:
                System.out.println("Pedido de Ligação ao DS.");
                DatagramSocket socket;
                try {
                    socket = new DatagramSocket();
                
                    InetAddress address = InetAddress.getByName(this.sd.getDsIP());

                    JSONObject ObjSend =  new JSONObject();

                    ObjSend.put("msg", "dataSubmitServer");
                    ObjSend.put("Port", "9998");

                    String StrToSend = ObjSend.toString();
                    byte []buf = StrToSend.getBytes();
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, address,
                            Integer.parseInt(this.sd.getDsPort()));

                    socket.send(packet);

                    socket.close();
                } catch (SocketException ex) {
                    ex.printStackTrace();
                } catch (UnknownHostException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                pingReply = new PingReply();
                pingReply.start();

                break;
            case 3:
                ServerIterface si = (ServerIterface) this.sd.getObjMudance().get("ServerIterface");
                ThreadClientRequests threadclass = new ThreadClientRequests(si); // pode ser mai pratica REVER
                threadclass.start();

                Scanner myObj = new Scanner(System.in);  // TEMP - pausa para manter a thread a correr. escreve algo pra parar thread
                String userName = myObj.nextLine();

                threadclass.stopthread();
                break;
            case 4:
                String excepcao = (String) this.sd.getObjMudance().get("exception");
                System.out.println(excepcao);
                break;
            default:
                System.out.println("Default!");
        }
    }
    
}
