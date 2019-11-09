package server.logic;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.*;

public class SendDataDs{

    private String DsIP;
    private String DsPort;

    public SendDataDs(String dsIP, String dsPort) {

        try {
            DsIP = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        DsPort = dsPort;
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

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
