package client.logic.comunication.server;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class ClientStart extends Thread {

    //Objetos para os comandos do utilizador
    private Scanner Sc;
    private String [] Command;
    private String Com;
    
    //Objeto do nome do utilizador / posi��o provis�ria at� confirma��o por parte do grupo
    private String username;
    
    //Objetos para a liga��o entre aplica��es
    private String Port;
    private String IP;
    
    //Objetos para a liga��o UDP com o DS
    private DatagramSocket DSocket;
    private DatagramPacket DPacketSend;
    private DatagramPacket DPacketReceive;
    
    //Objetos para a liga��o TCP com o Servidor
    private Socket Socket;
    
    //Objecto JSON para receber informação do servidor
    private static int TotalBytes = 4096;
    private JSONObject ReceiveJsonObj;
    private JSONObject SendJsonObj;

    public ClientStart(String Port, String IP) {
        this.Port = Port;
        this.IP = IP;
        this.Sc = new Scanner(System.in);
        this.ReceiveJsonObj = new JSONObject();
        this.SendJsonObj = new JSONObject();
    }

    //Fun��es a usar caso o Cliente tenha de ser relocado para um novo servidor
    public void setPort(String Port) {
        this.Port = Port;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }
    
    @Override
    public void run() {
        
        try {
            //C�digo de liga��o inicial com o DS:
            
            DSocket = new DatagramSocket(Integer.parseInt(Port));
            
            SendJsonObj.put("IP", String.valueOf(InetAddress.getLocalHost()));
            SendJsonObj.put("Port", String.valueOf(DSocket.getLocalPort()));
            
            String sendDataJSON = SendJsonObj.toString();
            
            byte [] send = sendDataJSON.getBytes();
            
            DPacketSend = new DatagramPacket(send, send.length, InetAddress.getByName(IP), Integer.parseInt(Port));
            
            DSocket.send(DPacketSend);
            
            byte [] receive = new byte[TotalBytes];
            
            DPacketReceive = new DatagramPacket(receive, receive.length);
            
            String SObj = new String(DPacketReceive.getData(), 0, DPacketReceive.getLength());
            JSONParser JsonParser = new JSONParser();
            ReceiveJsonObj = (JSONObject) JsonParser.parse(SObj);

            System.out.println(ReceiveJsonObj);
            
            //Passagem dos valores a usar para TCP:
            IP = (String) ReceiveJsonObj.get("IP");
            Port = (String) ReceiveJsonObj.get("Port");
            
            DSocket.close();
            
            //***********************************
            
            //Inicio da liga��o TCP com o Servidor
            
            Socket = new Socket(IP, Integer.parseInt(Port));
            
            while(Com.equalsIgnoreCase("exit") || Com.equalsIgnoreCase("logout")){
                //Implementa��o de c�digo de envio de pedidos de comando
                
                System.out.printf("> ");
                Com = Sc.nextLine();
                
                Command = Com.trim().split(";");
                
                //Se o comando for apenas uma palavra
                if(Command.length < 2){
                    //C�digo do tratamento de saida do utilizador com o envio do fim de sess�o para o servidor
                    
                    //****************************************************************************************
                    continue;
                }
                
                SendJsonObj = tratmentOfCommands(Command);
                
                //Local de inic�o de envio e resposta do Cliente-Servidor
                
                //******************************************************
            }
            
            Socket.close();
            
            //************************************
            
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    private JSONObject tratmentOfCommands(String[] Command) {
        
        JSONObject AuxJsonObj = new JSONObject();
        
        for(String comando : Command){
            
            String [] div = comando.split("|");
            
            if(div[0].equalsIgnoreCase("Tipo")){
                
                if(div[1].equalsIgnoreCase("login")){
                    AuxJsonObj.put("Tipo", "login");
                }
                else if(div[1].equalsIgnoreCase("Logout")){
                    AuxJsonObj.put("Tipo", "logout");
                }
            }
            //else if(div[0])
            //Falta a continua��o da realiza��o desta fun��o!
        }
        
        return AuxJsonObj;
    }
    
}
