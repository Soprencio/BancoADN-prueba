package com.mycompany.bancoadn.cliente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;


public class BancoADN_Grupo6_Pant_ModificarPerfilAdmin extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
        java.util.logging.Logger.getLogger(BancoADN_Grupo6_Pant_ModificarPerfilAdmin.class.getName());

    public JTextField txtNombrePerfil;
    public JTextField txtCodigoSecuencia;
    public JTextArea  txtDescripcion;
    public JTextField txtFechaMuestra;
    public JButton    btnModificar;
    public JButton    btnCancelar;

    public BancoADN_Grupo6_Pant_ModificarPerfilAdmin() {
        initComponents();
        configurarDiseno();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
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
        JLabel lblTitulo = new JLabel(
            "<html><font color='#4A90E2'>Simple</font><font color='#F39C12'>ADN</font></html>");
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

        // CODIGO DE SECUENCIA
        gbc.gridy  = 2;
        gbc.insets = new java.awt.Insets(15, 25, 0, 25);
        JLabel lblCodigo = new JLabel("Codigo de Secuencia (ADN)");
        lblCodigo.setFont(fuenteLabels);
        bodyPanel.add(lblCodigo, gbc);

        gbc.gridy  = 3;
        gbc.insets = new java.awt.Insets(5, 25, 0, 25);
        txtCodigoSecuencia = new JTextField();
        txtCodigoSecuencia.setPreferredSize(new Dimension(0, 35));
        txtCodigoSecuencia.setBackground(grisCampos);
        bodyPanel.add(txtCodigoSecuencia, gbc);

        // DESCRIPCION
        gbc.gridy  = 4;
        gbc.insets = new java.awt.Insets(15, 25, 0, 25);
        JLabel lblDescripcion = new JLabel("Descripcion");
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
        bodyPanel.add(txtFechaMuestra, gbc);

        // BOTONES
        JPanel btnPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 0));
        btnPanel.setBackground(grisFondoCuerpo);

        btnModificar = new JButton("Modificar Perfil");
        btnModificar.setBackground(azulBoton);
        btnModificar.setForeground(Color.WHITE);
        btnModificar.setPreferredSize(new Dimension(160, 40));
        btnModificar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnModificar.setFocusPainted(false);
        btnModificar.setBorderPainted(false);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(200, 200, 200));
        btnCancelar.setForeground(Color.BLACK);
        btnCancelar.setPreferredSize(new Dimension(100, 40));
        btnCancelar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorderPainted(false);

        btnPanel.add(btnModificar);
        btnPanel.add(btnCancelar);

        gbc.gridy  = 8;
        gbc.insets = new java.awt.Insets(25, 25, 20, 25);
        gbc.fill   = java.awt.GridBagConstraints.HORIZONTAL;
        bodyPanel.add(btnPanel, gbc);

        this.revalidate();
        this.repaint();
    }

    public String getNombrePerfil()    { return txtNombrePerfil.getText().trim(); }
    public String getCodigoSecuencia() { return txtCodigoSecuencia.getText().trim(); }
    public String getDescripcion()     { return txtDescripcion.getText().trim(); }
    public String getFechaMuestra()    { return txtFechaMuestra.getText().trim(); }

    public void setNombrePerfil(String v)    { txtNombrePerfil.setText(v); }
    public void setCodigoSecuencia(String v) { txtCodigoSecuencia.setText(v); }
    public void setDescripcion(String v)     { txtDescripcion.setText(v); }
    public void setFechaMuestra(String v)    { txtFechaMuestra.setText(v); }

    public void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
    public void mostrarMensaje(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Informacion", JOptionPane.INFORMATION_MESSAGE);
    }

    public void agregarListenerModificar(ActionListener l) { btnModificar.addActionListener(l); }
    public void agregarListenerCancelar(ActionListener l)  { btnCancelar.addActionListener(l); }

    private javax.swing.JLabel jLabel1;
}
