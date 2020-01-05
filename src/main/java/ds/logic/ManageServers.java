package ds.logic;


import ds.logic.gest.Server;
import ds.logic.gest.ServerList;
import mainObjects.Contants;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManageServers extends Thread {

    private ServerList servers;
    private boolean terminate;

    public ManageServers(ServerList servers) {

        this.servers = servers;
        this.terminate = false;

    }

    @Override
    synchronized public void run() {

        DatagramPacket packet;
        DatagramSocket socket;

        try {

            socket = new DatagramSocket();

            while (!terminate) {

                ServerList serversAux = new ServerList();
                serversAux.addAll(servers);

                for (Server item : serversAux) {

                    //Pinga o servidor

                    JSONObject objSend = new JSONObject();
                    objSend.put("message", "isAlive");
                    objSend.put("numberOfServers", serversAux.size());

                    String msgToSend = objSend.toJSONString();
                    byte[] buf = msgToSend.getBytes();
                    InetAddress serverIp = InetAddress.getByName(item.getIP());
                    int serverPort = Integer.parseInt(item.getPort());
                    packet = new DatagramPacket(buf, buf.length, serverIp, serverPort);
                    socket.send(packet);

                    try{

                        //Espera pela resposta

                        socket.setSoTimeout(20000);
                        buf = new byte[Contants.TOTALBYTES];
                        packet = new DatagramPacket(buf, buf.length);
                        socket.receive(packet);

                        String msgReply = new String(packet.getData(), 0, packet.getLength());
                        System.out.println(msgReply);

                    }catch (SocketTimeoutException e){
                        System.out.println("Este morreu");

                        //Remove o servidor
                        servers.remove(item);

                        //Se for o principal
                        if (item.isPrinci() && !servers.isEmpty()){
                            servers.get(0).setPrinci();
                            
                            //Aviso do novo servidor principal que a sua base de dados mudou
                            String IP = servers.get(0).getIP();
                            String Port = servers.get(0).getPort();
                            JSONObject jj = new JSONObject();
                            jj.put("message", "giveadmin");
                            jj.put("namebd", "Principal");
                            String resetPrincipal = jj.toJSONString();
                            byte[] buffs = resetPrincipal.getBytes();
                            InetAddress serverIpReset = InetAddress.getByName(IP);
                            int serverPortReset = Integer.parseInt(Port);
                            packet = new DatagramPacket(buffs, buffs.length, serverIpReset, serverPortReset);
                            socket.send(packet);
                            //--------------------------------------------------------------
                            
                        }
                        //Se não for o principal e não existirem mais servidores
                        else if(servers.isEmpty()){
                            //saida para reinicio dos checks de cada servidor
                            break;
                        }
                        
                        ServerList serverAux2 = new ServerList();
                        serverAux2.addAll(servers);

                        //Informar os servidores

                        for (Server item2 : serverAux2){

                            JSONObject objSend2 = new JSONObject();
                            objSend2.put("message", "ServerDown");
                            objSend2.put("numberOfServers", serverAux2.size());

                            String msgToSend2 = objSend2.toJSONString();
                            byte[] buf2 = msgToSend2.getBytes();
                            InetAddress serverIp2 = InetAddress.getByName(item2.getIP());
                            int serverPort2 = Integer.parseInt(item2.getPort());
                            packet = new DatagramPacket(buf2, buf2.length, serverIp2, serverPort2);
                            socket.send(packet);

                        }
                        
                    }

                }
                
                if(servers.size() > serversAux.size() && servers.size() > 1){
                    JSONObject objSend = new JSONObject();
                    objSend.put("message", "isAlive");
                    objSend.put("numberOfServers", serversAux.size());

                    String msgToSend = objSend.toJSONString();
                    byte[] buf = msgToSend.getBytes();
                    InetAddress serverIp = InetAddress.getByName(servers.get(servers.size() - 1).getIP());
                    int serverPort = Integer.parseInt(servers.get(servers.size() - 1).getPort());
                    packet = new DatagramPacket(buf, buf.length, serverIp, serverPort);
                    socket.send(packet);
                    
                    try{

                        //Espera pela resposta

                        socket.setSoTimeout(20000);
                        buf = new byte[Contants.TOTALBYTES];
                        packet = new DatagramPacket(buf, buf.length);
                        socket.receive(packet);

                        String msgReply = new String(packet.getData(), 0, packet.getLength());
                        System.out.println(msgReply);

                    }catch (SocketTimeoutException e){
                        System.out.println("Este morreu");

                        //Remove o servidor
                        servers.remove(servers.get(servers.size() - 1));

                        //Se for o principal
                        if (servers.get(servers.size() - 1).isPrinci() && !servers.isEmpty()){
                            servers.get(0).setPrinci();
                            
                            //Aviso do novo servidor principal que a sua base de dados mudou
                            String IP = servers.get(0).getIP();
                            String Port = servers.get(0).getPort();
                            JSONObject jj = new JSONObject();
                            jj.put("message", "giveadmin");
                            jj.put("namebd", "Principal");
                            String resetPrincipal = jj.toJSONString();
                            byte[] buffs = resetPrincipal.getBytes();
                            InetAddress serverIpReset = InetAddress.getByName(IP);
                            int serverPortReset = Integer.parseInt(Port);
                            packet = new DatagramPacket(buffs, buffs.length, serverIpReset, serverPortReset);
                            socket.send(packet);
                            //--------------------------------------------------------------
                            
                        }
                        //Se não for o principal e não existirem mais servidores
                        else if(servers.isEmpty()){
                            //saida para reinicio dos checks de cada servidor
                            break;
                        }
                        
                        ServerList serverAux2 = new ServerList();
                        serverAux2.addAll(servers);

                        //Informar os servidores

                        for (Server item2 : serverAux2){

                            JSONObject objSend2 = new JSONObject();
                            objSend2.put("message", "ServerDown");
                            objSend2.put("numberOfServers", serverAux2.size());

                            String msgToSend2 = objSend2.toJSONString();
                            byte[] buf2 = msgToSend2.getBytes();
                            InetAddress serverIp2 = InetAddress.getByName(item2.getIP());
                            int serverPort2 = Integer.parseInt(item2.getPort());
                            packet = new DatagramPacket(buf2, buf2.length, serverIp2, serverPort2);
                            socket.send(packet);

                        }
                        
                    }
                    
                }

                Thread.sleep(10000);

            }
            
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void terminate(){
        terminate = true;
        
        DatagramPacket packet;
        DatagramSocket socket;

        try {

            socket = new DatagramSocket();
        
            ServerList serversAux = new ServerList();
            serversAux.addAll(servers);

            //Avisa cada servidor que tem de morrer
            
            for (Server item : serversAux) {
                
                JSONObject objSend = new JSONObject();
                objSend.put("message", "orderToDie");
                objSend.put("numberOfServers", serversAux.size());

                String msgToSend = objSend.toJSONString();
                byte[] buf = msgToSend.getBytes();
                InetAddress serverIp = InetAddress.getByName(item.getIP());
                int serverPort = Integer.parseInt(item.getPort());
                packet = new DatagramPacket(buf, buf.length, serverIp, serverPort);
                socket.send(packet);

            }
            
            servers.clear();
        
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

}