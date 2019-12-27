package server.logic;

import mainObjects.Contants;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.ServerLogic;

import java.io.IOException;
import java.net.*;

public class DSAliveProcedure extends Thread {

    private boolean finished;
    private ServerLogic serverLogic;
    private DatagramSocket socket;

    public DSAliveProcedure(ServerLogic serverLogic) {
        this.finished = false;
        this.serverLogic = serverLogic;
    }

    @Override
    public void run() {

        try {

            //Socket que vai receber a mensagem para saber se está vivo
            socket = new DatagramSocket(serverLogic.getSd().getServerPort());

            while (!finished){

                byte[] buf = new byte[Contants.TOTALBYTES];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String msg = new String(packet.getData(), 0 , packet.getLength());
                System.out.println(msg);

                //get the json message
                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject)parser.parse(msg);

                if (obj.get("message").equals("isAlive")){

                    long numberOfServers = (long) obj.get("numberOfServers");
                    serverLogic.getSd().setNumberOfServers(numberOfServers);

                    System.out.printf("O numero de servidores e: " + numberOfServers);

                    int Port = packet.getPort();
                    InetAddress IP = packet.getAddress();

                    String reply = "Greetings!";
                    buf = reply.getBytes();
                    packet = new DatagramPacket(buf, buf.length, IP, Port);
                    socket.send(packet);

                }
                else if (obj.get("message").equals("ServerDown")){

                    long numberOfServers = (long) obj.get("numberOfServers");
                    serverLogic.getSd().setNumberOfServers(numberOfServers);

                    System.out.printf("Um servidor foi abaixo o numero de servidores e: " + numberOfServers);

                }

            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void terminate(){
        this.finished = true;
    }

}
