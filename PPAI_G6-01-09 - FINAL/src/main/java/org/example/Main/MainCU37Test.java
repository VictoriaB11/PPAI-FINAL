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
import org.example.Persistencia.SismografoDAO;
import org.example.Persistencia.CambioEstadoDAO;



public class MainCU37Test {
    public static void main(String[] args) {

        // Inicializa tablas y carga datos semilla ANTES de leer desde BD
        DbInit.init();
        SeedData.seedMotivos();
        SeedData.seedEstados();

        //  Crear DAOs
        MotivoTipoDAO motivoDAO = new MotivoTipoDAO();
        EstadoDAO estadoDAO = new EstadoDAO();
        SismografoDAO sismografoDAO = new SismografoDAO();
        CambioEstadoDAO cambioEstadoDAO = new CambioEstadoDAO();

        // Leer datos desde la BD
        List<MotivoTipo> motivos = motivoDAO.listar();
        List<Estado> estadosDelSistema = estadoDAO.listarTodos();

        //  Buscar estados específicos
        Estado estadoRealizada = buscarEstado(estadosDelSistema, "Completamente Realizada", "Orden de Inspeccion");
        Estado estadoCerrada = buscarEstado(estadosDelSistema, "Cerrada", "Orden de Inspeccion");
        Estado estadoFS = buscarEstado(estadosDelSistema, "Fuera de Servicio", "Sismografo");
        Estado estadoInhabilitado = buscarEstado(estadosDelSistema, "Inhabilitado por inspeccion", "Sismografo");

        // Rol, Empleado, Usuario y Sesión
        Rol rol = new Rol("...", "Responsable de Inspeccion");
        Empleado empleado = new Empleado(14952, "Martinez", "Ana", "...", "...", rol);
        Usuario usuario = new Usuario("ana123", "1234", empleado);
        Sesion sesion = new Sesion(LocalDateTime.now().minusDays(2), null, usuario);

        // SISMOGRAFO
          // Crear sismógrafo en BD
        int sismografoId = sismografoDAO.insertarSismografo();
        System.out.println("Sismografo creado en BD con ID = " + sismografoId);

         //Conseguir el ID del estado "Fuera de Servicio" desde la BD
        int estadoFsId = estadoDAO.obtenerIdPorNombreYAmbito("Fuera de Servicio", "Sismografo");

          //Insertar cambio de estado en BD
        int cambioEstadoId = cambioEstadoDAO.insertarCambioEstado(
                sismografoId,
                estadoFsId,
                LocalDateTime.now(),
                empleado.getNombre() + " " + empleado.getApellido()
        );
        System.out.println("CambioEstado creado en BD con ID = " + cambioEstadoId);

          // Asociar motivos (ej: todos los que existan en BD)
        for (MotivoTipo m : motivos) {
            int motivoId = motivoDAO.obtenerIdPorDescripcion(m.getDescripcion());
            cambioEstadoDAO.insertarMotivoEnCambio(cambioEstadoId, motivoId);
        }
        System.out.println("Motivos asociados al CambioEstado");

          //Marcar ese cambio como estado actual del sismógrafo
        sismografoDAO.actualizarEstadoActual(sismografoId, cambioEstadoId);
        System.out.println("Estado actual del sismografo actualizado");

        // Creamos Sismógrafo
        Sismografo sismografo1 = new Sismografo();
        CambioEstado cambioInicial = new CambioEstado(
                estadoInhabilitado,
                LocalDateTime.now().minusDays(2),
                null,
                new ArrayList<>(),
                empleado
        );
        sismografo1.getHistorialEstados().add(cambioInicial);
        sismografo1.setEstadoActual(cambioInicial);

        // Lista de sismógrafos
        List<Sismografo> sismografosDelSistema = Arrays.asList(sismografo1);

        // Estación
        EstacionSismologica estacion1 = new EstacionSismologica();
        estacion1.setNombre("Estación Central");
        estacion1.setSismografo(sismografo1);
        sismografo1.setEstacionSismologica(estacion1);

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
        orden2.setEstacionSismologica(estacion1);
        orden2.setEmpleado(empleado);

        List<OrdenDeInspeccion> ordenes = Arrays.asList(orden1, orden2);

        SwingUtilities.invokeLater(() -> new MenuPrincipal(
                sesion,
                ordenes,
                motivos,
                estadosDelSistema,
                sismografosDelSistema
        ));
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




