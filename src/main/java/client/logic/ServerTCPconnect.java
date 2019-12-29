/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
            //System.out.println("[ERROR] unable to parse json from tcp connect");
            JObjE = new JSONObject();
            JObjE.put("exception", "[ERROR] unable to parse json from tcp connect in ServerTCPconnect thread " + e.toString());
            data.setJObj(JObjE);
            upperclass.update(444, data);
            //this.upperclass.notifyObserver(444);
        }
    }
    
    public synchronized void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        String cmd;
        try {

            Socket  s = new Socket(this.ip, Integer.parseInt(this.port)); // DUMMY CODE : modificar para enviar o q é preciso
            
            while(stopthread){
                
                synchronized(s.getOutputStream()){

                    //Mostra consoante o menu em que se encontra
                    switch (data.getMenu()){

                        case 7:
                            this.upperclass.update(7, data);

                            if (data.getCommand().equals("entrar"))
                                data.setMenu(8);

                            break;

                        case 8:
                            this.upperclass.update(8, data);

                            if (data.getCommand().equals("gotoMusics"))
                                data.setMenu(6);

                            break;

                        case 6:
                            this.upperclass.update(6, data);

                            if (data.getCommand().equals("createMusic"))
                                data.setMenu(9);
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

                            PrintWriter pr = new PrintWriter(s.getOutputStream());
                            pr.println(obj.toString());
                            pr.flush();

                            InputStreamReader in = new InputStreamReader(s.getInputStream());
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

                        //Listar todas as musicas
                        case 10:



                            break;

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
        } /*catch (ParseException e) {
            JObjE = new JSONObject();
            JObjE.put("exception", e.toString());
            data.setJObj(JObjE);
            upperclass.update(444, data);
        }*/ catch (NullPointerException e) {
            JObjE = new JSONObject();
            JObjE.put("exception", e.toString());
            data.setJObj(JObjE);
            upperclass.update(444, data);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
    
    public String getCommand(){
        return (String) obj.get("Command");
    }
    
    public void stopthread(){ // pode ser ma pratica
        stopthread = false;
    }
    
    public JSONObject commandParser(String command){
        JSONObject Aux = new JSONObject();
        String [] zones = command.trim().split(";");
        boolean isLogin = false;
        boolean isRegister = false;
        boolean isResponse = false;
        boolean isList = false;
        boolean hasType = false;
        boolean hasUsernameLog = false;
        boolean hasUsernameReg = false;
        boolean hasListItens = false;
        boolean hasMessage = false;
        int i = 0;
        int max = 0;
        
        //Tradução e verificação de comando
        for (String zone : zones) {
            if(!hasType){
                if(zone.split("|")[0].equalsIgnoreCase("tipo")){
                    //se for do tipo login vai começar a correr as suas validações
                    if(zone.split("|")[1].equalsIgnoreCase("login")){
                        Aux.put("tipo", "login");
                        hasType = true;
                        isLogin = true;
                    }
                    //se for do tipo registo vai começar a correr as suas validações
                    else if(zone.split("|")[1].equalsIgnoreCase("registo")){
                        Aux.put("tipo", "registo");
                        hasType = true;
                        isRegister = true;
                    }
                    //se for do tipo lista vai começar a correr as suas validações
                    else if(zone.split("|")[1].equalsIgnoreCase("lista")){
                        Aux.put("tipo", "lista");
                        hasType = true;
                        isList = true;
                    }
                    //se for do tipo resposta vai começar a correr as suas validações
                    else if(zone.split("|")[1].equalsIgnoreCase("resposta")){
                        Aux.put("tipo", "resposta");
                        hasType = true;
                        isResponse = true;
                    }
                }
            }
            //se for do tipo login e ainda não tive username
            else if(isLogin && !hasUsernameLog){
                if(zone.split("|")[0].equalsIgnoreCase("username")){
                    Aux.put("username", zone.split("|")[1]);
                    hasUsernameLog = true;
                }
            }
            //se for do tipo registo e ainda não tive username
            else if(isRegister && !hasUsernameReg){
                if(zone.split("|")[0].equalsIgnoreCase("username")){
                    Aux.put("username", zone.split("|")[1]);
                    hasUsernameReg = true;
                }
            }
            //se for do tipo resposta e tiver o username do user que envia
            else if(isResponse && hasMessage){
                //-----------------Formato do comando:----------------//
                // tipo | resposta ; user | username ; msg | mensagem //
                //----------------------------------------------------//
                //guarda a mensagem que vai enviar
                if(zone.split("|")[0].equalsIgnoreCase("msg")){
                    Aux.put("msg", zone.split("|")[1]);
                    return Aux;
                }
                else{
                    return null;
                }
            }
            //se for do tipo resposta e não tiver username
            else if(isResponse && !hasMessage){
                if(zone.split("|")[0].equalsIgnoreCase("username")){
                    Aux.put("username", zone.split("|")[1]);
                    hasMessage = true;
                }
            }
            //se for do tipo lista e não tiver ainda itens na lista
            else if(isList && !hasListItens){
                if(zone.split("|")[0].equalsIgnoreCase("n_itens")){
                    max = Integer.parseInt(zone.split("|")[1]);
                    Aux.put("n_itens", Integer.parseInt(zone.split("|")[1]));
                    hasListItens = true;
                }
            }
            //se for do tipo registo e não tiver password, mas tem username
            else if(hasUsernameReg && isRegister){
                if(zone.split("|")[0].equalsIgnoreCase("password")){
                    Aux.put("password", zone.split("|")[1]);
                    return Aux;
                }
            }
            //se for do tipo lista e tiver a adicionar itens
            else if(hasListItens){
                if(zone.split("|")[0].equalsIgnoreCase("item_" + i)){
                    Aux.put("item_" + i, zone.split("|")[1]);
                    i++;
                }
                else{
                    return null;
                }
            }
        }
        //---------------------------------
        //se o número de itens não for o definido no comando
        if(max != i){
            return null;
        }
        
        return Aux;
    }
    
}
