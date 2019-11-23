package ds.logic;

import ds.logic.gest.Server;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class ManageServers extends Thread {

    private ArrayList<Server> servers;
    private boolean terminate;
    private DatagramSocket socket;

    public ManageServers(ArrayList<Server> servers) {
        this.servers = servers;

        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        terminate = false;

    }

    @Override
    public void run() {

        //A thread corre enquanto não haver a informação para este terminar
        while (!terminate){

            //Corre a lista de servidores e comunica com estes
            for (Server serv: servers) {

                synchronized (servers){
                    //Se o servidor não estiver vivo
                    if (!isAlive(serv)){

                        System.out.println("O servidor " + serv.getIP() + " morreu");
                        migrateClients(serv);

                    }
                }

            }

        }

    }

    //Esta função manda um ping ao servidor e espera pela resposta
    private boolean isAlive(Server server){

        JSONObject Jobj = new JSONObject();
        Jobj.put("msg","isAlive");

        byte[] buf = Jobj.toString().getBytes();

        try {

            //Envio de pedido de respota para saber se está vivo

            InetAddress IP = InetAddress.getByName(server.getIP());
            int PORT = Integer.parseInt(server.getPort());
            System.out.println(PORT);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, IP, PORT);
            socket.send(packet);

            //10 segundos de espera
            socket.setSoTimeout(10000);

            while (true){
                try {

                    //Espera resposta
                    buf = new byte[256];
                    packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);

                    String temp = new String(packet.getData(), 0, packet.getLength());
                    JSONParser JsonParser = new JSONParser();
                    JSONObject object = (JSONObject) JsonParser.parse(temp);

                    System.out.println("O servidor " +
                            server.getIP() + " disse " + object.get("msg"));

                    return true;

                }catch (SocketTimeoutException e){
                    return false;
                }catch (ParseException e) {
                    e.printStackTrace();
                }

            }

        }catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  true;

    }

    //Esta função migra os clientes de um servidor para outro se existir
    private void migrateClients(Server server){



    }

    public void endThread(){
        terminate = true;
    }

}
