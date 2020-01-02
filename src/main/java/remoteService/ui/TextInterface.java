package remoteService.ui;

import mainObjects.Readers;

public class TextInterface {

    public static String menu(){

        String value = null;

        do {
            System.out.println("\n--------Servico Remoto--------");
            System.out.println("listServers - Listar servidores");
            System.out.println("endServer - Terminar servidor");
            System.out.println("regListener - Registar listener");
            System.out.println("delListener - Eliminar Listener");
            System.out.println("exit - Sair");

            value = Readers.readString("\nInsira a opcao que pretende: ");

        }while (!value.equals("listServers") && !value.equals("endServer") &&
                !value.equals("regListener") && !value.equals("delListener")
                && !value.equals("exit"));

        return value;

    }

}
