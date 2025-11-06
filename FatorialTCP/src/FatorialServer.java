import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class FatorialServer {
    public static void main(String[] args) {
        int port = 5000;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor aguardando conexão...");
            try (Socket socket = serverSocket.accept()) {
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                System.out.println("Cliente conectado!");
                String numstr;
                while((numstr = input.readLine()) != null){
                    if(numstr.isEmpty()){
                        System.out.println("Cliente encerrou a conexão.");
                        break;
                    }
                    int num = Integer.parseInt(numstr);
                    System.out.println("Numero recebido: "+num);
                    long fat = 1;
                    System.out.print("Cálculo do fatorial: ");
                    for (int i = 1; i <= num; i++) {
                        fat *= i;
                        System.out.print(i);
                        if (i < num) System.out.print(" * ");
                    }
                    System.out.println(" = " + fat);
                    output.println(fat);
                }
                serverSocket.close();
                System.out.println("Conexão encerrada");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
