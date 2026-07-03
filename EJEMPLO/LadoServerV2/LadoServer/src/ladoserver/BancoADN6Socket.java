package ladoserver;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;


public class BancoADN6Socket {

    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingresar puerto del socket");

        int PORT = Integer.parseInt(scanner.nextLine());
        
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Escuchando puerto " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado" + clientSocket.getInetAddress());

                Thread t = new Thread(new BancoADN6RequestHandler(clientSocket));
                t.start();
            }

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
