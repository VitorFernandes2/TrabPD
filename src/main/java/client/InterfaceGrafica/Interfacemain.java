package client.InterfaceGrafica;

import client.Interfaces.observer;
import client.logic.ConnectData;
import mainObjects.Readers;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Scanner;

public class Interfacemain implements observer{

    @Override
    public void update(int acao, ConnectData data) {
        
        switch(acao){
            
            case 1:
                System.out.println("[ERROR] Excepao lanada na conexo UDP com ds");
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
                System.out.println("[ERROR] Excepo de IO lancada. Provavelmente o servidor" + "\n" + "[ERROR] desligou/perdeu-se a conecao ao servidor");
                String exc3 = (String) data.getJObj().get("exception");
                System.out.println("[ERROR] Excepo lancada aqui no notifier 5: " + exc3);
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

            //tocar musica
            case 11:
                this.playMusic(data);
                break;

            //remover musica
            case 12:
                this.removeMusic(data);
                break;

            //Listar musica
            case 13:
                this.listMusics(data);
                break;

            //Alterar musica
            case 14:
                this.changeMusic(data);
                break;

            //Menu das playlists
            case 15:
                this.playlistInterface(data);
                break;

            //Criacao de Playlist
            case 16:
                this.createPlaylist(data);
                break;

            //Remove de Playlist
            case 17:
                this.removePlaylist(data);
                break;

            //Change Playlist
            case 18:
                this.changePlaylist(data);
                break;

            //List Playlist
            case 19:
                this.ListPlaylists(data);
                break;

            //Play Playlist
            case 20:
                this.playPlayList(data);
                break;
                
            //View de pesquisa
            case 21:
                this.Searchnumber(data);
                break;
                
            //View de pesquisa Playlist
            case 22:
                this.SearchnumberPlaylist(data);
                break;

            case 444:
                String exc = (String) data.getJObj().get("exception");
                System.out.println("[ERROR] Excepcao lancada aqui no notifier: " + exc);
                break;

            case 86:
                String exc2 = (String) data.getJObj().get("output");
                System.out.println(exc2);
                break;
                
        }
        
    }

    private void playPlayList(ConnectData data) {

        String nome = Readers.readString("Insira o nome da playlist para reproduzir: ");
        data.setNome(nome);
        
        data.setMenu(15);

    }

    private void ListPlaylists(ConnectData data) {

        JSONObject obj = data.getJObj();

        long numberPlaylist = (long)obj.get("numberOfPlaylists");

        for (long i = 0; i < numberPlaylist; i++) {

            String nome = "playlist" + i;
            String name = (String) obj.get(nome);

            System.out.println(name);

        }
        
        data.setMenu(15);

    }

    private void changePlaylist(ConnectData data) {

        String nome = Readers.readString("Insira o nome da playlist que pretende alterar: ");
        String NewName = Readers.readString("Insira o novo nome da playlist que pretende alterar: ");

        data.setNome(nome);
        data.setAutor(NewName);
        
        data.setMenu(15);

    }

    private void removePlaylist(ConnectData data) {

        String name = Readers.readString("Insira o nome da playlist que pretende eliminar: ");
        data.setNome(name);
        
        data.setMenu(15);

    }

    private void createPlaylist(ConnectData data) {

        String nome = Readers.readString("Insira o nome da playlist: ");
        String musicName = null;
        String musicAuthor = null;
        data.setMusicAuthorList(new ArrayList<String>());
        data.setMusicNameList(new ArrayList<String>());
        String exit = null;

        do {
            musicName = Readers.readString("Insira o nome da musica: ");
            musicAuthor = Readers.readString("Insira o nome do autor da musica: ");
            data.getMusicAuthorList().add(musicAuthor);
            data.getMusicNameList().add(musicName);
            exit = Readers.readString("Pretende sair (sim - para sair): ");
        }while (!exit.equalsIgnoreCase("sim"));

        data.setNome(nome);
        
        data.setMenu(15);

    }



