/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.bancoadn.cliente;
 
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
 
/**
 * Pantalla para solicitar registrar un nuevo perfil genético.
 * 
 * Campos:
 * - Nombre del Perfil
 * - Código de Secuencia (ADN)
 * - Descripción
 * - Fecha de Muestra (formato: yyyy-MM-dd)
 * 
 * @author Admin
 */
public class BancoADN_Grupo6_Pant_SolicitarPerfil extends javax.swing.JFrame {
 
    private static final java.util.logging.Logger logger = 
        java.util.logging.Logger.getLogger(BancoADN_Grupo6_Pant_SolicitarPerfil.class.getName());
 
    // ── Atributos accesibles desde el controlador ──────────
    public JTextField txtNombrePerfil;
    public JTextField txtCodigoSecuencia;
    public JTextArea  txtDescripcion;
    public JTextField txtFechaMuestra;
    public JButton    btnEnviarSolicitud;
    public JButton    btnVolver;
 
    public BancoADN_Grupo6_Pant_SolicitarPerfil() {
        initComponents();
        configurarDiseno();
    }
 
    @SuppressWarnings("unchecked")
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(700, 600));
        setSize(new java.awt.Dimension(700, 600));
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(162, 162, 162)
                .addComponent(jLabel1)
                .addContainerGap(238, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(142, 142, 142)
                .addComponent(jLabel1)
                .addContainerGap(183, Short.MAX_VALUE))
        );
        pack();
    }
 
    private void configurarDiseno() {
        Color celesteEncabezado = new Color(169, 195, 207);
        Color grisFondoCuerpo   = new Color(235, 235, 235);
        Color grisCampos        = new Color(217, 217, 217);
        Color azulBoton         = new Color(74, 144, 226);
        Font fuenteLabels = new Font("SansSerif", Font.BOLD, 13);
 
        this.getContentPane().setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new LineBorder(new Color(128, 128, 128), 10));
        this.add(mainPanel, BorderLayout.CENTER);
 
        // ENCABEZADO
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(celesteEncabezado);
        headerPanel.setPreferredSize(new Dimension(0, 50));
        JLabel lblTitulo = new JLabel("<html><font color='#4A90E2'>Simple</font><font color='#F39C12'>ADN</font></html>");
        lblTitulo.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 24));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(lblTitulo, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
 
        // CUERPO
        JPanel bodyPanel = new JPanel(new java.awt.GridBagLayout());
        bodyPanel.setBackground(grisFondoCuerpo);
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        mainPanel.add(bodyPanel, BorderLayout.CENTER);
 
        gbc.gridx   = 0;
        gbc.fill    = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.anchor  = java.awt.GridBagConstraints.WEST;
        gbc.weightx = 1.0;
 
        // NOMBRE DEL PERFIL
        gbc.gridy  = 0;
        gbc.insets = new java.awt.Insets(15, 25, 0, 25);
        JLabel lblNombre = new JLabel("Nombre del Perfil");
        lblNombre.setFont(fuenteLabels);
        bodyPanel.add(lblNombre, gbc);
 
        gbc.gridy  = 1;
        gbc.insets = new java.awt.Insets(5, 25, 0, 25);
        txtNombrePerfil = new JTextField();
        txtNombrePerfil.setPreferredSize(new Dimension(0, 35));
        txtNombrePerfil.setBackground(grisCampos);
        bodyPanel.add(txtNombrePerfil, gbc);
 
        // CÓDIGO DE SECUENCIA
        gbc.gridy  = 2;
        gbc.insets = new java.awt.Insets(15, 25, 0, 25);
        JLabel lblCodigo = new JLabel("Código de Secuencia (ADN)");
        lblCodigo.setFont(fuenteLabels);
        bodyPanel.add(lblCodigo, gbc);
 
        gbc.gridy  = 3;
        gbc.insets = new java.awt.Insets(5, 25, 0, 25);
        txtCodigoSecuencia = new JTextField();
        txtCodigoSecuencia.setPreferredSize(new Dimension(0, 35));
        txtCodigoSecuencia.setBackground(grisCampos);
        bodyPanel.add(txtCodigoSecuencia, gbc);
 
        // DESCRIPCIÓN
        gbc.gridy  = 4;
        gbc.insets = new java.awt.Insets(15, 25, 0, 25);
        JLabel lblDescripcion = new JLabel("Descripción");
        lblDescripcion.setFont(fuenteLabels);
        bodyPanel.add(lblDescripcion, gbc);
 
        gbc.gridy  = 5;
        gbc.insets = new java.awt.Insets(5, 25, 0, 25);
        txtDescripcion = new JTextArea(3, 1);
        txtDescripcion.setBackground(grisCampos);
        txtDescripcion.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setPreferredSize(new Dimension(0, 80));
        bodyPanel.add(scrollDesc, gbc);
 
        // FECHA DE MUESTRA
        gbc.gridy  = 6;
        gbc.insets = new java.awt.Insets(15, 25, 0, 25);
        JLabel lblFecha = new JLabel("Fecha de Muestra (yyyy-MM-dd)");
        lblFecha.setFont(fuenteLabels);
        bodyPanel.add(lblFecha, gbc);
 
        gbc.gridy  = 7;
        gbc.insets = new java.awt.Insets(5, 25, 0, 25);
        txtFechaMuestra = new JTextField();
        txtFechaMuestra.setPreferredSize(new Dimension(0, 35));
        txtFechaMuestra.setBackground(grisCampos);
        txtFechaMuestra.setText("2026-04-23");  // Fecha por defecto (hoy)
        bodyPanel.add(txtFechaMuestra, gbc);
 
        // BOTONES (Enviar y Volver)
        JPanel btnPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 0));
        btnPanel.setBackground(grisFondoCuerpo);
 
        btnEnviarSolicitud = new JButton("Enviar Solicitud");
        btnEnviarSolicitud.setBackground(azulBoton);
        btnEnviarSolicitud.setForeground(Color.WHITE);
        btnEnviarSolicitud.setPreferredSize(new Dimension(150, 40));
        btnEnviarSolicitud.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnEnviarSolicitud.setFocusPainted(false);
        btnEnviarSolicitud.setBorderPainted(false);
 
        btnVolver = new JButton("Volver");
        btnVolver.setBackground(new Color(200, 200, 200));
        btnVolver.setForeground(Color.BLACK);
        btnVolver.setPreferredSize(new Dimension(100, 40));
        btnVolver.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnVolver.setFocusPainted(false);
        btnVolver.setBorderPainted(false);
 
        btnPanel.add(btnEnviarSolicitud);
        btnPanel.add(btnVolver);
 
        gbc.gridy  = 8;
        gbc.insets = new java.awt.Insets(25, 25, 20, 25);
        gbc.fill   = java.awt.GridBagConstraints.HORIZONTAL;
        bodyPanel.add(btnPanel, gbc);
 
        this.revalidate();
        this.repaint();
    }
 
    // ── Getters ────────────────────────────────────────────
    public String getNombrePerfil()     { return txtNombrePerfil.getText().trim(); }
    public String getCodigoSecuencia()  { return txtCodigoSecuencia.getText().trim(); }
    public String getDescripcion()      { return txtDescripcion.getText().trim(); }
    public String getFechaMuestra()     { return txtFechaMuestra.getText().trim(); }
 
    // ── Setters ────────────────────────────────────────────
    public void setNombrePerfil(String v)     { txtNombrePerfil.setText(v); }
    public void setCodigoSecuencia(String v)  { txtCodigoSecuencia.setText(v); }
    public void setDescripcion(String v)      { txtDescripcion.setText(v); }
    public void setFechaMuestra(String v)     { txtFechaMuestra.setText(v); }
 
    // ── Utilidades ─────────────────────────────────────────
    public void limpiarCampos() {
        txtNombrePerfil.setText("");
        txtCodigoSecuencia.setText("");
        txtDescripcion.setText("");
        txtFechaMuestra.setText("2026-04-23");
    }
 
    public void mostrarError(String mensaje) {
        javax.swing.JOptionPane.showMessageDialog(this, mensaje, "Error",
            javax.swing.JOptionPane.ERROR_MESSAGE);
    }
 
    public void mostrarMensaje(String mensaje) {
        javax.swing.JOptionPane.showMessageDialog(this, mensaje, "Información",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
 
    // ── Listeners ──────────────────────────────────────────
    public void agregarListenerEnviarSolicitud(ActionListener listener) {
        btnEnviarSolicitud.addActionListener(listener);
    }
 
    public void agregarListenerVolver(ActionListener listener) {
        btnVolver.addActionListener(listener);
    }
 
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new BancoADN_Grupo6_Pant_SolicitarPerfil().setVisible(true));
    }
 
    // Variables declaration - do not modify
    private javax.swing.JLabel jLabel1;
    // End of variables declaration
}
