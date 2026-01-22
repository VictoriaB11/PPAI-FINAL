package org.example.Vistas;

import org.example.Gestores.GestorRI;
import org.example.Modelos.MotivoTipo;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SeleccionMotivosYComentarios extends JFrame {

    private JPanel panelPrincipal;
    private JButton btnConfirmar;
    private JButton btnCancelar;

    private List<JCheckBox> checkBoxes;
    private Map<MotivoTipo, JTextField> camposTexto;

    private List<MotivoTipo> motivosDisponibles;
    private GestorRI gestor;

    public SeleccionMotivosYComentarios(GestorRI gestor, List<MotivoTipo> motivosDisponibles) {
        this.gestor = gestor;
        this.motivosDisponibles = motivosDisponibles;
        this.checkBoxes = new ArrayList<>();
        this.camposTexto = new HashMap<>();

        setTitle("Seleccionar Motivos y Cargar Comentarios");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(panelPrincipal);
        setContentPane(scrollPane);

        btnConfirmar = new JButton("Confirmar");
        btnCancelar = new JButton("Cancelar");

        gestor.registrarVentana(this);

        // Llamadas explícitas del diagrama
        mostrarMotivosTiposParaSeleccion(motivosDisponibles);
        pedirMotivosTipoParaSeleccionYComentario();

        // Acción Confirmar
        btnConfirmar.addActionListener(e -> {
            Map<MotivoTipo, String> motivosYComentarios = new HashMap<>();
            boolean algunComentarioVacio = false;
            boolean comentarioSinSeleccion = false;

            for (int i = 0; i < checkBoxes.size(); i++) {
                JCheckBox checkBox = checkBoxes.get(i);
                MotivoTipo motivo = motivosDisponibles.get(i);
                JTextField campo = camposTexto.get(motivo);
                String comentario = campo.getText();

                if (checkBox.isSelected()) {
                    if (comentario == null || comentario.trim().isEmpty()) {
                        algunComentarioVacio = true;
                    } else {
                        motivosYComentarios.put(motivo, comentario.trim());
                    }
                } else {
                    // Si no está seleccionado pero tiene comentario, es un error
                    if (comentario != null && !comentario.trim().isEmpty()) {
                        comentarioSinSeleccion = true;
                    }
                }
            }

            if (motivosYComentarios.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debe seleccionar al menos un motivo.");
                return;
            }

            if (algunComentarioVacio) {
                JOptionPane.showMessageDialog(null, "Debe ingresar un comentario para CADA motivo seleccionado.");
                return;
            }

            if (comentarioSinSeleccion) {
                JOptionPane.showMessageDialog(null, "No puede ingresar comentarios en motivos que no fueron seleccionados.");
                return;
            }


            // Paso 7
            List<MotivoTipo> seleccionados = new ArrayList<>(motivosYComentarios.keySet());

            tomarSeleccionMotivosTipos(seleccionados);
            tomarIngresoComentarioMotivo(motivosYComentarios);

            new ConfirmacionCierreOrden(gestor, gestor.getEstadosDisponibles());
            dispose();
        });

        // Acción Cancelar
        btnCancelar.addActionListener(e -> gestor.finCU());
    }

    //Paso 6: mostrar motivos en la interfaz.
    /**
     * Este metodo construye dinámicamente la interfaz gráfica para que el usuario
     * pueda seleccionar uno o más motivos y escribir un comentario asociado a cada uno.
     * Por cada motivo recibido, se crea una fila con un JCheckBox (para seleccionar)
     * y un JTextField (para ingresar el comentario).
     */
    public void mostrarMotivosTiposParaSeleccion(List<MotivoTipo> motivos) {
        panelPrincipal.removeAll();
        checkBoxes.clear();
        camposTexto.clear();

        for (MotivoTipo motivo : motivos) {
            JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT));

            JCheckBox checkBox = new JCheckBox(motivo.getDescripcion());
            JTextField textField = new JTextField(20);

            checkBoxes.add(checkBox);
            camposTexto.put(motivo, textField);

            fila.add(checkBox);
            fila.add(new JLabel("Comentario:"));
            fila.add(textField);

            panelPrincipal.add(fila);
        }

        // Panel de botones
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnConfirmar);
        panelBotones.add(btnCancelar);
        panelPrincipal.add(panelBotones);

        panelPrincipal.revalidate();
        panelPrincipal.repaint();
    }

    //Paso 6: mostrar la ventana de selección.
    /**
     * Este metodo hace visible la ventana que contiene los motivos y campos de comentario.
     * Es llamado luego de construir la interfaz con los motivos disponibles.
     */

    public void pedirMotivosTipoParaSeleccionYComentario() {
        setVisible(true);
    }

    // Paso 7-a: el usuario (RI) realiza la selección de motivos en la interfaz.
    /**
     * Este metodo representa la interacción RI -> PantallaRI : tomarSeleccionMotivosTipos()
     * y luego transmite esa selección al gestor para su procesamiento.
     *
     */
    public void tomarSeleccionMotivosTipos(List<MotivoTipo> seleccionados) {
        gestor.tomarSeleccionMotivosTipos(seleccionados);
    }

    //Paso 7-b: el usuario (RI) ingresa comentarios para los motivos seleccionados.
    /**
     * Este metodo representa la interacción RI -> PantallaRI : tomarIngresoComentarioMotivo()
     * y luego transmite esos comentarios al gestor para su registro.
     *
     */
    public void tomarIngresoComentarioMotivo(Map<MotivoTipo, String> comentarios) {
        gestor.tomarIngresoComentarioMotivo(comentarios);
    }
}