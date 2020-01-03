package client.logic;

import client.InterfaceGrafica.Interfacemain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.*;

public class DsConnect {

    private String DsIp;
    private String DsPort;
    private String ServerPort;
    private String ServerIp;
    private String IP;
    private String Port;
    private Interfacemain inter;
    private ConnectData datacomun;
    private JSONObject JObjE;

    public DsConnect(String dsIp, String dsPort, String serverPort,
                       String serverIp, String IP, String port) {
        this.DsIp = dsIp;
        this.DsPort = dsPort;
        this.ServerPort = serverPort;
        this.ServerIp = serverIp;
        this.IP = IP;
        this.Port = port;
    }

    public DsConnect(String dsIp, String dsPort){
        
        this.IP = dsIp;
        DsPort = dsPort;
        try{
            DsIp = (InetAddress.getLocalHost()).getHostAddress();
        }catch (Exception e)
        {
            System.out.println("Erra ao detetar o IP");
            System.out.println("IP default 9999 atribuido");
            this.IP = "9999";
        }
        Port = "9999";
    }

    DsConnect(String dsIp, String dsPort, Interfacemain inter, ConnectData datacomun) {
        this.IP = dsIp;
        DsPort = dsPort;
        try{
            DsIp = (InetAddress.getLocalHost()).getHostAddress();
        }catch (Exception e)
        {
            System.out.println("Erra ao detetar o IP");
            System.out.println("IP default 9999 atribuido");
            this.IP = "9999";
        }
        Port = "9999";
        this.inter = inter;
        this.datacomun = datacomun;
    }
    
    
    /*
        Serve para mandar ao Ds a informao acerca do cliente e coseguir receber informao
        relativa ao servidor que este cliente se vai alistar
     */
    public String run() {

        DatagramSocket socket;
        DatagramPacket packet;
        InetAddress address;

        try {

            //Criao da socket UDP
            socket = new DatagramSocket();
            socket.setSoTimeout(10000);
            address = InetAddress.getByName(DsIp);

            JSONObject obj = new JSONObject();

            obj.put("msg", "dataSubmitClient");
            obj.put("IP", IP);
            obj.put("Port", Port);

            //Criao do packet de envio dos dados relativos ao cliente para o DS
            packet = new DatagramPacket(obj.toString().getBytes(),
                    obj.toString().getBytes().length, address, Integer.parseInt(DsPort));
        
            socket.send(packet);
            
            // --------------- TEMP ---------------------
            System.out.println("DEBUG - Envio packet para ligao ao ds");
            this.IP = packet.getAddress().getHostAddress(); // voltar a adicionar o ip, pois pode ter mudado
            // ----------------endTEMP------------------
            
            //Receber dados acerca do servidor

            byte[] buf = new byte[4056];
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            // --------------- TEMP ---------------------
            System.out.println("DEBUG - Recebi packet do ds");
            // ----------------endTEMP------------------
            
            String strObj = new String(packet.getData(), 0, packet.getLength());

            JSONParser JsonParser = new JSONParser();
            JSONObject JObj = (JSONObject) JsonParser.parse(strObj);
            
            System.out.println("DEBUG - " + JObj.toString()); // Testar que objeto  returnado

            return JObj.toString();
            //Colocar aqui tratamento do servidor agregado

        } catch (SocketException e) {
            JObjE = new JSONObject();
            JObjE.put("exception", e.toString());
            datacomun.setJObj(JObjE);
            inter.update(444, datacomun);
        } catch (UnknownHostException e) {
            JObjE = new JSONObject();
            JObjE.put("exception", e.toString());
            datacomun.setJObj(JObjE);
            inter.update(444, datacomun);
        } catch (IOException e) {
            JObjE = new JSONObject();
            JObjE.put("exception", e.toString());
            datacomun.setJObj(JObjE);
            inter.update(444, datacomun);
        } catch (ParseException e) {
            JObjE = new JSONObject();
            JObjE.put("exception", e.toString());
            datacomun.setJObj(JObjE);
            inter.update(444, datacomun);
        }
        return "error";
    }

}
