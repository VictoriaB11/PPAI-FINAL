/**
 * 3. El Controlador:
 * Se crea solo cuando el usuario interactúa con la pantalla ( al hacer clic en el botón "Cerrar Orden").
 * La pantalla (MenúPrincipal) es la responsable de crearlo y de pasarle los datos que el gestor necesita,
 *                              usando el métodonuevoCierreOrdenInspeccion(...
 */
package org.example.Gestores;

import org.example.Modelos.*;
import org.example.Vistas.InterfazMonitor;
import org.example.Vistas.InterfazEnvioMail;
import org.example.Vistas.SeleccionOrdenDeInspeccion;

import java.time.LocalDateTime;
import java.util.*;
import javax.swing.SwingUtilities;
import java.awt.Window;
import java.util.ArrayList;
import jakarta.persistence.EntityManager;
import org.example.Persistencia.JPAUtil;



public class GestorRI {
    private Sesion sesion;
    private List<OrdenDeInspeccion> ordenesDeInspeccion;
    private OrdenDeInspeccion ordenSeleccionada;
    private String observacionCierre;
    private Map<MotivoTipo, String> motivosYComentarios; //ver si lo ponemos como lista
    private List<EstadoSismografo> estadosDisponibles;
    private List<MotivoTipo> motivosDisponibles;
    private boolean situacionSismografoHabilitada = false;
    private List<Sismografo> sismografosDisponibles;
    private Long idOrdenSeleccionada;


    private InterfazMonitor interfazMonitor;
    private InterfazEnvioMail interfazEnvioMail;

    private List<String> mailsResponsablesDeReparaciones = new ArrayList<>();
    private List<Empleado> empleados = new ArrayList<>();
    private final java.util.List<Window> ventanasAbiertas = new ArrayList<>(); // FinCU

    //Para persistencia
    private EntityManager em() {
        return JPAUtil.getEntityManager();
    }



//PASO 1:

    /**
     * Constructor. Se usa para crear la instancia del gestor.
     * Se inicializan las colecciones internas para que no estén nulas.
     */
    public GestorRI() {
        // Mapa MotivoTipo -> Comentario. Lo iniciamos vacío para evitar NullPointerException
        // cuando más adelante se agreguen/lean motivos y comentarios.
        this.motivosYComentarios = new HashMap<>();

        // Lista de mails de responsables a notificar
        this.mailsResponsablesDeReparaciones = new ArrayList<>();

        // Lista de empleados del sistema (se usará para filtrar responsables).
        this.empleados = new ArrayList<>();

        // Catálogo de sismógrafos disponibles (inyectado por la Pantalla en Paso 1).
        // Se inicializa en vacío para no depender de null.
        this.sismografosDisponibles = new ArrayList<>();

        // Inicialización dummy de interfaces para evitar NullPointer si no se inyectan
        // (En un caso real, la Pantalla debería setearlas)
        this.interfazMonitor = new InterfazMonitor();
        this.interfazEnvioMail = new InterfazEnvioMail();
    }

    /**
     * Este metodo inicia el caso de uso. Recibe los datos necesarios desde la pantalla.
     */
    public void nuevoCierreOrdenInspeccion(Sesion sesion) {
        this.sesion = sesion;
    }


//PASO 2:

    /**
     * Obtiene el objeto Empleado que está actualmente logueado en el sistema.
     * Diagrama: Gestor -> Sesion.obtenerUsuarioLogueado() -> Usuario.getEmpleado()
     * (La Sesión ya encapsula la delegación hacia Usuario.getEmpleado()).
     */
    public Empleado buscarEmpleadoLogueado() {
        // Devuelve directamente el Empleado asociado al Usuario de la sesión.
        return sesion.obtenerUsuarioLogueado();
    }

    /**
     * Busca, filtra y ordena las órdenes de inspección que pertenecen al empleado logueado
     * y que están en estado "Completamente Realizada".
     * Al finalizar, notifica a la Pantalla para que muestre las órdenes (Gestor -> Pantalla)
     */
    public List<OrdenDeInspeccion> buscarOrdenesDeInspeccionRealizadas() {
        List<OrdenDeInspeccion> buscarOrdenesDeInspeccionRealizadas; {
            Empleado emp = buscarEmpleadoLogueado();
            EntityManager em = JPAUtil.getEntityManager();
            try {

                return em.createQuery(
                                "SELECT o " +
                                        "FROM OrdenDeInspeccion o " +
                                        "JOIN FETCH o.estacionSismologica es " +
                                        "JOIN FETCH o.estado e " +
                                        "WHERE o.empleado = :emp " +
                                        "AND e.ambito = :ambito " +
                                        "AND e.nombre = :nombre " +
                                        "ORDER BY o.fechaFinalizacion ASC",
                                OrdenDeInspeccion.class
                        )
                        .setParameter("emp", emp)
                        .setParameter("ambito", "Orden de Inspeccion")
                        .setParameter("nombre", "Completamente Realizada")
                        .getResultList();

            } finally {
                em.close();
            }
        }
    }

