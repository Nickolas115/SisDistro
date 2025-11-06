import java.io.*;
import java.net.*;
import java.util.*;

public class Player {
    private static final String HOST = "10.14.163.43"; // ajuste cada vez que mudar de rede
    private static final int PORT = 5000;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Scanner scanner;
    private volatile boolean playing = true;

    public Player(String host, int port) {
        try {
            socket = new Socket(host, port);
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            scanner = new Scanner(System.in);
            //Pergunta o nome antes de iniciar threads (handshake)
            System.out.print("Digite seu nome: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) name = "Jogador_" + socket.getLocalPort();
            out.println("NOME:" + name);
            System.out.println("Bem-vindo, " + name + ".");
            //começa a ouvir o servidor e o teclado
            new Thread(this::listenServer, "ListenServer").start();
            new Thread(this::listenUser,   "ListenUser").start();
            while (playing && !socket.isClosed()) {
                try { 
                    Thread.sleep(200); 
                } catch (InterruptedException ignored) {}
            }
        } catch (IOException e) {
            System.out.println("Não foi possível conectar ao servidor: " + e.getMessage());
        } finally {
            try { 
                if (socket != null) socket.close(); 
            } catch (IOException ignored) {}
        }
    }

    //Recebe as mensagens do servidor
    private void listenServer() {
        try {
            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println(msg);
                if (msg.contains("Deseja jogar novamente?")) {
                    System.out.print(">>> ");
                } else if (msg.equals("Sua vez")) {
                    System.out.print("Digite 'carta' para pegar outra carta ou 'pare' para não pegar mais cartas:\n >>> ");
                }
            }
        } catch (IOException e) {
            if (playing) System.out.println("Conexão encerrada pelo servidor.");
        } finally {
            playing = false;
        }
    }

    //Recebe mensagens do usuario (teclado)
    private void listenUser() {
        try {
            while (playing && !socket.isClosed()) {
                if (scanner.hasNextLine()) {
                    String cmd = scanner.nextLine().trim();
                    out.println(cmd);
                    if (cmd.equalsIgnoreCase("n") || cmd.equalsIgnoreCase("não") || cmd.equalsIgnoreCase("nao")) {
                        System.out.println("Você saiu da mesa.");
                        playing = false;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erro de entrada: " + e.getMessage());
        }
    }

    //Inicio
    public static void main(String[] args) {
        new Player(HOST, PORT);
    }
}