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

public class ManageStarts extends Thread{

    private final int TotalBytes = 4096;

    private boolean finish;

    //Dados do DS
    private String IP;
    private String Port;

    //Variável de gestão de servidores
    private ArrayList<Server> servers;

    public ManageStarts(String IP, String port, ArrayList<Server> servers) {
        this.finish = false;
        this.IP = IP;
        Port = port;
        this.servers = servers;
    }

    //Thread para receber todos os novos servidores
    @Override
    public void run() {

        DatagramSocket Socket;
        DatagramPacket Packet;

        try {

            Socket = new DatagramSocket(Integer.parseInt(Port));

            while (!finish) {

                System.out.println("Estou a espera de Objectos: "); // TEMP
                byte[] buf = new byte[TotalBytes];
                Packet = new DatagramPacket(buf, buf.length);
                Socket.receive(Packet);

                String SObj = new String(Packet.getData(), 0, Packet.getLength());
                JSONParser JsonParser = new JSONParser();
                JSONObject JObj = (JSONObject) JsonParser.parse(SObj);

                String msg = (String) JObj.get("msg");

                //Tratamento de mensagens
                if ("dataSubmitClient".equals(msg)) {

                    //Cliente
                    System.out.println("Encontrei um cliente");
                    tratamentoClientes(Socket, Packet.getAddress(),
                            ("" + Packet.getPort()));

                } else if ("dataSubmitServer".equals(msg)) {

                    //Servidor
                    System.out.println("Encontrei um servidor");
                    tratamentoServidores(Packet);

                }

            }

        } catch(IOException e){
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void terminate(){
        finish = true;
    }

    private void tratamentoServidores(DatagramPacket packet){

        String IP = packet.getAddress().toString();
        int Port = packet.getPort();
        String sPort = ("" + Port);

        if (servers.size() == 0)
            servers.add(new Server(IP,sPort,0, true,true));
        else
            servers.add(new Server(IP,sPort,0, true,false));

    }

    private void tratamentoClientes(DatagramSocket socket, InetAddress Ip, String Port){

        try {

            //Tratar do envio ao cliente dos dados do servidor atribuido
            JSONObject obj = new JSONObject();

            if (servers.size() == 0){
                obj.put("msg","sair");
            }
            else{

                Server serverToCli = servers.get(0);
                int min = 0;

                for (Server item: servers)
                    if (item.getNumberClients() < min)
                        serverToCli = item;

                //Servidor atribuido
                obj.put("msg", "serverAtr");
                obj.put("IP", serverToCli.getIP());
                obj.put("Port", serverToCli.getPort());

            }

            System.out.println(Ip);
            DatagramPacket packet = new DatagramPacket(obj.toString().getBytes(),
                    obj.toString().getBytes().length,
                    Ip, Integer.parseInt(Port));

            socket.send(packet);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
