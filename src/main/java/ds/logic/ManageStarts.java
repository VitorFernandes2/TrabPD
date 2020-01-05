package ds.logic;

import ds.logic.gest.Client;
import ds.logic.gest.Server;
import ds.logic.gest.ServerList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManageStarts implements Runnable {

    private final int TotalBytes = 4096;

    private boolean finish;

    //Dados do DS
    private String IP;
    private String Port;
    private DatagramSocket Socket;

    //Variável de gestão de servidores
    private ServerList servers;

    public ManageStarts(String IP, String port, ServerList servers) {
        this.finish = false;
        this.IP = IP;
        Port = port;
        this.servers = servers;
    }
    
    public synchronized void start() {
        new Thread(this).start();
    }

    //Thread para receber todos os novos servidores
    @Override
    public void run() {
        
        DatagramPacket Packet;

        try {

            Socket = new DatagramSocket(Integer.parseInt(Port));

            while (!finish && !Socket.isClosed()) {

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
                    System.out.println("Encontrei um cliente.");
                    tratamentoClientes(Socket, Packet.getAddress(),
                            ("" + Packet.getPort()));

                } else if ("dataSubmitServer".equals(msg)) {

                    //Servidor
                    String Port = (String) JObj.get("Port");
                    System.out.println(Port);

                    System.out.println("Encontrei um servidor.");
                    tratamentoServidores(Packet, Port, Socket);

                } else if ("dataEraseServer".equals(msg)) {

                    //Eliminar um servidor desligado
                    String Port = (String) JObj.get("Port");
                    System.out.println(Port);

                    System.out.println("Encontrei um pedido de desligar de servidor.");
                    tratamentoEraseServidores(Packet, Port, Socket);

                } else if ("dataEraseClient".equals(msg)) {

                    //Eliminar um cliente que saiu do servidor
                    String Port = (String) JObj.get("Port");
                    System.out.println(Port);

                    System.out.println("O servidor de porto " + Port + " teve um cliente que saiu.");
                    tratamentoEraseClientes(Packet, Port, Socket, (String) JObj.get("ClientPort"), (String) JObj.get("ClientIP"));

                } else if ("Desliga DS".equals(msg)) {
                    Socket.close();
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

    private void tratamentoServidores(DatagramPacket packet, String Port,DatagramSocket socket){

        String IP = packet.getAddress().getHostAddress();

        System.out.println(IP);
        int resposta;

        if (servers.size() == 0){
            servers.add(new Server(IP, Port, true,true));
            resposta = 0;}
        else{
            servers.add(new Server(IP, Port, true,false));
            resposta = 1;}
        
        /// tratamento de principal
        byte[] b = String.valueOf(resposta).getBytes();
        
        DatagramPacket packete = new DatagramPacket(b,
                    b.length,
                    packet.getAddress(), packet.getPort());

        try {
            socket.send(packete);
        } catch (IOException ex) {
            System.out.println("Nao foi possivel enviar a role (principal ou nao) para o servidor. Erro :" + ex.getLocalizedMessage());
        }
        
    }

    private void tratamentoEraseServidores(DatagramPacket packet, String Port, DatagramSocket socket){

        String IP = packet.getAddress().getHostAddress();

        System.out.println(IP);
        boolean erased = false;

        if(!servers.isEmpty()){
            //Verifica todos os servidores
            for (Server item: servers){
                if (item.getPort().equals(Port)){
                    item.turnOff();
                    erased = true;
                    break;
                }
            }
        }
        
        /// tratamento de principal
        byte[] b = String.valueOf(erased).getBytes();
        
        DatagramPacket packete = new DatagramPacket(b,
                    b.length,
                    packet.getAddress(), packet.getPort());

        try {
            socket.send(packete);
        } catch (IOException ex) {
            System.out.println("Nao foi possivel enviar a role (principal ou nao) para o servidor. Erro :" + ex.getLocalizedMessage());
        }
        
    }

    private void tratamentoEraseClientes(DatagramPacket packet, String Port, DatagramSocket socket, String ClientPort, String ClientIP){

        String IP = packet.getAddress().getHostAddress();
        boolean lessOne = false;

        if(!servers.isEmpty()){
            //Verifica todos os servidores
            for (Server item: servers){
                if (item.getPort().equals(Port)){
                    Client teee = new Client(ClientIP, ClientPort);
                    System.out.println("Removed client IP: " + teee.getIp());
                    System.out.println("Removed client Port: " + teee.getPort());
                    item.removeClient(teee);
                    lessOne = true;
                    break;
                }
            }
        }
        
        /// tratamento de principal
        byte[] b = String.valueOf(lessOne).getBytes();
        
        DatagramPacket packete = new DatagramPacket(b,
                    b.length,
                    packet.getAddress(), packet.getPort());

        try {
            socket.send(packete);
        } catch (IOException ex) {
            System.out.println("Nao foi possivel enviar a role (principal ou nao) para o servidor. Erro :" + ex.getLocalizedMessage());
        }
        
    }

    private void tratamentoClientes(DatagramSocket socket, InetAddress Ip, String Port){

        try {

            //Tratar do envio ao cliente dos dados do servidor atribuido
            JSONObject obj = new JSONObject();

            if (servers.size() == 0){
                System.out.println("[ERROR] Nao foi encontrado nenhum servidor (nenhum servidor ativo)");
                obj.put("msg","sair");
            }
            else{
                //Se houver servidores

                Server serverToCli = servers.get(0);
                int min = serverToCli.getNumberClients();

                //Verifica todos os servidores e va aquele que tiver menos clientes
                for (Server item: servers)
                    if (item.isOn())
                        if (item.getNumberClients() < min){
                            serverToCli = item;
                            min = item.getNumberClients();
                        }
                
                Client client = new Client(String.valueOf(Ip), Port);
                serverToCli.addClient(client);
                //Servidor atribuido
                obj.put("msg", "serverAtr");
                obj.put("IP", serverToCli.getIP());
                obj.put("Port", serverToCli.getPort());

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
