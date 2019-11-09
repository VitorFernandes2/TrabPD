package ds.logic;

import ds.logic.gest.Client;
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
import java.util.Arrays;
import java.util.HashMap;

public class ManageStarts extends Thread{

    private final int TotalBytes = 4096;

    private boolean finish;

    //Dados do DS
    private String IP;
    private String Port;

    //Vari√°vel de gest√£o de servidores
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
                    String Port = (String) JObj.get("Port");
                    System.out.println("Encontrei um cliente");
                    tratamentoClientes(Socket, Packet.getAddress(),
                            ("" + Packet.getPort()));

                } else if ("dataSubmitServer".equals(msg)) {

                    //Servidor
                    String Port = (String) JObj.get("Port");
                    System.out.println(Port);

                    System.out.println("Encontrei um servidor");
                    tratamentoServidores(Packet, Port);

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

    private void tratamentoServidores(DatagramPacket packet, String Port){

        String IP = packet.getAddress().getHostAddress();

        System.out.println(IP);

        if (servers.size() == 0)
            servers.add(new Server(IP, Port, true,true));
        else
            servers.add(new Server(IP, Port, true,false));

    }

    private void tratamentoClientes(DatagramSocket socket, InetAddress Ip, String Port){

        try {

            //Tratar do envio ao cliente dos dados do servidor atribuido
            JSONObject obj = new JSONObject();

            if (servers.size() == 0){
                System.out.println("[ERROR] N„o foi encontrado nenhum servidor (nenhum servidor ativo)");
                obj.put("msg","sair");
            }
            else{

                /*Server serverToCli = servers.get(0); ALGORITMO ANTIGO- MANTER?
                int min = 0;

                for (Server item: servers)
                    if (item.getNumberClients() < min)
                        serverToCli = item;*/

                // novo algoritmo           
                
                Server escolhido = null;
                
                for (Server item: servers){
                    if (item.isbeingused() == false){
                        item.addClient(new Client(Arrays.toString(Ip.getAddress()),Port));
                        escolhido = item;
                        break;
                    }
                }
                
                if(escolhido == null){ // fazer exec para adicionar mais um servidor?
                    System.out.println("[ERROR] N„o foi encontrado nenhum servidor (servidores todos ocupados)");
                    obj.put("msg","sair");
                    
                }
                else{
                //Servidor atribuido
                obj.put("msg", "serverAtr");
                obj.put("IP", escolhido.getIP());
                obj.put("Port", escolhido.getPort());
                }
            }

            System.out.println(Ip.getHostAddress());
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
