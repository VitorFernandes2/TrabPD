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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import server.ServerLogic;

/**
 *
 * @author Joao Coelho
 */
public class MulticastUDP {

    private static MulticastSocket multicastSock;
    private static InetAddress group;
    private boolean duringupdate = false;
    //Mudado por Luis
    private static ServerLogic ci;
    //---------------
    private static int port; // TEMPPP
    private static boolean corre;

    //Mudado por Luis
    public MulticastUDP(ServerLogic ci) {
        this.ci = ci;
        this.port = ci.getSd().getServerPort(); // TEMPPPP
    }
    //---------------
    
    //public static void main(String[] args) throws UnknownHostException, IOException {
    public void comecamulticast() {
        try {
            // TODO code application logic here
            
            group = InetAddress.getByName("225.4.5.6");
            multicastSock = new MulticastSocket(3456);
            multicastSock.joinGroup(group);
            corre= true;
            
            new Thread(Receb).start();
            new Thread(Env).start();
            
            //código modificado
            //TimeUnit.SECONDS.sleep(30);
            //corre = false;
            
            //multicastSock.close();
        //} catch (InterruptedException ex) {
            //Logger.getLogger(MulticastUDP.class.getName()).log(Level.SEVERE, null, ex);
            //-----------------
        } catch (UnknownHostException ex) {
            Logger.getLogger(MulticastUDP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MulticastUDP.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void turnOff() {
        MulticastUDP.corre = false;
    }

    public static MulticastSocket getMulticastSock() {
        return multicastSock;
    }

    public static InetAddress getGroup() {
        return group;
    }

    private static Runnable Receb = new Runnable() {
        public void run() {
            try{
                while(corre){
                    
                    byte [] buffer = new byte [100];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    multicastSock.receive(packet);
                    //System.out.println("Recebi: " + new String(buffer));
                    
                    //Código para a atualização da base de dados, a partir do comando enviado
                    
                    String cmd = new String(buffer);
                    System.out.println("Tentativa de execução de comando: " + commandParse(cmd));
                    
                    //-----------------------------------------------------------------------
                    
                    //updatedatabase(JObjrecebido);
                    
                }
            } catch (IOException ex) {
                ci.Obj().put("exception", "[ERROR]Package not received in good state.\n" + ex.getMessage());
                ci.notifyObserver(4);
            }
            System.out.println("[ERROR] Servidor deixou de receber mensagens por multicast.");
        }
    };
    
    private static Runnable Env = new Runnable() { // TEMPPP
        public void run() {
            try{
                while(corre){
                    
                    TimeUnit.SECONDS.sleep(2);
                    byte [] buffer = new byte [100];
                    int msg = port;
                    byte[] b = String.valueOf(msg).getBytes(); 
                    DatagramPacket packet = new DatagramPacket(b, b.length,group,3456);
                    multicastSock.send(packet);
                    System.out.println("Enviei val: " + msg);
                    
                }
            } catch (Exception e){}
            System.out.println("[ERROR] Servidor deixou de enviar mensagens por multicast.");
        }
    };
    
    public void sendDataBaseUpdate(JSONObject JObjrecebido){ // OLDDD... ADAPTAR PARA A NECESSIDADE
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
            Receb.wait(); // Pausa a thread para q esta n roube a informação
            
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
            Receb.notify(); // continua a thread q foi parada
            duringupdate = false;
        } catch (UnknownHostException ex) {
            Logger.getLogger(MulticastUDP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MulticastUDP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(MulticastUDP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static String commandParse(String command){
        boolean hasTypeLog = false;
        boolean hasTypeReg = false;
        boolean hasTypeOut = false;
        boolean hasUserNameLog = false;
        boolean hasUserNameReg = false;
        boolean hasUserNameOut = false;
        boolean hasPasswordLog = false;
        boolean hasPasswordReg = false;
        boolean hasPasswordOut = false;
        String username = "";
        String password = "";
        command = command.replace(" ", "");
        String [] zones = command.split(";");
        for(String zone : zones){
            String parte = zone.replace("|", " ");
            String [] cmd = parte.split(" ");
            //vai verificar se o comando é inicializado corretamente
            if(cmd[0].equalsIgnoreCase("tipo") && !hasTypeLog && !hasTypeReg && !hasTypeOut){
                //vai verificar se o tipo de comando é o correto
                if(cmd[1].equalsIgnoreCase("login")){
                    hasTypeLog = true;
                }
                else if(cmd[1].equalsIgnoreCase("registo")){
                    hasTypeReg = true;
                }
                else if(cmd[1].equalsIgnoreCase("logout")){
                    hasTypeOut = true;
                }
                else{
                    return "erro de comando";
                }
            }
            else if(cmd[0].equalsIgnoreCase("username") && !hasUserNameLog && !hasUserNameReg && !hasUserNameOut){
                //vai verificar se o utilizador existe na base de dados
                username = cmd[1];
                if(hasTypeLog){
                    hasUserNameLog = ci.getDbaction().contaisUser(username);
                }
                else if(hasTypeReg){
                    hasUserNameReg = !ci.getDbaction().contaisUser(username);
                }
                else if(hasTypeOut){
                    hasUserNameOut = ci.getDbaction().contaisUser(username);
                }
                else{
                    return "erro de comando";
                }
            }
            else if(cmd[0].equalsIgnoreCase("password") && !hasPasswordLog && !hasPasswordReg && !hasPasswordOut){
                //vai verificar que a password introduzida cuincide com a introduzida para aquele username
                password = cmd[1];
                if(hasTypeLog && hasUserNameLog){
                    hasPasswordLog = ci.getDbaction().verifyUserPassword(username, password);
                }
                else if(hasTypeReg && hasUserNameReg){
                    hasPasswordReg = true;
                }
                else if(hasTypeOut && hasUserNameOut){
                    hasPasswordOut = ci.getDbaction().verifyUserPassword(username, password);
                }
                else{
                    return "erro de comando";
                }
            }
            else {
                //caso exista mais linhas de comando para além desta ou o comando introduzido tenha sido mal escrito
                //ou seja de outro tipo
                return "erro de comando";
            }
        }
        
        if(hasPasswordLog && hasTypeLog && hasUserNameLog){
            //Verifica se o utilizador se encontra na base de dados como logado
            return "Login com sucesso";
        }
        else if(hasPasswordReg && hasTypeReg && hasUserNameReg){
            ci.getDbaction().insertuser(username, username, password);
            return "Registo com sucesso";
        }
        else if(hasPasswordOut && hasTypeOut && hasUserNameOut){
            ci.getDbaction().removeuser(username, password);
            return "Registo com sucesso";
        }
        else{
            return "erro de comando";
        }
    }
    
}
