package org.example.Vistas;

import org.example.Gestores.GestorRI;
import org.example.Modelos.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MenuPrincipal extends JFrame {

    private JPanel panelPrincipal;
    private JButton btnCerrarOrdenInspeccion;
    private JButton btnSalir;

    private Sesion sesionActual;

    // Componentes de UI
    private JLabel lblTitulo;
    private JPanel panelTop;
    private JLabel lblUsuario;
    private JLabel lblFechaHora;
    private Timer timerReloj;

    public MenuPrincipal(Sesion sesion) {
        this.sesionActual = sesion;

        setTitle("Menú Principal - Red Sísmica");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Configuración del panel principal con layout
        panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setOpaque(false);

        // Establecemos el fondo con imagen (o color sólido si no hay imagen)
        setContentPane(crearFondoConImagen(panelPrincipal));

        construirBarraSuperior();
        construirContenidoCentral();
        iniciarReloj();

        // Listeners
        btnCerrarOrdenInspeccion.addActionListener(e -> opcionCerrarOrdenInspeccion());
        btnSalir.addActionListener(e -> salirAplicacion());

        // Ajustes finales de la ventana
        pack();
        setSize(new Dimension(900, 550));
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /* ================= BARRA SUPERIOR ================= */

    private void construirBarraSuperior() {
        panelTop = new JPanel(new BorderLayout());
        panelTop.setOpaque(false);
        panelTop.setBorder(new EmptyBorder(12, 16, 10, 16));

        String nombre = "Operador";
        String apellido = "";

        if (sesionActual != null && sesionActual.obtenerUsuarioLogueado() != null) {
            nombre = sesionActual.obtenerUsuarioLogueado().getNombre();
            try {
                apellido = sesionActual.obtenerUsuarioLogueado().getApellido();
                if (apellido == null) apellido = "";
            } catch (Exception ignored) {}
        }

        String nombreCompleto = apellido.isBlank() ? nombre : nombre + " " + apellido;

        lblUsuario = new JLabel("👤 " + nombreCompleto);
        lblUsuario.setFont(lblUsuario.getFont().deriveFont(Font.BOLD, 14f));
        lblUsuario.setForeground(Color.WHITE);

        lblFechaHora = new JLabel();
        lblFechaHora.setFont(lblFechaHora.getFont().deriveFont(Font.PLAIN, 14f));
        lblFechaHora.setForeground(Color.WHITE);

        panelTop.add(lblUsuario, BorderLayout.WEST);
        panelTop.add(lblFechaHora, BorderLayout.EAST);

        panelPrincipal.add(panelTop, BorderLayout.NORTH);
    }

    /* ================= CONTENIDO CENTRAL ================= */

    private void construirContenidoCentral() {
        JPanel panelCenter = new JPanel(new GridBagLayout());
        panelCenter.setOpaque(false);
        panelCenter.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel redondeado tipo "tarjeta"
        RoundedPanel card = new RoundedPanel(
                24,
                new Color(20, 90, 160, 160) // Azul semitransparente
        );
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(26, 34, 26, 34));

        lblTitulo = new JLabel("Red Sísmica");
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 24f));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Botón principal estilizado
        btnCerrarOrdenInspeccion = new JButton("Cerrar Orden de Inspección");
        btnCerrarOrdenInspeccion.setMaximumSize(new Dimension(360, 42));
        btnCerrarOrdenInspeccion.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCerrarOrdenInspeccion.setFocusPainted(false);
        btnCerrarOrdenInspeccion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrarOrdenInspeccion.setBackground(new Color(255, 255, 255));
        btnCerrarOrdenInspeccion.setForeground(new Color(20, 90, 160));

        // Botón salir
        btnSalir = new JButton("Salir");
        btnSalir.setMaximumSize(new Dimension(100, 30));
        btnSalir.setFocusPainted(false);
        btnSalir.setBackground(new Color(200, 50, 50));
        btnSalir.setForeground(new Color(20, 90, 160));

        // Panel para alinear el botón salir a la derecha dentro de la tarjeta
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setOpaque(false);
        panelBotones.setMaximumSize(new Dimension(1000, 40)); // Asegura que no se expanda verticalmente
        panelBotones.add(btnSalir);

        // Agregamos componentes a la tarjeta
        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(30)); // Espacio
        card.add(btnCerrarOrdenInspeccion);
        card.add(Box.createVerticalStrut(30)); // Espacio
        card.add(panelBotones);

        panelCenter.add(card);
        panelPrincipal.add(panelCenter, BorderLayout.CENTER);
    }

    /* ================= FONDO ================= */

    private JPanel crearFondoConImagen(JPanel contenido) {
        // Intenta cargar una imagen, si no existe usa un color sólido
        java.net.URL imgUrl = getClass().getResource("/img/fondo_sismico.jpg");
        Image image = null;
        if (imgUrl != null) {
            image = new ImageIcon(imgUrl).getImage();
        }

        return new BackgroundImagePanel(image, new Color(0, 0, 0, 140), contenido);
    }

    /* ================= RELOJ ================= */

    private void iniciarReloj() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        lblFechaHora.setText(LocalDateTime.now().format(fmt));
        timerReloj = new Timer(1000, e ->
                lblFechaHora.setText(LocalDateTime.now().format(fmt)));
        timerReloj.start();
    }

    /* ================= ACCIONES ================= */

    private void opcionCerrarOrdenInspeccion() {
        // Deshabilitamos botón y mostramos cursor de espera
        btnCerrarOrdenInspeccion.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        // Usamos SwingWorker para no congelar la UI mientras consulta la BD
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private GestorRI gestor;
            private List<OrdenDeInspeccion> ordenes;
            private List<MotivoTipo> motivos;
            private List<EstadoSismografo> estados;

            @Override
            protected Void doInBackground() {
                // Validación de sesión
                if (sesionActual == null) {
                    return null;
                }

                gestor = new GestorRI();

                // Paso 1: iniciar CU
                gestor.nuevoCierreOrdenInspeccion(sesionActual);

                // Paso 2: buscar datos necesarios
                ordenes = gestor.buscarOrdenesDeInspeccionRealizadas();
                motivos = gestor.buscarTiposDeMotivos();
                estados = gestor.getEstadosDisponibles(); // Nota: esto puede ser null si no se cargó antes, el gestor lo maneja

                return null;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                btnCerrarOrdenInspeccion.setEnabled(true);

                if (sesionActual == null) {
                    JOptionPane.showMessageDialog(MenuPrincipal.this, "Sesión nula. No se puede iniciar el Caso de Uso.");
                    return;
                }

                if (ordenes != null && !ordenes.isEmpty()) {
                    // Abrimos la siguiente pantalla con los datos recuperados
                    new SeleccionOrdenDeInspeccion(gestor, motivos, estados);
                } else {
                    JOptionPane.showMessageDialog(
                            MenuPrincipal.this,
                            "No hay órdenes completamente realizadas para cerrar.",
                            "Sin resultados",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void salirAplicacion() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Desea salir de la aplicación?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            if (timerReloj != null) timerReloj.stop();
            dispose();
            System.exit(0);
        }
    }

    /* ================= CLASES AUXILIARES PARA DISEÑO ================= */

    // Panel con bordes redondeados
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

            g2.setColor(new Color(255, 255, 255, 80)); // Borde sutil
            g2.draw(round);

            g2.dispose();
        }
    }

    // Panel con imagen de fondo y superposición de color
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

            // Si hay imagen, la dibuja escalada
            if (image != null) {
                g2.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            } else {
                // Si no hay imagen, usa un degradado por defecto
                GradientPaint gp = new GradientPaint(0, 0, new Color(40, 40, 60),
                        0, getHeight(), new Color(10, 10, 20));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            // Dibuja la capa de color semitransparente encima
            g2.setColor(overlay);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }
}