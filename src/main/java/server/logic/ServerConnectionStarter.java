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
 * @author Lus Antnio Moreira Ferreira da Silva
 */
public class ServerConnectionStarter {
    private ServerLogic sd;

    public ServerConnectionStarter(ServerLogic sd) {
        this.sd = sd;
    }
    
    public void connect(){
        DatagramSocket socket;
        
        while(true){
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

                DatagramPacket dp1 = new DatagramPacket(b1r,b1r.length); // so  necessario fazer o datagrampacket com ip e port se for pra receber coisas.

                socket.receive(dp1); // recebe o packet pela socket j criada

                String str = new String(dp1.getData()); // traduz a informao recebida para string
                int valorrec = Integer.parseInt(str.trim());
                System.out.println("O meu principal o: " + valorrec);

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

            sd.Obj().put("output", "Server a correr.\nA iniciar Pings.");
            sd.notifyObserver(1);

            //Comea a comunicao em multicast
            MulticastUDP multi = new MulticastUDP(sd);
            multi.comecamulticast();
            //---------------------------------

            //Inicio dos Ping
            DSAliveProcedure aliveProcedure = new DSAliveProcedure(sd, multi);
            aliveProcedure.start();
            //---------------

            sd.Obj().put("output", "Pings iniciados.\nPode receber clientes.");
            sd.notifyObserver(1);

            ThreadClientRequests threadclass = new ThreadClientRequests(sd, multi);

            threadclass.start();

            Scanner myObj = new Scanner(System.in);

            String wait;
            
            do{
                sd.Obj().put("output", "Quer desligar o servidor? (sim - nao)");
                sd.notifyObserver(1);
                wait = myObj.nextLine();
            }while(!wait.equals("sim"));
            
            if(!multi.isCorre() && aliveProcedure.isFinished()){
                break;
            }

            try {
                // abre socket para UDP para receber o desligar do servidor no DS
                socket = new DatagramSocket(0);

                InetAddress address = InetAddress.getByName(this.sd.getDsIP());

                sd.Obj().put("msg", "dataEraseServer");

                sd.Obj().put("Port", "" + sd.getServerPort());

                String StrToSend = sd.Obj().toString();

                byte [] buf = StrToSend.getBytes();

                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, Integer.parseInt(this.sd.getDsPort()));

                socket.send(packet);

                // recebe o tipo de servidor pelo ds
                byte[] b1r = new byte[1024]; // onde vais receber os bytes, se a msg for maior do q o tamanho, ela fica cortada.

                DatagramPacket dp1 = new DatagramPacket(b1r,b1r.length); // so  necessario fazer o datagrampacket com ip e port se for pra receber coisas.

                socket.receive(dp1); // recebe o packet pela socket j criada

                String str = new String(dp1.getData()); // traduz a informao recebida para string
                
                sd.Obj().put("output", "Desligado: " + str);
                sd.notifyObserver(1);

                socket.close();

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


            try {
                //Desligo das ligacoes UDP com o DS para pings
                aliveProcedure.terminate();//-----------------
                //--------------------------------------------
                
                //Cdigo de terminio de Multicast
                multi.turnOff();//--------------
                //------------------------------
                
                //Obriga a Thread do Servidor a parar
                threadclass.stopthread();//----------
                //-----------------------------------

            } catch (IOException ex) {

                sd.Obj().put("output", "[ERROR] Terminacao forcada do Servidor.\n" + ex.getMessage());
                sd.notifyObserver(1);

            }
            
            do{
                sd.Obj().put("output", "Quer reiniciar o servidor? (sim - nao)");
                sd.notifyObserver(1);
                wait = myObj.nextLine();
            }while(!wait.equals("sim") && !wait.equals("nao"));
            
            if(wait.equals("nao")){
                return;
            }
            
        }
    }
    
}
