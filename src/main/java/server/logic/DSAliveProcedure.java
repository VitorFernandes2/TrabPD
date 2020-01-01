package server.logic;

import mainObjects.Contants;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.ServerLogic;

import java.io.IOException;
import java.net.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
                
                serverLogic.getSd().getObjMudance().put("output", msg);
                serverLogic.notifyObserver(1);

                //get the json message
                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject)parser.parse(msg);

                if (obj.get("message").equals("isAlive")){

                    long numberOfServers = (long) obj.get("numberOfServers");
                    serverLogic.getSd().setNumberOfServers(numberOfServers);
                    
                    serverLogic.getSd().getObjMudance().put("output", "O numero de servidores e: " + numberOfServers);
                    serverLogic.notifyObserver(1);

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
                    
                    serverLogic.getSd().getObjMudance().put("output", "Um servidor foi abaixo o numero de servidores e: " + numberOfServers);
                    serverLogic.notifyObserver(1);

                }
                else if (obj.get("message").equals("giveadmin")){
                    
                    String namebd = (String) obj.get("namebd");
                    
                    serverLogic.getSd().getObjMudance().put("output", "Este servidor passou a ser o servidor principal: " + namebd);
                    serverLogic.notifyObserver(1);
                    
                    serverLogic.getDbaction().setNamedb(namebd);

                }

            }

        } catch (SocketException e) {
            serverLogic.getSd().getObjMudance().put("exception", "[ERROR] Erro no socket de pings de ligação.\n" + e.getMessage());
            serverLogic.notifyObserver(4);
        } catch (IOException e) {
            serverLogic.getSd().getObjMudance().put("exception", "[ERROR] Erro nos pings de ligação.\n" + e.getMessage());
            serverLogic.notifyObserver(4);
        } catch (ParseException e) {
            serverLogic.getSd().getObjMudance().put("exception", "[ERROR] Erro no Parse do objeto JSON nos pings de ligação.\n" + e.getMessage());
            serverLogic.notifyObserver(4);
        } catch (SQLException e) {
            serverLogic.getSd().getObjMudance().put("exception", "[ERROR] Erro no Código de ligação à base de dados.\n" + e.getMessage());
            serverLogic.notifyObserver(4);
        }

    }

    public void terminate(){
        this.finished = true;
    }

}
