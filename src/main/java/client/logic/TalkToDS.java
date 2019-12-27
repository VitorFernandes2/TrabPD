package client.logic;

import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Esta thread corre em background para que o cliente não fique pendurado quando o servidor for abaixo
 */
public class TalkToDS extends Thread{

    private String serverInfo; //String com os dados JSON sobre o servidor
    private ServerTCPconnect comunicationThread;
    private DatagramSocket socket;
    private boolean terminate;
    private int PORT = 9997;

    public TalkToDS(String serverInfo) {

        this.serverInfo = serverInfo;
        terminate = false;
        this.comunicationThread = new ServerTCPconnect(serverInfo);
        comunicationThread.start();

        try {
            this.socket = new DatagramSocket(PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    public void stopComunicationThread(){
        comunicationThread.stopthread();
    }

    //Este run vai comunicar com o DS e quando este lhe disser que tem de mudar de servidor
    //termina a ligação e recomeça uma outra com o novo servidor
    @Override
    public void run() {

        while (!terminate){

            //Fazer tratamento do cliente

        }

    }

    public void stopClient(){

        stopComunicationThread();
        terminate = true;

    }

}
