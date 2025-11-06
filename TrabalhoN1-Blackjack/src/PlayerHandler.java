import java.io.*;
import java.net.*;
import java.util.*;

public class PlayerHandler implements Runnable {
    private final Socket socket;
    private final Table table;
    private PrintWriter out;
    private BufferedReader in;
    private String name;
    private final List<String> hand = new ArrayList<>();
    private boolean active = false; //true quando a rodada começar para este jogador
    private boolean playNext = true; //assume que quer jogar ao conectar
    private boolean waiting = false; //aguardando resposta (s/n) no fim da rodada

    public PlayerHandler(Socket socket, Table table) {
        this.socket = socket;
        this.table = table;
        try {
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Erro ao inicializar streams do jogador: " + e.getMessage());
        }
    }

    public void run() {
        try {
            //Espera "NOME:..." do jogador (Handshake)
            String first = in.readLine();
            if (first != null && first.startsWith("NOME:")) {
                name = first.substring(5).trim();
            }
            if (name == null || name.isBlank()) {
                name = "Jogador_" + socket.getPort();
            }
            //Agora registra o jogador na mesa (passa a receber broadcasts)
            table.registerPlayer(this);
            //Loop de comandos enviados pelo cliente
            String input;
            while ((input = in.readLine()) != null) {
                if (waiting) { //Espera a resposta do jogador se quer jogar ou não
                    if (input.equalsIgnoreCase("s") || input.equalsIgnoreCase("sim")) {
                        playNext = true;
                        waiting = false;
                        //demais resets acontecem em resetForRound() na próxima rodada
                    } else if (input.equalsIgnoreCase("n") || input.equalsIgnoreCase("não") || input.equalsIgnoreCase("nao")) {
                        playNext = false;
                        waiting = false;
                        break; //sai no finally e Table remove no unregister
                    } else { //Caso a resposta não seja válida
                        sendMessage("Responda com 's' ou 'n'. Deseja jogar novamente? (s/n)");
                    }
                } else if (active) { //Jogador na partida
                    if (input.equalsIgnoreCase("carta")) {
                        table.dealCard(this);
                        if (active) sendMessage("Sua vez"); //Se o jogador não ganhou/estourou, permite que ele escreva de novo
                    } else if (input.equalsIgnoreCase("pare")) {
                        active = false; //Ignora este jogador até a partida acabar
                        sendMessage("Você parou. Sua pontuação final é: " + getScore());
                    } else { //Se a mensagem não for válida
                        sendMessage("Sua vez");
                    }
                } else { //Fora da sua vez/aguardando início da rodada
                    if (!table.isRoundInProgress()) {
                        sendMessage("Aguardando próxima rodada...");
                    }
                }
            }
        } catch (IOException e) {
            if (!"Socket closed".equals(e.getMessage())) {
                System.out.println("Erro no jogador " + name + ": " + e.getMessage());
            }
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
            table.unregisterPlayer(this);
            System.out.println("Jogador " + name + " saiu.");
        }
    }

    //Chamado pela Table no início de cada rodada para preparar o jogador
    public void resetForRound() {
        hand.clear();
        active = true;
        waiting = false;
    }

    //Adiciona cartas a mão do jogador e já informa se ele fez blackjack ou estourou
    public void addCard(String card) {
        hand.add(card);
        int score = getScore();
        out.println("Você recebeu: " + card + ". Sua mão: " + hand + " (pontuação: " + score + ")");
        if (score == 21) {
            out.println("BLACKJACK! Você atingiu 21.");
            active = false;
        } else if (score > 21) {
            out.println("Você estourou com " + score + " pontos.");
            active = false;
        }
    }

    //Calcula a pontuação dos jogadores
    public int getScore() {
        int score = 0, aces = 0;
        for (String card : hand) {
            if (card.equals("K") || card.equals("Q") || card.equals("J")) score += 10;
            else if (card.equals("A")) { 
                aces++; score += 11; 
            }
            else score += Integer.parseInt(card);
        }
        while (score > 21 && aces > 0) {
            score -= 10; aces--; 
        }
        return score;
    }

    //Pergunta se quer jogar novamente
    public void askPlayNext() {
        sendMessage("Deseja jogar novamente? (s/n)");
        waiting = true;
    }

    //Funções para as váriaveis
    public String getName() { 
        return name; 
    }
    public List<String> getHand() { 
        return hand; 
    }
    public boolean isActive() { 
        return active; 
    }
    public boolean isWaiting() { 
        return waiting; 
    }
    public boolean playNext() { 
        return playNext; 
    }

    //Envio de mensagens para o jogador
    public void sendMessage(String msg) {
        if ("Sua vez".equals(msg)) {//"Sua vez" só é enviado se o jogador não ganhou/estourou
            if (active) out.println(msg);
        } else {
            out.println(msg);
        }
    }
}