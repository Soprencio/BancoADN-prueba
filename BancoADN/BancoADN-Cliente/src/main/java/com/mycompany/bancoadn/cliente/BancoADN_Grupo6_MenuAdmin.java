package com.mycompany.bancoadn.cliente;

import com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces.IVistaMenuAdmin;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;


public class BancoADN_Grupo6_MenuAdmin extends JFrame implements IVistaMenuAdmin {

    private final Color azulClaro    = new Color(169, 195, 207);
    private final Color azulBoton    = new Color(74, 144, 226);
    private final Color verdeAprobar = new Color(80, 180, 80);
    private final Color rojoRechazar = new Color(200, 60, 60);
    private final Color naranjaTexto = new Color(211, 84, 0);
    private final Color grisFondo    = new Color(235, 235, 235);
    private final Color blanco       = Color.WHITE;
    private final Color grisOscuro   = new Color(100, 100, 100);
    private final Color grisBorde    = new Color(200, 200, 200);
    private final Color moradoBoton  = new Color(130, 80, 200);  // "Últimas Solicitudes"
    private final Color verdeBoton   = new Color(40, 150, 90);   // "Administrar Perfiles"

    public JLabel  lblHola;
    public JButton btnCerrarSesion;

    public JButton btnUltimasSolicitudes;
    public JButton btnLogs;

    public JButton btnAdministrarPerfiles;

    private JPanel panelSolicitudes;
    private String emailAdmin;

    public BancoADN_Grupo6_MenuAdmin() {
        setTitle("Banco ADN - Panel de Administración");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);

        JPanel mainWrapper = new JPanel(new BorderLayout());
        mainWrapper.setBackground(grisOscuro);
        mainWrapper.setBorder(new LineBorder(grisOscuro, 10, true));

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(azulClaro);
        mainWrapper.add(contentPane);

        contentPane.add(crearEncabezado(), BorderLayout.NORTH);
        contentPane.add(crearCuerpo(),     BorderLayout.CENTER);

