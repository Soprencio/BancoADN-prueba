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
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;

public class BancoADN_Grupo6_Pant_CrearCuenta extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(BancoADN_Grupo6_Pant_CrearCuenta.class.getName());

    // ── Atributos de clase (el controlador los puede ver) 
    public JTextField     txtEmail;
    public JPasswordField txtContraseña;
    public JTextField     txtNombre;
    public JButton        btnIngresar;
    public JButton        btnOjo;
    public JLabel         lblVolver;

    public BancoADN_Grupo6_Pant_CrearCuenta() {
        initComponents();
        configurarDiseno();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(new java.awt.Dimension(600, 400));

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
    }// </editor-fold>//GEN-END:initComponents

    private void configurarDiseno() {
        Color celesteEncabezado = new Color(169, 195, 207);
        Color grisFondoCuerpo   = new Color(235, 235, 235);
        Color grisCampos        = new Color(217, 217, 217);
        Color azulBoton         = new Color(74, 144, 226);
        Color azulLink          = new Color(70, 130, 180);
        Font fuenteLabels = new Font("SansSerif", Font.BOLD, 14);
        Font fuenteLink   = new Font("SansSerif", Font.PLAIN, 12);

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

        // EMAIL
        gbc.gridy  = 0;
        gbc.insets = new java.awt.Insets(15, 25, 0, 25);
        JLabel lblEmail = new JLabel("Ingresar Email");
        lblEmail.setFont(fuenteLabels);
        bodyPanel.add(lblEmail, gbc);

        gbc.gridy  = 1;
        gbc.insets = new java.awt.Insets(5, 25, 0, 25);
        txtEmail = new JTextField();
        txtEmail.setPreferredSize(new Dimension(0, 35));
        txtEmail.setBackground(grisCampos);
        bodyPanel.add(txtEmail, gbc);

        // CONTRASEÑA
        gbc.gridy  = 2;
        gbc.insets = new java.awt.Insets(15, 25, 0, 25);
        JLabel lblPass = new JLabel("Ingresar Contraseña");
        lblPass.setFont(fuenteLabels);
        bodyPanel.add(lblPass, gbc);

        gbc.gridy  = 3;
        gbc.insets = new java.awt.Insets(5, 25, 0, 25);
        JPanel passWrapper = new JPanel(new BorderLayout());
        passWrapper.setBackground(grisFondoCuerpo);
        txtContraseña = new JPasswordField();
        txtContraseña.setBackground(grisCampos);
        btnOjo = new JButton("👁");
        btnOjo.setPreferredSize(new Dimension(50, 35));
        btnOjo.setBackground(grisCampos);
        btnOjo.setFocusPainted(false);
        btnOjo.setBorderPainted(false);
        btnOjo.addActionListener(e -> {
            if (txtContraseña.getEchoChar() == '*') {
                txtContraseña.setEchoChar((char) 0);
                btnOjo.setText("🔒");
            } else {
                txtContraseña.setEchoChar('*');
                btnOjo.setText("👁");
            }
        });
        passWrapper.add(txtContraseña, BorderLayout.CENTER);
        passWrapper.add(btnOjo, BorderLayout.EAST);
        bodyPanel.add(passWrapper, gbc);

        // NOMBRE
        gbc.gridy  = 4;
        gbc.insets = new java.awt.Insets(15, 25, 0, 25);
        JLabel lblNombre = new JLabel("Ingresar Nombre");
        lblNombre.setFont(fuenteLabels);
        bodyPanel.add(lblNombre, gbc);

        gbc.gridy  = 5;
        gbc.insets = new java.awt.Insets(5, 25, 0, 25);
        txtNombre = new JTextField();
        txtNombre.setPreferredSize(new Dimension(0, 35));
        txtNombre.setBackground(grisCampos);
        bodyPanel.add(txtNombre, gbc);

        // LINK VOLVER
        gbc.gridy  = 6;
        gbc.insets = new java.awt.Insets(15, 25, 0, 25);
        gbc.fill   = java.awt.GridBagConstraints.NONE;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        JPanel volverPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        volverPanel.setBackground(grisFondoCuerpo);
        JLabel lblTextoVolver = new JLabel("¿Ya tienes Cuenta? ");
        lblTextoVolver.setFont(fuenteLink);
        lblTextoVolver.setForeground(Color.BLACK);
        lblVolver = new JLabel("Volver");
        lblVolver.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblVolver.setForeground(azulLink);
        lblVolver.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        volverPanel.add(lblTextoVolver);
        volverPanel.add(lblVolver);
        bodyPanel.add(volverPanel, gbc);

        // BOTÓN INGRESAR
        gbc.gridy  = 7;
        gbc.insets = new java.awt.Insets(30, 25, 20, 25);
        gbc.fill   = java.awt.GridBagConstraints.NONE;
        gbc.anchor = java.awt.GridBagConstraints.CENTER;
        btnIngresar = new JButton("Ingresar");
        btnIngresar.setBackground(azulBoton);
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setPreferredSize(new Dimension(180, 40));
        btnIngresar.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnIngresar.setFocusPainted(false);
        btnIngresar.setBorderPainted(false);
        bodyPanel.add(btnIngresar, gbc);

        this.revalidate();
        this.repaint();
    }

    // ── Getters ──────────────────────────────────────────
    public String getEmail()        { return txtEmail.getText(); }
    public String getContraseña()   { return new String(txtContraseña.getPassword()); }
    public String getNombreCuenta() { return txtNombre.getText(); }

    // ── Setters ──────────────────────────────────────────
    public void setEmail(String v)        { txtEmail.setText(v); }
    public void setContraseña(String v)   { txtContraseña.setText(v); }
    public void setNombreCuenta(String v) { txtNombre.setText(v); }

    // ── Utilidades ───────────────────────────────────────
    public void limpiarCampos() {
        txtEmail.setText("");
        txtContraseña.setText("");
        txtNombre.setText("");
    }

    public void mostrarError(String mensaje) {
        javax.swing.JOptionPane.showMessageDialog(this, mensaje, "Error",
            javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    public void mostrarMensaje(String mensaje) {
        javax.swing.JOptionPane.showMessageDialog(this, mensaje, "Información",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Listeners ────────────────────────────────────────
    public void agregarListenerBotonIngresar(ActionListener listener) {
        btnIngresar.addActionListener(listener);
    }

    public void agregarListenerVolver(ActionListener listener) {
        lblVolver.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listener.actionPerformed(null);
            }
        });
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new BancoADN_Grupo6_Pant_CrearCuenta().setVisible(true));
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
