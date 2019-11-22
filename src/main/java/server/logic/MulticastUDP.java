/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.logic;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 *
 * @author Joao Coelho
 */
public class MulticastUDP {

    private static MulticastSocket multicastSock;
    
    //public static void main(String[] args) throws UnknownHostException, IOException {
    public static void comecamulticast() throws UnknownHostException, IOException {
        // TODO code application logic here
        
        InetAddress group = InetAddress.getByName("225.4.5.6");
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
                    
                }
            } catch (Exception e){}
 
        }
    };
   
    
    
}
