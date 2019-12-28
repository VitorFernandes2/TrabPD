package client.logic;

import org.json.simple.JSONObject;

/**
 *
 * @author Joao Coelho
 * 
 * REVER!!! Pode ser mau implmentato... serve para fazer a conceção de datos entre observer e observable
 */
public class ConnectData {
    
    private String username;
    private String password;
    private JSONObject JObj;
    private String command;
    private int menu = 7;
    
    public JSONObject getJObj() {
        return JObj;
    }

    public void setJObj(JSONObject JObj) {
        this.JObj = JObj;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getMenu() {
        return menu;
    }

    public void setMenu(int menu) {
        this.menu = menu;
    }
}
