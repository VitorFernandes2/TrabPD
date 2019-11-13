package server.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author a21270909
 */
public class ThreadClientRequests extends Thread{

    private boolean runstatus = true;
    
    @Override
    public void run() {

        ServerSocket ss = null; 
        Socket s = null;
        
        try {
            ss = new ServerSocket(9998);
            s = ss.accept(); // é ma pratica meter um accept dentro de um try??
        } catch (IOException ex) {
            System.out.println("[ERROR] não foi possivel criar o socket");
            runstatus = false;
            return;
        }
        
        while(runstatus){
 
        System.out.println("client connected to tcp");

        InputStreamReader in = null;
        
            try {
                in = new InputStreamReader(s.getInputStream()); // DUMMY CODE : modificar para enviar o q é preciso
                
                BufferedReader bf = new BufferedReader(in);

                String str = bf.readLine();
                
                JSONParser JsonParser = new JSONParser();
                JSONObject JObj = (JSONObject) JsonParser.parse(str);
                
                System.out.println(JObj.toString());
               
                JSONObject obj = new JSONObject();

                obj.put("Data", "xxx");
                obj.put("Mp3", "music");
                
                PrintWriter pr = new PrintWriter(s.getOutputStream());
                pr.println(obj.toString());
                pr.flush();
                  
            } catch (IOException ex) {
                System.out.println("[ERROR] Erro no ciclo");
                runstatus = false;
                return;
            } catch (ParseException ex) {
                System.out.println("[ERROR] Erro na tradução do Json");
                runstatus = false;
                return;
            } catch (NullPointerException ex) {
                System.out.println("[ERROR] Erro de Nullpointer. Provavelmente o client se desconectou");
                runstatus = false;
                return;
            }

        }
        
    }
       
    
    public void stopthread(){ // pode ser ma pratica
        runstatus = false;
    }
    
    
}
