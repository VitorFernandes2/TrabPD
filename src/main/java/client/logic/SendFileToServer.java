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

        try {
            sendFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendFile() throws IOException {

        File file = new File(filename);
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        FileInputStream bis = new FileInputStream(file);

        //Get socket's output stream
        OutputStream os = out;

        //Read File Contents into contents array
        byte[] contents = new byte[254];
        long fileLength = file.length();
        long current = 0;

        /*while(current!=fileLength){

            int size = 256;

            if(fileLength - current >= size)
                current += size;
            else{
                size = (int)(fileLength - current);
                current = fileLength;
            }

            contents = new byte[size];
            bis.read(contents, 0, size);
            os.write(contents);

        }*/

        int bytes = 0;

        do {

            bytes = bis.read(contents);
            if (bytes < 0)
                break;
            os.write(contents, 0, bytes);

        }while (bytes > 0);

        System.out.println(current + " - " + fileLength);
        fis.close();

    }

}
