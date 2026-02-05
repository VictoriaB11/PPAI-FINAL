package org.example.Vistas;

import org.example.Gestores.GestorRI;
import org.example.Modelos.Empleado;
import org.example.Modelos.EstacionSismologica;
import org.example.Modelos.Estado;
import org.example.Modelos.Sismografo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConfirmacionCierreOrden extends JFrame {

    // Componentes UI
    private JPanel panelPrincipal;
    private JButton btnConfirmar;
    private JButton btnVolver;
    private JFrame ventanaAnterior;

    // Barra Superior
    private JPanel panelTop;
    private JLabel lblUsuario;
    private JLabel lblFechaHora;
    private Timer timerReloj;

    // Lógica
    private GestorRI gestor;
    private List<Estado> listaDeEstados;

    public ConfirmacionCierreOrden(GestorRI gestor,
                                   List<Estado> listaDeEstados,
                                   JFrame ventanaAnterior) {
        this.ventanaAnterior = ventanaAnterior;
        this.gestor = gestor;
        this.listaDeEstados = listaDeEstados;

        setTitle("Confirmar Cierre de Orden");
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
        iniciarReloj();

        gestor.registrarVentana(this);

        // Listeners
        // Delegamos al método del diagrama (Paso 8)
        btnConfirmar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pedirConfirmacionParaCerrarOrdenDeInspeccion();
            }
        });

        btnVolver.addActionListener(e -> {
            // Volvemos al paso anterior
            ventanaAnterior.setVisible(true);
            dispose();
        });


        // Ajustes de ventana
        pack();
        setSize(new Dimension(900, 550));
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /* ================= LÓGICA DE NEGOCIO (Pasos 8 y 9) ================= */

    // Paso 8: Solicita confirmación
    public void pedirConfirmacionParaCerrarOrdenDeInspeccion() {
        // Configura los textos para los botones
        UIManager.put("OptionPane.yesButtonText", "Sí");
        UIManager.put("OptionPane.noButtonText", "No");

        // Muestra un mensaje para confirmar si el usuario desea cerrar la orden
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Está segura/o de que desea cerrar la orden de inspección?",
                "Confirmar cierre",
                JOptionPane.YES_NO_OPTION
        );

        // Si el usuario selecciona "No", se cancela el cierre
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        // Si el usuario confirma, se procede al paso siguiente: ejecutar el cierre
        tomarConfirmacionParaCerrarOrdenDeInspeccion();
    }

    // Paso 9: Ejecuta el cierre
    public void tomarConfirmacionParaCerrarOrdenDeInspeccion() {
        try {
            // Invoca al gestor para validar y ejecutar el cierre de la orden
            boolean exito = gestor.tomarConfirmacionParaCerrarOrdenDeInspeccion(listaDeEstados);

            if (exito) {
                // Obtiene el estado final de la orden de inspección
                String estadoOrden = gestor.getOrdenSeleccionada().getEstado().getNombre();

                // Obtiene el estado final del sismógrafo de forma segura
                EstacionSismologica estacion = gestor.getOrdenSeleccionada().getEstacionSismologica();
                Sismografo sism = gestor.buscarSismografoPorEstacion(estacion);

                String estadoSismografo = (sism != null && sism.getEstadoActual() != null)
                        ? sism.getEstadoActual().getEstado().getNombre()
                        : "Desconocido";

                // Muestra un mensaje de confirmación con los estados finales
                JOptionPane.showMessageDialog(
                        this,
                        "Orden cerrada correctamente.\n\n" +
                                "Estado final de la Orden: " + estadoOrden + "\n" +
                                "Estado final del Sismógrafo: " + estadoSismografo,
                        "Confirmación Final",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Finaliza el caso de uso
                gestor.finCU();
                dispose(); // Cierra la ventana
            } else {
                // Si la validación lógica falla (ej: faltan observaciones)
                JOptionPane.showMessageDialog(this,
                        "No se pudo cerrar la orden. Verifique que haya ingresado observaciones y motivos.",
                        "Validación fallida",
                        JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception e) {
            // CAPTURA DE ERRORES: Esto hará que aparezca el error en pantalla
            e.printStackTrace(); // Imprime en consola para detalles técnicos
            JOptionPane.showMessageDialog(this,
                    "Ocurrió un error técnico al intentar cerrar la orden:\n" + e.getMessage(),
                    "Error del Sistema",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /* ================= BARRA SUPERIOR ================= */

    private void construirBarraSuperior() {
        panelTop = new JPanel(new BorderLayout());
        panelTop.setOpaque(false);
        panelTop.setBorder(new EmptyBorder(12, 16, 10, 16));

        btnVolver = new JButton("←");
        btnVolver.setFont(btnVolver.getFont().deriveFont(Font.BOLD, 20f));
        btnVolver.setFocusPainted(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setContentAreaFilled(false);
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVolver.setToolTipText("Cancelar y volver al menú");

        String nombreMostrar = "Responsable";
        try {
            Empleado empleado = gestor.buscarEmpleadoLogueado();
            if (empleado != null) {
                nombreMostrar = empleado.getNombre() + " " + empleado.getApellido();
            }
        } catch (Exception ignored) {}

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

    /* ================= CONTENIDO CENTRAL ================= */

    private void construirContenidoCentral() {
        JPanel panelCenter = new JPanel(new GridBagLayout());
        panelCenter.setOpaque(false);
        panelCenter.setBorder(new EmptyBorder(20, 20, 20, 20));

        RoundedPanel card = new RoundedPanel(24, new Color(20, 90, 160, 160));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(26, 34, 26, 34));

        JLabel lblTitulo = new JLabel("Confirmación Final");
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 24f));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPaso = new JLabel("Paso 4 de 4");
        lblPaso.setFont(lblPaso.getFont().deriveFont(Font.PLAIN, 13f));
        lblPaso.setForeground(new Color(255, 255, 255, 220));
        lblPaso.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblMensaje = new JLabel("<html><center>Se procederá a cerrar la orden de inspección<br>y actualizar el estado del sismógrafo.</center></html>");
        lblMensaje.setFont(lblMensaje.getFont().deriveFont(Font.PLAIN, 16f));
        lblMensaje.setForeground(Color.WHITE);
        lblMensaje.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnConfirmar = new JButton("Confirmar Cierre");
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setBackground(new Color(255, 255, 255));
        btnConfirmar.setForeground(new Color(20, 90, 160));
        btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirmar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnConfirmar.setMaximumSize(new Dimension(200, 40));

        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(6));
        card.add(lblPaso);
        card.add(Box.createVerticalStrut(30));
        card.add(lblMensaje);
        card.add(Box.createVerticalStrut(30));
        card.add(btnConfirmar);

        panelCenter.add(card);
        panelPrincipal.add(panelCenter, BorderLayout.CENTER);
    }

    /* ================= FONDO ================= */

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

    /* ================= RELOJ ================= */

    private void iniciarReloj() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        lblFechaHora.setText(LocalDateTime.now().format(fmt));
        timerReloj = new Timer(1000, e -> lblFechaHora.setText(LocalDateTime.now().format(fmt)));
        timerReloj.start();
    }

    /* ================= PANELES ================= */

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
            Shape round = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius);
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