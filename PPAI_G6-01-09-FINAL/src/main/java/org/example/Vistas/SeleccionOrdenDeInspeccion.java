package org.example.Vistas;

import org.example.Gestores.GestorRI;
import org.example.Modelos.EstadoSismografo;
import org.example.Modelos.MotivoTipo;
import org.example.Modelos.OrdenDeInspeccion;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

// Esta clase representa la pantalla para seleccionar una orden de inspección
public class SeleccionOrdenDeInspeccion extends JFrame {

    // Componentes de la interfaz
    private JPanel panelPrincipal;
    private JComboBox<OrdenDeInspeccion> comboOrdenes;
    private JButton btnConfirmar;
    private JButton btnCancelar;

    // Referencia al gestor y listas necesarias
    private GestorRI gestor;
    private List<MotivoTipo> motivos;
    private List<EstadoSismografo> estadoSismografos;

    // Constructor principal
    public SeleccionOrdenDeInspeccion(GestorRI gestor, List<MotivoTipo> motivos, List<EstadoSismografo> estadoSismografos) {
        this.gestor = gestor;
        this.motivos = motivos;
        this.estadoSismografos = estadoSismografos;

        // Configuración de la ventana
        setTitle("Seleccionar Orden de Inspección");
        setSize(400, 200);
        setContentPane(panelPrincipal);
        setLocationRelativeTo(null); // Centra la ventana
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true); // Muestra la ventana
        gestor.registrarVentana(this);
        mostrarOrdenesDeInspeccionRealizadas();
        pedirOrdenesDeInspeccionRealizadas();



        // Configuración del botón "Confirmar"
        btnConfirmar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tomarSelecOrdenDeInspeccion(); // Solo informa al gestor
                // Ya no se crea la vista directamente acá
            }
        });

        btnCancelar.addActionListener(e -> gestor.finCU());

        setVisible(true); // Muestra la ventana al final
    }


    public void mostrarOrdenesDeInspeccionRealizadas() {
        comboOrdenes.removeAllItems();
        List<OrdenDeInspeccion> ordenes = gestor.buscarOrdenesDeInspeccionRealizadas();
        for (OrdenDeInspeccion orden : ordenes) {
            comboOrdenes.addItem(orden); // Usa toString()
        }
    }

    public void pedirOrdenesDeInspeccionRealizadas() {
        setVisible(true);
    }


    public void tomarSelecOrdenDeInspeccion() {
        OrdenDeInspeccion ordenSeleccionada = (OrdenDeInspeccion) comboOrdenes.getSelectedItem();
        if (ordenSeleccionada != null) {
            gestor.tomarSelecOrdenDeInspeccion(this, ordenSeleccionada);
        } else {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una orden de inspección.");
        }
    }

    public void pedirIngresoObservacionDeCierre() {
        new IngresoObservacionCierre(gestor, motivos);
        dispose(); // Cerrás esta pantalla después de abrir la siguiente
    }
}
