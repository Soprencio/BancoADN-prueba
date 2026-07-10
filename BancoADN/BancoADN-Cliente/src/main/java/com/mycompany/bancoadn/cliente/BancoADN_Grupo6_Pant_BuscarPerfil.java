package com.mycompany.bancoadn.cliente;
 
import com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces.IVistaBuscarPerfil;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
/**
 * Pantalla de búsqueda de perfiles genéticos.
 * Permite buscar por ID o Nombre y muestra resultados dinámicamente.
 * Los botones "Ver Perfil" están vinculados a los datos del perfil.
 * 
 * @author Admin
 */
public class BancoADN_Grupo6_Pant_BuscarPerfil extends JFrame implements IVistaBuscarPerfil {
 
    private final Color celesteEncabezado = new Color(169, 195, 207);
    private final Color grisFondoCuerpo = new Color(235, 235, 235);
    private final Color azulBoton = new Color(74, 144, 226);
    private final Color grisBordeClaro = new Color(200, 200, 200);
 
    // ── Componentes accesibles ─────────────────────────────
    public JButton btnAtras;
    public JButton btnBuscar;
    public JTextField txtBusqueda;
    public JComboBox<String> cbFiltro;
    
    private JPanel resultsContainer;
    
    // Guardar referencias a datos de perfiles y sus botones
    private Map<JButton, Map<String, String>> perfilData = new HashMap<>();
    private List<JButton> botonesVer = new ArrayList<>();
 
