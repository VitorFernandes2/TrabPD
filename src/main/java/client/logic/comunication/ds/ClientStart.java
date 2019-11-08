package client.logic.comunication.ds;

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

    /*
        Serve para mandar ao Ds a informação acerca do cliente e coseguir receber informação
        relativa ao servidor que este cliente se vai alistar
     */
    public void run() {

        DatagramSocket socket;
        DatagramPacket packet;
        InetAddress address;

        try {

            //Criação da socket UDP
            socket = new DatagramSocket(Integer.parseInt(DsPort));
            address = InetAddress.getByName(DsIp);

            JSONObject obj = new JSONObject();
            obj.put("IP", IP);
            obj.put("Port", Port);

            //Criação do packet de envio dos dados relativos ao cliente para o DS
            packet = new DatagramPacket(obj.toString().getBytes(),
                    obj.toString().getBytes().length, address, Integer.parseInt(DsPort));

            socket.send(packet);

            //Receber dados acerca do servidor

            byte[] buf = new byte[4056];
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            String strObj = new String(buf, 0, buf.length);

            JSONParser JsonParser = new JSONParser();
            JSONObject JObj = (JSONObject) JsonParser.parse(strObj);

            System.out.println(JObj.toString());

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
