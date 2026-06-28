package com.mycompany.bancoadn.cliente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;


public class BancoADN_Grupo6_Pant_AdminPerfiles extends JFrame {

    // ── Paleta ────────────────────────────────────────────
    private final Color azulClaro    = new Color(169, 195, 207);
    private final Color grisFondo    = new Color(235, 235, 235);
    private final Color blanco       = Color.WHITE;
    private final Color grisOscuro   = new Color(100, 100, 100);
    private final Color grisBorde    = new Color(200, 200, 200);
    private final Color azulBoton    = new Color(74, 144, 226);
    private final Color verdeBoton   = new Color(60, 160, 60);
    private final Color rojoBoton    = new Color(200, 60, 60);
    private final Color naranjaTexto = new Color(211, 84, 0);

    // ── Componentes accesibles desde el controlador ──────
    public JButton        btnVolver;
    public JButton        btnBuscar;
    public JTextField     txtBusqueda;
    public JComboBox<String> cbFiltro;
    private JPanel        panelPerfiles;

    public BancoADN_Grupo6_Pant_AdminPerfiles() {
        setTitle("Banco ADN - Administrar Perfiles");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 680);
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

        btnVolver = new JButton("← Volver");
        btnVolver.setContentAreaFilled(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setForeground(Color.GRAY);
        btnVolver.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblLogo = new JLabel(
            "<html><font color='#4A90E2'>Simple</font><font color='#D35400'>ADN</font></html>");
        lblLogo.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 28));
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblTitulo = new JLabel("Administrar Perfiles");
        lblTitulo.setForeground(naranjaTexto);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitulo.setHorizontalAlignment(SwingConstants.RIGHT);

        header.add(btnVolver, BorderLayout.WEST);
        header.add(lblLogo,   BorderLayout.CENTER);
        header.add(lblTitulo, BorderLayout.EAST);
        return header;
    }

    private JPanel crearCuerpo() {
        JPanel cuerpo = new JPanel(new BorderLayout(0, 10));
        cuerpo.setBackground(grisFondo);
        cuerpo.setBorder(new EmptyBorder(20, 40, 20, 40));

        // ── Barra de filtros ─────────────────────────────
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filtros.setOpaque(false);
        filtros.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lblFiltrarPor = new JLabel("Filtrar por:");
        lblFiltrarPor.setFont(new Font("SansSerif", Font.BOLD, 13));

        cbFiltro = new JComboBox<>(new String[]{"Todos", "Nombre", "ID"});
        cbFiltro.setPreferredSize(new Dimension(120, 36));

        txtBusqueda = new JTextField();
        txtBusqueda.setPreferredSize(new Dimension(280, 36));
        txtBusqueda.setBackground(new Color(217, 217, 217));

        btnBuscar = crearBotonAzul("Buscar");
        btnBuscar.setPreferredSize(new Dimension(110, 36));

        filtros.add(lblFiltrarPor);
        filtros.add(cbFiltro);
        filtros.add(txtBusqueda);
        filtros.add(btnBuscar);

        cuerpo.add(filtros, BorderLayout.NORTH);

        // ── Panel de resultados con scroll ───────────────
        panelPerfiles = new JPanel();
        panelPerfiles.setLayout(new BoxLayout(panelPerfiles, BoxLayout.Y_AXIS));
        panelPerfiles.setBackground(grisFondo);

        JScrollPane scroll = new JScrollPane(panelPerfiles);
        scroll.setBorder(new LineBorder(grisBorde, 1));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        cuerpo.add(scroll, BorderLayout.CENTER);

        return cuerpo;
    }

    public void agregarTarjetaPerfil(int idPerfil,
                                     String nombre,
                                     String codigo,
                                     String descripcion,
                                     String fechaMuestra,
                                     String email,
                                     boolean activo,
                                     ActionListener onModificar,
                                     ActionListener onBajaRestaura) {

        JPanel tarjeta = new JPanel(new BorderLayout(10, 0));
        tarjeta.setBackground(blanco);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(grisBorde, 1, true),
            new EmptyBorder(12, 18, 12, 18)
        ));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Información del perfil (5 líneas) ────────────
        JPanel info = new JPanel(new GridLayout(5, 1, 0, 1));
        info.setOpaque(false);

        JLabel lbl1 = new JLabel(
            "<html><b>" + esc(nombre) + "</b>&nbsp;&nbsp;"
            + "<font color='#888888'>ID:&nbsp;" + idPerfil + "</font></html>");
        lbl1.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JLabel lbl2 = new JLabel(
            "<html><font color='#555555'>Código:&nbsp;<b>" + esc(codigo) + "</b></font></html>");
        lbl2.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JLabel lbl3 = new JLabel(
            "<html><font color='#555555'>Descripción:&nbsp;" + esc(descripcion) + "</font></html>");
        lbl3.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JLabel lbl4 = new JLabel(
            "<html><font color='#555555'>Fecha muestra:&nbsp;" + esc(fechaMuestra) + "</font></html>");
        lbl4.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JLabel lbl5 = new JLabel(
            "<html><font color='#888888'>Titular:&nbsp;" + esc(email) + "</font></html>");
        lbl5.setFont(new Font("SansSerif", Font.PLAIN, 11));

        info.add(lbl1);
        info.add(lbl2);
        info.add(lbl3);
        info.add(lbl4);
        info.add(lbl5);
        tarjeta.add(info, BorderLayout.CENTER);

        // ── Panel derecho: badge + botones ───────────────
        JPanel derecha = new JPanel();
        derecha.setLayout(new BoxLayout(derecha, BoxLayout.Y_AXIS));
        derecha.setOpaque(false);
        derecha.setBorder(new EmptyBorder(0, 10, 0, 0));

        // Badge de estado
        String badgeTexto = activo ? "ACTIVO" : "INACTIVO";
        Color  badgeColor = activo ? new Color(60, 160, 60) : new Color(180, 60, 60);
        JLabel badge = new JLabel(badgeTexto, SwingConstants.CENTER);
        badge.setFont(new Font("SansSerif", Font.BOLD, 12));
        badge.setForeground(Color.WHITE);
        badge.setBackground(badgeColor);
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(4, 10, 4, 10));
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Botón modificar (siempre azul)
        JButton btnMod = crearBotonIcono("✎ Modificar", azulBoton);
        btnMod.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (onModificar != null) btnMod.addActionListener(onModificar);

        // Botón baja o restaurar
        JButton btnBajaRest;
        if (activo) {
            btnBajaRest = crearBotonIcono("✕ Dar de baja", rojoBoton);
        } else {
            btnBajaRest = crearBotonIcono("↺ Restaurar", verdeBoton);
        }
        btnBajaRest.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (onBajaRestaura != null) btnBajaRest.addActionListener(onBajaRestaura);

        derecha.add(badge);
        derecha.add(Box.createVerticalStrut(6));
        derecha.add(btnMod);
        derecha.add(Box.createVerticalStrut(4));
        derecha.add(btnBajaRest);

        tarjeta.add(derecha, BorderLayout.EAST);

        panelPerfiles.add(tarjeta);
        panelPerfiles.add(Box.createVerticalStrut(8));
        panelPerfiles.revalidate();
        panelPerfiles.repaint();
    }

    public void limpiarPerfiles() {
        panelPerfiles.removeAll();
        panelPerfiles.revalidate();
        panelPerfiles.repaint();
    }

    public void mostrarSinResultados() {
        JLabel lbl = new JLabel("No se encontraron perfiles.", SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.ITALIC, 15));
        lbl.setForeground(Color.GRAY);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(40, 0, 0, 0));
        panelPerfiles.add(lbl);
        panelPerfiles.revalidate();
        panelPerfiles.repaint();
    }

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

    public String getTipoFiltro()    { return (String) cbFiltro.getSelectedItem(); }
    public String getTextoBusqueda() { return txtBusqueda.getText().trim(); }

    public void agregarListenerBuscar(java.awt.event.ActionListener l) {
        btnBuscar.addActionListener(l);
    }
    public void agregarListenerVolver(java.awt.event.ActionListener l) {
        btnVolver.addActionListener(l);
    }

    // ── Helpers ───────────────────────────────────────────
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

    private JButton crearBotonIcono(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(blanco);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(155, 30));
        btn.setMaximumSize(new Dimension(155, 30));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private String esc(String s) {
        if (s == null) return "—";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
