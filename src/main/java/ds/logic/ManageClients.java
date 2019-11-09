package ds.logic;

import ds.logic.gest.Server;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class ManageClients extends Thread{

    private final int TotalBytes = 4096;

    private static int NumberOfThreads = 0;
    private int ThreadNumber;
    private boolean finish;

    //Dados do DS
    private String IP;
    private String Port;

    public ManageClients(String IP, String Port)
    {

        this.ThreadNumber = ++NumberOfThreads;
        this.finish = false;
        this.IP = IP;
        this.Port = Port;

    }

    public ManageClients() throws UnknownHostException
    {
        // temp
        this.ThreadNumber = ++NumberOfThreads;
        this.finish = false;
        this.IP = (InetAddress.getLocalHost()).getHostAddress();
        this.Port = "9999";
        //this.ServerMaps = ServerMaps;

    }


    public int getThreadNumber() {
        return ThreadNumber;
    }

    //Thread para receber todos os novos servidores
    @Override
    public void run() {

        DatagramSocket Socket;
        DatagramPacket Packet;
        Server server = null;

        try {

            Socket = new DatagramSocket(Integer.parseInt(Port));

            while (!finish) {

                System.out.println("Estou a espera de Clientes"); // TEMP
                byte[] buf = new byte[TotalBytes];
                Packet = new DatagramPacket(buf, buf.length);
                Socket.receive(Packet);

                String SObj = new String(Packet.getData(), 0, Packet.getLength());
                JSONParser JsonParser = new JSONParser();
                JSONObject JObj = (JSONObject) JsonParser.parse(SObj);

                System.out.println(JObj);

            }

        } catch(IOException e){
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


}
