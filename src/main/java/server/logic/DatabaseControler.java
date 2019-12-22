/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import server.ServerLogic;

/**
 *
 * @author a21270909
 */
public class DatabaseControler {
    
   static String BD_URL = "jdbc:mysql://localhost:3306/?useTimezone=true&serverTimezone=UTC&useSSL=false"; 
   //static final String DB_URL = "jdbc:mysql://localhost:3306/EMP?useTimezone=true&serverTimezone=UTC&useSSL=false";
   private String user = "test"; // mudar para o igual ao definido no sql.
   private String pass = "1234";
   private Connection connect;
   private Statement stmt;
   private String namedb;
   private int serverport;
   private boolean principal;
   

    /**
     * ONLY USE TO OVERRIDE USERNAME AND PASSWORD
     */
    public DatabaseControler(String user, String pass, int si,boolean pinc) {
        this.user = user;
        this.pass = pass;
        this.serverport = si;
        this.principal = pinc;
    }

    public DatabaseControler(int si,boolean pinc) {
        this.serverport = si;
        this.principal = pinc;
    }
    
    public boolean startdatabase(){
        
        System.out.println("Connecting to database...");
          
        try {
            if(connect == null)
                    connect = DriverManager.getConnection(BD_URL,user,pass);
            
            if(principal == true)
                namedb = "Principal";
            else
                namedb = numbtoletter();
                
            stmt = connect.createStatement();
            String CreateSql = ("CREATE DATABASE IF NOT EXISTS " + namedb);
            stmt.execute(CreateSql); 

            // REVER: Funcional mas agressivo demais
            String NewString = "jdbc:mysql://localhost:3306/"+namedb+"?useTimezone=true&serverTimezone=UTC&useSSL=false";
            BD_URL = NewString;
            connect.close();stmt.close();
            connect = DriverManager.getConnection(NewString,user,pass);
            stmt = connect.createStatement();

            String tableSql = "CREATE TABLE IF NOT EXISTS users"
            + "(user_id int PRIMARY KEY AUTO_INCREMENT, name varchar(30),"
            + "username varchar(30), password varchar(30))";
            stmt.execute(tableSql);

            String tableSql2 = "CREATE TABLE IF NOT EXISTS musics"
            + "(music_id int PRIMARY KEY AUTO_INCREMENT, name varchar(30),"
            + "artist varchar(30), album varchar(30), year varchar(30), duration double, genre varchar(30), localname varchar(30))";
            stmt.execute(tableSql2);

            String tableSql3 = "CREATE TABLE IF NOT EXISTS playlist"
            + "(play_id int PRIMARY KEY AUTO_INCREMENT, music_id int,"
            + "user_id int, name varchar(30))";
            stmt.execute(tableSql3);

        } catch (SQLException ex) {
            System.out.println("Tipo: " + ex);
            return false;
        }
        return true;
    }
    
    public boolean insertuser(String name,String username,String password){
        
        try {
            /*String insertSql = "INSERT INTO employees(name, position, salary)"
            + " VALUES('john', 'developer', 2000)";
            stmt.executeUpdate(insertSql);*/   
            String insertSql = "INSERT INTO users(name, username, password)"
            + " VALUES('"+name+"', '"+username+"', '"+password+"')";
            stmt.executeUpdate(insertSql);
        } catch (SQLException ex) {
            return false;
        }
        return true;
    }
    
    public boolean insertmusic(String name,String artist,String album, String year, double duration, String genre, String localname){
        
        try {
            String insertSql = "INSERT INTO musics(name, artist, album, year,duration,genre,localname)"
            + " VALUES('"+name+"', '"+artist+"', '"+album+"', '"+year+"', '"+duration+"', '"+genre+"', '"+localname+"')";
            stmt.executeUpdate(insertSql);
        } catch (SQLException ex) {
            return false;
        }
        return true;
    }
    
    public boolean runinsertedcode(String insert){
        
        try{
            stmt.executeUpdate(insert);
        } catch (SQLException ex){
            return false;
        }
        return true;
    }
    
    public int getIntegerfromdb(String proc){
        try {
            String selectSql = ("SELECT * FROM " + namedb);
            ResultSet resultSet = stmt.executeQuery(selectSql);
            while(resultSet.next()){
                //Retrieve by column name
                int procu  = resultSet.getInt(proc);
                return procu;
            }
        } catch (SQLException ex) {
           return -1;
        }
        return -1;
    }
    
    public String getStringfromdb(String proc){ 
        try {
            String selectSql = ("SELECT * FROM " + namedb);
            ResultSet resultSet = stmt.executeQuery(selectSql);          
            while(resultSet.next()){
                //Retrieve by column name
                String procu  = resultSet.getString(proc);
                return procu;
            }
        } catch (SQLException ex) {
           return "-1";
        }
        return "-1";
    }

    public ResultSet getRawDatabaseinfo(){
        try {
            String selectSql = ("SELECT * FROM " + namedb);
            return stmt.executeQuery(selectSql);          
        } catch (SQLException ex) {
           return null;
        }
    }
    
    public boolean updatedatabase(JSONObject JObjrecebido){
        
        // TODO - vai ser agressivo
        
        return false;
    }
    
    
    private String numbtoletter(){
        //tradutor de numero para uma string, para criar o nome da base de dados com o porto
        int num, temp, digit, count = 0;
        num = this.serverport;
        char[] array = { 'A', 'B', 'C', 'D', 'E', 'F' , 'G' , 'H' , 'I' , 'J' , 'K'};
        String output = "";
        
        temp = num;
        
        while(num > 0)
        {
            num = num / 10;
            count++;
        }
        while(temp > 0)
        {
            digit = temp % 10;
            output = output + array[digit];
            temp = temp / 10;
            count--;
        }
        
        //this.serverport;
        return output;
     
    }
   
   
   
}
