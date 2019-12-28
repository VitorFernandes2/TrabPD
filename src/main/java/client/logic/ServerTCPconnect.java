package client.logic;

import client.InterfaceGrafica.Interfacemain;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import mainObjects.Music;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ServerTCPconnect implements Runnable{
    
    private String ip;
    private String port;
    private boolean stopthread = true;

    ConnectData data;
    Interfacemain upperclass;
    JSONObject JObjE;
    JSONObject obj;
    
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

    public ServerTCPconnect(String json, ConnectData datacomun,Interfacemain man) {

        try{
            //Inicializacao do objeto de comando
            obj = new JSONObject();
            obj.put("Command", "");
            //----------------------------------
            
            JSONParser JsonParser = new JSONParser();
            JSONObject JObj = (JSONObject) JsonParser.parse(json);

            ip = (String) JObj.get("IP");
            port = (String) JObj.get("Port");

            System.out.println(ip);
            System.out.println(port);
            
            this.data = datacomun;
            this.upperclass = man;
            
        }
        catch (ParseException e){
            JObjE = new JSONObject();
            JObjE.put("exception", "[ERROR] unable to parse json from tcp connect in ServerTCPconnect thread " + e.toString());
            data.setJObj(JObjE);
            upperclass.update(444, data);
        }
    }
    
    public synchronized void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            int guardaMenu = 7;
            boolean didPush = false;
            Socket  s = new Socket(this.ip, Integer.parseInt(this.port)); // DUMMY CODE : modificar para enviar o q é preciso
            PrintWriter pr;
            InputStreamReader in = new InputStreamReader(s.getInputStream());
            StringBuilder sb;
            while(stopthread){
                
                synchronized(s.getOutputStream()){

                    //Mostra consoante o menu em que se encontra
                    switch (data.getMenu()){
                        
                        case 2:
                            this.upperclass.update(2, data);
                            
                            sb = new StringBuilder();
                            sb.append("tipo|login;username|").append(data.getUsername()).append(";password|").append(data.getPassword());
                            
                            obj.put("Command", sb.toString());
                            
                            pr = new PrintWriter(s.getOutputStream());
                            pr.println(obj.toString());
                            pr.flush();
                            didPush = true;
                            guardaMenu = 7;
                            
                            break;

                        case 3:
                            this.upperclass.update(10, data);
                            
                            sb = new StringBuilder();
                            sb.append("tipo|registo;username|").append(data.getUsername()).append(";password|").append(data.getPassword());
                            
                            obj.put("Command", sb.toString());
                            
                            pr = new PrintWriter(s.getOutputStream());
                            pr.println(obj.toString());
                            pr.flush();
                            didPush = true;
                            guardaMenu = 7;
                            
                            break;
                        
                        case 7:
                            this.upperclass.update(7, data);

                            if (data.getCommand().equals("entrar")){
                                data.setMenu(8);
                            }
                            else if(data.getCommand().equals("entraLogin")){
                                data.setMenu(2);
                            }
                            else if(data.getCommand().equals("entraRegisto")){
                                data.setMenu(3);
                            }
                            else if(data.getCommand().equals("sair")){
                                //Flag de saida de programa
                                obj.put("Command", "exit");
                                break;
                            }
                            
                            break;

                        case 8:
                            this.upperclass.update(8, data);

                            if (data.getCommand().equals("gotoMusics")){
                                data.setMenu(6);
                            }
                            else if(data.getCommand().equals("logout")){
                                sb = new StringBuilder();
                                sb.append("tipo|logout;username|").append(data.getUsername()).append(";password|").append(data.getPassword());

                                obj.put("Command", sb.toString());
                                
                                pr = new PrintWriter(s.getOutputStream());
                                pr.println(obj.toString());
                                pr.flush();
                                didPush = true;
                                data.setMenu(7);
                            }

                            break;

                        case 6:
                            this.upperclass.update(6, data);

                            if (data.getCommand().equals("createMusic")){
                                data.setMenu(9);
                            }
                            
                            break;

                        //Criar musica
                        case 9:
                            this.upperclass.update(9, data);

                            Music music = data.getMusic();
                            System.out.printf(music.toString());

                            obj.put("Command", "createMusic");
                            obj.put("MusicName", music.getName());
                            obj.put("MusicAuthor", music.getAuthor());
                            obj.put("MusicYear", music.getYear());
                            obj.put("MusicAlbum", music.getAlbum());
                            obj.put("MusicDuration", music.getDuration());
                            obj.put("MusicGenre", music.getGenre());
                            obj.put("MusicPath", music.getPath());

                            pr = new PrintWriter(s.getOutputStream());
                            pr.println(obj.toString());
                            pr.flush();

                            in = new InputStreamReader(s.getInputStream());
                            BufferedReader bf = new BufferedReader(in);

                            String str = bf.readLine();

                            JSONParser JsonParser = new JSONParser();
                            JSONObject JObj = (JSONObject) JsonParser.parse(str);
                            data.setJObj(JObj);

                            this.upperclass.update(3, data);

                            SendFileToServer sendFileToServer = new SendFileToServer(music.getPath(),
                                    s.getOutputStream());
                            sendFileToServer.start();

                            break;

                        case 10:



                            break;

                    }
                    
                    if(didPush){
                        BufferedReader bf = new BufferedReader(in);

                        String str = bf.readLine();

                        JSONParser JsonParser = new JSONParser();
                        JSONObject JObj = (JSONObject) JsonParser.parse(str);
                        boolean Sucesso = (boolean) JObj.get("sucesso");

                        if(!Sucesso){
                            data.setMenu(guardaMenu);
                        }

                        data.getJObj().put("output", JObj.toString());
                        upperclass.update(86, data);
                    }

                }
                
            }
            
            s.close();

        } catch (IOException e) {
            JObjE = new JSONObject();
            JObjE.put("exception", e.toString());
            data.setJObj(JObjE);
            upperclass.update(5, data);
            //Provisório para fechar aplicação quando o servidor for abaixo
            obj.put("Command", "exit");
            //-------------------------------------------------------------
        } catch (ParseException e) {
            JObjE = new JSONObject();
            JObjE.put("exception", e.toString());
            data.setJObj(JObjE);
            upperclass.update(444, data);
        } catch (NullPointerException e) {
            JObjE = new JSONObject();
            JObjE.put("exception", e.toString());
            data.setJObj(JObjE);
            upperclass.update(444, data);
        }

    }
    
    public String getCommand(){
        return (String) obj.get("Command");
    }
    
    public void stopthread(){ // pode ser ma pratica
        stopthread = false;
    }
    
}