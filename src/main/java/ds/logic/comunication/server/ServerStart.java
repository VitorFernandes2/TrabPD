package ds.logic.comunication.server;

import org.json.simple.JSONObject;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;

public class ServerStart extends Thread {

    private static int NumberOfThreads = 0;
    private int ThreadNumber;

    //Objecto JSON para receber informação do servidor
    private JSONObject ReceiveJsonObj;

    //Dados do DS
    private String IP;
    private String Port;

    //Socket de controlo dos Servidores
    DatagramSocket Socket;
    DatagramPacket Packet;

    //Mapa de IP'S/Portos dos Servidores
    HashMap<String, String> ServerMaps;

    public ServerStart(String IP, String Port) {
        ReceiveJsonObj = new JSONObject();
        ThreadNumber = ++NumberOfThreads;
        this.IP = IP;
        this.Port = Port;
    }

    public static int getNumberOfThreads() {
        return NumberOfThreads;
    }

    public int getThreadNumber() {
        return ThreadNumber;
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void run() {
        super.run();
    }

}
