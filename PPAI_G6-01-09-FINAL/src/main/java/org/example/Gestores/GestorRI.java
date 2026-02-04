package org.example.Gestores;

import org.example.Modelos.*;
import org.example.Vistas.InterfazMonitor;
import org.example.Vistas.InterfazEnvioMail;
import org.example.Vistas.SeleccionOrdenDeInspeccion;
import org.example.Persistencia.JPAUtil;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.*;
import java.awt.Window;
import javax.swing.SwingUtilities;

/**
 * Controlador del Caso de Uso "Cerrar Orden de Inspección".
 * Orquesta el flujo entre las Vistas y el Modelo.
 */
public class GestorRI {
    // Atributos de estado del Caso de Uso
    private Sesion sesion;
    private List<OrdenDeInspeccion> ordenesDeInspeccion; // Colección completa inyectada al inicio
    private OrdenDeInspeccion ordenSeleccionada;
    private String observacionCierre;
    private Map<MotivoTipo, String> motivosYComentarios;
    private Long idOrdenSeleccionada;

    private List<EstadoSismografo> estadosDisponibles;
    private List<MotivoTipo> motivosDisponibles;
    private boolean situacionSismografoHabilitada = false;
    private List<Sismografo> sismografosDisponibles;


    // Colaboradores externos (Interfaces)
    private InterfazMonitor interfazMonitor;
    private InterfazEnvioMail interfazEnvioMail;

    private List<String> mailsResponsablesDeReparaciones = new ArrayList<>();
    private List<Empleado> empleados = new ArrayList<>();
    // Gestión de ventanas para el finCU
    private final java.util.List<Window> ventanasAbiertas = new ArrayList<>();


    /**
     * Constructor. Inicializa las colecciones y colaboradores.
     */
    public GestorRI() {
        this.motivosYComentarios = new HashMap<>();
        this.interfazMonitor = new InterfazMonitor();
        this.interfazEnvioMail = new InterfazEnvioMail();
    }


    // PASO 1: INICIO DEL CASO DE USO

    //Inicia el caso de uso, recibiendo los datos iniciales desde la pantalla.
    public void nuevoCierreOrdenInspeccion(Sesion sesion, List<OrdenDeInspeccion> todasLasOrdenes) {
        this.sesion = sesion;
        this.ordenesDeInspeccion = todasLasOrdenes;
    }


    // PASO 2: BÚSQUEDA Y FILTRADO DE ÓRDENES

    //Obtiene el objeto Empleado que está actualmente logueado en el sistema.
    public Empleado buscarEmpleadoLogueado() {
        if (sesion == null) {
            throw new IllegalStateException("La sesión no ha sido inicializada.");
        }
        return sesion.obtenerUsuarioLogueado();
    }

    /**
     * Busca en memoria, filtra y ordena las órdenes de inspección que pertenecen al empleado logueado
     * y están en estado "Completamente Realizada", respetando el patrón Experto.
     */
    public List<OrdenDeInspeccion> buscarOrdenesDeInspeccionRealizadas() {
        Empleado empleadoLogueado = buscarEmpleadoLogueado();
        List<OrdenDeInspeccion> ordenesFiltradas = new ArrayList<>();

        if (ordenesDeInspeccion != null) {
            for (OrdenDeInspeccion orden : ordenesDeInspeccion) {
                // Delegación a la entidad (Patrón Experto)
                if (orden.esEmpleado(empleadoLogueado) && orden.esCompletamenteRealizada()) {
                    ordenesFiltradas.add(orden);
                    orden.getDatos(); // Cumple el llamado del diagrama
                }
            }
        }

        ordenarOrdenesDeInspeccionRealizadas(ordenesFiltradas);
        return ordenesFiltradas;
    }

    /**
     * Ordena una lista de órdenes por fecha de finalización ascendente.
     */
    private void ordenarOrdenesDeInspeccionRealizadas(List<OrdenDeInspeccion> ordenesFiltradas) {
        if (ordenesFiltradas != null) {
            ordenesFiltradas.sort(
                    Comparator.comparing(OrdenDeInspeccion::getFechaHoraFinalizacion)
                            .reversed()
            );
        }
    }

    // PASOS 3 a 7: INTERACCIÓN CON EL USUARIO

    //Guarda la orden de inspección seleccionada por el usuario y notifica a la pantalla.
    public void tomarSelecOrdenDeInspeccion(SeleccionOrdenDeInspeccion pantalla, OrdenDeInspeccion ordenSeleccionada) {
        this.ordenSeleccionada = ordenSeleccionada;
        if (ordenSeleccionada != null) {
            this.idOrdenSeleccionada = ordenSeleccionada.getId();
        }
        pantalla.pedirIngresoObservacionDeCierre();
    }

    //Guarda la observación de cierre ingresada por el usuario.
    public void tomarIngresoObservacionCierreInspeccion(String observacionCierre) {
        this.observacionCierre = observacionCierre;
        if (this.ordenSeleccionada != null) {
            this.ordenSeleccionada.setObservacionCierre(observacionCierre);
        }
    }

    public void setEstadosDisponibles(List<EstadoSismografo> estado) {
        this.estadosDisponibles = estado;
    }

