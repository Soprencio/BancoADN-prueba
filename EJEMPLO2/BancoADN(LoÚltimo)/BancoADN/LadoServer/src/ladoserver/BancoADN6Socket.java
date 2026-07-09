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


public class BancoADN6Socket {

    private static final int PORT = 4990;

    public static void main(String[] args) {
        
        /*
        File file = new File("cuentas.txt");
        try {
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        */
        
        /*
        line="BuscarIDNOM - 1 - NULL";
        String[] Partes = line.split(" - ");
        String[] Cuenta;
        
        try (BufferedReader br = new BufferedReader(new FileReader("PerfilGenetico.txt"))) {
            line=br.readLine();
            while(line != null){
                Cuenta = line.split(" - ");
                if(Cuenta[0].equals(Partes[1]) && !Partes[1].equals("NULL")){
                    System.out.println(Cuenta[0] + "-" + Cuenta[1] + "-" + Cuenta[2] + "-" + Cuenta[3] + "-" + Cuenta[4] + "-" + Cuenta[5]);
                }
                else if(Cuenta[1].equals(Partes[2]) && !Partes[2].equals("NULL")){
                    System.out.println(Cuenta[0] + "-" + Cuenta[1] + "-" + Cuenta[2] + "-" + Cuenta[3] + "-" + Cuenta[4] + "-" + Cuenta[5]);
                }
                
                line=br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        
        
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
