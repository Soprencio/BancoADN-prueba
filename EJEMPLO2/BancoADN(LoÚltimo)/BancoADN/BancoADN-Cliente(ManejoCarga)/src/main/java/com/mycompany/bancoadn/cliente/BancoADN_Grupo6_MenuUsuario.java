/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bancoadn.cliente;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.Cursor;
 
public class BancoADN_Grupo6_MenuUsuario extends JFrame {
    private final Color azulClaro       = new Color(169, 195, 207);
    private final Color azulBoton       = new Color(74, 144, 226);
    private final Color naranjaTexto    = new Color(211, 84, 0);
    private final Color grisFondo       = new Color(235, 235, 235);
    private final Color grisPanelInterno= new Color(200, 200, 200);
    private final Color grisOscuro      = new Color(100, 100, 100);
 
    // ── Botones accesibles desde el controlador 
    public JButton btnBuscar;
    public JButton btnSolicitarPerfil;
    public JButton btnSolicitarModificacion;
    public JButton btnSolicitarDesactivar;
    public JButton btnSolicitarReactivar;
    public JButton btnCerrarSesion;

    //datos extraidos al iniciar sesión
    public JLabel lblHola;
    private String emailUsuario;
 
    // ── Panel derecho (ficha de perfil) ────────────────────
    public JButton btnVerPerfil;
    private JLabel lblCodigoSecuencia;
    private JLabel lblDescripcion;
    private JLabel lblFecha;
    private JLabel lblNombrePerfil;
    private JLabel lblEstado;
 
    public BancoADN_Grupo6_MenuUsuario() {
        setTitle("Banco ADN - Menú de Usuario");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null);
 
        JPanel mainWrapper = new JPanel(new BorderLayout());
        mainWrapper.setBackground(new Color(128, 128, 128));
        mainWrapper.setBorder(new LineBorder(grisOscuro, 10, true));
 
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(azulClaro);
        mainWrapper.add(contentPane);
 
        contentPane.add(crearEncabezado(), BorderLayout.NORTH);
 
        JPanel body = new JPanel(new GridLayout(1, 2, 20, 0));
        body.setBackground(grisFondo);
        body.setBorder(new EmptyBorder(20, 20, 20, 20));
        body.add(crearPanelIzquierdo());
        body.add(crearPanelDerecho());
        contentPane.add(body, BorderLayout.CENTER);
 