    public BancoADN_Grupo6_Pant_BuscarPerfil() {
        setTitle("Banco ADN - Buscar Perfil");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 750);
        setLocationRelativeTo(null);
        configurarDiseno();
    }
 
    private void configurarDiseno() {
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().setBackground(grisFondoCuerpo);
 
        // ENCABEZADO
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(celesteEncabezado);
        pnlHeader.setPreferredSize(new Dimension(950, 80));
        pnlHeader.setBorder(new EmptyBorder(0, 30, 0, 30));
 
        btnAtras = new JButton("<--");
        btnAtras.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnAtras.setBackground(Color.WHITE);
        
        JLabel lblLogo = new JLabel("<html><font color='#4A90E2'>Simple</font><font color='#D35400'>ADN</font></html>");
        lblLogo.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 32));
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
 
        pnlHeader.add(btnAtras, BorderLayout.WEST);
        pnlHeader.add(lblLogo, BorderLayout.CENTER);
        pnlHeader.add(Box.createRigidArea(new Dimension(60, 0)), BorderLayout.EAST);
 
        this.add(pnlHeader, BorderLayout.NORTH);
 
        // MARCO GRIS REDONDEADO
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(30, 50, 30, 50));
 
        JPanel mainWrapper = new JPanel(new BorderLayout());
        mainWrapper.setBackground(Color.WHITE);
        mainWrapper.setBorder(new LineBorder(grisBordeClaro, 8, true));
        
        JLabel lblTitulo = new JLabel("Buscar Perfil por ID/Nombre", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.PLAIN, 36));
        lblTitulo.setBorder(new EmptyBorder(20, 0, 10, 0));
        mainWrapper.add(lblTitulo, BorderLayout.NORTH);
 
        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setOpaque(false);
        bodyPanel.setBorder(new EmptyBorder(10, 40, 20, 40));
 
        // PANEL DE FILTROS
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        filterPanel.setOpaque(false);
 
        cbFiltro = new JComboBox<>(new String[]{"Nombre", "ID"});
        cbFiltro.setPreferredSize(new Dimension(120, 40));
 
        txtBusqueda = new JTextField("Buscar...");
        txtBusqueda.setPreferredSize(new Dimension(300, 40));
 
        btnBuscar = new JButton("Buscar Perfil");
        btnBuscar.setBackground(azulBoton);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnBuscar.setPreferredSize(new Dimension(150, 40));
 
        filterPanel.add(new JLabel("Filtrar por:"));
        filterPanel.add(cbFiltro);
        filterPanel.add(txtBusqueda);
        filterPanel.add(btnBuscar);
 
        // CONTENEDOR DE RESULTADOS
        resultsContainer = new JPanel();
        resultsContainer.setLayout(new BoxLayout(resultsContainer, BoxLayout.Y_AXIS));
        resultsContainer.setBackground(new Color(245, 245, 245));
 
        JScrollPane scrollPane = new JScrollPane(resultsContainer);
        scrollPane.setBorder(new LineBorder(grisBordeClaro, 1));
 
        bodyPanel.add(filterPanel, BorderLayout.NORTH);
        bodyPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainWrapper.add(bodyPanel, BorderLayout.CENTER);
        container.add(mainWrapper, BorderLayout.CENTER);
        this.add(container, BorderLayout.CENTER);
    }
 
    /**
     * Crea un panel individual para un resultado de búsqueda.
     * Guarda los datos del perfil en el botón "Ver Perfil".
     */
    private JPanel crearPanelResultado(String idPerfil, String nombreCompleto, String codigoSecuencia,
                                       String descripcion, String fechaMuestra) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(1200, 80));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(grisBordeClaro, 1),
                new EmptyBorder(10, 20, 10, 20)
        ));
 
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);
        
        JLabel lblNombre = new JLabel(nombreCompleto);
        lblNombre.setFont(new Font("SansSerif", Font.BOLD, 15));
        
        JLabel lblDatos = new JLabel("Código: " + codigoSecuencia + "  |  Fecha Muestra: " + fechaMuestra);
        lblDatos.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblDatos.setForeground(Color.DARK_GRAY);
 
        infoPanel.add(lblNombre);
        infoPanel.add(lblDatos);
 
        JButton btnVer = new JButton("Ver Perfil");
        btnVer.setBackground(azulBoton);
        btnVer.setForeground(Color.WHITE);
        btnVer.setPreferredSize(new Dimension(100, 30));
 
        // Guardar datos del perfil en el botón
        Map<String, String> datos = new HashMap<>();
        datos.put("idPerfil", idPerfil);
        datos.put("nombreCompleto", nombreCompleto);
        datos.put("codigoSecuencia", codigoSecuencia);
        datos.put("descripcion", descripcion);
        datos.put("fechaMuestra", fechaMuestra);
        perfilData.put(btnVer, datos);
        botonesVer.add(btnVer);
 
        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(btnVer, BorderLayout.EAST);
 
        return panel;
    }
    
    public void configurarAccionesVerPerfil(ActionListener listener) {
        for (JButton btn : botonesVer) {
            // Limpiamos listeners previos por si acaso para no duplicar acciones
            for (ActionListener al : btn.getActionListeners()) {
                btn.removeActionListener(al);
            }
            btn.addActionListener(listener);
        }
    }
 
    // ── Getters para el controlador ────────────────────────
    public String getTipoFiltro() {
        return (String) cbFiltro.getSelectedItem();
    }
 
    public String getTextoBusqueda() {
        return txtBusqueda.getText();
    }
 
    /**
     * Obtiene los datos de un perfil específico desde su botón "Ver Perfil".
     */
    public Map<String, String> getPerfilData(JButton btnVer) {
        return perfilData.get(btnVer);
    }
 
    /**
     * Obtiene todos los botones "Ver Perfil" de los resultados actuales.
     */
    public List<JButton> getBotonesVer() {
        return new ArrayList<>(botonesVer);
    }
 
    // ── Métodos para gestionar resultados ──────────────────
    public void limpiarResultados() {
        resultsContainer.removeAll();
        perfilData.clear();
        botonesVer.clear();
        resultsContainer.revalidate();
        resultsContainer.repaint();
    }
 
    /**
     * Agrega un resultado a la lista de búsqueda.
     * 
     * @param idPerfil ID del perfil
     * @param nombreCompleto Nombre completo del usuario
     * @param codigoSecuencia Código de secuencia genética
     * @param descripcion Descripción del perfil
     * @param fechaMuestra Fecha de toma de muestra
     */
    public void agregarResultado(String idPerfil, String nombreCompleto, String codigoSecuencia,
                                  String descripcion, String fechaMuestra) {
        resultsContainer.add(crearPanelResultado(idPerfil, nombreCompleto, codigoSecuencia,
                                                  descripcion, fechaMuestra));
        resultsContainer.add(Box.createVerticalStrut(10));
        resultsContainer.revalidate();
        resultsContainer.repaint();
    }
 
    // ── Utilidades ─────────────────────────────────────────
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
 
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
 

    @Override
    public void navegarAMenu() {
        this.dispose();
        BancoADN_Grupo6_MenuUsuario menu = new BancoADN_Grupo6_MenuUsuario();
        menu.setVisible(true);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BancoADN_Grupo6_Pant_BuscarPerfil().setVisible(true));
    }
}