    //Habilita la sección para actualizar la situación del sismógrafo.
    public boolean habilitarActualizarSituacionSismografo() {
        this.situacionSismografoHabilitada = (this.ordenSeleccionada != null);
        return this.situacionSismografoHabilitada;
    }

    public void setMotivosDisponibles(List<MotivoTipo> motivosDisponibles) {
        this.motivosDisponibles = motivosDisponibles;
    }

    public List<MotivoTipo> buscarTiposDeMotivos() {
        // Aseguramos que la lista de motivos esté cargada
        if (this.motivosDisponibles == null || this.motivosDisponibles.isEmpty()) {
            try (EntityManager em = JPAUtil.getEntityManager()) {
                this.motivosDisponibles = em.createQuery("SELECT m FROM MotivoTipo m", MotivoTipo.class).getResultList();
            }
        }

        if (this.motivosDisponibles != null) {
            for (MotivoTipo motivo : this.motivosDisponibles) {
                motivo.getDescripcion(); // <-- Esta es la llamada que pide tu diagrama.
            }
        }

        return this.motivosDisponibles != null ? this.motivosDisponibles : new ArrayList<>();
    }


    public void tomarSeleccionMotivosTipos(List<MotivoTipo> motivos) {
        if (this.motivosYComentarios == null) {
            this.motivosYComentarios = new HashMap<>();
        }
        for (MotivoTipo m : motivos) {
            // Asegura que el motivo esté en el mapa, incluso si aún no tiene comentario.
            this.motivosYComentarios.putIfAbsent(m, "");
        }
    }

    //Guarda los motivos y comentarios seleccionados por el usuario.
    public void tomarIngresoComentarioMotivo(Map<MotivoTipo, String> motivosYComentarios) {
        this.motivosYComentarios = motivosYComentarios;
    }

    // PASO 9 y 10: CONFIRMACIÓN Y VALIDACIÓN

    //Orquesta el proceso final de cierre de la orden tras la confirmación del usuario.
    public boolean tomarConfirmacionParaCerrarOrdenDeInspeccion(List<Estado> todosLosEstados) {
        if (!validarExistenciaObservaciones() || !validarMotivosMinimos()) {
            return false;
        }

        Estado estadoCerrada = buscarEstadoCerradaOrdenInspeccion(todosLosEstados);
        if (estadoCerrada == null) {
            throw new IllegalStateException("No se encontró el estado 'Cerrada' para la orden de inspección.");
        }

        LocalDateTime fechaActual = tomarFechaHoraActual();
        cerrarOrdenInspeccion(estadoCerrada, fechaActual);

        Sismografo sismografo = cambiarEstadoSismografo();

        guardarCierreEnBD(ordenSeleccionada, sismografo);
        enviarMail();
        return true;
    }

    public boolean validarExistenciaObservaciones() {
        // La observación se guarda en el atributo del gestor, no directamente en la orden hasta el final.
        return this.observacionCierre != null && !this.observacionCierre.trim().isEmpty();
    }

    public boolean validarMotivosMinimos() {
        if (motivosYComentarios == null || motivosYComentarios.isEmpty()) {
            return false;
        }
        // Verifica que al menos un motivo tenga un comentario no vacío.
        return motivosYComentarios.values().stream().anyMatch(c -> c != null && !c.trim().isEmpty());
    }

    // PASO 11 y 12: CAMBIO DE ESTADO

    private Estado buscarEstadoCerradaOrdenInspeccion(List<Estado> todosLosEstados) {
        if (todosLosEstados == null) return null;
        for (Estado estado : todosLosEstados) {
            if (estado.esAmbitoOrdenDeInspeccion() && estado.esCerrada()) {
                return estado;
            }
        }
        return null;
    }

    private LocalDateTime tomarFechaHoraActual() {
        return LocalDateTime.now();
    }

    private void cerrarOrdenInspeccion(Estado estadoCerrada, LocalDateTime fechaHoraActual) {
        if (ordenSeleccionada != null) {
            ordenSeleccionada.cerrar(estadoCerrada, fechaHoraActual);
        }
    }

    private Sismografo cambiarEstadoSismografo() {
        EstadoSismografo estadoSismografoFS = buscarEstadoFueraDeServicioParaSismografo();
        if (estadoSismografoFS == null) {
            throw new IllegalStateException("Error de consistencia: No se encuentra el estado 'Fuera de Servicio' en el sistema.");
        }

        Empleado RILogueado = buscarEmpleadoLogueado();
        LocalDateTime fechaHoraActual = tomarFechaHoraActual();
        EstacionSismologica estacion = ordenSeleccionada.getEstacionSismologica();
        Sismografo sismografo = buscarSismografoPorEstacion(estacion);
        if (sismografo == null) {
            throw new IllegalStateException("No se encontró el sismógrafo para la estación: " + estacion.getNombre());
        }

        sismografo.ponerEnReparacion(fechaHoraActual, this.motivosYComentarios, RILogueado);
        return sismografo;
    }


    // PASO 13: NOTIFICACIÓN

