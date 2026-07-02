package com.mycompany.bancoadn.cliente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;


public class BancoADN_Grupo6_Pant_UltimasSolicitudes extends JFrame {

    // ── Colores (misma paleta que MenuAdmin) ──────────────
    private final Color azulClaro    = new Color(169, 195, 207);
    private final Color grisFondo    = new Color(235, 235, 235);
    private final Color blanco       = Color.WHITE;
    private final Color grisOscuro   = new Color(100, 100, 100);
    private final Color grisBorde    = new Color(200, 200, 200);
    private final Color verdeOk      = new Color(60, 160, 60);
    private final Color rojoRech     = new Color(200, 60, 60);
    private final Color naranjaTexto = new Color(211, 84, 0);

    public JButton btnCerrar;
    private JPanel panelSolicitudes;

    public BancoADN_Grupo6_Pant_UltimasSolicitudes() {
        setTitle("Banco ADN - Últimas 10 Solicitudes Resueltas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);
        construirUI();
    }

    private void construirUI() {
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

    private JPanel crearEncabezado() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel lblTitulo = new JLabel("Últimas 10 Solicitudes Resueltas");
        lblTitulo.setForeground(naranjaTexto);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel lblLogo = new JLabel(
            "<html><font color='#4A90E2'>Simple</font><font color='#D35400'>ADN</font></html>");
        lblLogo.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 28));
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);

        btnCerrar = new JButton("✕  Cerrar");
        btnCerrar.setContentAreaFilled(false);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setForeground(Color.GRAY);
        btnCerrar.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        header.add(lblTitulo, BorderLayout.WEST);
        header.add(lblLogo,   BorderLayout.CENTER);
        header.add(btnCerrar, BorderLayout.EAST);
        return header;
    }

    private JPanel crearCuerpo() {
        JPanel cuerpo = new JPanel(new BorderLayout(0, 10));
        cuerpo.setBackground(grisFondo);
        cuerpo.setBorder(new EmptyBorder(20, 40, 20, 40));

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
                                        String fechaCreacion,
                                        String fechaResolucion,
                                        String tipo,
                                        String estadoTexto,
                                        Color  estadoColor,
                                        String lineaExtra) {

        JPanel tarjeta = new JPanel(new BorderLayout(10, 0));
        tarjeta.setBackground(blanco);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(grisBorde, 1, true),
            new EmptyBorder(12, 18, 12, 18)
        ));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Texto (4 líneas siempre) ─────────────────────
        boolean conDesc = tipo.equals("registrar") || tipo.equals("modificar");
        JPanel info = new JPanel(new GridLayout(conDesc ? 4 : 3, 1, 0, 2));
        info.setOpaque(false);

        // Línea 1: mail en negrita y más grande
        JLabel lbl1 = new JLabel("<html><b>" + esc(mail) + "</b></html>");
        lbl1.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Línea 2: fechas + asunto
        JLabel lbl2 = new JLabel(
            "<html><font color='#888888'>Creada:&nbsp;" + esc(fechaCreacion) + "</font>"
            + "&nbsp;&nbsp;|&nbsp;&nbsp;"
            + "Asunto:&nbsp;<b>" + esc(tipo) + "</b></html>");
        lbl2.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // Línea 3: fecha resolución
        JLabel lbl3 = new JLabel(
            "<html><font color='#555555'>Resuelta:&nbsp;" + esc(fechaResolucion) + "</font></html>");
        lbl3.setFont(new Font("SansSerif", Font.PLAIN, 12));

        info.add(lbl1);
        info.add(lbl2);
        info.add(lbl3);

        // Línea 4: solo para registrar/modificar (descripción)
        if (tipo.equals("registrar") || tipo.equals("modificar")) {
            JLabel lbl4 = new JLabel(
                "<html><font color='#444444'>Descripción:&nbsp;<i>" + esc(lineaExtra) + "</i></font></html>");
            lbl4.setFont(new Font("SansSerif", Font.PLAIN, 12));
            info.add(lbl4);
        }

        tarjeta.add(info, BorderLayout.CENTER);

        // ── Badge de estado ──────────────────────────────
        JLabel badge = new JLabel(estadoTexto, SwingConstants.CENTER);
        badge.setFont(new Font("SansSerif", Font.BOLD, 13));
        badge.setForeground(Color.WHITE);
        badge.setBackground(estadoColor);
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(6, 14, 6, 14));
        badge.setPreferredSize(new Dimension(140, 36));

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        derecha.setOpaque(false);
        derecha.add(badge);
        tarjeta.add(derecha, BorderLayout.EAST);

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
        JLabel lbl = new JLabel("No hay solicitudes resueltas aún.", SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.ITALIC, 15));
        lbl.setForeground(Color.GRAY);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(40, 0, 0, 0));
        panelSolicitudes.add(lbl);
        panelSolicitudes.revalidate();
        panelSolicitudes.repaint();
    }

    public void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void agregarListenerCerrar(ActionListener l) {
        btnCerrar.addActionListener(l);
    }

    // ── Colores públicos para el controlador ─────────────
    public Color getColorAceptada()  { return verdeOk;  }
    public Color getColorRechazada() { return rojoRech; }

    private String esc(String s) {
        if (s == null) return "—";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
