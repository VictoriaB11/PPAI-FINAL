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
        GestorRI gestor = new GestorRI();

        // Paso 1 (ahora solo sesión)
        gestor.nuevoCierreOrdenInspeccion(this.sesionActual);

        // Paso 2
        List<OrdenDeInspeccion> ordenesAMostrar = gestor.buscarOrdenesDeInspeccionRealizadas();
        List<MotivoTipo> motivosGestor = gestor.buscarTiposDeMotivos();
        List<Estado> estadosGestor = gestor.getEstadosDisponibles();

        if (!ordenesAMostrar.isEmpty()) {
            new SeleccionOrdenDeInspeccion(gestor, motivosGestor, estadosGestor);
        } else {
            JOptionPane.showMessageDialog(this, "No hay órdenes completamente realizadas para cerrar.");
        }
    }
}
