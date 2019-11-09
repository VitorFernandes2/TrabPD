/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author a21270909
 */
public class ThreadClientRequests implements Runnable{

    private boolean runstatus = true;
    
    @Override
    public void run() {

        ServerSocket ss = null; 
        Socket s = null;
        
        try {
            ss = new ServerSocket(4999);
            s = ss.accept(); // é ma pratica meter um accept dentro de um try??
        } catch (IOException ex) {
            System.out.println("[ERROR] não foi possivel criar o socket");
            runstatus = false;
            return;
        }
        
        while(runstatus){
 
        System.out.println("client connected");

        InputStreamReader in = null;
        
            try {
                in = new InputStreamReader(s.getInputStream());
                
                    BufferedReader bf = new BufferedReader(in);
        
                    String str = bf.readLine();
                    System.out.println("client : " + str);

                    // MUDAR PARA JSON
                    PrintWriter pr = new PrintWriter(s.getOutputStream());
                    pr.println("yes");
                    pr.flush();
                  
            } catch (IOException ex) {
                    System.out.println("[ERROR] Erro no ciclo");
                    runstatus = false;
                    return;
            }

        }
        
    }
       
    
    public void stopthread(){ // pode ser ma pratica
        runstatus = false;
    }
    
    
}
