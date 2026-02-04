package org.example.Vistas;

import org.example.Gestores.GestorRI;
import org.example.Modelos.Empleado; // Import necesario
import org.example.Modelos.EstadoSismografo;
import org.example.Modelos.MotivoTipo;
import org.example.Modelos.OrdenDeInspeccion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Pantalla para seleccionar una orden de inspección (Paso 1 del wizard)
public class SeleccionOrdenDeInspeccion extends JFrame {

    private JPanel panelPrincipal;
    private JComboBox<OrdenDeInspeccion> comboOrdenes;
    private JButton btnConfirmar;
    private JButton btnVolver;
    private JFrame ventanaAnterior;

    // Componentes de Estilo
    private JPanel panelTop;
    private JLabel lblUsuario;
    private JLabel lblFechaHora;
    private Timer timerReloj;

    private GestorRI gestor;
    private List<MotivoTipo> motivos;
    private List<EstadoSismografo> estados;

    public SeleccionOrdenDeInspeccion(GestorRI gestor,
                                      List<MotivoTipo> motivos,
                                      List<EstadoSismografo> estados,
                                      JFrame ventanaAnterior) {
        this.gestor = gestor;
        this.motivos = motivos;
        this.estados = estados;
        this.ventanaAnterior = ventanaAnterior;

        setTitle("Seleccionar Orden de Inspección");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Si el usuario cierra con la X, cancelamos el Caso de Uso
                gestor.finCU();
                dispose();
            }
        });

        panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setOpaque(false);

        setContentPane(crearFondoConImagen(panelPrincipal));

        construirBarraSuperior();
        construirContenidoCentral();

        gestor.registrarVentana(this);

        mostrarOrdenesDeInspeccionRealizadas();

        // Validar estado inicial del botón confirmar
        btnConfirmar.setEnabled(comboOrdenes.getItemCount() > 0);

        // Listeners
        btnConfirmar.addActionListener(e -> tomarSelecOrdenDeInspeccion());
        btnVolver.addActionListener(e -> volverAtras());
        comboOrdenes.addActionListener(e ->
                btnConfirmar.setEnabled(comboOrdenes.getSelectedItem() != null));

        iniciarReloj();

        // Ajustes de ventana
        pack();
        setSize(new Dimension(900, 550));
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);

        // Llamada al método explícito como lo requiere el diseño
        pedirOrdenesDeInspeccionRealizadas();
    }

    // =========================================================
    // Barra superior: flecha + usuario + fecha/hora
    // =========================================================
    private void construirBarraSuperior() {
        panelTop = new JPanel(new BorderLayout());
        panelTop.setOpaque(false);
        panelTop.setBorder(new EmptyBorder(12, 16, 10, 16));

        // Flecha volver
        btnVolver = new JButton("←");
        btnVolver.setFont(btnVolver.getFont().deriveFont(Font.BOLD, 20f));
        btnVolver.setFocusPainted(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setContentAreaFilled(false);
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVolver.setToolTipText("Volver al menú principal");

        // --- RECUPERAR USUARIO LOGUEADO ---
        String nombreMostrar = "Responsable";
        try {
            Empleado empleado = gestor.buscarEmpleadoLogueado();
            if (empleado != null) {
                nombreMostrar = empleado.getNombre() + " " + empleado.getApellido();
            }
        } catch (Exception e) {
            // Si falla algo, mantenemos el default
        }

        lblUsuario = new JLabel("👤 " + nombreMostrar);
        lblUsuario.setFont(lblUsuario.getFont().deriveFont(Font.BOLD, 14f));
        lblUsuario.setForeground(Color.WHITE);

        JPanel panelIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelIzq.setOpaque(false);
        panelIzq.add(btnVolver);
        panelIzq.add(lblUsuario);

        lblFechaHora = new JLabel();
        lblFechaHora.setFont(lblFechaHora.getFont().deriveFont(Font.PLAIN, 14f));
        lblFechaHora.setForeground(Color.WHITE);

        panelTop.add(panelIzq, BorderLayout.WEST);
        panelTop.add(lblFechaHora, BorderLayout.EAST);

        panelPrincipal.add(panelTop, BorderLayout.NORTH);
    }

    // =========================================================
    // Card central (Diseño de tarjeta translúcida)
    // =========================================================
    private void construirContenidoCentral() {
        JPanel panelCenter = new JPanel(new GridBagLayout());
        panelCenter.setOpaque(false);
        panelCenter.setBorder(new EmptyBorder(20, 20, 20, 20));

        RoundedPanel card = new RoundedPanel(24, new Color(20, 90, 160, 160));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(26, 34, 26, 34));

        JLabel lblTitulo = new JLabel("Seleccionar Orden");
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 24f));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPaso = new JLabel("Paso 1 de 4");
        lblPaso.setFont(lblPaso.getFont().deriveFont(Font.PLAIN, 13f));
        lblPaso.setForeground(new Color(255, 255, 255, 220));
        lblPaso.setAlignmentX(Component.CENTER_ALIGNMENT);

        comboOrdenes = new JComboBox<>();
        comboOrdenes.setMaximumSize(new Dimension(420, 32));
        comboOrdenes.setAlignmentX(Component.CENTER_ALIGNMENT);
        comboOrdenes.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Estilo de botones
        btnConfirmar = new JButton("Confirmar");

        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setBackground(new Color(255, 255, 255));
        btnConfirmar.setForeground(new Color(20, 90, 160));
        btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Panel de botones (Solo Confirmar a la derecha)
        JPanel panelBotones = new JPanel(new BorderLayout());
        panelBotones.setOpaque(false);
        panelBotones.setMaximumSize(new Dimension(420, 40));

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        der.setOpaque(false);
        der.add(btnConfirmar);

        panelBotones.add(der, BorderLayout.EAST);

        // Armado de la tarjeta
        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(6));
        card.add(lblPaso);
        card.add(Box.createVerticalStrut(25));
        card.add(comboOrdenes);
        card.add(Box.createVerticalStrut(30));
        card.add(panelBotones);

        panelCenter.add(card);
        panelPrincipal.add(panelCenter, BorderLayout.CENTER);
    }

    // =========================================================
    // Fondo
    // =========================================================
    private JPanel crearFondoConImagen(JPanel contenido) {
        java.net.URL url = getClass().getResource("/img/fondo_sismico.jpg");

        if (url == null) {
            JPanel fallback = new JPanel(new BorderLayout());
            fallback.setBackground(new Color(20, 30, 50));
            fallback.add(contenido, BorderLayout.CENTER);
            return fallback;
        }

        ImageIcon icon = new ImageIcon(url);
        return new BackgroundImagePanel(icon.getImage(), new Color(0, 0, 0, 140), contenido);
    }

    // =========================================================
    // Reloj
    // =========================================================
    private void iniciarReloj() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        lblFechaHora.setText(LocalDateTime.now().format(fmt));
        timerReloj = new Timer(1000, e ->
                lblFechaHora.setText(LocalDateTime.now().format(fmt)));
        timerReloj.start();
    }

    // =========================================================
    // Lógica de Negocio
    // =========================================================
    public void mostrarOrdenesDeInspeccionRealizadas() {
        comboOrdenes.removeAllItems();
        List<OrdenDeInspeccion> ordenes = gestor.buscarOrdenesDeInspeccionRealizadas();

        for (OrdenDeInspeccion orden : ordenes) {
            comboOrdenes.addItem(orden);
        }

        if (ordenes.isEmpty()) {
            btnConfirmar.setEnabled(false);
            JOptionPane.showMessageDialog(this,
                    "No hay órdenes realizadas disponibles para cerrar.",
                    "Sin resultados",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    //Este metodo es el responsable final de hacer visible la ventana para que el usuario pueda interactuar.

    public void pedirOrdenesDeInspeccionRealizadas() {
        setVisible(true);
    }

    public void tomarSelecOrdenDeInspeccion() {
        OrdenDeInspeccion ordenSeleccionada =
                (OrdenDeInspeccion) comboOrdenes.getSelectedItem();

        if (ordenSeleccionada != null) {
            gestor.tomarSelecOrdenDeInspeccion(this, ordenSeleccionada);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar una orden de inspección.");
        }
    }

    public void pedirIngresoObservacionDeCierre() {
        // Pasamos a la siguiente pantalla
        new IngresoObservacionCierre(gestor, motivos, this);
        setVisible(false);

    }

    private void volverAtras() {
        ventanaAnterior.setVisible(true); // Vuelve al menú
        dispose();
    }


    // =========================================================
    // Clases auxiliares para diseño
    // =========================================================
    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color color;

        public RoundedPanel(int radius, Color color) {
            this.radius = radius;
            this.color = color;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Shape round = new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), radius, radius);

            g2.setColor(color);
            g2.fill(round);
            g2.setColor(new Color(255, 255, 255, 80));
            g2.draw(round);
            g2.dispose();
        }
    }

    private static class BackgroundImagePanel extends JPanel {
        private final Image image;
        private final Color overlay;
        private final JPanel content;

        public BackgroundImagePanel(Image image, Color overlay, JPanel content) {
            this.image = image;
            this.overlay = overlay;
            this.content = content;
            setLayout(new BorderLayout());
            add(content, BorderLayout.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            g2.setColor(overlay);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }
}