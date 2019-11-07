package client.logic.comunication.server;

import java.net.*;
import java.util.*;
import org.json.simple.JSONObject;


public class ClientStart extends Thread {

    //Objetos para os comandos do utilizador
    private Scanner Sc;
    private String [] Command;
    
    //Objetos para a ligação entre aplicações
    private String Port;
    private String IP;
    
    //Objetos para a ligação UDP com o DS
    private DatagramSocket DSocket;
    private DatagramPacket DPacketSend;
    private DatagramPacket DPacketReceive;
    
    //Objetos para a ligação TCP com o Servidor
    private ServerSocket ServerSocket;
    private Socket Socket;
    
    //Objecto JSON para receber informaÃ§Ã£o do servidor
    private JSONObject ReceiveJsonObj;

    public ClientStart(String Port, String IP) {
        this.Port = Port;
        this.IP = IP;
        this.Sc = new Scanner(System.in);
        this.ReceiveJsonObj = new JSONObject();
    }
    
    @Override
    public void run() {
        //Código de ligação:
        
        //******************
        
        while(true){
            //Implementação de código de envio de pedidos de comando
            
            //******************************************************
            System.out.printf("> ");
            String Com = Sc.nextLine();
            
            //Fazer o corte dos comandos
            
            //**************************
            
            if(Com.equalsIgnoreCase("exit")){
                break;
            }
            
        }
        
    }

    @Override
    public synchronized void start() {
        super.start();
    }
    
}