        add(mainWrapper);
    }
 
    // ── Encabezado ─────────────────────────────────────────
    private JPanel crearEncabezado() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15, 30, 15, 30));
 
        lblHola = new JLabel("Hola, Usuario");
        lblHola.setForeground(naranjaTexto);
        lblHola.setFont(new Font("SansSerif", Font.BOLD, 26));
 
        JLabel lblLogo = new JLabel("<html><font color='#4A90E2'>Simple</font><font color='#D35400'>ADN</font></html>");
        lblLogo.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 28));
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
 
        btnCerrarSesion = new JButton("Cerrar Sesión ⎋");
        btnCerrarSesion.setContentAreaFilled(false);
        btnCerrarSesion.setBorderPainted(false);
        btnCerrarSesion.setForeground(Color.GRAY);
        btnCerrarSesion.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnCerrarSesion.setCursor(new Cursor(Cursor.HAND_CURSOR));
 
        header.add(lblHola,        BorderLayout.WEST);
        header.add(lblLogo,        BorderLayout.CENTER);
        header.add(btnCerrarSesion,BorderLayout.EAST);
 
        return header;
    }
 
    // ── Panel izquierdo ────────────────────────────────────
    private JPanel crearPanelIzquierdo() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
 
        JLabel lblBuscarTitulo = new JLabel("Buscar otros perfiles Genéticos", SwingConstants.CENTER);
        lblBuscarTitulo.setFont(new Font("SansSerif", Font.BOLD, 13));
 
        btnBuscar = crearBotonAzul("Buscar Perfiles");
 
        JPanel acciones = new JPanel(new GridLayout(4, 1, 0, 15));
        acciones.setBackground(grisPanelInterno);
        acciones.setBorder(new EmptyBorder(20, 20, 20, 20));
 
        btnSolicitarPerfil       = crearBotonAzul("Solicitar Perfil");
        btnSolicitarModificacion = crearBotonAzul("Solicitar Modificación de Perfil");
        btnSolicitarDesactivar   = crearBotonAzul("Solicitar Desactivar Perfil");
        btnSolicitarReactivar    = crearBotonAzul("Solicitar Reactivar Perfil");
 
        acciones.add(btnSolicitarPerfil);
        acciones.add(btnSolicitarModificacion);
        acciones.add(btnSolicitarDesactivar);
        acciones.add(btnSolicitarReactivar);
 
        gbc.gridy = 0; panel.add(lblBuscarTitulo,gbc);
        gbc.gridy = 1; panel.add(btnBuscar,gbc);
        gbc.gridy = 2; gbc.weighty = 1.0; panel.add(acciones,gbc);
        return panel;
    }
 
    // ── Panel derecho (ficha de perfil) ────────────────────
      // ── Panel derecho (ficha de perfil) CORREGIDO ────────────────────
    private JPanel crearPanelDerecho() {
        JPanel panelFicha = new JPanel(new BorderLayout(0, 10));
        panelFicha.setBackground(new Color(217, 217, 217));
        panelFicha.setBorder(new EmptyBorder(25, 25, 25, 25));

        // --- ENCABEZADO (Título + Botón) ---
        // Usamos un panel con BoxLayout para apilar el título y el botón verticalmente
        JPanel pnlSuperior = new JPanel();
        pnlSuperior.setLayout(new BoxLayout(pnlSuperior, BoxLayout.Y_AXIS));
        pnlSuperior.setOpaque(false);

        JLabel lblTitulo = new JLabel("Perfil Genético", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrado horizontal
        
        btnVerPerfil = crearBotonAzul("Consultar mis datos");
        btnVerPerfil.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrado horizontal

        pnlSuperior.add(lblTitulo);
        pnlSuperior.add(Box.createVerticalStrut(10)); // Espacio entre título y botón
        pnlSuperior.add(btnVerPerfil);
        
        // Agregamos el bloque superior al NORTE del panel principal
        panelFicha.add(pnlSuperior, BorderLayout.NORTH);
        
        // --- CUERPO (Datos del perfil) ---
        JPanel datos = new JPanel(new GridBagLayout());
        datos.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx  = 0;

        lblNombrePerfil    = new JLabel("Nombre: —");
        lblCodigoSecuencia = new JLabel("Código de Secuencia: —");
        lblDescripcion     = new JLabel("Descripción: —");
        lblEstado          = new JLabel("Estado: —");

        Font fuenteDatos = new Font("SansSerif", Font.PLAIN, 13);
        lblNombrePerfil.setFont(fuenteDatos);
        lblCodigoSecuencia.setFont(fuenteDatos);
        lblDescripcion.setFont(fuenteDatos);
        lblEstado.setFont(fuenteDatos);

        gbc.gridy = 0; datos.add(lblNombrePerfil,    gbc);
        gbc.gridy = 1; datos.add(lblCodigoSecuencia, gbc);
        gbc.gridy = 2; datos.add(lblDescripcion,     gbc);
        gbc.gridy = 3; datos.add(lblEstado,          gbc);
        
        panelFicha.add(datos, BorderLayout.CENTER);

        // --- PIE (Fecha) ---
        lblFecha = new JLabel("—", SwingConstants.RIGHT);
        lblFecha.setFont(new Font("SansSerif", Font.ITALIC, 11));
        panelFicha.add(lblFecha, BorderLayout.SOUTH);

        return panelFicha;
    }
 
    // ── Método para mostrar datos en el panel derecho ──────
    /**
     * Llamado desde el controlador cuando se recibe un perfil del servidor.
     * Formato esperado del servidor (BuscarIDNOM):
     *   "idPerfil - nombreCompleto - codigoSecuencia - descripcion - estado - fecha - email"
     */
    public void mostrarPerfil(String nombreCompleto, String codigo,
                              String descripcion, String estado, String fecha) {
        lblNombrePerfil.setText("Nombre: "              + nombreCompleto);
        lblCodigoSecuencia.setText("Código de Secuencia: " + codigo);
        lblDescripcion.setText("Descripción: "          + descripcion);
        lblEstado.setText("Estado: "                    + estado);
        lblFecha.setText("Fecha de muestra: "           + fecha);
        revalidate();
        repaint();
    }
    
    public void limpiarPerfil() {
        lblNombrePerfil.setText("Nombre: —");
        lblCodigoSecuencia.setText("Código de Secuencia: —");
        lblDescripcion.setText("Descripción: —");
        lblEstado.setText("Estado: —");
        lblFecha.setText("—");
    }
    
    /**
     * Deshabilita o habilita todos los botones de la pantalla.
     * Cambia el cursor a reloj de espera mientras carga.
     * Se llama antes y después de cada comunicación con el servidor.
     */
    public void mostrarCargando(boolean cargando) {
        btnBuscar.setEnabled(!cargando);
        btnVerPerfil.setEnabled(!cargando);
        btnSolicitarPerfil.setEnabled(!cargando);
        btnSolicitarModificacion.setEnabled(!cargando);
        btnSolicitarDesactivar.setEnabled(!cargando);
        btnSolicitarReactivar.setEnabled(!cargando);
        btnCerrarSesion.setEnabled(!cargando);
        setCursor(cargando ?
            Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) :
            Cursor.getDefaultCursor()
        );
    }
 
    // ── Utilidades ──
    public void setNombreUsuario(String nombre) { lblHola.setText("Hola, " + nombre); }
    public void setEmailUsuario(String email)   { this.emailUsuario = email; }
    public String getEmailUsuario()             { return emailUsuario; }
 
    public void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
 
    public void mostrarMensaje(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
 
    // ── Listeners ──────────────────────────────────────────
    public void agregarListenerBuscar(ActionListener l)              { btnBuscar.addActionListener(l); }
    public void agregarListenerSolicitarPerfil(ActionListener l)     { btnSolicitarPerfil.addActionListener(l); }
    public void agregarListenerSolicitarModificacion(ActionListener l){ btnSolicitarModificacion.addActionListener(l); }
    public void agregarListenerSolicitarDesactivar(ActionListener l) { btnSolicitarDesactivar.addActionListener(l); }
    public void agregarListenerSolicitarReactivar(ActionListener l)  { btnSolicitarReactivar.addActionListener(l); }
    public void agregarListenerCerrarSesion(ActionListener l)        { btnCerrarSesion.addActionListener(l); }
    public void agregarListenerVerPerfil(ActionListener l)           { btnVerPerfil.addActionListener(l); }
 
    private JButton crearBotonAzul(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(azulBoton);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBorder(new EmptyBorder(10, 15, 10, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BancoADN_Grupo6_MenuUsuario().setVisible(true));
    }
}
