package mainObjects;

import java.util.Scanner;

public class Readers {

    public static String readString(String message){

        Scanner scanner = new Scanner(System.in);
        System.out.println(message);
        String retorno = scanner.nextLine();
        return retorno;

    }

}
