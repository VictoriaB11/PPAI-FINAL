package org.example.Main;

import org.example.Modelos.*;
import org.example.Vistas.MenuPrincipal;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainCU37Test {
    public static void main(String[] args) {

        // --- CREACIÓN DE DATOS DE PRUEBA ---

        // Estados
        Estado estadoRealizada = new Estado("Completamente Realizada", "...", "Orden de Inspeccion");
        Estado estadoCerrada = new Estado("Cerrada", "...", "Orden de Inspeccion");
        Estado estadoFS = new Estado("Fuera de Servicio", "...", "Sismografo");
        Estado estadoInhabilitado = new Estado("Inhabilitado por inspeccion", "...", "Sismografo");
        List<Estado> estadosDelSistema = Arrays.asList(estadoRealizada, estadoCerrada, estadoFS, estadoInhabilitado);

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

        // Motivos
        MotivoTipo m1 = new MotivoTipo("Falta calibracion");
        MotivoTipo m2 = new MotivoTipo("Sensor dañado");
        List<MotivoTipo> motivos = Arrays.asList(m1, m2);


        SwingUtilities.invokeLater(() -> {
            //  Llama al constructor actualizado del MenuPrincipal,
            // pasando la nueva lista de sismógrafos.
            new MenuPrincipal(sesion, ordenes, motivos, estadosDelSistema, sismografosDelSistema);
        });
    }
}