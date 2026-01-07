package org.example.Main;

import org.example.Modelos.*;
import org.example.Vistas.MenuPrincipal;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.example.Persistencia.DbInit; // Para crear tablas al arrancar.
import org.example.Persistencia.SeedData; // Para cargar datos iniciales.
import org.example.Persistencia.MotivoTipoDAO; // Para leer los motivos desde la BD.
import org.example.Persistencia.EstadoDAO;


public class MainCU37Test {
    public static void main(String[] args) {

        // Estados
        EstadoDAO estadoDAO = new EstadoDAO();
        List<Estado> estadosDelSistema = estadoDAO.listarTodos();
        Estado estadoRealizada = buscarEstado(estadosDelSistema, "Completamente Realizada", "Orden de Inspeccion");
        Estado estadoCerrada = buscarEstado(estadosDelSistema, "Cerrada", "Orden de Inspeccion");
        Estado estadoFS = buscarEstado(estadosDelSistema, "Fuera de Servicio", "Sismografo");
        Estado estadoInhabilitado = buscarEstado(estadosDelSistema, "Inhabilitado por inspeccion", "Sismografo");


        // Rol, Empleado, Usuario y Sesión
        Rol rol = new Rol("...", "Responsable de Inspeccion");
        Empleado empleado = new Empleado(14952, "Martinez", "Ana", "...", "...", rol);
        Usuario usuario = new Usuario("ana123", "1234", empleado);
        Sesion sesion = new Sesion(LocalDateTime.now().minusDays(2), null, usuario);

        // Sismógrafo
        Sismografo sismografo1 = new Sismografo();
        CambioEstado cambioInicial = new CambioEstado(estadoInhabilitado, LocalDateTime.now().minusDays(2), null, new ArrayList<>(), empleado);
        sismografo1.getHistorialEstados().add(cambioInicial);
        sismografo1.setEstadoActual(cambioInicial);

        //  Crea una lista de sismógrafos.
        // Aunque solo tengas uno, debe estar dentro de una lista.
        List<Sismografo> sismografosDelSistema = Arrays.asList(sismografo1);

        // Estación
        EstacionSismologica estacion1 = new EstacionSismologica();
        estacion1.setNombre("Estación Central");
        estacion1.setSismografo(sismografo1);
        sismografo1.setEstacionSismologica(estacion1); // Es buena práctica tener la relación bidireccional

        // Ordenes de Inspección
        OrdenDeInspeccion orden1 = new OrdenDeInspeccion();
        orden1.setEstado(estadoRealizada);
        orden1.setFechaHoraFinalizacion(LocalDateTime.of(2025, 7, 15, 10, 30));
        orden1.setNumeroDeOrden(1);
        orden1.setEstacionSismologica(estacion1);
        orden1.setEmpleado(empleado);

        OrdenDeInspeccion orden2 = new OrdenDeInspeccion();
        orden2.setEstado(estadoRealizada);
        orden2.setFechaHoraFinalizacion(LocalDateTime.of(2025, 8, 11, 15, 30));
        orden2.setNumeroDeOrden(2);
        orden2.setEstacionSismologica(estacion1); // Ambas órdenes para la misma estación
        orden2.setEmpleado(empleado);
        List<OrdenDeInspeccion> ordenes = Arrays.asList(orden1, orden2);

        //Motivo tipo:
        DbInit.init(); //  Crea tablas si no existen, para que no falle el SELECT/INSERT.
        SeedData.seedMotivos(); //  Inserta motivos base si no estaban.
        MotivoTipoDAO motivoDAO = new MotivoTipoDAO(); //  Creamos DAO para leer desde BD.
        List<MotivoTipo> motivos = motivoDAO.listar(); //  Ahora la lista motivos ya viene de la BD


        SwingUtilities.invokeLater(() -> {
            //  Llama al constructor actualizado del MenuPrincipal,
            // pasando la nueva lista de sismógrafos.
            new MenuPrincipal(sesion, ordenes, motivos, estadosDelSistema, sismografosDelSistema);
        });
    }

    private static Estado buscarEstado(List<Estado> estados, String nombre, String ambito) {
        for (Estado e : estados) {
            if (e.getNombre().equals(nombre) && e.getAmbito().equals(ambito)) {
                return e;
            }
        }
        throw new RuntimeException("No se encontró el estado: " + nombre + " (" + ambito + ")");
    }
}




