package org.example.Vistas;

import org.example.Gestores.GestorRI;
import org.example.Modelos.Estado;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ConfirmacionCierreOrden extends JFrame {

    private JPanel panelPrincipal;
    private JButton btnConfirmar;
    private JButton btnCancelar;
    private GestorRI gestor;
    private List<Estado> listaDeEstados;

    public ConfirmacionCierreOrden(GestorRI gestor, List<Estado> listaDeEstados) {
        this.gestor = gestor;
        this.listaDeEstados = listaDeEstados;

        setTitle("Confirmar Cierre de Orden");
        setSize(400, 200);
        setContentPane(panelPrincipal);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        gestor.registrarVentana(this);

        //  Delegamos al método del diagrama
        btnConfirmar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pedirConfirmacionParaCerrarOrdenDeInspeccion();
            }
        });

        btnCancelar.addActionListener(e -> gestor.finCU());


    }

    // Paso 8: Solicita confirmación
    public void pedirConfirmacionParaCerrarOrdenDeInspeccion() {
        // Configura los textos para los botones

        UIManager.put("OptionPane.yesButtonText", "Sí");
        UIManager.put("OptionPane.noButtonText", "No");

        // Muestra un mensaje para confirmar si el usuario desea cerrar la orden
        int opcion = JOptionPane.showConfirmDialog(
                null,
                "¿Está segura/o de que desea cerrar la orden de inspección?",
                "Confirmar cierre",
                JOptionPane.YES_NO_OPTION
        );

        // Si el usuario selecciona "No", se cancela el cierre y se registra en consola
        if (opcion != JOptionPane.YES_OPTION) {
            System.out.println("[Paso 8] Usuario canceló el cierre de la orden.");
            return;
        }
        // Si el usuario confirma, se procede al paso siguiente: ejecutar el cierre
        tomarConfirmacionParaCerrarOrdenDeInspeccion();
    }

    // Paso 9: Ejecuta el cierre
    public void tomarConfirmacionParaCerrarOrdenDeInspeccion() {
        // Invoca al gestor para validar y ejecutar el cierre de la orden
        boolean exito = gestor.tomarConfirmacionParaCerrarOrdenDeInspeccion(listaDeEstados);

        if (exito) {
            // Obtiene el estado final de la orden de inspección
            String estadoOrden = gestor.getOrdenSeleccionada().getEstado().getNombre();
            // Obtiene el estado final del sismógrafo asociado a la estación sismológica

            String estadoSismografo = gestor.getOrdenSeleccionada()
                    .getEstacionSismologica()
                    .getSismografo()
                    .getEstadoActual()
                    .getEstado()
                    .getNombre();
            // Muestra un mensaje de confirmación con los estados finales

            JOptionPane.showMessageDialog(
                    null,
                    " Orden cerrada correctamente.\n\n" +
                            "Estado final de la Orden: " + estadoOrden + "\n" +
                            "Estado final del Sismógrafo: " + estadoSismografo,
                    "Confirmación Final",
                    JOptionPane.INFORMATION_MESSAGE
            );
            // Finaliza el caso de uso
            gestor.finCU();
        } else {
            // Si la validación falla, se informa al usuario que no se pudo cerrar la orden
            JOptionPane.showMessageDialog(null, " No se pudo cerrar la orden. Revisá los datos.");
        }
    }
}