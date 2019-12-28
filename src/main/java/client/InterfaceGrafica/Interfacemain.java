package client.InterfaceGrafica;

import client.Interfaces.observer;
import client.logic.ConnectData;
import mainObjects.Readers;

import java.util.Scanner;

public class Interfacemain implements observer{
    
    public void inicio (){
        
        System.out.println("Bem Vindo \n Insira o seu username: \n");
        Scanner myObj = new Scanner(System.in);
        String userName = myObj.nextLine();
        System.out.println( userName + " insira a sua palavra passe: \n");
        String password = myObj.nextLine();
        
    }

    @Override
    public void update(int acao, ConnectData data) {
        
        switch(acao){
            
            case 1:
                System.out.println("[ERROR] Excepção lançada na conexão UDP com ds");
                break;
                
            case 2:
                this.logininter(data);
                break;
                
            case 3:
                this.tempshowreceb(data);
                break;
                
            case 4:
                Scanner myObj = new Scanner(System.in);  // TEMP - pausa para manter a thread a correr. escreve algo pra parar thread
                String userName = myObj.nextLine();
                break;
            
            case 5:
                System.out.println("[ERROR] Excepção de IO lançada. Provavelmente o servidor" + "\n" + "[ERROR] desligou/perdeu-se a conecção ao servidor");
                break;

            //Menu de musicas
            case 6:
                this.musicInterface(data);
                break;

            //Menu inicial
            case 7:
                this.initialMenu(data);
                break;

            //Menu com todas as opcoes
            case 8:
                this.secondMenu(data);
                break;

            case 444:
                String exc = (String) data.getJObj().get("exception");
                System.out.println("[ERROR] Excepção lançada aqui no notifier: " + exc);
                break;
                
        }
        
    }

    private void secondMenu(ConnectData data) {

        String cmd = null;

        do{

            System.out.println("\nMusicas:");
            System.out.println("gotoMusics - Ir para menu de Musicas");
            System.out.println("gotoPlaylists - Ir para menu de Musicas");
            System.out.println("logout - Fazer logout");

            cmd = Readers.readString("\nInsira a opcao que pretende: ");

        }while (!cmd.equals("gotoMusics") && !cmd.equals("gotoPlaylists")
                && !cmd.equals("logout"));

        data.setCommand(cmd);

    }

    private void musicInterface(ConnectData data) {

        String cmd = null;

        do{

            System.out.println("\nMusicas:");
            System.out.println("listMusics - Listar as musicas na base de dados");
            System.out.println("createMusic - Criar uma musica");
            System.out.println("removeMusic - Remover musica");
            System.out.println("changeMusic - Alterar music");

            cmd = Readers.readString("\nInsira a opcao que pretende: ");

        }while (!cmd.equals("listMusics") && !cmd.equals("createMusic")
                && !cmd.equals("removeMusic") && !cmd.equals("changeMusic"));

        data.setCommand(cmd);

    }

    private void initialMenu(ConnectData data){

        String cmd = null;

        do{

            System.out.println("\nTrabalho de PD:");
            System.out.println("entraLogin - Fazer login");
            System.out.println("entraRegisto - Fazer registo");
            System.out.println("entrar - Entrar direto");

            cmd = Readers.readString("\nInsira a opcao que pretende: ");

        }while (!cmd.equals("entraLogin") && !cmd.equals("entraRegisto")
                && !cmd.equals("entrar"));

        data.setCommand(cmd);

    }

    private void logininter(ConnectData data) {

        System.out.println("\n Autenticação: ");

        System.out.print("\n Login:");
        Scanner myObj = new Scanner(System.in);
        data.setUsername(myObj.nextLine());

        System.out.print(" PassWord:");
        myObj = new Scanner(System.in);
        data.setPassword(myObj.nextLine());

    }

    private void tempshowreceb(ConnectData data) {

        System.out.println(data.getJObj().toString());
    
    }



    
}
