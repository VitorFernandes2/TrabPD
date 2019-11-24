package server.logic;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class PingReply extends Thread {

    private DatagramSocket socket;

    public PingReply(int port) {

        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    //C�digo para enviar ao Ds que este est� vivo
    @Override
    public void run() {

        try{

            while (true){

                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                socket.receive(packet);

                int PORT = packet.getPort();
                InetAddress IP = packet.getAddress();
                String tmp = new String(packet.getData(),0,packet.getLength());
                JSONParser jsonParser = new JSONParser();
                JSONObject object = (JSONObject) jsonParser.parse(tmp);

                String msg = (String) object.get("msg");

                if (msg.equals("isAlive")){

                    object = new JSONObject();
                    object.put("msg", true);

                    buf = object.toString().getBytes();

                    packet = new DatagramPacket(buf, buf.length, IP, PORT);
                    socket.send(packet);

                }else if (msg.equals("serverMorto")){

                    //Caso seja necess�rio tratar algo do server morto
                    System.out.println("O servidor que morreu, foi " + object.get("serv"));

                }

            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

}
