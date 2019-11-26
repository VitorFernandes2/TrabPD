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

import org.json.simple.JSONObject;
import server.comunicationInterface.ComunicationInterface;

/**
 *
 * @author a21270909
 */
public class DatabaseControler {
    
   static final String BD_URL = "jdbc:mysql://localhost:3306/?useTimezone=true&serverTimezone=UTC&useSSL=false"; 
   //static final String DB_URL = "jdbc:mysql://localhost:3306/EMP?useTimezone=true&serverTimezone=UTC&useSSL=false";
   private String user = "test"; // mudar para o igual ao definido no sql.
   private String pass = "1234";
   private Connection connect;
   private Statement stmt;
   private String namedb;
   private final ComunicationInterface si;

    /**
     * ONLY USE TO OVERRIDE USERNAME AND PASSWORD
     */
    public DatabaseControler(String user, String pass, ComunicationInterface si) {
        this.user = user;
        this.pass = pass;
        this.si = si;
    }

    public DatabaseControler(ComunicationInterface si) {
        this.si = si;
    }
    
    public boolean startdatabase(int porto){
        
        System.out.println("Connecting to database...");
          
        try {
            
            if(connect == null)
                connect = DriverManager.getConnection(BD_URL,user,pass);
                
            namedb = numbtoletter();
            
            //TEMP
            stmt = connect.createStatement();
            String CreateSql = ("CREATE DATABASE IF NOT EXIST " + namedb);
            stmt.execute(CreateSql); 
            
            String tableSql = "CREATE TABLE IF NOT EXISTS employees"
            + "(emp_id int PRIMARY KEY AUTO_INCREMENT, name varchar(30),"
            + "position varchar(30), salary double)";
            stmt.execute(tableSql);    
                
            String insertSql = "INSERT INTO employees(name, position, salary)"
            + " VALUES('john', 'developer', 2000)";
            stmt.executeUpdate(insertSql);    
                
                
        } catch (SQLException ex) {
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
        num = si.getServerPort();
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
        
        si.getServerPort();
        return output;
     
    }
   
   
   
}
