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
import java.awt.Cursor;
 
public class BancoADN_Grupo6_Pant_IniciarSesion extends javax.swing.JFrame {
 
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(BancoADN_Grupo6_Pant_IniciarSesion.class.getName());
 
    // Componentes accesibles desde controlador
    public JTextField txtEmail;
    public JPasswordField txtContraseña;
    public JButton btnIniciarSesion;
    public JLabel lblRegistro;
    public JButton btnOjo;
    private boolean cargando = false; 
 
    public BancoADN_Grupo6_Pant_IniciarSesion() {
        initComponents();
        configurarDiseno();
    }
 
    @SuppressWarnings("unchecked")
    private void initComponents() {
 
        jLabel1 = new javax.swing.JLabel();
 
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(600, 450));
        setSize(new java.awt.Dimension(600, 450));
 
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
        // 1. COLORES Y FUENTES
        Color celesteEncabezado = new Color(169, 195, 207);
        Color grisFondoCuerpo = new Color(235, 235, 235);
        Color grisCampos = new Color(217, 217, 217);
        Color azulBoton = new Color(74, 144, 226);
        Color azulLink = new Color(70, 130, 180);
        Font fuenteLabels = new Font("SansSerif", Font.BOLD, 14);
        Font fuenteLink = new Font("SansSerif", Font.PLAIN, 12);
 
        // 2. ESTRUCTURA PRINCIPAL
        this.getContentPane().setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new LineBorder(new Color(128, 128, 128), 10));
        this.add(mainPanel, BorderLayout.CENTER);
 
        // 3. ENCABEZADO
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(celesteEncabezado);
        headerPanel.setPreferredSize(new Dimension(0, 50));
        JLabel lblTitulo = new JLabel("<html><font color='#4A90E2'>Simple</font><font color='#F39C12'>ADN</font></html>");
        lblTitulo.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 24));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(lblTitulo, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
 
        // 4. CUERPO (GridBagLayout)
        JPanel bodyPanel = new JPanel(new java.awt.GridBagLayout());
        bodyPanel.setBackground(grisFondoCuerpo);
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        mainPanel.add(bodyPanel, BorderLayout.CENTER);
 
        // --- CONFIGURACIÓN GLOBAL DE CELDAS ---
        gbc.gridx = 0;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.weightx = 1.0;
 
        // --- EMAIL ---
        gbc.gridy = 0;
        gbc.insets = new java.awt.Insets(15, 25, 0, 25);
        JLabel lblEmail = new JLabel("Email");
        lblEmail.setFont(fuenteLabels);
        bodyPanel.add(lblEmail, gbc);
 
        gbc.gridy = 1;
        gbc.insets = new java.awt.Insets(5, 25, 0, 25);
        txtEmail = new JTextField();
        txtEmail.setPreferredSize(new Dimension(0, 35));
        txtEmail.setBackground(grisCampos);
        bodyPanel.add(txtEmail, gbc);
 
        // --- CONTRASEÑA ---
        gbc.gridy = 2;
        gbc.insets = new java.awt.Insets(15, 25, 0, 25);
        JLabel lblPass = new JLabel("Contraseña");
        lblPass.setFont(fuenteLabels);
        bodyPanel.add(lblPass, gbc);
 
        gbc.gridy = 3;
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
 
        // Lógica del ojo
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
 
        // --- LINK DE REGISTRO ---
        gbc.gridy = 4;
        gbc.insets = new java.awt.Insets(15, 25, 0, 25);
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        JPanel registroPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        registroPanel.setBackground(grisFondoCuerpo);
 
        JLabel lblTextoRegistro = new JLabel("¿No tienes Cuenta? ");
        lblTextoRegistro.setFont(fuenteLink);
        lblTextoRegistro.setForeground(Color.BLACK);
 
        lblRegistro = new JLabel("Registrate");
        lblRegistro.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblRegistro.setForeground(azulLink);
        lblRegistro.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
 
        registroPanel.add(lblTextoRegistro);
        registroPanel.add(lblRegistro);
        bodyPanel.add(registroPanel, gbc);
 
        // --- BOTÓN INICIAR SESIÓN ---
        gbc.gridy = 5;
        gbc.insets = new java.awt.Insets(30, 25, 20, 25);
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.anchor = java.awt.GridBagConstraints.CENTER;
        btnIniciarSesion = new JButton("Iniciar Sesión");
        btnIniciarSesion.setBackground(azulBoton);
        btnIniciarSesion.setForeground(Color.WHITE);
        btnIniciarSesion.setPreferredSize(new Dimension(180, 40));
        btnIniciarSesion.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnIniciarSesion.setFocusPainted(false);
        btnIniciarSesion.setBorderPainted(false);
        bodyPanel.add(btnIniciarSesion, gbc);
 
        // REFRESCAR
        this.revalidate();
        this.repaint();
    }
 
    // === GETTERS Y SETTERS ===
    public String getEmail() {
        return txtEmail.getText();
    }
 
    public String getContraseña() {
        return new String(txtContraseña.getPassword());
    }
 
    public void setEmail(String email) {
        txtEmail.setText(email);
    }
 
    public void setContraseña(String contraseña) {
        txtContraseña.setText(contraseña);
    }
 
    public void limpiarCampos() {
        txtEmail.setText("");
        txtContraseña.setText("");
    }
 
    // === MÉTODOS PARA DIÁLOGOS ===
    public void mostrarError(String mensaje) {
        javax.swing.JOptionPane.showMessageDialog(this, mensaje, "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
    }
 
    public void mostrarMensaje(String mensaje) {
        javax.swing.JOptionPane.showMessageDialog(this, mensaje, "Información", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
    /**
     * Muestra u oculta el indicador de carga.
     * Deshabilita todos los elementos interactivos durante la espera del servidor.
     * lblRegistro es un JLabel — se bloquea con la bandera cargando en el listener.
     */
    public void mostrarCargando(boolean cargando) {
        this.cargando = cargando;
        btnIniciarSesion.setEnabled(!cargando);
        btnOjo.setEnabled(!cargando);
        lblRegistro.setEnabled(!cargando);
        setCursor(cargando ?
            Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) :
            Cursor.getDefaultCursor()
        );
    }
 
    // === MÉTODOS PARA AGREGAR LISTENERS ===
    public void agregarListenerBotonIniciarSesion(ActionListener listener) {
        btnIniciarSesion.addActionListener(listener);
    }
 
    public void agregarListenerRegistro(ActionListener listener) {
        lblRegistro.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (cargando) return;
                listener.actionPerformed(null);
            }
        });
    }
 
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new BancoADN_Grupo6_Pant_IniciarSesion().setVisible(true));
    }
 
    // Variables declaration - do not modify
    private javax.swing.JLabel jLabel1;
    // End of variables declaration
}