    /**
     * Ordena por fecha de finalización ascendente (antiguas primero).
     * Se aísla la lógica de ordenación en un método privado para mantener cohesión y facilitar cambios.
     */
    private void ordenarOrdenesDeInspeccionRealizadas(List<OrdenDeInspeccion> ordenesFiltradas) {
        // Protección por si llega null
        if (ordenesFiltradas == null) return;
        ordenesFiltradas.sort(Comparator.comparing(OrdenDeInspeccion::getFechaHoraFinalizacion)
        );
    }
// Fin PASO 2

    // Paso 3: RI informa al gestor la orden seleccionada
    public void tomarSelecOrdenDeInspeccion(SeleccionOrdenDeInspeccion pantalla, OrdenDeInspeccion ordenSeleccionada) {
        this.ordenSeleccionada = ordenSeleccionada;
        this.idOrdenSeleccionada = ordenSeleccionada.getId(); // tenés que tener getId() en OrdenDeInspeccion entity
        pantalla.pedirIngresoObservacionDeCierre();
    }


    // Paso 5: RI ingresa la observación de cierre
    public void tomarIngresoObservacionCierreInspeccion(String observacionCierre) {
        this.ordenSeleccionada.setObservacionCierre(observacionCierre);
        this.observacionCierre = observacionCierre;
    }

    public void setEstadosDisponibles(List<EstadoSismografo> estado) {
        this.estadosDisponibles = estado;
    }

    //Paso 6: habilita la actualización de la situación del sismógrafo.

    /**
     * Este metodo verifica si hay una orden seleccionada. Si no la hay,
     * no se puede continuar con la actualización y devuelve false.
     *
     * @return true si se habilita correctamente (hay orden seleccionada), false si no.
     */
    public boolean habilitarActualizarSituacionSismografo() {
        if (ordenSeleccionada == null) {
            // No hay orden activa, no se puede habilitar la actualización
            return false;
        }

        // Se habilita la actualización de situación del sismógrafo
        this.situacionSismografoHabilitada = true;
        return true;
    }

    // Paso 6: devolver motivos disponibles

    /**
     * Este metodo permite cargar dinámicamente la lista de motivos que serán
     * utilizados en la interfaz para selección y comentarios.
     */
    public void setMotivosDisponibles(List<MotivoTipo> motivosDisponibles) {
        this.motivosDisponibles = motivosDisponibles;
    }

