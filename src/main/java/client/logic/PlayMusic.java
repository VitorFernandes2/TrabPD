package client.logic;

import java.io.*;

public class PlayMusic {

    public static void playMusic(String filename){

        try {
            Process pro = Runtime.getRuntime().exec("cmd.exe /c " + filename);
            pro.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