    private void playMusic(ConnectData data) {

        String nome = Readers.readString("Insira o nome da musica: ");
        String author = Readers.readString("Insira o nome da autor: ");

        data.setNome(nome);
        data.setAutor(author);
        
        data.setMenu(6);

    }

    private void createMusic(ConnectData data) {

        String nome = Readers.readString("Insira o nome da musica: ");
        String album = Readers.readString("Insira o nome do album: ");
        String autor = Readers.readString("Insira o nome da autor: ");
        String ano = Readers.readString("Insira o ano da musica: ");

        String duracao;
        double duration = 0;

        do {
            duracao = Readers.readString("Insira o duracao (em minutos e segundos) da musica (3:09 -> 3.09): ");
            duration = Double.parseDouble(duracao);
        }while (duration <= 0);

        String genero = Readers.readString("Insira o genero da musica: ");
        String caminho = Readers.readString("Insira o local da musica (ex: C:\\Users\\Utilizador\\Desktop\\ISEC\\TerceiroAno\\1Semestre\\PD\\12.mp3): ");

        data.setMusic(ano, nome, album, autor, genero, duration, caminho);

    }

    private void changeMusic(ConnectData data) {

        String nomeAlterar = Readers.readString("Insira o nome da musica a alterar: ");
        String autorAlterar = Readers.readString("Insira o autor da musica a alterar: ");
        String nome = Readers.readString("Insira o novo nome da musica: ");
        String album = Readers.readString("Insira o novo nome do album: ");
        String autor = Readers.readString("Insira o novo nome da autor: ");
        String ano = Readers.readString("Insira o novo ano da musica: ");

        String duracao;
        double duration = 0;

        do {
            duracao = Readers.readString("Insira a nova duracao (em minutos e segundos) da musica (3:09 -> 3.09): ");
            duration = Double.parseDouble(duracao);
        }while (duration <= 0);

        String genero = Readers.readString("Insira o novo genero da musica: ");

        data.setNome(nomeAlterar);
        data.setAutor(autorAlterar);
        data.setMusic(ano, nome, album, autor, genero, duration, null);
        
        data.setMenu(6);

    }

    private void listMusics(ConnectData data){

        JSONObject obj = data.getJObj();

        long numberMusics = (long)obj.get("numberOfMusics");

        for (long i = 1; i <= numberMusics; i++) {

            String nome = "music" + i;
            JSONArray array = (JSONArray) obj.get(nome);

            System.out.println(nome + "\n" + array.get(0) + "\n" + array.get(1) + "\n"
                    + array.get(2) + "\n" + array.get(3) + "\n"
                    + array.get(4) + "\n" + array.get(5) + "\n");

        }

        data.setMenu(6);
        
    }
    
    private void Searchnumber(ConnectData data){

        System.out.println("Entrou no Searchnumber");
        System.out.println("---Como Utilizar---");
        System.out.println("  -> Escreva o nome da musica para procurar por nome (ex: don't stop me now) ");
        System.out.println("  -> Escreva o nome : filtro : parametro para utilizar filtros (ex: don't stop me now : autor : Queen)");
        System.out.println("  -> Filtros possiveis: artist, album, year, genre, duration");
        
        String comando = Readers.readString("Insira o Comando: ");
        
        /*JSONObject obj = data.getJObj();

        long numberMusics = (long)obj.get("numberOfMusics");

        for (long i = 1; i <= numberMusics; i++) {

            String nome = "music" + i;
            JSONArray array = (JSONArray) obj.get(nome);

            System.out.println(nome + "\n" + array.get(0) + "\n" + array.get(1) + "\n"
                    + array.get(2) + "\n" + array.get(3) + "\n"
                    + array.get(4) + "\n" + array.get(5) + "\n");

        }*/
        data.setCommand(comando);
        data.setMenu(6);
        
    }

