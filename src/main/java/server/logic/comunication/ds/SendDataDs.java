package server.logic.comunication.ds;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.*;

public class SendDataDs extends Thread{

    private String DsIP;
    private String DsPort;
    private String ServerIp;
    private String ServerPort;

    public SendDataDs(String dsIP, String dsPort, String serverIp, String serverPort) {
        DsIP = dsIP;
        DsPort = dsPort;
        ServerIp = serverIp;
        ServerPort = serverPort;
    }

    @Override
    public void run() {

        try {

            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(DsIP);

            JSONObject ObjSend =  new JSONObject();

            ObjSend.put("IP", ServerIp);
            ObjSend.put("Port", ServerPort);

            String StrToSend = ObjSend.toString();
            byte []buf = StrToSend.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address,
                    Integer.parseInt(DsPort));

            socket.send(packet);

            socket.close();

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