        add(mainWrapper);
    }

    // ── Encabezado ──────────────────────────
    private JPanel crearEncabezado() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15, 30, 15, 30));

        lblHola = new JLabel("Hola, Admin");
        lblHola.setForeground(naranjaTexto);
        lblHola.setFont(new Font("SansSerif", Font.BOLD, 26));

        JLabel lblLogo = new JLabel(
            "<html><font color='#4A90E2'>Simple</font><font color='#D35400'>ADN</font></html>");
        lblLogo.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 28));
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);

        btnCerrarSesion = new JButton("Cerrar Sesión ⎋");
        btnCerrarSesion.setContentAreaFilled(false);
        btnCerrarSesion.setBorderPainted(false);
        btnCerrarSesion.setForeground(Color.GRAY);
        btnCerrarSesion.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnCerrarSesion.setCursor(new Cursor(Cursor.HAND_CURSOR));

        header.add(lblHola,         BorderLayout.WEST);
        header.add(lblLogo,         BorderLayout.CENTER);
        header.add(btnCerrarSesion, BorderLayout.EAST);
        return header;
    }

    // ── Cuerpo con fila de botones ──────────────────
    private JPanel crearCuerpo() {
        JPanel cuerpo = new JPanel(new BorderLayout(0, 10));
        cuerpo.setBackground(grisFondo);
        cuerpo.setBorder(new EmptyBorder(20, 40, 20, 40));

        // ── Fila de título + botones de acción ───────────
        JPanel filaTitulo = new JPanel(new BorderLayout());
        filaTitulo.setOpaque(false);
        filaTitulo.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Izquierda: título + botón "Administrar Perfiles"
        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        izquierda.setOpaque(false);

        JLabel lblTitulo = new JLabel("Solicitudes Pendientes");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));

        btnAdministrarPerfiles = crearBotonColoreado("Administrar Perfiles", verdeBoton);
        btnAdministrarPerfiles.setPreferredSize(new Dimension(200, 36));

        izquierda.add(lblTitulo);
        izquierda.add(Box.createHorizontalStrut(20));
        izquierda.add(btnAdministrarPerfiles);

        // Derecha: "Últimas Solicitudes" + "Actualizar"
        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        derecha.setOpaque(false);

        btnUltimasSolicitudes = crearBotonColoreado("Últimas Solicitudes", moradoBoton);
        btnUltimasSolicitudes.setPreferredSize(new Dimension(190, 36));

        btnLogs = crearBotonColoreado("Logs", naranjaTexto);
        btnLogs.setPreferredSize(new Dimension(100, 36));

        derecha.add(btnUltimasSolicitudes);
        derecha.add(btnLogs);

        filaTitulo.add(izquierda, BorderLayout.WEST);
        filaTitulo.add(derecha,   BorderLayout.EAST);

        cuerpo.add(filaTitulo, BorderLayout.NORTH);

        // ── Panel de solicitudes con scroll ──────────────
        panelSolicitudes = new JPanel();
        panelSolicitudes.setLayout(new BoxLayout(panelSolicitudes, BoxLayout.Y_AXIS));
        panelSolicitudes.setBackground(grisFondo);

        JScrollPane scroll = new JScrollPane(panelSolicitudes);
        scroll.setBorder(new LineBorder(grisBorde, 1));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        cuerpo.add(scroll, BorderLayout.CENTER);

        return cuerpo;
    }

    public void agregarTarjetaSolicitud(int idSolicitud,
                                        String mail,
                                        String fechaEnvio,
                                        String tipo,
                                        String lineaExtra,
                                        ActionListener onAprobar,
                                        ActionListener onRechazar) {

        JPanel tarjeta = new JPanel(new BorderLayout(10, 0));
        tarjeta.setBackground(blanco);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(grisBorde, 1, true),
            new EmptyBorder(12, 18, 12, 18)
        ));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);

        boolean conDesc = tipo.equals("registrar") || tipo.equals("modificar");
        JPanel info = new JPanel(new GridLayout(conDesc ? 3 : 2, 1, 0, 3));
        info.setOpaque(false);

        // Línea 1: mail en negrita y más grande (igual para todos los tipos)
        JLabel lblLinea1 = new JLabel("<html><b>" + esc(mail) + "</b></html>");
        lblLinea1.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Línea 2: fecha + asunto
        JLabel lblLinea2 = new JLabel(
            "<html>"
            + "<font color='#888888'>Fecha de envío:&nbsp;" + esc(fechaEnvio) + "</font>"
            + "&nbsp;&nbsp;|&nbsp;&nbsp;"
            + "Asunto:&nbsp;<b>" + esc(tipo) + "</b>"
            + "</html>");
        lblLinea2.setFont(new Font("SansSerif", Font.PLAIN, 12));

        info.add(lblLinea1);
        info.add(lblLinea2);

        // Línea 3: solo para registrar/modificar (descripción)
        if (tipo.equals("registrar") || tipo.equals("modificar")) {
            JLabel lblLinea3 = new JLabel(
                "<html><font color='#444444'>Descripción:&nbsp;<i>" + esc(lineaExtra) + "</i></font></html>");
            lblLinea3.setFont(new Font("SansSerif", Font.PLAIN, 12));
            info.add(lblLinea3);
        }

        tarjeta.add(info, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);

        JButton btnAprobar  = crearBotonIcono("✓", verdeAprobar);
        JButton btnRechazar = crearBotonIcono("✗", rojoRechazar);
        btnAprobar.setToolTipText("Aprobar solicitud #" + idSolicitud);
        btnRechazar.setToolTipText("Rechazar solicitud #" + idSolicitud);

        if (onAprobar  != null) btnAprobar.addActionListener(onAprobar);
        if (onRechazar != null) btnRechazar.addActionListener(onRechazar);

        botones.add(btnAprobar);
        botones.add(btnRechazar);
        tarjeta.add(botones, BorderLayout.EAST);

        panelSolicitudes.add(tarjeta);
        panelSolicitudes.add(Box.createVerticalStrut(8));
        panelSolicitudes.revalidate();
        panelSolicitudes.repaint();
    }

    public void limpiarSolicitudes() {
        panelSolicitudes.removeAll();
        panelSolicitudes.revalidate();
        panelSolicitudes.repaint();
    }

    public void mostrarSinSolicitudes() {
        JLabel lbl = new JLabel("No hay solicitudes pendientes.", SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.ITALIC, 15));
        lbl.setForeground(Color.GRAY);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(40, 0, 0, 0));
        panelSolicitudes.add(lbl);
        panelSolicitudes.revalidate();
        panelSolicitudes.repaint();
    }

    public void setNombreAdmin(String nombre) { lblHola.setText("Hola, " + nombre); }
    public void setEmailAdmin(String email)   { this.emailAdmin = email; }
    public String getEmailAdmin()             { return emailAdmin; }

    public void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
    public void mostrarMensaje(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
    public boolean confirmar(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Confirmar",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    // ── Listeners ─────────────────────────────────────────
    public void agregarListenerCerrarSesion(ActionListener l)         { btnCerrarSesion.addActionListener(l); }
    public void agregarListenerUltimasSolicitudes(ActionListener l)   { btnUltimasSolicitudes.addActionListener(l); }
    public void agregarListenerLogs(ActionListener l)                 { btnLogs.addActionListener(l); }
    public void agregarListenerAdministrarPerfiles(ActionListener l)  { btnAdministrarPerfiles.addActionListener(l); }

    // ── Fábrica de botones ────────────────────────────────
    private JButton crearBotonAzul(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(azulBoton);
        btn.setForeground(blanco);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton crearBotonColoreado(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(blanco);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton crearBotonIcono(String icono, Color color) {
        JButton btn = new JButton(icono);
        btn.setBackground(color);
        btn.setForeground(blanco);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setPreferredSize(new Dimension(64, 44));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private String esc(String s) {
        if (s == null) return "—";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }


    // === IVistaMenuAdmin interface methods ===

    /**
     * Bridge adapter: accepts the simplified interface parameters.
     * The actual approval/rejection action listeners must be wired externally
     * by the controller after this method populates the card.
     */
    @Override
    public void agregarTarjetaSolicitud(int idSolicitud, String mail, String fecha,
                                         String tipo, String lineaExtra,
                                         int idSolicitudParaAccion,
                                         String datosSolicitud) {
        agregarTarjetaSolicitud(idSolicitud, mail, fecha, tipo, lineaExtra,
            null, null);
    }

    @Override
    public void navegarALogin() {
        this.dispose();
        BancoADN_Grupo6_Pant_IniciarSesion login = new BancoADN_Grupo6_Pant_IniciarSesion();
        login.setVisible(true);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BancoADN_Grupo6_MenuAdmin().setVisible(true));
    }
}