    private void removeMusic(ConnectData data) {

        String nome = Readers.readString("Insira o nome da musica: ");
        String autor = Readers.readString("Insira o nome do autor: ");

        data.setNome(nome);
        data.setAutor(autor);
        
        data.setMenu(6);

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
            System.out.println("searchMusics - Procurar musicas");
            System.out.println("createMusic - Criar uma musica");
            System.out.println("removeMusic - Remover musica");
            System.out.println("changeMusic - Alterar musica");
            System.out.println("playMusic - Tocar musica");
            System.out.println("voltar - Voltar para o menu anterior");

            cmd = Readers.readString("\nInsira a opcao que pretende: ");

        }while (!cmd.equals("listMusics") && !cmd.equals("createMusic")
                && !cmd.equals("removeMusic") && !cmd.equals("changeMusic")
                && !cmd.equals("playMusic") && !cmd.equals("voltar") && !cmd.equals("searchMusics"));

        data.setCommand(cmd);

    }

    private void playlistInterface(ConnectData data) {

        String cmd = null;

        do{

            System.out.println("\nPlaylists:");
            System.out.println("listPlaylists - Listar as playlists na base de dados");
            System.out.println("searchPlaylists - Procurar por playlists na base de dados");
            System.out.println("createPlaylist - Criar uma playlists");
            System.out.println("removePlaylist - Remover playlists");
            System.out.println("changePlaylist - Alterar playlists");
            System.out.println("playPlaylist - Tocar playlists");
            System.out.println("voltar - Voltar para o menu anterior");

            cmd = Readers.readString("\nInsira a opcao que pretende: ");

        }while (!cmd.equals("listPlaylists") && !cmd.equals("createPlaylist")
                && !cmd.equals("removePlaylist") && !cmd.equals("changePlaylist")
                && !cmd.equals("playPlaylist") && !cmd.equals("voltar") && !cmd.equals("searchPlaylists"));

        data.setCommand(cmd);

    }

    private void initialMenu(ConnectData data){

        String cmd = null;

        do{

            System.out.println("\nTrabalho de PD:");
            System.out.println("entraLogin - Fazer login");
            System.out.println("entraRegisto - Fazer registo");
            System.out.println("entrar - Entrar direto");
            System.out.println("sair - Sair direto");

            cmd = Readers.readString("\nInsira a opcao que pretende: ");

        }while (!cmd.equals("entraLogin") && !cmd.equals("entraRegisto")
                && !cmd.equals("entrar") && !cmd.equals("sair"));

        data.setCommand(cmd);

    }

    private void logininter(ConnectData data) {
        String user = new String();
        String pass = new String();
        System.out.println("\n Autenticao (nao aceitavel espacos em branco): ");

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
        String name = new String();
        String user = new String();
        String pass = new String();
        System.out.println("\n Registo (nao aceitavel espacos em branco): ");

        do{
            System.out.print("\n Name:");
            Scanner myObj = new Scanner(System.in);
            name = myObj.nextLine();
        }while(user.contains(" "));

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
        
        data.setName(name);
        data.setUsername(user);
        data.setPassword(pass);
        
        //No fim de se registar ele volta ao menu para poder realizar login
        data.setMenu(7);
        
    }

    private void tempshowreceb(ConnectData data) {
        System.out.println(data.getJObj().toString());
    }

    private void SearchnumberPlaylist(ConnectData data) {
                System.out.println("Entrou no SearchPlayList");
        System.out.println("---Como Utilizar---");
        System.out.println("  -> Escreva o nome da Playlist para procurar por nome (ex: Playlist1) ");
        System.out.println("  -> Escreva o nome : filtro : parametro para utilizar filtros (ex: Playlist1 : name : Music1)");
        System.out.println("  -> Filtros possiveis: name, artist, album, year");
        System.out.println("Nota: esta pesquisa mostra só o nome da playlist pesquisada");
        
        String comando = Readers.readString("Insira o Comando: ");
        
        data.setCommand(comando);
        data.setMenu(6);
    }
    
}
