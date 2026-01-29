package org.example.Vistas;

import org.example.Gestores.GestorRI;
import org.example.Modelos.*;

import javax.swing.*;
import java.util.List;

public class MenuPrincipal extends JFrame {

    private JPanel panelPrincipal;
    private JButton btnCerrarOrdenInspeccion;

    private Sesion sesionActual;

    public MenuPrincipal(Sesion sesion) {
        this.sesionActual = sesion;

        setTitle("Menú Principal - Red Sísmica");
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        btnCerrarOrdenInspeccion.addActionListener(e -> opcionCerrarOrdenInspeccion());
    }

    private void opcionCerrarOrdenInspeccion() {
        System.out.println("Pantalla: Se ha presionado la opción para cerrar la orden.");
        abrirVentana();
    }

    private void abrirVentana() {

        //  Validación de sesión
        if (sesionActual == null) {
            JOptionPane.showMessageDialog(this, "Sesión nula. No se puede iniciar el Caso de Uso.");
            return;
        }

        GestorRI gestor = new GestorRI();

        // Paso 1: iniciar CU
        gestor.nuevoCierreOrdenInspeccion(this.sesionActual);

        // Paso 2: buscar órdenes
        List<OrdenDeInspeccion> ordenesAMostrar = gestor.buscarOrdenesDeInspeccionRealizadas();
        List<MotivoTipo> motivosGestor = gestor.buscarTiposDeMotivos();
        List<EstadoSismografo> estadosGestor = gestor.getEstadosDisponibles();

        if (!ordenesAMostrar.isEmpty()) {
            new SeleccionOrdenDeInspeccion(gestor, motivosGestor, estadosGestor);
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "No hay órdenes completamente realizadas para cerrar."
            );
        }
    }
}
