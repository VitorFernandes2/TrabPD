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
import java.util.ArrayList;
import java.util.HashMap;

public class ManageClients {

    private final int TotalBytes = 4096;

    private static int NumberOfThreads = 0;
    private int ThreadNumber;
    private boolean finish;

    //Dados do DS
    private String IP;
    private String Port;

    //ArrayList de classes server
    private ArrayList<ManageServer> ServerList =  new ArrayList<ManageServer>();

    public ManageClients(String IP, String Port, ArrayList<ManageServer> ServerMaps)
    {

        this.ThreadNumber = ++NumberOfThreads;
        this.finish = false;
        this.IP = IP;
        this.Port = Port;
        this.ServerList = ServerMaps;

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
    public Server run() {



        DatagramSocket Socket;
        DatagramPacket Packet;

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

                // ------------------ TEMP -----------------

                ServerList.add(new ManageServer((Packet.getAddress()).getHostAddress()));




                JSONObject obj = new JSONObject();
                obj.put("IP", "recebi");
                obj.put("Port", "info");

                InetAddress endreclient = Packet.getAddress(); // endere�o do client;
                
                DatagramPacket packetresp = new DatagramPacket(obj.toString().getBytes(),
                    obj.toString().getBytes().length, endreclient, Packet.getPort());

                Socket.send(packetresp);

                System.out.println("Enviei coisas devolta ao Cliente");
                /*Socket.close(); N�o fechar o programa

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
