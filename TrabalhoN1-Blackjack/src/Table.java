import java.io.*;
import java.net.*;
import java.util.*;
//Integrantes do Grupo: Lucas Emanuel Maifrede Machado, Mozar Guimaraes Junior, Nickolas Carvalho Azofeifa
public class Table {
    private static final int PORT = 5000;
    private final  List<PlayerHandler> players;
    private final List<String> dealerHand;
    private final Random random;
    private volatile boolean run;
    private volatile boolean roundInProgress;

    public Table() {
        this.players = Collections.synchronizedList(new ArrayList<>());
        this.dealerHand = new ArrayList<>();
        this.random = new Random();
    }

    //Gera carta
    private String drawCard() {
        String[] cards = {"2","3","4","5","6","7","8","9","10","J","Q","K","A"};
        return cards[random.nextInt(cards.length)];
    }

    //Distribui carta
    public synchronized void dealCard(PlayerHandler player) {
        String card = drawCard();
        player.addCard(card);
    }

    //Calcula a pontuação do Dealer
    private int calculateScore(List<String> hand) {
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

    private void broadcast(String msg) {
        synchronized (players) {
            for (PlayerHandler p : players) p.sendMessage(msg);
        }
    }

    //Chamado pelo PlayerHandler após ler "NOME:... (Handshake)"
    public void registerPlayer(PlayerHandler p) {
        players.add(p);
        if (roundInProgress) { //Se uma rodada estiver em andamento, deixa o jogador em espera até a próxima
            p.sendMessage("Rodada em andamento. Você entrará na próxima rodada.");
        } else { //
            p.sendMessage("Aguardando próxima rodada...");
        }
        System.out.println(">> " + p.getName() + " entrou na mesa.");
        broadcast(">> " + p.getName() + " entrou na mesa.");
    }

    //Chamado quando o socket do jogador fecha
    public void unregisterPlayer(PlayerHandler p) {
        players.remove(p);
        broadcast(">> " + p.getName() + " saiu da mesa.");
    }

    public boolean isRoundInProgress() { 
        return roundInProgress; 
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor iniciado na porta " + PORT);
            run = true;
            //Thread do loop de jogo
            new Thread(this::gameLoop, "GameLoop").start();
            //Thread principal: aceitar conexões
            while (run) {
                Socket socket = serverSocket.accept();
                PlayerHandler handler = new PlayerHandler(socket, this);
                new Thread(handler, "PH-" + socket.getPort()).start();
                System.out.println("Conexão aceita de " + socket.getRemoteSocketAddress());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void gameLoop() {
        while (run) {
            //Espera ter pelo menos um jogador pronto
            if (players.isEmpty()) {
                try { 
                    Thread.sleep(500); 
                } catch (InterruptedException ignored) {}
                continue;
            }
            //Pequena janela de lobby para permitir que mais jogadores entrem
            try { 
                Thread.sleep(2000); 
            } catch (InterruptedException ignored) {}

            //Snapshot de participantes desta rodada
            final List<PlayerHandler> participants = new ArrayList<>();
            synchronized (players) { 
                participants.addAll(players); 
            }
            if (participants.isEmpty()) continue;
            roundInProgress = true;
            startRound(participants); //Todos na rodada jogam
            dealerTurn(); //Vez do Dealer
            checkWinners(participants); //Contagem para ver quem ganhou
            //Pergunta quem quer continuar (apenas a quem jogou esta rodada)
            for (PlayerHandler p : participants) p.askPlayNext();
            //Espera respostas
            boolean waiting = true;
            while (waiting) {
                waiting = false;
                for (PlayerHandler p : participants) {
                    if (p.isWaiting()) { waiting = true; break; }
                }
                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            }
            //Remove quem não quer continuar da lista global de prontos
            synchronized (players) {
                players.removeIf(p -> participants.contains(p) && !p.playNext());
            }
            roundInProgress = false;
            if (players.isEmpty()) {
                System.out.println("Todos os jogadores saíram. Aguardando novos jogadores...");
            }
        }
    }

    //Vez dos Jogadores
    private void startRound(List<PlayerHandler> participants) {
        System.out.println("Rodada começou");
        broadcast("Rodada começou");
        dealerHand.clear();
        //Dealer pega duas cartas (uma para cima e outra para baixo)
        dealerHand.add(drawCard());
        dealerHand.add(drawCard());
        broadcast("Dealer: [" + dealerHand.get(0) + ", ?]");
        //Cada jogador recebe 2 cartas
        for (PlayerHandler p : participants) {
            p.resetForRound();
            p.addCard(drawCard());
            p.addCard(drawCard());
            p.sendMessage("Sua vez");
        }
        //Espera todos terminarem
        while (true) {
            boolean allInactive = true;
            for (PlayerHandler p : participants) {
                if (p.isActive()) { 
                    allInactive = false; 
                    break; 
                }
            }
            if (allInactive) break;
            try { 
                Thread.sleep(400); 
            } catch (InterruptedException ignored) {}
        }
    }

    //Vez do Dealer
    private void dealerTurn() {
        broadcast("Dealer revela: " + dealerHand + " (" + calculateScore(dealerHand) + ")");
        while (calculateScore(dealerHand) < 17) {
            String card = drawCard();
            dealerHand.add(card);
            broadcast("Dealer compra: " + dealerHand + " (" + calculateScore(dealerHand) + ")");
        }
    }

    //Contagem da pontuação para ver quem ganha
    private void checkWinners(List<PlayerHandler> participants) {
        int dealerScore = calculateScore(dealerHand);
        System.out.println("\n=== RESULTADOS ===");
        if (dealerScore <= 21) {
            System.out.println("Dealer terminou com " + dealerScore);
            broadcast("Dealer terminou com " + dealerScore);
        } else {
            System.out.println("Dealer estourou!");
            broadcast("Dealer estourou!");
        }
        for (PlayerHandler p : participants) {
            int score = p.getScore();
            System.out.println(p.getName() + " -> " + p.getHand() + " (" + score + ")");
            if (score > 21) {
                p.sendMessage("Você estourou.");
            } else if (dealerScore > 21 || score > dealerScore) {
                p.sendMessage("Você ganhou!");
            } else if (score == dealerScore) {
                p.sendMessage("Empate!");
            } else {
                p.sendMessage("Você perdeu.");
            }
        }
        System.out.println("Rodada encerrada.");
        broadcast("Rodada encerrada.");
    }

    //Inicio
    public static void main(String[] args) {
        new Table().start();
    }
}