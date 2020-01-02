package server.logic;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;
import server.ServerLogic;

/**
 *
 * @author Luís António Moreira Ferreira da Silva
 */
public class ServerConnectionStarter {
    private ServerLogic sd;

    public ServerConnectionStarter(ServerLogic sd) {
        this.sd = sd;
    }
    
    public void connect(){
        DatagramSocket socket;
        
        try {
            // abre socket para UDP para receber o resgisto no DS
            socket = new DatagramSocket(0);

            InetAddress address = InetAddress.getByName(this.sd.getDsIP());

            sd.Obj().put("msg", "dataSubmitServer");
            
            sd.Obj().put("Port", "" + socket.getLocalPort());

            sd.setServerPort(socket.getLocalPort());

            String StrToSend = sd.Obj().toString();
            
            byte [] buf = StrToSend.getBytes();
            
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, Integer.parseInt(this.sd.getDsPort()));

            socket.send(packet);
            
            // recebe o tipo de servidor pelo ds
            byte[] b1r = new byte[1024]; // onde vais receber os bytes, se a msg for maior do q o tamanho, ela fica cortada.
        
            DatagramPacket dp1 = new DatagramPacket(b1r,b1r.length); // so é necessario fazer o datagrampacket com ip e port se for pra receber coisas.

            socket.receive(dp1); // recebe o packet pela socket já criada

            String str = new String(dp1.getData()); // traduz a informação recebida para string
            int valorrec = Integer.parseInt(str.trim());
            System.out.println("O meu principal é: " + valorrec);

            socket.close();
            
            // cria a base de dados para o servidor
            if (valorrec == 0){
                sd.startdatabase(true);}
            else if (valorrec == 1){
                sd.startdatabase(false);}
            else {
                sd.Obj().put("exception", "Erro a receber qual base de dados criar");
                sd.notifyObserver(4);
            }
            
            if (sd.getDbaction().startdatabase(sd) == false){
                sd.Obj().put("exception", "Erro a o criar a base de dados");
                sd.notifyObserver(4);
            }
            //----
            
        } catch (SocketException ex) {
            
            sd.Obj().put("exception", ex.getMessage());
            
            sd.notifyObserver(4);
            
        } catch (UnknownHostException ex) {
            
            sd.Obj().put("exception", ex.getMessage());
            
            sd.notifyObserver(4);
            
        } catch (IOException ex) {
            
            sd.Obj().put("exception", ex.getMessage());
            
            sd.notifyObserver(4);
            
        }
        
        sd.Obj().put("output", "Server a correr.");
        
        sd.notifyObserver(1);
        
        //Começa a comunicação em multicast
        MulticastUDP multi = new MulticastUDP(sd);
        multi.comecamulticast();
        //---------------------------------

        //Inicio dos Ping
        DSAliveProcedure aliveProcedure = new DSAliveProcedure(sd, multi);
        aliveProcedure.start();
        //---------------
        
        ThreadClientRequests threadclass = new ThreadClientRequests(sd); // pode ser mai pratica REVER
        
        threadclass.start();

        Scanner myObj = new Scanner(System.in);
        
        String wait = myObj.nextLine();
        
        try {
            //Desligo das ligacoes UDP com o DS para pings
            aliveProcedure.terminate();
            //--------------------------------------------
            
            //Obriga a Thread do Servidor a parar
            threadclass.stopthread();
            //-----------------------------------
            
            //Código de terminio de Multicast
            multi.turnOff();
            //-------------------------------
            
            //retirar depois para tratamento de morte do servidor
            System.exit(0);//------------------------------------
            //---------------------------------------------------
            
        } catch (IOException ex) {
            
            sd.Obj().put("output", "[ERROR] Terminação forçada do Servidor.\n" + ex.getMessage());
            sd.notifyObserver(1);
            
        }
    }
    
}
