package client.InterfaceGrafica;

import client.Interfaces.observer;
import client.logic.ConnectData;
import mainObjects.Readers;

import java.util.Scanner;

public class Interfacemain implements observer{

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

            case 9:
                this.createMusic(data);
                break;

            case 10:
                this.registinter(data);
                break;

            case 444:
                String exc = (String) data.getJObj().get("exception");
                System.out.println("[ERROR] Excepção lançada aqui no notifier: " + exc);
                break;

            case 86:
                String exc2 = (String) data.getJObj().get("output");
                System.out.println(exc2);
                break;
                
        }
        
    }

    private void createMusic(ConnectData data) {

        String nome = Readers.readString("Insira o nome da musica: ");
        String album = Readers.readString("Insira o nome do album: ");
        String autor = Readers.readString("Insira o nome da autor: ");
        String ano = Readers.readString("Insira o ano da musica: ");

        String duracao;
        double duration = 0;

        do {
            duracao = Readers.readString("Insira o duracao da musica: ");
            duration = Double.parseDouble(duracao);
        }while (duration == 0);

        String genero = Readers.readString("Insira o genero da musica: ");
        String caminho = Readers.readString("Insira o local da musica: ");

        data.setMusic(ano, nome, album, autor, genero, duration, caminho);

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
            System.out.println("changeMusic - Alterar musica");
            System.out.println("playMusic - Tocar musica");

            cmd = Readers.readString("\nInsira a opcao que pretende: ");

        }while (!cmd.equals("listMusics") && !cmd.equals("createMusic")
                && !cmd.equals("removeMusic") && !cmd.equals("changeMusic")
                && !cmd.equals("playMusic"));

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
        String user = new String();
        String pass = new String();
        System.out.println("\n Autenticação (não aceitavel espacos em branco): ");

        do{
            System.out.print("\n Username:");
            Scanner myObj = new Scanner(System.in);
            user = myObj.nextLine();
        }while(user.contains(" "));

        do{
            System.out.print(" Password:");
            Scanner myObj = new Scanner(System.in);
            pass = myObj.nextLine();
        }while(pass.contains(" "));
        
        data.setUsername(user);
        data.setPassword(pass);
        
        data.setMenu(8);
    }
    
    private void registinter(ConnectData data) {
        String user = new String();
        String pass = new String();
        System.out.println("\n Registo (não aceitavel espacos em branco): ");

        do{
            System.out.print("\n Username:");
            Scanner myObj = new Scanner(System.in);
            user = myObj.nextLine();
        }while(user.contains(" "));

        do{
            System.out.print(" Password:");
            Scanner myObj = new Scanner(System.in);
            pass = myObj.nextLine();
        }while(pass.contains(" "));
        
        data.setUsername(user);
        data.setPassword(pass);
        
        data.setMenu(8);
    }

    private void tempshowreceb(ConnectData data) {
        System.out.println(data.getJObj().toString());
    }
    
}
