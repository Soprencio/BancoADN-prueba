package com.mycompany.bancoadn.cliente;

import com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces.IVistaLogs;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;


public class BancoADN_Grupo6_Pant_Logs extends JFrame implements IVistaLogs {

    private final Color azulClaro    = new Color(169, 195, 207);
    private final Color grisFondo    = new Color(235, 235, 235);
    private final Color blanco       = Color.WHITE;
    private final Color grisOscuro   = new Color(100, 100, 100);
    private final Color grisBorde    = new Color(200, 200, 200);
    private final Color naranjaTexto = new Color(211, 84, 0);

    public JButton btnCerrar;
    private JPanel panelLogs;

    public BancoADN_Grupo6_Pant_Logs(boolean isAdmin) {
        setTitle("Banco ADN - Logs (" + (isAdmin ? "Admin" : "Usuario") + ")");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);
        construirUI(isAdmin);
    }

    private void construirUI(boolean isAdmin) {
        JPanel mainWrapper = new JPanel(new BorderLayout());
        mainWrapper.setBackground(grisOscuro);
        mainWrapper.setBorder(new LineBorder(grisOscuro, 10, true));

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(azulClaro);
        mainWrapper.add(contentPane);

        contentPane.add(crearEncabezado(isAdmin), BorderLayout.NORTH);
        contentPane.add(crearCuerpo(),           BorderLayout.CENTER);

        add(mainWrapper);
    }

    private JPanel crearEncabezado(boolean isAdmin) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel lblTitulo = new JLabel("Historial de Logs (Últimos " + (isAdmin ? "30" : "15") + ")");
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

        panelLogs = new JPanel();
        panelLogs.setLayout(new BoxLayout(panelLogs, BoxLayout.Y_AXIS));
        panelLogs.setBackground(grisFondo);

        JScrollPane scroll = new JScrollPane(panelLogs);
        scroll.setBorder(new LineBorder(grisBorde, 1));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        cuerpo.add(scroll, BorderLayout.CENTER);

        return cuerpo;
    }


    public void agregarTarjetaLog(int idLog,
                                  String nombreCuenta,
                                  String email,
                                  String descripcion,
                                  String fecha,
                                  String acciones,
                                  boolean isAdminLog) {

        JPanel tarjeta = new JPanel(new BorderLayout(10, 0));
        tarjeta.setBackground(blanco);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(grisBorde, 1, true),
            new EmptyBorder(12, 18, 12, 18)
        ));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel info = new JPanel(new GridLayout(4, 1, 0, 2));
        info.setOpaque(false);

        // Línea 1: Log ID, Fecha y Tag Admin
        String tagAdmin = isAdminLog ? " <font color='#3C9F3C'>[Admin]</font>" : "";
        JLabel lbl1 = new JLabel("<html><b>Log #" + idLog + "</b> - <font color='#888888'>" + esc(fecha) + "</font>" + tagAdmin + "</html>");
        lbl1.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Línea 2: Cuenta (Nombre y Email)
        JLabel lbl2 = new JLabel("<html>Cuenta: <b>" + esc(nombreCuenta) + "</b> (" + esc(email) + ")</html>");
        lbl2.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // Línea 3: Descripción
        JLabel lbl3 = new JLabel("<html>Descripción: <i>" + esc(descripcion) + "</i></html>");
        lbl3.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // Línea 4: Acciones
        JLabel lbl4 = new JLabel("<html>Acciones: <font color='#4A90E2'>" + esc(acciones) + "</font></html>");
        lbl4.setFont(new Font("SansSerif", Font.PLAIN, 12));

        info.add(lbl1);
        info.add(lbl2);
        info.add(lbl3);
        info.add(lbl4);

        tarjeta.add(info, BorderLayout.CENTER);

        panelLogs.add(tarjeta);
        panelLogs.add(Box.createVerticalStrut(8));
        panelLogs.revalidate();
        panelLogs.repaint();
    }

    public void limpiarLogs() {
        panelLogs.removeAll();
        panelLogs.revalidate();
        panelLogs.repaint();
    }

    public void mostrarSinLogs() {
        JLabel lbl = new JLabel("No hay logs registrados.", SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.ITALIC, 15));
        lbl.setForeground(Color.GRAY);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(40, 0, 0, 0));
        panelLogs.add(lbl);
        panelLogs.revalidate();
        panelLogs.repaint();
    }

    public void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void agregarListenerCerrar(ActionListener l) {
        btnCerrar.addActionListener(l);
    }

    private String esc(String s) {
        if (s == null) return "—";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
