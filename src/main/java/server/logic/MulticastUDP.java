/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.logic;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

/**
 *
 * @author Joao Coelho
 */
public class MulticastUDP {

    private static MulticastSocket multicastSock;
    private static InetAddress group;
    private boolean duringupdate = false;
    
    //public static void main(String[] args) throws UnknownHostException, IOException {
    public static void comecamulticast() throws UnknownHostException, IOException {
        // TODO code application logic here
        
        group = InetAddress.getByName("225.4.5.6");
        multicastSock = new MulticastSocket(3456);
        multicastSock.joinGroup(group);
        boolean corre= true;
            
        new Thread(t1).start();
        while(corre){
            
            Scanner myObj = new Scanner(System.in);
            String msg = myObj.nextLine();
 
            DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(),group,3456);
            
            multicastSock.send(packet);

        }
        
        multicastSock.close();
        
    }

    private static Runnable t1 = new Runnable() {
        public void run() {
            try{
                while(true){
                    
                    byte [] buffer = new byte [100];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    multicastSock.receive(packet);
                    System.out.println("Recebi: " + new String(buffer));
                    
                    //updatedatabase(JObjrecebido);
                    
                }
            } catch (Exception e){}
 
        }
    };
    
    public void sendDataBaseUpdate(JSONObject JObjrecebido){
        //REVER -> Meter dentro de um thread? remover a thread já feita? cuidado q tem wait de thread. O timeour está correto?
        try {
            if(multicastSock == null){
                group = InetAddress.getByName("225.4.5.6");
                multicastSock = new MulticastSocket(3456);
                multicastSock.joinGroup(group);
            }
            duringupdate = true;    
            // TEMP --> Definir e mandar o necessario
            Scanner myObj = new Scanner(System.in);
            String msg = myObj.nextLine();

            DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(),group,3456);

            multicastSock.send(packet);

            // recebe confirmação
            t1.wait(); // Pausa a thread para q esta n roube a informação
            
            multicastSock.setSoTimeout(10000);
            int numberofrun = 100,pos=1;
            while(pos <= numberofrun){
                try{
                    
                    byte [] buffer = new byte [100];
                    DatagramPacket packetrec = new DatagramPacket(buffer, buffer.length);
                    multicastSock.receive(packet);
                    System.out.println("Recebi: " + new String(buffer));

                }catch (InterruptedIOException e){
                    // TEMP --> Definir e mandar o necessario
                    Scanner myObjs = new Scanner(System.in);
                    String msgs = myObjs.nextLine();
                    DatagramPacket packetes = new DatagramPacket(msgs.getBytes(), msgs.length(),group,3456);
                    multicastSock.send(packetes);
                    multicastSock.setSoTimeout(10000);
                    numberofrun = 100;pos = 1;
                } 
            }
            multicastSock.setSoTimeout(0); // disable timeout
            t1.notify(); // continua a thread q foi parada
            duringupdate = false;
        } catch (UnknownHostException ex) {
            Logger.getLogger(MulticastUDP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MulticastUDP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(MulticastUDP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
