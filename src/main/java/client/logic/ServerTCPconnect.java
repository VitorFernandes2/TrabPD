/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ServerTCPconnect extends Thread{
    
    private String ip;
    private String port;
    private boolean stopthread = true;

    public ServerTCPconnect(String json){
        
        try{

            JSONParser JsonParser = new JSONParser();
            JSONObject JObj = (JSONObject) JsonParser.parse(json);

            ip = (String) JObj.get("IP");
            port = (String) JObj.get("Port");

            System.out.println(ip);
            System.out.println(port);

        }
        catch (ParseException e){
            System.out.println("[ERROR] unable to parse json from tcp connect");
        }
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void run() {
    
        try {

            Socket  s = new Socket(this.ip, Integer.parseInt(this.port)); // DUMMY CODE : modificar para enviar o q é preciso
            
            while(stopthread){
 
                JSONObject obj = new JSONObject();
                obj.put("Login", "xxx");
                obj.put("Password", "YYY");
            
                PrintWriter pr = new PrintWriter(s.getOutputStream());
                pr.println(obj.toString());
                pr.flush();
                
                InputStreamReader in = new InputStreamReader(s.getInputStream());
                BufferedReader bf = new BufferedReader(in);

                String str = bf.readLine();
                
                JSONParser JsonParser = new JSONParser();
                JSONObject JObj = (JSONObject) JsonParser.parse(str);
                
                System.out.println(JObj.toString());
            
            }
            s.close();

        } catch (IOException ex) {
            Logger.getLogger(ServerTCPconnect.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(ServerTCPconnect.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            System.out.println("[ERROR] Erro de Nullpointer");
        }

    }
    
    public void stopthread(){ // pode ser ma pratica
        stopthread = false;
    }
    
    public JSONObject commandParser(String command){
        JSONObject Aux = new JSONObject();
        //Tradução e verificação de comando
        
        //*********************************
        return Aux;
    }
    
}
