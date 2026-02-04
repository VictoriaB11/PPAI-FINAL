package org.example.Vistas;

import org.example.Gestores.GestorRI;
import org.example.Modelos.Empleado;
import org.example.Modelos.MotivoTipo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeleccionMotivosYComentarios extends JFrame {

    // Componentes UI
    private JPanel panelPrincipal;
    private JButton btnConfirmar;
    private JButton btnVolver; // Reemplaza a cancelar para volver atrás
    private JPanel contenedorLista; // Panel interno para el scroll
    private JFrame ventanaAnterior;

    // Componentes Barra Superior
    private JPanel panelTop;
    private JLabel lblUsuario;
    private JLabel lblFechaHora;
    private Timer timerReloj;

    // Estructuras de datos
    private List<JCheckBox> checkBoxes;
    private Map<MotivoTipo, JTextField> camposTexto;

    private final List<MotivoTipo> motivosDisponibles;
    private final GestorRI gestor;

    public SeleccionMotivosYComentarios(GestorRI gestor,
                                        List<MotivoTipo> motivosDisponibles,
                                        JFrame ventanaAnterior) {
        this.ventanaAnterior = ventanaAnterior;
        this.gestor = gestor;
        this.motivosDisponibles = motivosDisponibles;
        this.checkBoxes = new ArrayList<>();
        this.camposTexto = new HashMap<>();

        // Configuración Ventana
        setTitle("Seleccionar Motivos y Cargar Comentarios");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Si el usuario cierra con la X, cancelamos el Caso de Uso
                gestor.finCU();
                dispose();
            }
        });

        // Setup Panel Principal y Fondo
        panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setOpaque(false);
        setContentPane(crearFondoConImagen(panelPrincipal));

        // Construcción de UI
        construirBarraSuperior();
        construirContenidoCentral();
        iniciarReloj();

        gestor.registrarVentana(this);

        mostrarMotivosTiposParaSeleccion(motivosDisponibles);
        pedirMotivosTipoParaSeleccionYComentario();

        // Listeners
        btnConfirmar.addActionListener(e -> confirmarSeleccion());
        btnVolver.addActionListener(e -> volverAtras());

        // Ajustes finales
        pack();
        setSize(new Dimension(900, 600));
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
    }

    /* ================= MÉTODOS DEL DIAGRAMA ================= */

    // Paso 6: mostrar motivos en la interfaz.
    public void mostrarMotivosTiposParaSeleccion(List<MotivoTipo> motivos) {
        contenedorLista.removeAll();
        checkBoxes.clear();
        camposTexto.clear();

        for (MotivoTipo motivo : motivos) {
            // Usamos un método auxiliar para crear la fila con estilo, pero la lógica es la misma
            JPanel fila = crearFilaMotivo(motivo);
            contenedorLista.add(fila);
            contenedorLista.add(Box.createVerticalStrut(10)); // Espacio entre filas
        }

        contenedorLista.revalidate();
        contenedorLista.repaint();
    }

    // Paso 6: mostrar la ventana de selección.
    public void pedirMotivosTipoParaSeleccionYComentario() {
        setVisible(true);
    }

    // Paso 7-a: el usuario (RI) realiza la selección de motivos en la interfaz.
    public void tomarSeleccionMotivosTipos(List<MotivoTipo> seleccionados) {
        gestor.tomarSeleccionMotivosTipos(seleccionados);
    }

    // Paso 7-b: el usuario (RI) ingresa comentarios para los motivos seleccionados.
    public void tomarIngresoComentarioMotivo(Map<MotivoTipo, String> comentarios) {
        gestor.tomarIngresoComentarioMotivo(comentarios);
    }

    /* ================= LÓGICA DE CONFIRMACIÓN ================= */

    private void confirmarSeleccion() {
        Map<MotivoTipo, String> motivosYComentarios = new HashMap<>();
        boolean algunComentarioVacio = false;
        boolean comentarioSinSeleccion = false;

        // Iteramos sobre los checkboxes guardados
        for (JCheckBox checkBox : checkBoxes) {
            MotivoTipo motivo = (MotivoTipo) checkBox.getClientProperty("motivo");
            JTextField campo = camposTexto.get(motivo);
            String comentario = campo.getText();

            if (checkBox.isSelected()) {
                if (comentario == null || comentario.trim().isEmpty()) {
                    algunComentarioVacio = true;
                } else {
                    motivosYComentarios.put(motivo, comentario.trim());
                }
            } else {
                // Validación: Si no está seleccionado pero tiene texto
                if (comentario != null && !comentario.trim().isEmpty()) {
                    comentarioSinSeleccion = true;
                }
            }
        }

        // Validaciones
        if (motivosYComentarios.isEmpty() && !algunComentarioVacio) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar al menos un motivo.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (algunComentarioVacio) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un comentario para CADA motivo seleccionado.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (comentarioSinSeleccion) {
            JOptionPane.showMessageDialog(this, "No puede ingresar comentarios en motivos que no fueron seleccionados.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Paso 7: Llamadas a los métodos puente
        List<MotivoTipo> seleccionados = new ArrayList<>(motivosYComentarios.keySet());

        tomarSeleccionMotivosTipos(seleccionados);
        tomarIngresoComentarioMotivo(motivosYComentarios);

        // Avanzar al siguiente paso (Confirmación)
        new ConfirmacionCierreOrden(gestor, gestor.buscarEstadosOrden(), this); // 🔹
        setVisible(false);

    }

    private void volverAtras() {
        // Volvemos al paso anterior
        ventanaAnterior.setVisible(true);
        dispose();
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
        btnVolver.setToolTipText("Cancelar y Salir");

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
        panelCenter.setBorder(new EmptyBorder(16, 20, 20, 20));

        RoundedPanel card = new RoundedPanel(24, new Color(20, 90, 160, 160));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(22, 26, 18, 26));

        // Header de la tarjeta
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel("Motivos del cierre");
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 24f));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPaso = new JLabel("Paso 3 de 4");
        lblPaso.setFont(lblPaso.getFont().deriveFont(Font.PLAIN, 13f));
        lblPaso.setForeground(new Color(255, 255, 255, 220));
        lblPaso.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hint = new JLabel("✔ Seleccioná al menos uno. ✍️ Comentario obligatorio si está seleccionado.");
        hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 13.5f));
        hint.setForeground(new Color(255, 255, 255, 220));
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(lblTitulo);
        header.add(Box.createVerticalStrut(6));
        header.add(lblPaso);
        header.add(Box.createVerticalStrut(15));
        header.add(hint);

        // Contenedor de la lista (Scroll)
        contenedorLista = new JPanel();
        contenedorLista.setOpaque(false);
        contenedorLista.setLayout(new BoxLayout(contenedorLista, BoxLayout.Y_AXIS));
        contenedorLista.setBorder(new EmptyBorder(14, 0, 10, 0));

        JScrollPane scroll = new JScrollPane(contenedorLista);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        // Botón Confirmar
        btnConfirmar = new JButton("Confirmar");
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setBackground(new Color(255, 255, 255));
        btnConfirmar.setForeground(new Color(20, 90, 160));
        btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnConfirmar.setPreferredSize(new Dimension(140, 34));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);
        panelBotones.add(btnConfirmar);

        card.add(header, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        card.add(panelBotones, BorderLayout.SOUTH);

        panelCenter.add(card);
        panelPrincipal.add(panelCenter, BorderLayout.CENTER);
    }


    // Metodo auxiliar para crear filas bonitas (Estética)
    private JPanel crearFilaMotivo(MotivoTipo motivo) {
        JPanel fila = new RoundedPanel(18, new Color(255, 255, 255, 30));
        fila.setLayout(new GridBagLayout());
        fila.setBorder(new EmptyBorder(12, 14, 12, 14));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Checkbox
        JCheckBox checkBox = new JCheckBox(motivo.getDescripcion());
        checkBox.setOpaque(false);
        checkBox.setForeground(Color.WHITE);
        checkBox.setFont(checkBox.getFont().deriveFont(Font.BOLD, 13.5f));
        checkBox.putClientProperty("motivo", motivo); // Guardamos referencia para recuperarla luego

        // TextField
        JTextField textField = new JTextField();
        textField.setColumns(22);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Guardamos referencias
        checkBoxes.add(checkBox);
        camposTexto.put(motivo, textField);

        // Layout
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.45;
        fila.add(checkBox, gbc);

        JLabel lbl = new JLabel("Comentario:");
        lbl.setForeground(new Color(255, 255, 255, 230));
        gbc.gridx = 1; gbc.weightx = 0.15;
        fila.add(lbl, gbc);

        gbc.gridx = 2; gbc.weightx = 0.40;
        fila.add(textField, gbc);

        return fila;
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