    //Paso 6: obtener los tipos de motivos disponibles
    public List<MotivoTipo> buscarTiposDeMotivos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Consulta JPA para traer todos los motivos cargados en el Main
            return em.createQuery("SELECT m FROM MotivoTipo m", MotivoTipo.class).getResultList();
        } finally {
            em.close();
        }
    }

    // Paso 7-a: registrar motivos seleccionados por el usuario.
    /**
     * Este metodo representa la interacción PantallaRI -> GestorRI : tomarSeleccionMotivosTipos()
     * Inicializa el mapa interno motivosYComentarios con los motivos seleccionados,
     * asignando un comentario vacío a cada uno para ser completado posteriormente.
     */
    public void tomarSeleccionMotivosTipos(List<MotivoTipo> motivos) {
        Map<MotivoTipo, String> vacios = new HashMap<>();
        for (MotivoTipo m : motivos) {
            vacios.put(m, "");
        }
        this.motivosYComentarios = vacios;
    }

    // Paso 7-b: registrar comentarios asociados a los motivos.

    /**
     * Este metodo representa la interacción PantallaRI -> GestorRI : tomarIngresoComentarioMotivo()
     * Reemplaza el mapa interno motivosYComentarios con los comentarios completos
     * que serán utilizados en los pasos siguientes del cierre de orden.
     */
    public void tomarIngresoComentarioMotivo(Map<MotivoTipo, String> motivosYComentarios) {
        this.motivosYComentarios = motivosYComentarios;
    }

    //Paso 9: confirmacion para cerrar orden de inspección
    public boolean tomarConfirmacionParaCerrarOrdenDeInspeccion(List<Estado> todosLosEstados) {
        // Paso 10
        //  Se verifica que exista una observación de cierre y
        //  al menos un motivo válido para poner el sismógrafo fuera de servicio.
        if (!validarExistenciaObservaciones()) {
            return false; // Si no hay observación de cierre, no se puede cerrar la orden.
        }
        if (!validarMotivosMinimos()) {
            return false; // Si no hay al menos un motivo con comentario, no se puede cerrar la orden.
        }
        Estado estadoCerrada = buscarEstadoCerradaOrdenInspeccion(todosLosEstados);
        LocalDateTime fechaActual = tomarFechaHoraActual();
        cerrarOrdenInspeccion(estadoCerrada, fechaActual);

        // Cambiar estado sismógrafo y conservar referencia
        EstacionSismologica estacion = ordenSeleccionada.getEstacionSismologica();
        Sismografo sismografo = buscarSismografoPorEstacion(estacion);
        if (sismografo == null) {
            throw new IllegalStateException("No se encontró sismógrafo para estación: " + estacion.getNombre());
        }

        //Le pasamos el sismógrafo encontrado al metodo
        cambiarEstadoSismografo();

//Persistencia real
        guardarCierreEnBD(ordenSeleccionada, sismografo);
        return true;
    }

// Paso 10: Sistema: valida que exista una observación de cierre de orden y
    // al menos un motivo seleccionado asociado a la puesta a Fuera de Servicio y es correcto.

    // El metodo validarExistenciaObservaciones() verifica si la orden seleccionada
    // tiene una observación de cierre válida y que está no sea nula ni esté vacía.

    public boolean validarExistenciaObservaciones() {
        return ordenSeleccionada != null
                && ordenSeleccionada.getObservacionCierre() != null
                && !ordenSeleccionada.getObservacionCierre().trim().isEmpty();
        // se asegura que la observación no esté vacía ni compuesta por espacios en blanco.
    }

    // El metodo validarMotivosMinimos() verifica que exista al menos un comentario y motivo válido.
    public boolean validarMotivosMinimos() {
        if (motivosYComentarios == null || motivosYComentarios.isEmpty()) {
            return false; // No hay motivos cargados.
        }

        // Recorre todos los comentarios y verifica que al menos uno no esté vacío
        for (String comentario : motivosYComentarios.values()) {
            if (comentario != null && !comentario.trim().isEmpty()) {
                return true; // Se encontró un motivo válido.
            }
        }

        return false; // ningún motivo tiene comentario válido.
    }

    // Paso 11: buscarEstadoCerradoOrdenInspeccion() Recorre todos los estados disponibles buscando aquel que:
    // Sea del ámbito "Orden de Inspección" estado.esAmbitoOrdenDeInspeccion()
    // Tenga el nombre "Cerrado" (estado.esCerrada())
    private Estado buscarEstadoCerradaOrdenInspeccion(List<Estado> todosLosEstados) {
        for (Estado estado : todosLosEstados) {
            if (estado.esAmbitoOrdenDeInspeccion() && estado.esCerrada()) {
                return estado;
            }
        }
        return null; // Si no encuentra, devuelve null
    }

    // Paso 11: tomarFechaHoraActual() Obtiene la fecha y hora actual del sistema que se usará para registrar cuándo se cerró la orden
    private LocalDateTime tomarFechaHoraActual() {
        return LocalDateTime.now();
    }

    // Paso 11: cerrarOrdenInspeccion() Indica a la orden seleccionada que cambie su estado a "Cerrada" y que guarde la fecha y hora actuales como fecha de cierre
    private void cerrarOrdenInspeccion(Estado estadoCerrada, LocalDateTime fechaHoraActual) {
        if (ordenSeleccionada != null && estadoCerrada != null && fechaHoraActual != null) {
            ordenSeleccionada.cerrar(estadoCerrada, fechaHoraActual);
        }
    }

    //Metodo 1 y de enganche del patrón
    // Busca y devuelve el Estado "Fuera de Servicio" del ámbito Sismógrafo.
