package client.logic;

import java.io.*;

public class SendFileToServer extends Thread {

    private String filename;
    private OutputStream out;

    public SendFileToServer(String filename, OutputStream out) {
        this.filename = filename;
        this.out = out;
    }

    @Override
    public void run() {

        File file = new File(filename);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedInputStream bis = new BufferedInputStream(fis);

        //Get socket's output stream
        OutputStream os = out;

        //Read File Contents into contents array
        byte[] contents;
        long fileLength = file.length();
        long current = 0;

        while(current!=fileLength){
            int size = 10000;
            if(fileLength - current >= size)
                current += size;
            else{
                size = (int)(fileLength - current);
                current = fileLength;
            }
            contents = new byte[size];
            try {
                bis.read(contents, 0, size);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                os.write(contents);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
