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

            for (JCheckBox checkBox : checkBoxes) {

                // Recupero el motivo asociado a ESTE checkbox (sin depender de índices)
                MotivoTipo motivo = (MotivoTipo) checkBox.getClientProperty("motivo");

                JTextField campo = camposTexto.get(motivo);
                String comentario = (campo != null) ? campo.getText().trim() : "";

                if (checkBox.isSelected()) {
                    if (comentario.isEmpty()) {
                        algunComentarioVacio = true;
                    } else {
                        motivosYComentarios.put(motivo, comentario);
                    }
                } else {
                    // Si no está seleccionado pero tiene comentario, es error
                    if (!comentario.isEmpty()) {
                        comentarioSinSeleccion = true;
                    }
                }
            }

            if (motivosYComentarios.isEmpty()) {
                JOptionPane.showMessageDialog(SeleccionMotivosYComentarios.this,
                        "Debe seleccionar al menos un motivo.");
                return;
            }

            if (algunComentarioVacio) {
                JOptionPane.showMessageDialog(SeleccionMotivosYComentarios.this,
                        "Debe ingresar un comentario para CADA motivo seleccionado.");
                return;
            }

            if (comentarioSinSeleccion) {
                JOptionPane.showMessageDialog(SeleccionMotivosYComentarios.this,
                        "No puede ingresar comentarios en motivos que no fueron seleccionados.");
                return;
            }

            // Paso 7
            List<MotivoTipo> seleccionados = new ArrayList<>(motivosYComentarios.keySet());
            tomarSeleccionMotivosTipos(seleccionados);
            tomarIngresoComentarioMotivo(motivosYComentarios);

            new ConfirmacionCierreOrden(gestor, gestor.buscarEstadosOrden());
            dispose();
        });

        // Acción Cancelar
        btnCancelar.addActionListener(e -> gestor.finCU());
    }

    // Paso 6: mostrar motivos en la interfaz
    public void mostrarMotivosTiposParaSeleccion(List<MotivoTipo> motivos) {
        panelPrincipal.removeAll();
        checkBoxes.clear();
        camposTexto.clear();

        for (MotivoTipo motivo : motivos) {
            JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT));

            JCheckBox checkBox = new JCheckBox(motivo.getDescripcion());
            // Guardo el motivo asociado al checkbox (para no depender de la posición i)
            checkBox.putClientProperty("motivo", motivo);

            JTextField textField = new JTextField(20);

            checkBoxes.add(checkBox);
            camposTexto.put(motivo, textField);

            fila.add(checkBox);
            fila.add(new JLabel("Comentario:"));
            fila.add(textField);

            panelPrincipal.add(fila);
        }

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnConfirmar);
        panelBotones.add(btnCancelar);
        panelPrincipal.add(panelBotones);

        panelPrincipal.revalidate();
        panelPrincipal.repaint();
    }

    public void pedirMotivosTipoParaSeleccionYComentario() {
        setVisible(true);
    }

    // Paso 7-a
    public void tomarSeleccionMotivosTipos(List<MotivoTipo> seleccionados) {
        gestor.tomarSeleccionMotivosTipos(seleccionados);
    }

    // Paso 7-b
    public void tomarIngresoComentarioMotivo(Map<MotivoTipo, String> comentarios) {
        gestor.tomarIngresoComentarioMotivo(comentarios);
    }
}
