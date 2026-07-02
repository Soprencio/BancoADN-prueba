package mavenproject1;

import java.io.*;
import java.net.Socket;

public class Mavenproject1 {

    public static void main(String[] args) {
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            // Attempt connection
            socket = new Socket("localhost", 4990);
            
            // Setup streams
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            //busca usuarios por id o nombre siendo el formato NULL nombre o id NULL (en caso de buscar por nombre busca todos los coincidentes)
            /*
            out.println("BuscarIDNOM - NULL - Mamuel");
            
            String response1 = in.readLine();
            while (!response1.equals("FINISH")){
                System.out.println("Valoree: " + response1);
                response1 = in.readLine();
            }
            
            // crea cuenta por medio de mail nombreusr y contraseña
            
            out.println("CrearC - waws@gmail.com - dwa - 4321");
            String response2 = in.readLine();
            System.out.println("Creado exitoso o no: " + response2);
            
            // por medio del mail y contraseña inicia sesion (si funciona significa que ese mail y contraseña son validos y estan en la db)
            
            out.println("IniciarS - waws@gmail.com - 4321");
            String response3 = in.readLine();
            System.out.println("si -1 mail/contra mal/no existe 0 usuario y login correcto 1 admin y login correcto:   " + response3);
            
            // por medio del mail del usr da de baja
            
            out.println("DarDBaja - wasd@gmail.com");
            String response4 = in.readLine();
            System.out.println("Bajado: " + response4);
            
            // por medio del mail del usr restaura
            
            out.println("DarDRestaur - wasd@gmail.com");
            String response5 = in.readLine();
            System.out.println("restaurado: " + response5);
            
            // modifica datos de perfil por medio del mail de usr
            
            out.println("ModificP - 4@gmail.com - Raton Perez - dgrhdtfegtrh5 - vivito y coleando - 2026-01-22");
            String response6 = in.readLine();
            System.out.println("Modificado: " + response6);
            
            //Buscar datos por medio de mail
            /*
            out.println("BuscarDat - PEpeelperwaswsa2@gmail.com");
            String response7 = in.readLine();
            System.out.println("Valore: " + response7);
            
            
            
            
            // 4 = id de solicitud, en el segundo valor 1 es aceptar 2 es rechazar
            // el mail es el del admin
            
            // si respuesta es -1 no se encontro solicitud, si respuesta es 1 se acepto con exito y si es 0 se rechazo con exito
            */
            
            // IMPORTANTE BOTIN
            // IMPORTANTE BOTIN
            // IMPORTANTE BOTIN
            // IMPORTANTE BOTIN
            // IMPORTANTE BOTIN
            

// si respuesta es -1 no se encontro solicitud, si respuesta es 1 se acepto con exito y si es 0 se rechazo con exito
            
            out.println("ResSol - 4 - 1 - Nuevo@gmail.com");
            String response8 = in.readLine();
            System.out.println("Valore: " + response8);
            
            // IMPORTANTE BOTIN
            // IMPORTANTE BOTIN
            // IMPORTANTE BOTIN
            // IMPORTANTE BOTIN
            // IMPORTANTE BOTIN
            
            
            
            
            /*
            //El mail (primer valor) es el mail de quien envia la solicitud
            
            //El segundo valor tiene " " o decidi vos que va adentro (esto si es sol baja o restaurar)
            
            //O el segundo valor tiene los datos de modificar perfil con el siguiente formato:
            //Normi@gmail.com _ sandwatiago _ wdada1wdfaw _ desc2 _ 2026-02-14
            //siendo maildeusr _ nombrecompleto _ cadSec _ descrip _ fechaMuestra
            
            //el tercer valor define que tipo es, hay baja restaurar y modificar como opciones, modificar necesitando lo de arriba
            
            out.println("CrearSolPer - mail2@gmail.com -   - restaurar");
            String response9 = in.readLine();
            System.out.println("Valore: " + response9);
            
            
            // similar al anterior solo que es para solicitar la creacion de un perfil, este necesita el siguiente formato:
            // maildeusr _ nombrecompleto _ cadSec _ descrip _ fechaMuestra
            
            
            //out.println("CrearPerfilSol - Nuevo@gmail.com - Nuevo@gmail.com _ NombreCompleto _ CadSec _ Descripcion _ 2026-04-24");
            //String response10 = in.readLine();
            //System.out.println("Valore: " + response10);
            
            
            // mira la lista de solicitudes (esto es para que admin acepte o vea historial de acptadas y rechazadas 
            // ejemplo:
            
            out.println("ListaSol");
            String response11 = in.readLine();
            while (!response11.equals("FINISH")){
                System.out.println("Valore: " + response11);
                response11 = in.readLine();
            }
            
            out.println("UltSol");
            String response12 = in.readLine();
            while (!response12.equals("FINISH")){
                System.out.println("Valore: " + response12);
                response12 = in.readLine();
            }*/
            
            
        } catch (IOException e) {
            System.err.println("Error: Could not connect. Check if your server is running!");
            e.printStackTrace();
        } finally {
            // Manually closing everything for older Java versions
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}