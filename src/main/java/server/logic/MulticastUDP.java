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
    private static ServerLogic ci;
    private static int port; // TEMPPP
    private static boolean corre;

    public MulticastUDP(ServerLogic ci) {
        this.ci = ci;
        this.port = ci.getSd().getServerPort(); // TEMPPPP
    }
    
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
                    String result = commandParse(cmd);
                    if(!result.contains("Sem Tipo definido [2] !\n")){
                        System.out.println("\nRecebi: " + cmd + "\nTentativa de execução de comando: " + result);
                    }
                    
                    //-----------------------------------------------------------------------
                    
                    //updatedatabase(JObjrecebido);
                    
                }
            } catch (IOException ex) {
                ci.Obj().put("exception", "[ERROR]Package not received in good state.\n" + ex.getMessage());
                ci.notifyObserver(4);
            }
            ci.Obj().put("exception", "[ERROR] Servidor deixou de receber mensagens por multicast.");
            ci.notifyObserver(4);
        }
    };
    
    private static Runnable Env = new Runnable() { // TEMPPP
        public void run() {
            try{
                while(corre){
                    
                    TimeUnit.SECONDS.sleep(2);
                    int msg = port;
                    byte[] b = String.valueOf(msg).getBytes(); 
                    DatagramPacket packet = new DatagramPacket(b, b.length,group,3456);
                    multicastSock.send(packet);
                    //System.out.println("Enviei val: " + msg);
                    
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
                    return "Sem tipo definido [1] !\n";
                }
            }
            else if(cmd[0].equalsIgnoreCase("username") && !hasUserNameLog && !hasUserNameReg && !hasUserNameOut){
                if(cmd[1] == null || cmd[1].length() == 0){
                    return "Sem Username defenido!\n";
                }
                //vai verificar se o utilizador existe na base de dados
                username = cmd[1];
                if(hasTypeLog){
                    hasUserNameLog = ci.getDbaction().contaisUserMultiCast(username, ci);
                }
                else if(hasTypeReg){
                    hasUserNameReg = !ci.getDbaction().contaisUserMultiCast(username, ci);
                }
                else if(hasTypeOut){
                    hasUserNameOut = ci.getDbaction().contaisUserMultiCast(username, ci);
                }
            }
            else if(cmd[0].equalsIgnoreCase("password") && !hasPasswordLog && !hasPasswordReg && !hasPasswordOut){
                if(cmd[1] == null || cmd[1].length() == 0){
                    return "Sem Password defenida!\n";
                }
                //vai verificar que a password introduzida cuincide com a introduzida para aquele username
                password = cmd[1];
                if(hasTypeLog && hasUserNameLog){
                    hasPasswordLog = ci.getDbaction().verifyUserPasswordMultiCast(username, password, ci);
                }
                else if(hasTypeReg && hasUserNameReg){
                    hasPasswordReg = true;
                }
                else if(hasTypeOut && hasUserNameOut){
                    hasPasswordOut = ci.getDbaction().verifyUserPasswordMultiCast(username, password, ci);
                }
            }
            else {
                //caso exista mais linhas de comando para além desta ou o comando introduzido tenha sido mal escrito
                //ou seja de outro tipo
                return "Sem Tipo definido [2] !\n";
            }
        }
        
        if(hasPasswordLog && hasTypeLog && hasUserNameLog){
            //Verifica se o utilizador se encontra na base de dados como logado
            return "Login com sucesso.\n";
        }
        else if(hasPasswordReg && hasTypeReg && hasUserNameReg){
            if(ci.getDbaction().insertuserMultiCast(username, username, password, ci)){
                return "Registo com sucesso.\n";
            }
            else{
                return "Registo sem sucesso.\n";
            }
        }
        else if(hasPasswordOut && hasTypeOut && hasUserNameOut){
            if(ci.getDbaction().removeuserMultiCast(username, password, ci)){
                return "Logout com sucesso.\n";
            }
            else{
                return "Logout sem sucesso.\n";
            }
        }
        else{
            return "Erro de comando total.\n";
        }
    }
    
}
