package org.example.Main;

import jakarta.persistence.EntityManager;
import org.example.Modelos.*;
import org.example.Persistencia.JPAUtil;
import org.example.Vistas.MenuPrincipal;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // 1. Configurar Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Generar datos y obtener el USUARIO
        // Este metodo ahora usará la misma BD que el resto de la app
        Usuario usuarioLogueado = generarDatosBase();

        if (usuarioLogueado == null) {
            System.err.println("Error crítico: No se pudo obtener el usuario para la sesión.");
            return;
        }

        // 3. Crear la Sesión con el Usuario
        Sesion sesionActual = new Sesion(usuarioLogueado);
        sesionActual.setFechaHoraInicio(LocalDateTime.now());

        // 4. Iniciar la Ventana Principal
        SwingUtilities.invokeLater(() -> {
            MenuPrincipal ventana = new MenuPrincipal(sesionActual);
        });

        // Opcional pero recomendado: Añadir un hook para cerrar la fábrica al salir de la JVM
        Runtime.getRuntime().addShutdownHook(new Thread(JPAUtil::shutdown));
    }

    private static Usuario generarDatosBase() {
        // NO creamos una fábrica local. Usamos la compartida de JPAUtil.
        EntityManager em = JPAUtil.getEntityManager();
        Usuario usuario = null;

        try {
            // --- VERIFICACIÓN DE DATOS EXISTENTES ---
            // Una forma más robusta es buscar un dato maestro, como un Rol.
            long countRoles = em.createQuery("SELECT COUNT(r) FROM Rol r", Long.class).getSingleResult();

            if (countRoles > 0) {
                System.out.println("=== DATOS PREVIOS DETECTADOS. OMITIENDO CREACIÓN. ===");
                // No necesitamos hacer commit si no cambiamos nada
                List<Usuario> usuarios = em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();
                if (!usuarios.isEmpty()) {
                    return usuarios.get(0);
                } else {
                    return null; // No debería pasar si hay roles
                }
            }

            // --- SI NO EXISTEN DATOS, PROCEDEMOS A CREARLOS ---
            em.getTransaction().begin();

            // 1. Estados
            Estado estadoRealizada = new Estado("Completamente Realizada", "Orden lista", "Orden de Inspeccion");
            Estado estadoCerrada = new Estado("Cerrada", "Orden finalizada", "Orden de Inspeccion");
            em.persist(estadoRealizada);
            em.persist(estadoCerrada);

            // Estados Sismógrafo
            EstadoSismografo estadoInhabilitado = new InhabilitadoPorInspeccion();
            EstadoSismografo estadoFueraServicio = new FueraDeServicio();
            em.persist(estadoInhabilitado);
            em.persist(estadoFueraServicio);

            // 1.5 Motivos
            MotivoTipo motivo1 = new MotivoTipo("Descalibración del sensor");
            MotivoTipo motivo2 = new MotivoTipo("Falla de batería");
            MotivoTipo motivo3 = new MotivoTipo("Daños por vandalismo");
            MotivoTipo motivo4 = new MotivoTipo("Mantenimiento preventivo");

            em.persist(motivo1);
            em.persist(motivo2);
            em.persist(motivo3);
            em.persist(motivo4);

            // 2. Rol, Empleado y Usuario
            Rol rol = new Rol("Tecnico", "Tecnico de mantenimiento");
            em.persist(rol);

            Empleado empleado = new Empleado("Perez", "Manuel", "manuel@ppai.com", "1234", rol);
            em.persist(empleado);

            usuario = new Usuario("admin", "1234", empleado);
            em.persist(usuario);

            // 3. Estación y Sismógrafo
            EstacionSismologica estacion = new EstacionSismologica();
            estacion.setNombre("Estacion Cordoba");
            estacion.setCodEstacion(123);
            estacion.setLatitud(-31.42);
            estacion.setLongitud(-64.18);
            em.persist(estacion);

            Sismografo sismografo = new Sismografo();
            sismografo.setFechaAdquisicion(LocalDate.now());
            sismografo.setIdentificadorSismografo(999);
            sismografo.setNroSerie(1234);
            sismografo.setEstacionSismologica(estacion);

            CambioEstado cambioInicial = new CambioEstado(estadoInhabilitado, LocalDateTime.now(), empleado);
            cambioInicial.setSismografo(sismografo);
            sismografo.agregarCambioEstado(cambioInicial);
            em.persist(sismografo);

            // 3.2. Segunda Estacion y Sismografo
            EstacionSismologica estacion2 = new EstacionSismologica();
            estacion2.setNombre("Estacion Mendoza");
            estacion2.setCodEstacion(456);
            estacion2.setLatitud(-32.89);
            estacion2.setLongitud(-68.84);
            em.persist(estacion2);

            Sismografo sismografo2 = new Sismografo();
            sismografo2.setFechaAdquisicion(LocalDate.now().minusYears(1));
            sismografo2.setIdentificadorSismografo(888);
            sismografo2.setNroSerie(5678);
            sismografo2.setEstacionSismologica(estacion2);

            CambioEstado cambioInicial2 =
                    new CambioEstado(estadoInhabilitado, LocalDateTime.now().minusDays(10), empleado);
            cambioInicial2.setSismografo(sismografo2);
            sismografo2.agregarCambioEstado(cambioInicial2);

            em.persist(sismografo2);


            // 4. Orden de Inspección
            OrdenDeInspeccion orden = new OrdenDeInspeccion();
            orden.setNumeroDeOrden(1001);
            orden.setFechaHoraFinalizacion(LocalDateTime.now().minusDays(1));
            orden.setEstacionSismologica(estacion);
            orden.setEstado(estadoRealizada);
            orden.setEmpleado(empleado);

            em.persist(orden);

            // 4.2 Segunda Orden de Inspección
            OrdenDeInspeccion orden2 = new OrdenDeInspeccion();
            orden2.setNumeroDeOrden(1002);
            orden2.setFechaHoraFinalizacion(LocalDateTime.now().minusDays(2));
            orden2.setEstacionSismologica(estacion2);
            orden2.setEstado(estadoRealizada);
            orden2.setEmpleado(empleado);

            em.persist(orden2);

            em.getTransaction().commit();
            System.out.println("=== DATOS CARGADOS CORRECTAMENTE ===");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return null;
        } finally {
            // Solo cerramos el EntityManager, NUNCA la fábrica aquí.
            em.close();
        }
        return usuario;
    }
}