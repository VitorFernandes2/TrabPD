package ds.logic.comunication.server;
import org.json.simple.JSONObject;

public class ServerStart extends Thread {

    private static int NumberOfThreads = 0;
    private int ThreadNumber;
    private JSONObject ReceiveJsonObj;

    public ServerStart() {
        ReceiveJsonObj = new JSONObject();
        ThreadNumber = ++NumberOfThreads;
    }

    public static int getNumberOfThreads() {
        return NumberOfThreads;
    }

    public int getThreadNumber() {
        return ThreadNumber;
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void run() {
        super.run();
    }

}
