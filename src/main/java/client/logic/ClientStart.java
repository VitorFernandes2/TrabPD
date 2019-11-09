package client.logic;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.*;

public class ClientStart {

    private String DsIp;
    private String DsPort;
    private String ServerPort;
    private String ServerIp;
    private String IP;
    private String Port;

    public ClientStart(String dsIp, String dsPort, String serverPort,
                       String serverIp, String IP, String port) {
        DsIp = dsIp;
        DsPort = dsPort;
        ServerPort = serverPort;
        ServerIp = serverIp;
        this.IP = IP;
        Port = port;
    }

    public ClientStart(String dsIp, String dsPort){
        
        this.IP = dsIp;
        DsPort = dsPort;
        try{
            DsIp = (InetAddress.getLocalHost()).getHostAddress();
        }catch (Exception e)
        {
            System.out.println("Erra ao detetar o IP");
            this.IP = "999";
        }
        Port = "9999";
    }
    
    
    /*
        Serve para mandar ao Ds a informa√ß√£o acerca do cliente e coseguir receber informa√ß√£o
        relativa ao servidor que este cliente se vai alistar
     */
    public void run() {

        DatagramSocket socket;
        DatagramPacket packet;
        InetAddress address;

        try {

            //Cria√ß√£o da socket UDP
            socket = new DatagramSocket();
            address = InetAddress.getByName(DsIp);

            JSONObject obj = new JSONObject();
            obj.put("IP", IP);
            obj.put("Port", Port);

            //Cria√ß√£o do packet de envio dos dados relativos ao cliente para o DS
            packet = new DatagramPacket(obj.toString().getBytes(),
                    obj.toString().getBytes().length, address, Integer.parseInt(DsPort));
        
            socket.send(packet);
            
            // --------------- TEMP ---------------------
            System.out.println("Envio packet pra ligaÁ„o ao ds");
            this.IP = packet.getAddress().getHostAddress(); // voltar a adicionar o ip, pois pode ter mudado
            // ----------------endTEMP------------------
            
            //Receber dados acerca do servidor

            byte[] buf = new byte[4056];
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            // --------------- TEMP ---------------------
            System.out.println("Envio packet pra ligaÁ„o ao ds");
            // ----------------endTEMP------------------
            
            String strObj = new String(packet.getData(), 0, packet.getLength());

            JSONParser JsonParser = new JSONParser();
            JSONObject JObj = (JSONObject) JsonParser.parse(strObj);

            System.out.println(JObj.toString()); // Testar que objeto È returnado

            //Colocar aqui tratamento do servidor agregado

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
