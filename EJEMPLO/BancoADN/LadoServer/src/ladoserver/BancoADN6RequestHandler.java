package ladoserver;
import java.io.*;
import java.awt.*;
import java.net.*;
import java.nio.file.Files;
import java.util.*;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Paths;
import java.util.List; 
import java.util.concurrent.*; 
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BancoADN6RequestHandler implements Runnable {
 
    private final Socket socket;
    private static final Semaphore mutexModRegistro = new Semaphore(1);
    private static final Semaphore mutexModCuenta = new Semaphore(1);
    private static final Semaphore mutexModCuentaAsignada = new Semaphore(1);
    private static final Semaphore mutexModSolicitud = new Semaphore(1);
    private static final Semaphore mutexModPerfilGenetico = new Semaphore(1);
    private static final Semaphore mutexModTipoAccion = new Semaphore(1);
    
    // Updated to ThreadLocal for thread safety
    private final ThreadLocal<String> mailusr = new ThreadLocal<>();
 
    public BancoADN6RequestHandler(Socket socket) {
        this.socket = socket;
    }

    static List<String> leerRegistros(String nombreArchivo) throws Exception { 
        File file=new File(nombreArchivo);
        List<String> resultado=new ArrayList<>();
        if(!file.exists()) return resultado;
        
        BufferedReader reader=new BufferedReader(new FileReader(file));
        String linea;
        boolean primeraLinea=true;
        while ((linea = reader.readLine()) != null) {
            if (primeraLinea) { primeraLinea = false; }
            else if (linea!=null) { resultado.add(linea); }
        }
        reader.close();
        return resultado;
    }    

    static int leerContador(String nombreArchivo) throws Exception { 
        File file=new File(nombreArchivo);
        if (!file.exists()) return 0;
        BufferedReader reader=new BufferedReader(new FileReader(file));
        String primera=reader.readLine(); 
        reader.close();
        if (primera == null || !primera.contains("=")) return 0;
        return Integer.parseInt(primera.split("=")[1].trim());
    }
    
    static void actualizarContador(String nombreArchivo, int nuevoContador) throws Exception {
        File file=new File(nombreArchivo);
        List<String> registros=leerRegistros(nombreArchivo);
        BufferedWriter writer=new BufferedWriter(new FileWriter(file, false));
        writer.write("idIncremental=" + nuevoContador);
        writer.newLine();
        for (String reg : registros) {
            writer.write(reg);
            writer.newLine();
        }
        writer.close();
    }

    static int agregarRegistro(String nombreArchivo, String datos) throws Exception { 
        File file=new File(nombreArchivo);
        int nuevoId=leerContador(nombreArchivo) + 1;
        actualizarContador(nombreArchivo, nuevoId);
        BufferedWriter writer=new BufferedWriter(new FileWriter(file, true)); 
        writer.write(nuevoId + " - " + datos);
        writer.newLine();
        writer.close();
        return nuevoId; 
    }

    static void editarRegistro(String nombreArchivo, int id, String nuevoDatos) throws Exception {
        File file=new File(nombreArchivo);
        List<String> registros=leerRegistros(nombreArchivo);
        int contador=leerContador(nombreArchivo);
        BufferedWriter writer=new BufferedWriter(new FileWriter(file, false)); 
        writer.write("idIncremental=" + contador);
        writer.newLine();
        for (String reg : registros) {
            int idReg=Integer.parseInt(reg.split(" - ")[0]);
            if (idReg==id)
                writer.write(nuevoDatos);
            else
                writer.write(reg);
            writer.newLine();
        }
        writer.close();
    }

    static String[] buscarPorId(String nombreArchivo, int id) throws Exception {
        for (String reg : leerRegistros(nombreArchivo)) {
            String[] p = reg.split(" - ");
            if (Integer.parseInt(p[0]) == id) return p;
        }
        return null;
    }

    static String[] buscarPorCampo(String nombreArchivo, int indice, String valor) throws Exception { 
        for (String reg : leerRegistros(nombreArchivo)) {
            String[] p = reg.split(" - ");
            if (p[indice].equalsIgnoreCase(valor)) return p;
        }
        return null;
    }
    
    static List<String[]> buscarMultiplesPorCampo(String nombreArchivo, int indice, String valor) throws Exception { 
        List<String[]> resultados = new ArrayList<>();
        
        for (String reg : leerRegistros(nombreArchivo)) {
            String[] p = reg.split(" - ");
            if (p.length > indice && p[indice].equalsIgnoreCase(valor)) {
                resultados.add(p); 
            }
        }
        return resultados; 
    }
    
    static List<String> buscarPorIndice(String nombreArchivo, int indice) throws Exception { 
        List<String> respuesta = new ArrayList<>();
        for (String reg : leerRegistros(nombreArchivo)) {
            respuesta.add(reg.split(" - ")[indice]);
        }
        return respuesta;
    }
    
    int iniciarSesion(String mail, String psw) throws Exception {
        mutexModCuenta.acquire();
        for (String reg : leerRegistros("CuentaPersonal.txt")) {
            String[] p = reg.split(" - ");
            
            if (p[2].equalsIgnoreCase(mail) && p[3].equals(psw)) {
                mutexModCuenta.release();
                
                // ThreadLocal set
                mailusr.set(mail);
                
                mutexModRegistro.acquire();
                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                
                // ThreadLocal get
                agregarRegistro("Logs.txt", "2 - " + obtenerIdCuenta(mailusr.get()) + " - " + date + " - " + obtenerNombre() + " inicio sesion");
                mutexModRegistro.release();
                return Integer.parseInt(p[4]);
            }
        }
        mutexModCuenta.release();
        return -1;
    }
    
    String obtenerNombre() throws Exception {
        String nombre;
        mutexModCuenta.acquire();
        
        // ThreadLocal get
        nombre = buscarPorCampo("CuentaPersonal.txt", 2 , mailusr.get())[1];
        
        mutexModCuenta.release();
        return nombre;
    }
    
    static void agregarUsuarioAsignado(int idSolicitud, int idCuenta) throws Exception {
        mutexModCuentaAsignada.acquire();
        BufferedWriter writer = new BufferedWriter(new FileWriter("CuentaAsignada.txt", true));
        writer.write(idSolicitud + " - " + idCuenta + " - " + "NULL"); 
        writer.newLine();
        writer.close();
        mutexModCuentaAsignada.release();
    }
    
    String crearCuenta(String nombreArchivo, String[] Partes) throws Exception {
        String respuesta="";
        mutexModCuenta.acquire();
        String[] valor=buscarPorCampo("CuentaPersonal.txt",2,Partes[1]);
            
        String linea="";
        Boolean Repetido = false;
                    
        if(valor!=null){
            respuesta="Ya existe una cuenta con ese email";
            Repetido=true; 
            mutexModCuenta.release();
        }
                    
        if (Repetido == false){
            // ThreadLocal set
            mailusr.set(Partes[1]);
            agregarRegistro("CuentaPersonal.txt",Partes[2] + " - " + Partes[1] + " - " + Partes[3] + " - 1");
            respuesta="Creado completado con exito";
            mutexModCuenta.release();
            
            mutexModRegistro.acquire();
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                
            // ThreadLocal get
            agregarRegistro("Logs.txt", "1 - " + obtenerIdCuenta(mailusr.get()) + " - " + date + " - " + obtenerNombre() + " se creo una cuenta");
            mutexModRegistro.release();
        }
        
        return respuesta; 
    }
    
    void crearPerfil(int id, String Nombre, String CodSec, String Descripcion, String Fecha) throws Exception {
        mutexModPerfilGenetico.acquire();
        
        if(buscarPorCampo("PerfilGenetico.txt", 6, Integer.toString(id))==null){
            agregarRegistro("PerfilGenetico.txt",Nombre + " - " + CodSec + " - " + Descripcion + " - 1 - " + Fecha + " - " +String.valueOf(id));
            mutexModPerfilGenetico.release();
            
            mutexModRegistro.acquire();
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                
            // ThreadLocal get
            agregarRegistro("Logs.txt", "6 - " + obtenerIdCuenta(mailusr.get()) + " - " + date + " - " + obtenerNombre() + " creo el perfilgenetico de:" + Nombre);
            mutexModRegistro.release();
            
        } else {
            mutexModPerfilGenetico.release();
        }
    }
    
    static int obtenerIdCuenta(String mail) throws Exception{
        int id=0;
        mutexModCuenta.acquire();
        String aux[]=buscarPorCampo("CuentaPersonal.txt", 2 , mail);
        mutexModCuenta.release();
        if(aux!=null){
            id=Integer.parseInt(aux[0]);
        }
        return id;
    }
    
     String crearPerfilSolicitud(String nombreArchivo, String mail, String datosSolicitud) throws Exception {
        int id=obtenerIdCuenta(mail);
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        mutexModSolicitud.acquire();
        
        agregarRegistro(nombreArchivo,"registrar - 0 - "+ datosSolicitud +" - NULL - "+ date);  
        agregarUsuarioAsignado(leerContador(nombreArchivo),id);
        
        mutexModSolicitud.release();
        
        mutexModRegistro.acquire();
        date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                
        // ThreadLocal get
        agregarRegistro("Logs.txt", "9 - " + obtenerIdCuenta(mailusr.get()) + " - " + date + " - " + obtenerNombre() + " creo una solicitud de perfil");
        mutexModRegistro.release();
        
        return "Solicitud enviada con exito";
    }
    
    static int obtenerIdPerfil(String mail) throws Exception{
        int id=0;
        
        List<String> aux2 = new ArrayList<>();
        mutexModPerfilGenetico.acquire();
        mutexModCuenta.acquire();
        
        String[] aux=buscarPorCampo("CuentaPersonal.txt", 2, mail);
        aux2=buscarPorIndice("PerfilGenetico.txt", 6);
        
        mutexModCuenta.release();
        
        if (aux != null && aux2!=null){
            for (String valor : aux2) {
                if(valor.equals(aux[0])){
                    id = Integer.valueOf(buscarPorCampo("PerfilGenetico.txt", 6, valor)[0]);
                }
            }
        }
        
        mutexModPerfilGenetico.release();
        
        return id;
    }
    
    String CrearSolicitudP(String nombreArchivo, String mail, String datosSolicitud, String tipo) throws Exception {
        int idUsuario = obtenerIdCuenta(mail);
        int idPerfil = obtenerIdPerfil(mail);
        
        if(idPerfil>0){
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            
            mutexModSolicitud.acquire();
            
            agregarRegistro(nombreArchivo,tipo + " - 0 - "+ datosSolicitud + " - " + idPerfil + " - " + date);
            agregarUsuarioAsignado(leerContador(nombreArchivo),idUsuario);
            
            mutexModSolicitud.release();
            
            mutexModRegistro.acquire();
            date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                
            // ThreadLocal get
            agregarRegistro("Logs.txt", "8 - " + obtenerIdCuenta(mailusr.get()) + " - " + date + " - " + obtenerNombre() + " creo una solicitud de "+tipo+" con los datos: "+datosSolicitud);
            mutexModRegistro.release();
        }
        
        return "Solicitud enviada con exito";
    }
    
    boolean DarBajaPerfil(String mail) throws Exception {
        int id;
        String[] aux;
        String novafila;
        boolean resultado=true;
        
        id=obtenerIdPerfil(mail);
        if (id!=0){
            mutexModPerfilGenetico.acquire();
            aux=buscarPorId("PerfilGenetico.txt", id);
            aux[4]="0";
            novafila=String.join(" - ",aux);
            editarRegistro("PerfilGenetico.txt",id,novafila);
            mutexModPerfilGenetico.release();
            
            mutexModRegistro.acquire();
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                
            // ThreadLocal get
            agregarRegistro("Logs.txt", "3 - " + obtenerIdCuenta(mailusr.get()) + " - " + date + " - " + obtenerNombre() + " dio de baja el perfil de:" + mail);
            mutexModRegistro.release();
        }
        else{
            resultado=false;
        }
        
        return resultado;
    }
    
    boolean RestaurarPerfil(String mail) throws Exception {
        int id;
        String[] aux;
        String novafila;
        boolean resultado=true;
        
        id=obtenerIdPerfil(mail);
        if (id!=0){
            mutexModPerfilGenetico.acquire();
            aux=buscarPorId("PerfilGenetico.txt", id);
            aux[4]="1";
            novafila=String.join(" - ",aux);
            editarRegistro("PerfilGenetico.txt",id,novafila);
            mutexModPerfilGenetico.release();
            
            mutexModRegistro.acquire();
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                
            // ThreadLocal get
            agregarRegistro("Logs.txt", "4 - " + obtenerIdCuenta(mailusr.get()) + " - " + date + " - " + obtenerNombre() + " restauro el perfil de:" + mail);
            mutexModRegistro.release();
        }
        else{
            resultado=false;
        }
        
        return resultado;
    }
    
    static List<String> BuscarPerfiles(Integer id,String nombre) throws Exception{
        String mail;
        List<String> Respuesta = new ArrayList<>();
        mutexModPerfilGenetico.acquire();
        mutexModCuenta.acquire();
        System.out.println("SOY ID:"+id);
        if(nombre!=null && nombre.equals("NULL") && id==null){
            for(String aux : leerRegistros("PerfilGenetico.txt")){
                String[]perfil=aux.split(" - ");
                id=Integer.parseInt(perfil[6]);
                String[] aux2= buscarPorId("CuentaPersonal.txt", id);
                if(aux2!=null){
                    mail=aux2[2];
                    Respuesta.add(perfil[0]+" - "+perfil[1]+" - "+perfil[2]+" - "+perfil[3]+" - "+perfil[4]+" - "+perfil[5]+" - "+mail);                
                }
            }
        }
        else if(id==null){
            for(String[] perfil : buscarMultiplesPorCampo("PerfilGenetico.txt", 1 , nombre)){
                id=Integer.parseInt(perfil[6]);
                String[] aux= buscarPorId("CuentaPersonal.txt", id);
                if(aux!=null){
                    mail=aux[2];
                    Respuesta.add(perfil[0]+" - "+perfil[1]+" - "+perfil[2]+" - "+perfil[3]+" - "+perfil[4]+" - "+perfil[5]+" - "+mail);                
                }
            }
        }
        else if(id!=0){
            String[] perfil=buscarPorId("PerfilGenetico.txt", id);
            String[] aux= buscarPorId("CuentaPersonal.txt", id);
            if(aux!=null){
                mail=aux[2];
                Respuesta.add(perfil[0]+" - "+perfil[1]+" - "+perfil[2]+" - "+perfil[3]+" - "+perfil[4]+" - "+perfil[5]+" - "+mail);                
            }
        }
        
        mutexModCuenta.release();
        mutexModPerfilGenetico.release();
        
        return Respuesta;
    }
    
    String ModificarPerfil(String mail, String Nombre, String CodSec, String Descripcion, String Fecha) throws Exception {
        String[] perfil= buscarPorId("PerfilGenetico.txt", obtenerIdPerfil(mail));
        
        mutexModPerfilGenetico.acquire();
        
        editarRegistro("PerfilGenetico.txt", Integer.parseInt(perfil[0]), perfil[0]+" - " + Nombre+" - " +CodSec+" - " +Descripcion+" - "+ perfil[4] +" - " +Fecha+" - " +perfil[6]);
        mutexModPerfilGenetico.release();
        
        mutexModRegistro.acquire();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                
        // ThreadLocal get
        agregarRegistro("Logs.txt", "5 - " + obtenerIdCuenta(mailusr.get()) + " - " + date + " - " + obtenerNombre() + " modifico perfil de:" + mail + "con los siguientes datos:" + perfil[0]+" _ " + Nombre+" _ " +CodSec+" _ " +Descripcion+" _ "+ perfil[4] +" _ " +Fecha+" _ " +perfil[6]);
        mutexModRegistro.release();
        
        return "1";
    }
    
    static String obtenerEmailPorPerfil(int id) throws Exception {
        String mail="0";
        List<String> aux2 = new ArrayList<>();
        
        mutexModPerfilGenetico.acquire();
        mutexModCuenta.acquire();
        
        String[] aux=buscarPorCampo("PerfilGenetico.txt", 0, String.valueOf(id));
        aux2=buscarPorIndice("CuentaPersonal.txt", 0);
        
        mutexModPerfilGenetico.release();
        
        if (aux != null && aux2!=null){
            for (String valor : aux2) {
                if(valor.equals(aux[6])){
                    mail = buscarPorCampo("CuentaPersonal.txt", 0, valor)[2];
                }
            }
        }
        
        mutexModCuenta.release();
        
        return mail;
    }
    
    static List<String> verSolicitudes() throws Exception {
        List<String> lista=new ArrayList<>();
        
        mutexModSolicitud.acquire();
        
        for(String valor : leerRegistros("Solicitud.txt")){
            if(valor.split(" - ")[2].equals("0")){
                lista.add(valor);
            }
        }
        mutexModSolicitud.release();
        return lista;
    }
    
    static List<String> ObtenerUltimasSolicitudes() throws Exception {
        List<String> lista=new ArrayList<>();
        
        mutexModSolicitud.acquire();
        
        for(String valor : leerRegistros("Solicitud.txt")){
            if(!valor.split(" - ")[2].equals("0")){
                lista.add(valor);
            }
        }
        mutexModSolicitud.release();
        int aux = Math.max(lista.size() - 10, 0); 
        lista = lista.subList(aux, lista.size());
        return lista;
    }
    
    static boolean tienePerfil(int id) throws Exception {
        boolean existe=false;
        mutexModPerfilGenetico.acquire();
        if(buscarPorCampo("PerfilGenetico.txt", 6, String.valueOf(id))!=null){
            existe=true; // Added missing logic here
        }
        mutexModPerfilGenetico.release(); // Added missing release
        return existe;
    }
    
    static void editarRegistroCuentaAsignada(String nombreArchivo, int id, String nuevoDatos) throws Exception {
        File file=new File(nombreArchivo);
        List<String> registros=leerRegistros(nombreArchivo);
        BufferedWriter writer=new BufferedWriter(new FileWriter(file, false)); 
        for (String reg : registros) {
            int idReg=Integer.parseInt(reg.split(" - ")[0]);
            if (idReg==id)
                writer.write(nuevoDatos);
            else
                writer.write(reg);
            writer.newLine();
        }
        writer.close();
    }
    
    int ResolverSolicitud(int idSol, int estadoNuevo, String Mail) throws Exception {
        int Respuesta=-1;
        
        mutexModSolicitud.acquire();
        String[] aux = buscarPorCampo("Solicitud.txt", 0, String.valueOf(idSol));
        
        
        
        if (aux!=null && aux[2].equals("0")){
            if(estadoNuevo==2){
                String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                agregarUsuarioAsignado(idSol, obtenerIdCuenta(Mail));
                 mutexModCuentaAsignada.acquire();
                String[] ponerfecha = buscarPorCampo("CuentaAsignada.txt", 0,  String.valueOf(idSol));
                editarRegistroCuentaAsignada("CuentaAsignada.txt", idSol,  ponerfecha[0] + " - " + ponerfecha[1] + " - " + fecha);
                
                editarRegistro("Solicitud.txt", idSol,  aux[0]+" - "+aux[1]+" - 2 - "+aux[3]+" - "+aux[4]+" - "+aux[5]);
                Respuesta=0;
                mutexModCuentaAsignada.release();
            }
            else if(estadoNuevo==1){
        
                if(aux[1].equals("restaurar")){
                    RestaurarPerfil(aux[4]);
                }
                else if(aux[1].equals("baja")){
                    DarBajaPerfil(aux[4]);
                }
                else if(aux[1].equals("modificar")){
                    String[]aus=aux[3].split(" _ ");
                    ModificarPerfil(aus[0], aus[1], aus[2], aus[3], aus[4]);
                }
                else if(aux[1].equals("registrar")){
                    String[]aus=aux[3].split(" _ ");
                    crearPerfil(obtenerIdCuenta(aus[0]), aus[1], aus[2], aus[3], aus[4]);
                }
                String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                agregarUsuarioAsignado(idSol, obtenerIdCuenta(Mail));
                 mutexModCuentaAsignada.acquire();
                String[] ponerfecha = buscarPorCampo("CuentaAsignada.txt", 0,  String.valueOf(idSol));
                editarRegistroCuentaAsignada("CuentaAsignada.txt", idSol,  ponerfecha[0] + " - " + ponerfecha[1] + " - " + fecha);
                
                editarRegistro("Solicitud.txt", idSol,  aux[0]+" - "+aux[1]+" - 1 - "+aux[3]+" - "+aux[4]+" - "+aux[5]);
                Respuesta=1;
                mutexModCuentaAsignada.release();
            }
        }
        mutexModSolicitud.release();
        
        mutexModRegistro.acquire();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                
        agregarRegistro("Logs.txt", "7 - " + obtenerIdCuenta(mailusr.get()) + " - " + date + " - " + obtenerNombre() + " modifico la solicitud: " + idSol + " y ahora esta" + estadoNuevo);
        mutexModRegistro.release();
        
        return Respuesta;
    }
    
    static List<String> ObtenerLogsAdmin() throws Exception {
        List<String> logsFormateados = new ArrayList<>();
        
        mutexModRegistro.acquire();
        List<String> todosLosLogs = leerRegistros("Logs.txt");
        mutexModRegistro.release();
        for (String log : todosLosLogs) {
            
            String[] aux = log.split(" - ");
                
            String mail = aux[0];
            String idTipoAccion = aux[1].trim();
            String idCuenta = aux[2].trim();
            String fecha = aux[3];
                
            StringBuilder detalle = new StringBuilder();
            for (int i = 4; i < aux.length; i++) {
                if (i > 4) detalle.append(" - ");
                detalle.append(aux[i]);
            }
            mutexModCuenta.acquire();
            String[] cuenta = buscarPorId("CuentaPersonal.txt", Integer.parseInt(idCuenta));
            mutexModCuenta.release();
            if (cuenta == null) continue;
             
            String nombreCuenta = cuenta[1];
            String email = cuenta[2];
            int idRol = Integer.parseInt(cuenta[4]);
            String admin = (idRol == 2) ? "1" : "0";

            String nombreAccion="";
            mutexModTipoAccion.acquire();
            String[] accion = buscarPorId("TipoAccion.txt", Integer.parseInt(idTipoAccion));
            mutexModTipoAccion.release();
            if (accion != null) {
                nombreAccion = accion[1];
            }

            String filaLog = aux[0] + " - " + idCuenta + " - " + nombreCuenta + " - " + email + " - " + detalle.toString() + " - " + fecha + " - " + nombreAccion + " - " + admin;
            logsFormateados.add(filaLog);
        }
        
        return logsFormateados;
    }
    
    @Override
    public void run() {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String line;
            while ((line = reader.readLine()) != null) {

                System.out.println("Recibido: " + line);
                String[] Partes = line.split(" - ");
                System.out.println("Partes: " + java.util.Arrays.toString(Partes));

                if (Partes.length > 3 && Partes[0].equals("CrearC")){
                    try{
                        writer.println(crearCuenta("CuentaPersonal.txt", Partes));
                    }catch (Exception ex) {
                        Logger.getLogger(BancoADN6RequestHandler.class.getName()).log(Level.SEVERE, null, ex);
                        writer.println("Error al crear cuenta");
                    }
                }
                else if (Partes.length > 2 && Partes[0].equals("IniciarS")){
                    try{
                        writer.println(iniciarSesion(Partes[1],Partes[2]));
                    }catch (Exception ex) {
                        Logger.getLogger(BancoADN6RequestHandler.class.getName()).log(Level.SEVERE, null, ex);
                        writer.println("Error al iniciar sesion");
                    }
                }
                else if (Partes.length > 2 && Partes[0].equals("CrearPerfilSol")){
                    try{
                        writer.println(crearPerfilSolicitud("Solicitud.txt", Partes[1], Partes[2]));
                    }catch (Exception ex) {
                        Logger.getLogger(BancoADN6RequestHandler.class.getName()).log(Level.SEVERE, null, ex);
                        writer.println("Error solicitar la creacion de perfil");
                    }
                }
                else if (Partes.length > 3 && Partes[0].equals("CrearSolPer")){
                    try{
                        writer.println(CrearSolicitudP("Solicitud.txt",Partes[1],Partes[2],Partes[3]));
                    }catch (Exception ex) {
                        Logger.getLogger(BancoADN6RequestHandler.class.getName()).log(Level.SEVERE, null, ex);
                        writer.println("Error al crear la solicitud");
                    }
                }
                else if (Partes.length > 1 && Partes[0].equals("DarDBaja")){
                    try{
                        writer.println(DarBajaPerfil(Partes[1]));
                    }catch (Exception ex) {
                        Logger.getLogger(BancoADN6RequestHandler.class.getName()).log(Level.SEVERE, null, ex);
                        writer.println("Error al dar de baja el perfil");
                    }
                }
                else if (Partes.length > 1 && Partes[0].equals("DarDRestaur")){
                    try{
                        writer.println(RestaurarPerfil(Partes[1]));
                    }catch (Exception ex) {
                        Logger.getLogger(BancoADN6RequestHandler.class.getName()).log(Level.SEVERE, null, ex);
                        writer.println("Error al restaurar perfil");
                    }
                }
                else if (Partes.length > 2 && Partes[0].equals("BuscarIDNOM")){
                    try{
                        Integer aux=null;
                        List<String> Respuesta = new ArrayList<>();
                        if(Partes[1].equals("NULL")){
                            aux=null;
                        }else{
                            aux=Integer.valueOf(Partes[1]);
                        }
                        
                        Respuesta=BuscarPerfiles(aux,Partes[2]);
                        if (Respuesta.isEmpty()){
                            writer.println("No se encontro el perfil");
                        }
                        for (String reg : Respuesta) {
                            writer.println(reg);
                        }
                        writer.println("FINISH");

                    }catch (Exception ex) {
                        Logger.getLogger(BancoADN6RequestHandler.class.getName()).log(Level.SEVERE, null, ex);
                        writer.println("Error al buscar perfil");
                    }
                }
                else if (Partes.length > 5 && Partes[0].equals("ModificP")){
                    try{
                        writer.println(ModificarPerfil(Partes[1],Partes[2],Partes[3],Partes[4],Partes[5]));
                    }catch (Exception ex) {
                        Logger.getLogger(BancoADN6RequestHandler.class.getName()).log(Level.SEVERE, null, ex);
                        writer.println("Error al modificar el perfil");
                    }
                }
                else if (Partes.length > 0 && Partes[0].equals("ListaSol")){
                    try{
                        List<String> Respuesta = new ArrayList<>();
                        Respuesta=verSolicitudes();
                        
                        for (String reg : Respuesta) {
                            writer.println(reg);
                        }
                        writer.println("FINISH");

                    }catch (Exception ex) {
                        Logger.getLogger(BancoADN6RequestHandler.class.getName()).log(Level.SEVERE, null, ex);
                        writer.println("Error al buscar las solicitudes");
                    }
                }
                else if (Partes.length > 0 && Partes[0].equals("UltSol")){
                    try{
                        List<String> Respuesta = new ArrayList<>();
                        Respuesta=ObtenerUltimasSolicitudes();
                        
                        for (String reg : Respuesta) {
                            writer.println(reg);
                        }
                        writer.println("FINISH");

                    }catch (Exception ex) {
                        Logger.getLogger(BancoADN6RequestHandler.class.getName()).log(Level.SEVERE, null, ex);
                        writer.println("Error al buscar las ultimas solicitudes");
                    }
                }
                else if(Partes.length > 1 && Partes[0].equals("BuscarDat")){
                    try{
                        List<String> Respuesta = new ArrayList<>();
                        
                        Respuesta=BuscarPerfiles(Integer.valueOf(obtenerIdPerfil(Partes[1])), null);
                        if (Respuesta.isEmpty()){
                            writer.println("No se encontro el perfil");
                        }
                        for (String reg : Respuesta) {
                            writer.println(reg);
                        }

                    }catch (Exception ex) {
                        Logger.getLogger(BancoADN6RequestHandler.class.getName()).log(Level.SEVERE, null, ex);
                        writer.println("Error al buscar el perfil");
                    }
                }
                else if (Partes.length > 1 && Partes[0].equals("EmailPorPerfil")) {
                    try {
                        String resultado = obtenerEmailPorPerfil(Integer.parseInt(Partes[1]));
                        
                        if (!resultado.isEmpty()) {
                            writer.println(resultado);
                        } else {
                            writer.println("—");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        writer.println("—");
                    }
                }
                else if (Partes.length > 3 && Partes[0].equals("ResSol")) {
                    try {
                        int resultado=ResolverSolicitud(Integer.valueOf(Partes[1]),Integer.valueOf(Partes[2]),Partes[3]);
                        writer.println(resultado);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        writer.println("—");
                    }
                }
                else if (Partes.length > 0 && Partes[0].equals("GetLogs")) {
                    try {
                        List<String> Respuesta = new ArrayList<>();
                        Respuesta=ObtenerLogsAdmin();
                        
                        for (String reg : Respuesta) {
                            writer.println(reg);
                        }
                        writer.println("FINISH");
                        
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        writer.println("—");
                    }
                }
                else {
                    writer.println("Comando Invalido");
                }
            } // fin while
        } catch (IOException e) {
            Logger.getLogger(BancoADN6RequestHandler.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            // CRITICAL: Clean up the ThreadLocal memory when the socket closes
            mailusr.remove();
        }
    }
}