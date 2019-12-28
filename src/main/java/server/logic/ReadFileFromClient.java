package server.logic;

import java.io.*;
import java.net.Socket;

public class ReadFileFromClient extends Thread {

    private InputStream in;
    private OutputStream out;
    private String filename;

    public ReadFileFromClient(InputStream in, OutputStream out, String filename) {
        this.in = in;
        this.out = out;
        this.filename = filename;
    }

    @Override
    public void run() {

        //Initialize the FileOutputStream to the output file's full path.
        FileOutputStream fos = null;
        byte[] contents = new byte[10000];

        try {
            fos = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        InputStream is = in;

        //No of bytes read in one read() call
        int bytesRead = 0;

        try {
            while((bytesRead=is.read(contents))!=-1)
                bos.write(contents, 0, bytesRead);
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
