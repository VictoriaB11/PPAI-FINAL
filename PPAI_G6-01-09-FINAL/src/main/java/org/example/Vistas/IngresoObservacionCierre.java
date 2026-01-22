package org.example.Vistas;

import org.example.Gestores.GestorRI;
import org.example.Modelos.MotivoTipo;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

// Pantalla para ingresar la observación de cierre de la orden seleccionada
public class IngresoObservacionCierre extends JFrame {

    // Componentes de la vista
    private JPanel panelPrincipal;
    private JTextArea txtObservacion;
    private JButton btnConfirmar;
    private JButton btnCancelar;
    private GestorRI gestor;
    private List<MotivoTipo> motivosDisponibles;

    // Constructor de la pantalla, recibe el gestor y la lista de motivos disponibles
    public IngresoObservacionCierre(GestorRI gestor, List<MotivoTipo> motivosDisponibles) {
        this.gestor = gestor;
        this.motivosDisponibles = motivosDisponibles;

        // Configuración básica de la ventana
        setTitle("Ingreso de Observación de Cierre");
        setSize(400, 300);
        setContentPane(panelPrincipal);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        gestor.registrarVentana(this);

        // Evento del botón confirmar
        //Paso 5: RI ingresa la observacion de cierre
        // Evento del botón confirmar
        btnConfirmar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Paso 5: RI escribe la observación
                String observacion = txtObservacion.getText().trim();

                if (observacion.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Debe ingresar una observación válida.");
                    return;
                }

                // Paso 5: RI informa al gestor la observación de cierre (a través del metodo puente)
                tomarIngresoObservacionCierreInspeccion(observacion);

                // Paso 6: El gestor decide si se habilita la actualización de situación del sismógrafo
                if (gestor.habilitarActualizarSituacionSismografo()) {
                    new SeleccionMotivosYComentarios(gestor, motivosDisponibles);
                    dispose(); // Cerramos esta ventana
                } else {
                    JOptionPane.showMessageDialog(null, "Ocurrió un error inesperado. Verifique los datos.");
                }
            }
        });


        btnCancelar.addActionListener(e -> gestor.finCU());

    }

    public void tomarIngresoObservacionCierreInspeccion(String observacionCierre) {
        gestor.tomarIngresoObservacionCierreInspeccion(observacionCierre);
    }

}