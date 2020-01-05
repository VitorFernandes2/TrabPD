package server.logic;

import org.json.simple.JSONObject;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class SendPlaylistToClient extends Thread{

    private PrintWriter pr;
    private OutputStream out;
    private ArrayList<String> list;

    public SendPlaylistToClient(PrintWriter pr, OutputStream out, ArrayList<String> list) {
        this.pr = pr;
        this.out = out;
        this.list = list;
    }

    @Override
    public void run() {

        JSONObject obj = new JSONObject();
        obj.put("message", "receiveFiles");
        obj.put("numberOfFiles", list.size());

        for (int i = 0; i < list.size(); i++) {

            String nome = "Nome" + i;
            obj.put(nome, list.get(i));

            File file = new File(list.get(i));

            String size = "Size" + i;
            obj.put(size, file.length());

        }

        System.out.println(obj.toString());
        pr.println(obj.toString());
        pr.flush();

        for (int i = 0; i < list.size(); i++) {

            SendFileToClient sendFileToClient =
                    new SendFileToClient(list.get(i), out);
            sendFileToClient.start();
            try {
                sendFileToClient.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }


}
