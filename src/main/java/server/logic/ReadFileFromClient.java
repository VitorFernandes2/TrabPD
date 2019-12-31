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

        try {
            readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void readFile() throws IOException {

        //Initialize the FileOutputStream to the output file's full path.
        FileOutputStream fos = null;
        byte[] contents = new byte[256];

        try {
            fos = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        InputStream is = in;

        //No of bytes read in one read() call
        int bytesRead = 0;

        do {

            bytesRead = is.read(contents);
            if (bytesRead < 0)
                break;
            bos.write(contents, 0, bytesRead);

        }while (bytesRead > 0);

        System.out.println("Recebi tudo");
        bos.flush();

    }

}
