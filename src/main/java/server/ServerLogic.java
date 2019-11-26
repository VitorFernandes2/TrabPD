package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;
import org.json.simple.JSONObject;
import server.comunicationInterface.ComunicationInterface;
import server.interfaces.subjectServer;
import server.logic.ServerData;
import server.logic.ThreadClientRequests;

public class ServerLogic implements subjectServer{

    private ServerData sd;
    private ComunicationInterface sml;
    private JSONObject Ob;

    public ServerLogic(ServerData sd) {
        
        this.sd = sd;
        
        this.Ob = new JSONObject();
    
    }

    public ServerData getSd() {
        
        return sd;
    
    }

    public void setSd(ServerData sd) {
        
        this.sd = sd;
    
    }

    public JSONObject getOb() {
        
        return Ob;
    
    }

    public void setOb(JSONObject Ob) {
        
        this.Ob = Ob;
    
    }
    
    public void connect(){
        
        this.notifyObserver(2);
        
        DatagramSocket socket;
        
        try {
            socket = new DatagramSocket(0);

            InetAddress address = InetAddress.getByName(this.sd.getDsIP());

            JSONObject ObjSend =  new JSONObject();

            ObjSend.put("msg", "dataSubmitServer");
            
            ObjSend.put("Port", "" + socket.getLocalPort());

            sd.setServerPort(socket.getLocalPort());

            String StrToSend = ObjSend.toString();
            
            byte [] buf = StrToSend.getBytes();
            
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address,
                    Integer.parseInt(this.sd.getDsPort()));

            socket.send(packet);

            socket.close();
            
        } catch (SocketException ex) {
            
            this.Ob.put("exception", ex.getMessage());
            
            this.notifyObserver(4);
            
        } catch (UnknownHostException ex) {
            
            this.Ob.put("exception", ex.getMessage());
            
            this.notifyObserver(4);
            
        } catch (IOException ex) {
            
            this.Ob.put("exception", ex.getMessage());
            
            this.notifyObserver(4);
            
        }
        
        Ob.put("output", "Server a correr.");
        
        this.notifyObserver(1);
        
        ThreadClientRequests threadclass = new ThreadClientRequests(this); // pode ser mai pratica REVER
        
        threadclass.start();

        Scanner myObj = new Scanner(System.in);  // TEMP - pausa para manter a thread a correr. escreve algo pra parar thread
        
        String userName = myObj.nextLine();
        
        try {
            
            threadclass.stopthread();
            
            System.exit(0);//retirar depois para tratamento de morte do servidor
            
        } catch (IOException ex) {
            
            Ob.put("output", "[ERROR] Terminação forçada do Servidor.\n" + ex.getMessage());
            
            this.notifyObserver(1);
            
        }
        
    }

    @Override
    public void addinterface(ComunicationInterface sml) {
        
        this.sml = sml;
        
    }

    @Override
    public void notifyObserver(int acao) {
        
        sml.update(acao, Ob);
        
    }
    
}