// Devuelve null si no se encuentra o si la lista de estados no está inicializada.
    private EstadoSismografo buscarEstadoFueraDeServicioParaSismografo() {
        // Si la lista en memoria es nula o vacía, la cargamos desde la BD
        if (this.estadosDisponibles == null || this.estadosDisponibles.isEmpty()) {
            EntityManager em = JPAUtil.getEntityManager();
            try {
                // Traemos todos los estados de sismógrafo (Inhabilitado, FueraDeServicio, etc.)
                this.estadosDisponibles = em.createQuery("SELECT e FROM EstadoSismografo e", EstadoSismografo.class).getResultList();
            } finally {
                em.close();
            }
        }

        // Ahora sí buscamos en la lista cargada
        for (EstadoSismografo estadoSismografo : this.estadosDisponibles) {
            if (estadoSismografo != null && estadoSismografo.esAmbitoSismografo() && estadoSismografo.esFueraDeServicio()) {
                return estadoSismografo;
            }
        }
        return null;
    }

    public Sismografo buscarSismografoPorEstacion(EstacionSismologica estacion) {
        // Cargamos los sismógrafos de la BD si no están cargados
        if (sismografosDisponibles == null || sismografosDisponibles.isEmpty()) {
            EntityManager em = JPAUtil.getEntityManager();
            try {
                // CORRECCIÓN 1: Agregamos JOIN FETCH s.estacionSismologica
                // Esto asegura que el sismógrafo traiga su estación cargada y no sea un proxy vacío.
                sismografosDisponibles = em.createQuery(
                        "SELECT s FROM Sismografo s " +
                                "LEFT JOIN FETCH s.historialEstados " +
                                "JOIN FETCH s.estacionSismologica",
                        Sismografo.class).getResultList();
            } finally {
                em.close();
            }
        }

        // CORRECCIÓN 2: Comparación por ID
        // Comparamos los IDs (Long) en lugar de los objetos completos para evitar problemas de referencias.
        for (Sismografo sismografo : sismografosDisponibles) {
            Long idEstacionDelSismografo = sismografo.getEstacionSismologica().getId();
            Long idEstacionBuscada = estacion.getId();

            if (idEstacionDelSismografo.equals(idEstacionBuscada)) {
                return sismografo;
            }
        }
        return null;
    }

    public void cambiarEstadoSismografo() {
        // 1 Obtener datos necesarios
        Empleado RILogueado = buscarEmpleadoLogueado();
        LocalDateTime fechaHoraActual = tomarFechaHoraActual();

        // 2 Buscar el sismografo
        EstacionSismologica estacion = ordenSeleccionada.getEstacionSismologica();
        Sismografo sismografo = buscarSismografoPorEstacion(estacion);
        if (sismografo == null) {
            throw new IllegalStateException("No se encontró el sismógrafo para la estación: " + estacion.getNombre());
        }
        // 3. Convertir Map<MotivoTipo, String> a List<MotivoFueraDeServicio
        // Esto es necesario porque Sismografo.ponerEnReparacion espera una Lista.
        List<MotivoFueraDeServicio> listaMotivos = new ArrayList<>();
        if (this.motivosYComentarios != null) {
            for (Map.Entry<MotivoTipo, String> entry : this.motivosYComentarios.entrySet()) {
                // Creamos el objeto MotivoFueraDeServicio con (comentario, tipo)
                listaMotivos.add(new MotivoFueraDeServicio(entry.getValue(), entry.getKey()));
            }
        }

        // 4. EL ENGANCHE (Self-Call):
        // Ejecutamos el metodo porque el diagrama lo exige.
        // Esto valida que el estado exista en la lista de estados disponibles del sistema.
        EstadoSismografo estadoSismografoFS = buscarEstadoFueraDeServicioParaSismografo();

        if (estadoSismografoFS == null) {
            throw new IllegalStateException("Error de consistencia: No se encuentra el estado 'Fuera de Servicio' en el sistema.");
        }

        // 5. Llamar al sismógrafo
        sismografo.ponerEnReparacion(fechaHoraActual, listaMotivos, RILogueado);
    }



    //Paso 13: Buscar los mails de los responsables
    public void buscarMailsResponsablesDeReparaciones() {
        //Cargar empleados desde la BD antes de filtrar
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Traemos los empleados y sus roles para evitar LazyInitializationException
            this.empleados = em.createQuery("SELECT e FROM Empleado e JOIN FETCH e.rol", Empleado.class).getResultList();
        } finally {
            em.close();
        }

        // Filtramos en memoria
        for (Empleado e : empleados) {
            if (e.esResponsableDeReparacion()) {
                mailsResponsablesDeReparaciones.add(e.obtenerMail());
            }
        }
    }

    public String publicarEnMail() {// Paso 13
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Se notifican tareas de reparación para la orden de inspección:\n"); //Crea el encabezado: "Se notifican tareas de reparación para la orden de inspección:\n"
        mensaje.append("- Observación de cierre: ").append(observacionCierre).append("\n"); //Agrega la observación de cierre escrita por el usuario: "- Observación de cierre: [lo que haya escrito el usuario]\n"
        mensaje.append("- Motivos seleccionados:\n"); //Enumera los motivos seleccionados:
        for (Map.Entry<MotivoTipo, String> entry : motivosYComentarios.entrySet()) {
            mensaje.append("  · ").append(entry.getKey().getDescripcion()).append(": ").append(entry.getValue()).append("\n");
        } //getDescripcion() saca la descripción del motivo (ej: "Falla técnica"), entry.getValue() obtiene el comentario asociado a ese motivo (ej: "Se detectó un error en la lectura").
        mensaje.append("- Fecha: ").append(LocalDateTime.now().toString()); //Agrega la fecha actual: "Fecha: 2025-07-17T16:10:00" (esto se obtiene con LocalDateTime.now().toString()).
        return mensaje.toString(); //Devuelve el mensaje completo en forma de String.
    }

    public void enviarMail() {  // Paso 13: Unir los metodos
        // Buscar responsables
        buscarMailsResponsablesDeReparaciones();

        // Generar contenido según Observación 2
        String mensaje = publicarEnMail();

        // Mostrar en monitor
        interfazMonitor.publicarEnMonitor(mensaje); // con repetición si lo requiere el CU

        // Enviar mail
        interfazEnvioMail.enviarMail(mailsResponsablesDeReparaciones, mensaje);
    }

    // Fin Cu

    /**
     * Registrar una ventana creada durante el CU
     */
    public void registrarVentana(Window w) {
        if (w != null) ventanasAbiertas.add(w);
    }

    /**
     * (Opcional) eliminar si cerrás manualmente una ventana
     */
    public void eliminarVentana(Window w) {
        ventanasAbiertas.remove(w);
    }

    /**
     * Fin del Caso de Uso: cierra pantallas y limpia estado
     */
    public void finCU() {
        // Cerrar ventanas en el EDT
        SwingUtilities.invokeLater(() -> {
            for (Window w : new ArrayList<>(ventanasAbiertas)) {
                try {
                    if (w.isDisplayable()) w.dispose();
                } catch (Exception ignored) {
                }
            }
            ventanasAbiertas.clear();
        });

        // Limpiar estado del Gestor
        this.ordenSeleccionada = null;
        this.observacionCierre = null;
        if (this.motivosYComentarios != null) this.motivosYComentarios.clear();
        this.situacionSismografoHabilitada = false;
        // (si hace falta) this.estadosDisponibles = null; this.motivosDisponibles = null;
    }

    // Getters auxiliares
    public OrdenDeInspeccion getOrdenSeleccionada() {
        return ordenSeleccionada;
    }

    public List<EstadoSismografo> getEstadosDisponibles() {
        return this.estadosDisponibles;
    }

    //para persistencia
    // NUEVO METODO: Buscar los estados correspondientes a Órdenes (tabla estado_orden)
    public List<Estado> buscarEstadosOrden() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Buscamos todos los registros de la entidad Estado
            return em.createQuery("SELECT e FROM Estado e", Estado.class).getResultList();
        } finally {
            em.close();
        }
    }
    private void guardarCierreEnBD(OrdenDeInspeccion ordenSeleccionada, Sismografo sismografo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // IMPORTANTE: aseguramos entidades managed
            OrdenDeInspeccion ordenManaged = em.merge(ordenSeleccionada);
            Sismografo sismManaged = em.merge(sismografo);

            // Si tu cambio de estado creó NUEVOS objetos (CambioEstado, MotivoFueraDeServicio, etc)
            // y tenés cascade bien (OneToMany con cascade=ALL), con merge alcanza.
            // Si no tenés cascade en algún punto, acá se persiste explícito.

            em.getTransaction().commit();

            // sincronizamos referencias del gestor (opcional pero recomendado)
            this.ordenSeleccionada = ordenManaged;

        } catch (Exception ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

}





