package server.logic;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.comunicationInterface.ComunicationInterface;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ServerMulticastUDP extends Thread implements Constants{

    private MulticastSocket socket;
    private boolean terminate;
    private String groupIP;
    private int groupPORT;
    private ComunicationInterface ci; //Layer de comunicação

    public ServerMulticastUDP(String groupIP, int groupPORT, ComunicationInterface ci) {
        this.groupIP = groupIP;
        this.groupPORT = groupPORT;
        this.terminate = false;
        this.ci = ci;
    }

    @Override
    public void run() {

        try {

            socket = new MulticastSocket(groupPORT);
            InetAddress addr = InetAddress.getByName(groupIP);
            socket.joinGroup(addr); //junta-se ao grupo de multicast

            //Enquanto for para ler
            while (!terminate){

                byte[] buf = new byte[BUFFER_MAX_LENGTH];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                //Retiram a mensagem recebida
                String msg = new String(packet.getData(), 0, packet.getLength());
                JSONParser jsonParser = new JSONParser();
                JSONObject object = (JSONObject) jsonParser.parse(msg);

                //Trata a mensagem de Multicast


            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    public void terminateThread(){
        terminate = true;
    }

}