    public void buscarMailsResponsablesDeReparaciones() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            this.empleados = em.createQuery("SELECT e FROM Empleado e JOIN FETCH e.rol", Empleado.class).getResultList();
        }
        this.mailsResponsablesDeReparaciones.clear();
        for (Empleado e : empleados) {
            if (e.esResponsableDeReparacion()) {
                mailsResponsablesDeReparaciones.add(e.obtenerMail());
            }
        }
    }

    public String publicarEnMail() {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Se notifican tareas de reparación para la orden de inspección:").append(System.lineSeparator());
        mensaje.append("- Observación de cierre: ").append(observacionCierre).append(System.lineSeparator());
        mensaje.append("- Motivos seleccionados:").append(System.lineSeparator());
        for (Map.Entry<MotivoTipo, String> entry : motivosYComentarios.entrySet()) {
            mensaje.append("  · ").append(entry.getKey().getDescripcion()).append(": ").append(entry.getValue()).append(System.lineSeparator());
        }
        mensaje.append("- Fecha: ").append(LocalDateTime.now().toString());
        return mensaje.toString();
    }

    public void enviarMail() {
        buscarMailsResponsablesDeReparaciones();
        String mensaje = publicarEnMail();
        interfazMonitor.publicarEnMonitor(mensaje);
        interfazEnvioMail.enviarMail(mailsResponsablesDeReparaciones, mensaje);
    }


    // MÉTODOS AUXILIARES Y DE GESTIÓN DE CICLO DE VIDA

    public void registrarVentana(Window w) {
        if (w != null) ventanasAbiertas.add(w);
    }

    public void eliminarVentana(Window w) {
        ventanasAbiertas.remove(w);
    }

    public void finCU() {
        SwingUtilities.invokeLater(() -> {
            for (Window w : new ArrayList<>(ventanasAbiertas)) {
                if (w.isDisplayable()) w.dispose();
            }
            ventanasAbiertas.clear();
        });

        this.sesion = null;
        this.ordenesDeInspeccion = null;
        this.ordenSeleccionada = null;
        this.observacionCierre = null;
        if (this.motivosYComentarios != null) this.motivosYComentarios.clear();
        this.situacionSismografoHabilitada = false;
        this.estadosDisponibles = null;
        this.motivosDisponibles = null;
        this.sismografosDisponibles = null;
        this.empleados = null;
    }

    // Métodos de búsqueda para inyección de datos y persistencia ---

    private EstadoSismografo buscarEstadoFueraDeServicioParaSismografo() {
        if (this.estadosDisponibles == null || this.estadosDisponibles.isEmpty()) {
            try (EntityManager em = JPAUtil.getEntityManager()) {
                this.estadosDisponibles = em.createQuery("SELECT e FROM EstadoSismografo e", EstadoSismografo.class).getResultList();
            }
        }
        for (EstadoSismografo estado : this.estadosDisponibles) {
            if (estado.esFueraDeServicio()) {
                return estado;
            }
        }
        return null;
    }

    public Sismografo buscarSismografoPorEstacion(EstacionSismologica estacion) {
        if (sismografosDisponibles == null || sismografosDisponibles.isEmpty()) {
            try (EntityManager em = JPAUtil.getEntityManager()) {
                // CORRECCIÓN AQUÍ:
                // Agregamos "LEFT JOIN FETCH s.historialEstados"
                // Esto obliga a traer la lista de historial junto con el sismógrafo
                // antes de cerrar el EntityManager.
                sismografosDisponibles = em.createQuery(
                        "SELECT s FROM Sismografo s " +
                                "JOIN FETCH s.estacionSismologica " +
                                "LEFT JOIN FETCH s.historialEstados", // <--- ESTA LÍNEA ES LA SOLUCIÓN
                        Sismografo.class).getResultList();
            }
        }

        // La lógica de búsqueda en memoria sigue igual
        for (Sismografo sismografo : sismografosDisponibles) {
            // Comparamos por ID para mayor seguridad
            if (sismografo.getEstacionSismologica().getId().equals(estacion.getId())) {
                return sismografo;
            }
        }
        return null;
    }

    public List<Estado> buscarEstadosOrden() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery("SELECT e FROM Estado e WHERE e.ambito = 'Orden de Inspeccion'", Estado.class).getResultList();
        }
    }

    private void guardarCierreEnBD(OrdenDeInspeccion orden, Sismografo sismografo) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            OrdenDeInspeccion ordenManaged = em.merge(orden);
            Sismografo sismografoManaged = em.merge(sismografo);
            em.getTransaction().commit();

            // Actualizar referencias locales con las entidades gestionadas
            this.ordenSeleccionada = ordenManaged;
        } catch (Exception ex) {
            ex.printStackTrace();
            // Considerar relanzar una excepción personalizada
        }
    }

    // Getters para las Vistas

    public OrdenDeInspeccion getOrdenSeleccionada() {
        return ordenSeleccionada;
    }

    public List<EstadoSismografo> getEstadosDisponibles() {
        if (this.estadosDisponibles == null) {
            buscarEstadoFueraDeServicioParaSismografo(); // Carga la lista si es nula
        }
        return this.estadosDisponibles;
    }
}