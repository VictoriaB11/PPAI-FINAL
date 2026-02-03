package org.example.Vistas;

import org.example.Gestores.GestorRI;
import org.example.Modelos.Empleado;
import org.example.Modelos.MotivoTipo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Pantalla Paso 2: Ingreso de observación de cierre
public class IngresoObservacionCierre extends JFrame {

    // Base
    private JPanel panelPrincipal;
    private JTextArea txtObservacion;
    private JButton btnConfirmar;

    // Botón para volver atrás
    private JButton btnVolver;

    // Barra superior
    private JPanel panelTop;
    private JLabel lblUsuario;
    private JLabel lblFechaHora;
    private Timer timerReloj;

    private GestorRI gestor;
    private List<MotivoTipo> motivosDisponibles;

    public IngresoObservacionCierre(GestorRI gestor, List<MotivoTipo> motivosDisponibles) {
        this.gestor = gestor;
        this.motivosDisponibles = motivosDisponibles;

        setTitle("Ingreso de Observación de Cierre");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setOpaque(false);

        setContentPane(crearFondoConImagen(panelPrincipal));

        construirBarraSuperior();
        construirContenidoCentral();
        iniciarReloj();

        gestor.registrarVentana(this);

        // Listener del botón "Confirmar"
        btnConfirmar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String observacion = txtObservacion.getText().trim();

                if (observacion.isEmpty()) {
                    JOptionPane.showMessageDialog(IngresoObservacionCierre.this,
                            "Debe ingresar una observación válida.",
                            "Dato requerido",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Llamada al metodo público que actúa como puente, como en tu diseño original
                tomarIngresoObservacionCierreInspeccion(observacion);

                // El flujo continúa hacia el siguiente paso.
                if (gestor.habilitarActualizarSituacionSismografo()) {
                    new SeleccionMotivosYComentarios(gestor, motivosDisponibles);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(IngresoObservacionCierre.this,
                            "Ocurrió un error inesperado. Verifique los datos.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Listener del botón "Volver"
        btnVolver.addActionListener(e -> volverAtras());

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

        // Flecha volver
        btnVolver = new JButton("←");
        btnVolver.setFont(btnVolver.getFont().deriveFont(Font.BOLD, 20f));
        btnVolver.setFocusPainted(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setContentAreaFilled(false);
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVolver.setToolTipText("Volver al paso anterior");

        // RECUPERAR USUARIO LOGUEADO
        String nombreMostrar = "Responsable";
        try {
            Empleado empleado = gestor.buscarEmpleadoLogueado();
            if (empleado != null) {
                nombreMostrar = empleado.getNombre() + " " + empleado.getApellido();
            }
        } catch (Exception e) {
            // Si falla, usa default
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

    /* ================= CONTENIDO CENTRAL ================= */

    private void construirContenidoCentral() {
        JPanel panelCenter = new JPanel(new GridBagLayout());
        panelCenter.setOpaque(false);
        panelCenter.setBorder(new EmptyBorder(20, 20, 20, 20));

        RoundedPanel card = new RoundedPanel(24, new Color(20, 90, 160, 160)); // azul glass
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(26, 34, 26, 34));

        JLabel lblTitulo = new JLabel("Observación de Cierre");
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 24f));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPaso = new JLabel("Paso 2 de 4");
        lblPaso.setFont(lblPaso.getFont().deriveFont(Font.PLAIN, 13f));
        lblPaso.setForeground(new Color(255, 255, 255, 220));
        lblPaso.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtObservacion = new JTextArea(5, 30);
        txtObservacion.setLineWrap(true);
        txtObservacion.setWrapStyleWord(true);
        txtObservacion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Margen interno para que el texto no pegue al borde
        txtObservacion.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane scroll = new JScrollPane(txtObservacion);
        scroll.setMaximumSize(new Dimension(520, 140));
        scroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        btnConfirmar = new JButton("Confirmar");

        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setBackground(new Color(255, 255, 255));
        btnConfirmar.setForeground(new Color(20, 90, 160));
        btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Panel de botones (Solo Confirmar a la derecha)
        JPanel panelBotones = new JPanel(new BorderLayout());
        panelBotones.setOpaque(false);
        // Ajustamos el ancho máximo para que coincida con el scroll o un poco menos
        panelBotones.setMaximumSize(new Dimension(520, 40));

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        der.setOpaque(false);
        der.add(btnConfirmar);

        panelBotones.add(der, BorderLayout.EAST);

        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(6));
        card.add(lblPaso);
        card.add(Box.createVerticalStrut(25));
        card.add(scroll);
        card.add(Box.createVerticalStrut(30));
        card.add(panelBotones);

        panelCenter.add(card);
        panelPrincipal.add(panelCenter, BorderLayout.CENTER);
    }

    /* ================= LÓGICA ================= */

    //Este metodo actúa como puente entre la interfaz y el gestor para pasar la observación de cierre.

    public void tomarIngresoObservacionCierreInspeccion(String observacionCierre) {
        // Paso 5: La pantalla informa al gestor la observación.
        gestor.tomarIngresoObservacionCierreInspeccion(observacionCierre);
    }

    private void volverAtras() {
        // Al volver atrás, cancelamos el caso de uso
        gestor.finCU();
        dispose();
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
        timerReloj = new Timer(1000,
                e -> lblFechaHora.setText(LocalDateTime.now().format(fmt)));
        timerReloj.start();
    }

    /* ================= CLASES AUX ================= */

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