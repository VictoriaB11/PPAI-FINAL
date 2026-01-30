package org.example.Main;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.Modelos.*;
import org.example.Vistas.MenuPrincipal;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List; // Agregado para manejar listas

public class Main {

    public static void main(String[] args) {
        // 1. Configurar Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Generar datos y obtener el USUARIO
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
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }

    private static Usuario generarDatosBase() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ppaiPU");
        EntityManager em = emf.createEntityManager();
        Usuario usuario = null;

        try {
            em.getTransaction().begin();

            // --- VERIFICACIÓN DE DATOS EXISTENTES ---
            // Consultamos si ya existe la estación con código 123 para evitar el error de restricción única.
            List<EstacionSismologica> existentes = em.createQuery(
                            "SELECT e FROM EstacionSismologica e WHERE e.codEstacion = :cod", EstacionSismologica.class)
                    .setParameter("cod", 123)
                    .getResultList();

            if (!existentes.isEmpty()) {
                System.out.println("=== DATOS PREVIOS DETECTADOS EN LA BD. OMITIENDO CREACIÓN. ===");

                // Si ya existen datos, buscamos un usuario cualquiera para loguear (ej. el admin creado anteriormente)
                List<Usuario> usuarios = em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();

                em.getTransaction().commit(); // Cerramos la transacción de lectura

                if (!usuarios.isEmpty()) {
                    return usuarios.get(0); // Devolvemos el primer usuario encontrado
                } else {
                    System.err.println("Error: Existen estaciones pero no usuarios. Por favor borra el archivo 'ppai.db' y reinicia.");
                    return null;
                }
            }

            // --- SI NO EXISTEN DATOS, PROCEDEMOS A CREARLOS ---

            // --- 1. Estados ---
            Estado estadoRealizada = new Estado("Completamente Realizada", "Orden lista", "Orden de Inspeccion");
            Estado estadoCerrada = new Estado("Cerrada", "Orden finalizada", "Orden de Inspeccion");
            em.persist(estadoRealizada);
            em.persist(estadoCerrada);

            // Estados Sismógrafo
            EstadoSismografo estadoInhabilitado = new InhabilitadoPorInspeccion();
            EstadoSismografo estadoFueraServicio = new FueraDeServicio();
            em.persist(estadoInhabilitado);
            em.persist(estadoFueraServicio);

            // --- 1.5 Crear Motivos de Fuera de Servicio ---
            MotivoTipo motivo1 = new MotivoTipo("Descalibración del sensor");
            MotivoTipo motivo2 = new MotivoTipo("Falla de batería");
            MotivoTipo motivo3 = new MotivoTipo("Daños por vandalismo");
            MotivoTipo motivo4 = new MotivoTipo("Mantenimiento preventivo");

            em.persist(motivo1);
            em.persist(motivo2);
            em.persist(motivo3);
            em.persist(motivo4);


            // --- 2. Estación y Sismógrafo ---
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

            CambioEstado cambioInicial = new CambioEstado(estadoInhabilitado, LocalDateTime.now(), null, null);
            sismografo.agregarCambioEstado(cambioInicial);
            em.persist(sismografo);

            // --- 3. Rol, Empleado y Usuario ---
            Rol rol = new Rol("Tecnico", "Tecnico de mantenimiento");
            em.persist(rol);

            Empleado empleado = new Empleado("Perez", "Manuel", "manuel@ppai.com", "1234", rol);
            em.persist(empleado);

            usuario = new Usuario("admin", "1234", empleado);
            em.persist(usuario);

            // --- 4. Orden de Inspección ---
            OrdenDeInspeccion orden = new OrdenDeInspeccion();

            orden.setNumeroDeOrden(1001);
            orden.setFechaHoraFinalizacion(LocalDateTime.now().minusDays(1));
            orden.setEstacionSismologica(estacion);
            orden.setEstado(estadoRealizada);
            orden.setEmpleado(empleado);

            em.persist(orden);

            // --- DEBUG ---
            System.out.println("DEBUG MAIN: Orden creada con ID: " + orden.getId());
            System.out.println("DEBUG MAIN:   Estado de la orden: " + orden.getEstado().getNombre());

            em.getTransaction().commit();
            System.out.println("=== DATOS CARGADOS CORRECTAMENTE ===");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return null;
        } finally {
            em.close();
            emf.close();
        }
        return usuario;
    }
}