package mainObjects;

import java.util.Scanner;

public class Readers {

    public static String readString(String message){

        Scanner scanner = new Scanner(System.in);
        System.out.println(message);
        String retorno = scanner.nextLine();
        return retorno;

    }

    public static int readInteger(String s) {

        Scanner scanner = new Scanner(System.in);
        System.out.println(s);
        int retorno = 0;

        do {
            while (!scanner.hasNextInt())
                scanner.next();
            retorno = scanner.nextInt();
        }while (retorno < 0);

        return retorno;

    }
}
