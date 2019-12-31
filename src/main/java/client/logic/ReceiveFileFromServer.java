package client.logic;

import java.io.*;

public class ReceiveFileFromServer extends Thread {

    private InputStream in;
    private OutputStream out;
    private String filename;
    private long size = 0;

    public ReceiveFileFromServer(InputStream in, OutputStream out, String filename, long size) {
        this.in = in;
        this.out = out;
        this.filename = filename;
        this.size = size;
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
        byte[] contents = new byte[254];

        try {
            fos = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        InputStream is = in;

        //No of bytes read in one read() call
        int bytesRead = 0;
        int current = 0;
        do {

            bytesRead = is.read(contents);

            fos.write(contents, 0, bytesRead);
            current += bytesRead;

            System.out.println(current + " - " + size);

            if (current >= size)
                break;

        }while (bytesRead > 0);

        System.out.println("Recebi tudo");
        fos.close();

    }

}
