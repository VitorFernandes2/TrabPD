package ds.logic;

import ds.logic.gest.Client;
import ds.logic.gest.Server;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

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

            ArrayList<Server> serversNew = new ArrayList<>(servers);

            //Corre a lista de servidores e comunica com estes
            for (Server serv: serversNew) {

                synchronized (serversNew){
                    //Se o servidor não estiver vivo
                    if (serv.isOn())
                        if (!isAlive(serv)){

                            System.out.println("O servidor " + serv.getIP() + " morreu");
                            //Migra todos os clientes que o servidor tinha
                            migrateClients(serv, serversNew);
                            //Comunica a todos os servidores que o servidor morreu
                            comunicateServerDeathToServ(serv, serversNew);

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
                    
                    TimeUnit.SECONDS.sleep(1);
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
                } catch (InterruptedException e) {
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
    private void migrateClients(Server server, ArrayList<Server> serverArrayList){

        synchronized (serverArrayList){

            //Mete o servidor como desligado
            server.turnOff();

            int index = 0, conta = -1;

            for (int i = 0; i < serverArrayList.size(); i++) {

                Server item = serverArrayList.get(i);

                if (item.isOn() && conta < 0){
                    conta = item.getNumberClients();
                    index = i;
                }

                //Se o servidor que morreu for o principal, passa o primeiro da lista a
                //ser o principal
                if (server.isPrinci() && item.isOn()){
                    server.unsetPrinci();
                    item.setPrinci();
                }

                //Se nao for o mesmo servidor e estiver ativo
                if (item.isOn() && item.getNumberClients() < conta){

                    conta = item.getNumberClients();
                    index = i;

                }

            }

            //Se houver algum servidor disponivel
            if (conta >= 0){

                Server serv = serverArrayList.get(index);

                for (Client item : server.getClients()) {

                    serv.addClient(item);
                    server.removeClient(item);
                    //comunica a cada cliente que o servidor vai mudar
                    //comunicateServerDeathToCli(item, serv);

                }

            }

        }

    }

    private void comunicateServerDeathToServ(Server server, ArrayList<Server> serverArrayList){

        try {
            DatagramSocket sock = new DatagramSocket();

            for (Server item : serverArrayList) {

                //Se outro servidor estiver ativo
                if (item != server && item.isOn()){

                    //Comunica aos servidores que o servidor mudou
                    InetAddress IP = InetAddress.getByName(item.getIP());
                    int PORT = Integer.parseInt(item.getPort());

                    String msg = server.getIP();
                    JSONObject object = new JSONObject();
                    object.put("msg","serverMorto");
                    object.put("serv", msg);

                    byte[] buf = object.toString().getBytes();
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, IP, PORT);

                    sock.send(packet);

                }

            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void comunicateServerDeathToCli(Client client, Server server){

        try {

            /*
             *Criação de socket de envio de dados para o cliente poder trocar o servidor
             */
            DatagramSocket socket = new DatagramSocket();
            InetAddress IP = InetAddress.getByName(client.getIp());
            int PORT = Integer.parseInt(client.getPort());

            //Criação do objeto que vai comunicar com o cliente qual o novo server
            JSONObject object = new JSONObject();
            object.put("msg", "Server Down");
            object.put("IP", server.getIP());
            object.put("PORT", server.getPort());

            byte[] buf = object.toString().getBytes();

            DatagramPacket packet = new DatagramPacket(buf, buf.length, IP, PORT);
            socket.send(packet);

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void endThread(){
        terminate = true;
    }

}
