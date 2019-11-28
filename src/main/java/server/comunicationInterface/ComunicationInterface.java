package server.comunicationInterface;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;
import org.json.simple.JSONObject;
import server.interfaces.observerServer;
import server.logic.ServerData;
import server.logic.ThreadClientRequests;

public class ComunicationInterface implements observerServer{
    
    private ServerData sd;

    public ComunicationInterface(ServerData sd) {
        this.sd = sd;
    }

    public ServerData getSd() {
        return sd;
    }

    public void setSd(ServerData sd) {
        this.sd = sd;
    }
    
    private void connect(){
        DatagramSocket socket;
        
        try {
            socket = new DatagramSocket(0);

            InetAddress address = InetAddress.getByName(this.sd.getDsIP());

            sd.getObjMudance().put("msg", "dataSubmitServer");
            
            sd.getObjMudance().put("Port", "" + socket.getLocalPort());

            sd.setServerPort(socket.getLocalPort());

            String StrToSend = sd.getObjMudance().toString();
            
            byte [] buf = StrToSend.getBytes();
            
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, Integer.parseInt(this.sd.getDsPort()));

            socket.send(packet);

            socket.close();
            
        } catch (SocketException ex) {
            
            sd.getObjMudance().put("exception", ex.getMessage());
            
            this.update(4, sd.getObjMudance());
            
        } catch (UnknownHostException ex) {
            
            sd.getObjMudance().put("exception", ex.getMessage());
            
            this.update(4, sd.getObjMudance());
            
        } catch (IOException ex) {
            
            sd.getObjMudance().put("exception", ex.getMessage());
            
            this.update(4, sd.getObjMudance());
            
        }
        
        sd.getObjMudance().put("output", "Server a correr.");
        
        this.update(1, sd.getObjMudance());
        
        ThreadClientRequests threadclass = new ThreadClientRequests(this); // pode ser mai pratica REVER
        
        threadclass.start();

        Scanner myObj = new Scanner(System.in);  // TEMP - pausa para manter a thread a correr. escreve algo pra parar thread
        
        String userName = myObj.nextLine();
        
        try {
            
            threadclass.stopthread();
            
            System.exit(0);//retirar depois para tratamento de morte do servidor
            
        } catch (IOException ex) {
            
            sd.getObjMudance().put("output", "[ERROR] Terminação forçada do Servidor.\n" + ex.getMessage());
            
            this.update(1, sd.getObjMudance());
            
        }
    }
    
    @Override
    public void update(int acao, JSONObject Ob) {
        
        switch(acao){
            
            case 1:
                String output = (String) Ob.get("output");
                System.out.println(output);
                break;
                
            case 2:
                System.out.println("Pedido de Ligação ao DS.");
                break;
                
            case 4:
                String excepcao = (String) Ob.get("exception");
                System.out.println(excepcao);
                break;
                
            case 5:
                connect();
                break;
                
            default:
                System.out.println("Default!");
        
        }
        
    }
    
}
