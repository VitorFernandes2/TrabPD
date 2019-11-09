package ds.logic;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;

public class ManageClients {

    private final int TotalBytes = 4096;

    private static int NumberOfThreads = 0;
    private int ThreadNumber;
    private boolean finish;

    //Dados do DS
    private String IP;
    private String Port;

    //Mapa de IP'S/Portos dos Servidores
    private HashMap<String, String> ServerMaps;

    public ManageClients(String IP, String Port, HashMap<String, String> ServerMaps)
    {

        this.ThreadNumber = ++NumberOfThreads;
        this.finish = false;
        this.IP = IP;
        this.Port = Port;
        this.ServerMaps = ServerMaps;

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
    public void run() {
        
        System.out.println("estou a correr"); // TEMP
        
        DatagramSocket Socket;
        DatagramPacket Packet;

        try {

            Socket = new DatagramSocket(Integer.parseInt(Port));

            while (!finish) {

                byte[] buf = new byte[TotalBytes];
                Packet = new DatagramPacket(buf, buf.length);
                Socket.receive(Packet);

                String SObj = new String(Packet.getData(), 0, Packet.getLength());
                JSONParser JsonParser = new JSONParser();
                JSONObject JObj = (JSONObject) JsonParser.parse(SObj);

                System.out.println(JObj);

                
                
                // ------------------ TEMP -----------------
                JSONObject obj = new JSONObject();
                obj.put("IP", "recebi");
                obj.put("Port", "info");
                
                InetAddress endreclient = Packet.getAddress(); // enderesso do client;
                
                DatagramPacket packetresp = new DatagramPacket(obj.toString().getBytes(),
                    obj.toString().getBytes().length, endreclient, Packet.getPort());
        
                Socket.send(packetresp);
                
                System.out.println("Enviei coisas devolta");
                /*Socket.close(); Não fechar o programa

                finish = true;
                */
                
                // ---------------------ENDTEMP------------------
                
            }

        } catch(IOException e){
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


}
