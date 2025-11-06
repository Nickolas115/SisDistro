import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class FatorialClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5000;
        try (Socket socket = new Socket(host, port)){
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);
            System.out.println("Digite números inteiros para calcular o fatorial.");
            String in;
            while ((in = scanner.nextLine()) != null) {
                output.println(in);
                if(in.isEmpty()){
                    System.out.println("Encerrando conexão...");
                    break;
                }
                String result = input.readLine();
                System.out.println("Resultado recebido do servidor: " + result);
                
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
