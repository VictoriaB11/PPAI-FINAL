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


public class GestorRI {
    private Sesion sesion;
    private List<OrdenDeInspeccion> ordenesDeInspeccion;
    private OrdenDeInspeccion ordenSeleccionada;
    private String observacionCierre;
    private Map<MotivoTipo, String> motivosYComentarios;
    private List<Estado> estadosDisponibles;
    private List<MotivoTipo> motivosDisponibles;
    private boolean situacionSismografoHabilitada = false;
    private List<Sismografo> sismografosDisponibles;
    private InterfazMonitor interfazMonitor;
    private InterfazEnvioMail interfazEnvioMail;
    private List<String> mailsResponsablesDeReparaciones = new ArrayList<>();
    private List<Empleado> empleados = new ArrayList<>();
    private final java.util.List<Window> ventanasAbiertas = new ArrayList<>(); // FinCU


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
    }

    /**
     * Este metodo inicia el caso de uso. Recibe los datos necesarios desde la pantalla.
     */
    public void nuevoCierreOrdenInspeccion(Sesion sesion, List<OrdenDeInspeccion> ordenes, List<Sismografo> sismografos) {
        // Guarda la Sesión actual (para poder obtener al Usuario/Empleado logueado en Paso 2).
        this.sesion = sesion;

        // Guarda TODAS las órdenes (el Gestor será el responsable de filtrarlas según el empleado y estado).
        this.ordenesDeInspeccion = ordenes;

        // Guarda el catálogo de Sismógrafos (se usará en pasos posteriores para cambiar estado, etc.).
        this.sismografosDisponibles = sismografos;
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
        //Obtiene al Empleado actual usando el método anterior.
        Empleado empleadoLogueado = buscarEmpleadoLogueado();

        //Prepara una lista resultado para ir acumulando solo las órdenes válidas.
        List<OrdenDeInspeccion> ordenesFiltradas = new ArrayList<>();

        // Recorre TODAS las órdenes disponibles (inyectadas en Paso 1)...
        for (OrdenDeInspeccion orden : ordenesDeInspeccion) {
            // ...y se queda solo con las que:
            //    a) Son del empleado logueado (orden.esEmpleado(empleadoLogueado))
            //    b) Tienen estado "Completamente Realizada" (orden.esCompletamenteRealizada())
            if (orden.esEmpleado(empleadoLogueado) && orden.esCompletamenteRealizada()) {
                // Si cumple ambas condiciones, se agrega a la lista resultado.
                // (Los datos puntuales para mostrar se pedirán más tarde con getDatos())
                ordenesFiltradas.add(orden);
                // Cumple el llamado del diagrama sin mostrar nada
                orden.getDatos();
            }
        }
        //Para ordenar las órdenes filtradas por fecha de finalización (ascendente: antiguas primero).
        ordenarOrdenesDeInspeccionRealizadas(ordenesFiltradas);
        return ordenesFiltradas;
    }

    /**
     * Ordena por fecha de finalización ascendente (antiguas primero).
     * Se aísla la lógica de ordenación en un método privado para mantener cohesión y facilitar cambios.
     */
    private void ordenarOrdenesDeInspeccionRealizadas(List<OrdenDeInspeccion> ordenesFiltradas) {
        // Protección por si llega null
        if (ordenesFiltradas == null) return;
        ordenesFiltradas.sort(
                java.util.Comparator.comparing(OrdenDeInspeccion::getFechaHoraFinalizacion)
        );
    }

// Fin PASO 2

    // Paso 3: RI informa al gestor la orden seleccionada
    public void tomarSelecOrdenDeInspeccion(SeleccionOrdenDeInspeccion pantallaSeleccionOrdenDeInspeccion, OrdenDeInspeccion ordenSeleccionada) {
        this.ordenSeleccionada = ordenSeleccionada;
        pantallaSeleccionOrdenDeInspeccion.pedirIngresoObservacionDeCierre();
    }


    // Paso 5: RI ingresa la observación de cierre
    public void tomarIngresoObservacionCierreInspeccion(String observacionCierre) {
        this.ordenSeleccionada.setObservacionCierre(observacionCierre);
        this.observacionCierre = observacionCierre;
    }

    public void setEstadosDisponibles(List<Estado> estados) {
        this.estadosDisponibles = estados;
    }

    //Paso 6: habilita la actualización de la situación del sismógrafo.
    /**
     * Este metodo verifica si hay una orden seleccionada. Si no la hay,
     * no se puede continuar con la actualización y devuelve false.
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
     *Este metodo permite cargar dinámicamente la lista de motivos que serán
     * utilizados en la interfaz para selección y comentarios.
     */
    public void setMotivosDisponibles(List<MotivoTipo> motivosDisponibles) {
        this.motivosDisponibles = motivosDisponibles;
    }

    //Paso 6: obtener los tipos de motivos disponibles
    public List<MotivoTipo> buscarTiposDeMotivos() {
        // Devuelve la lista si está disponible, o una lista vacía si no fue inicializada
        return this.motivosDisponibles != null ? this.motivosDisponibles : new ArrayList<>();
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

    //Paso 9:
    public boolean tomarConfirmacionParaCerrarOrdenDeInspeccion(List<Estado> todosLosEstados){
        // Paso 10
        //  Se verifica que exista una observación de cierre y
        //  al menos un motivo válido para poner el sismógrafo fuera de servicio.
        if (!validarExistenciaObservaciones()) {
            return false; // Si no hay observación de cierre, no se puede cerrar la orden.
        }
        if (!validarMotivosMinimos()){
            return false; // Si no hay al menos un motivo con comentario, no se puede cerrar la orden.
        }
        // si pasa la validación cambiamos el estado de la orden
        Estado estadoCerrada = buscarEstadoCerradaOrdenInspeccion(todosLosEstados);
        LocalDateTime fechaActual = tomarFechaHoraActual();
        cerrarOrdenInspeccion(estadoCerrada, fechaActual);

        //cambiamos el estado del sismografo
        cambiarEstadoSismografo();

        return true; // El cierre fue exitoso.
    }

    public OrdenDeInspeccion getOrdenSeleccionada() {
        return ordenSeleccionada;
    }

    public List<Estado> getEstadosDisponibles() {
        return this.estadosDisponibles;
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
    private Estado buscarEstadoCerradaOrdenInspeccion(List<Estado> estados) {
        for (Estado estado : estados) {
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

    public Estado buscarEstadoFueraDeServicioParaSismografo() {
        for (Estado estado : estadosDisponibles) {
            if (estado.esAmbitoSismografo() && estado.esFueraDeServicio()) {
                return estado;
            }
        }
        return null;
    }

    public void cambiarEstadoSismografo() {
        // Buscar el estado 'Fuera de Servicio'
        Estado estadoFS = buscarEstadoFueraDeServicioParaSismografo();

        if (estadoFS == null) {
            throw new IllegalStateException("No se encontró el estado 'Fuera de Servicio'");
        }

        // Obtener el empleado responsable
        Empleado responsable = buscarEmpleadoLogueado();

        // Buscar el sismógrafo asociado a la estación seleccionada
        EstacionSismologica estacion = ordenSeleccionada.getEstacionSismologica();
        Sismografo sismografo = buscarSismografoPorEstacion(estacion);

        if (sismografo == null) {
            throw new IllegalStateException("No se encontró el sismógrafo para la estación: " + estacion.getNombre());
        }

        // Poner el sismógrafo en reparación (usa el Map<MotivoTipo, String> directamente)
        sismografo.ponerEnReparacion(estadoFS, responsable, this.motivosYComentarios);
    }

    public Sismografo buscarSismografoPorEstacion(EstacionSismologica estacion) {
        for (Sismografo sismografo : sismografosDisponibles) {
            if (sismografo.getEstacionSismologica().equals(estacion)) {
                return sismografo;
            }
        }
        return null; // Si no se encontró ninguno
    }

    //Paso 13: Buscar los mails de los responsables
    public void buscarMailsResponsablesDeReparaciones() {
        for (Empleado e : empleados) {  // Nos dirigimos a la clase empleado para saber si es un responsable de reparacion
            if (e.esResponsableDeReparacion()) {
                mailsResponsablesDeReparaciones.add(e.obtenerMail()); // Si el empleado es un responsable en reparacion obtenemos el mail
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
    /** Registrar una ventana creada durante el CU */
    public void registrarVentana(Window w) {
        if (w != null) ventanasAbiertas.add(w);
    }

    /** (Opcional) eliminar si cerrás manualmente una ventana */
    public void eliminarVentana(Window w) {
        ventanasAbiertas.remove(w);
    }

    /** Fin del Caso de Uso: cierra pantallas y limpia estado */
    public void finCU() {
        // Cerrar ventanas en el EDT
        SwingUtilities.invokeLater(() -> {
            for (Window w : new ArrayList<>(ventanasAbiertas)) {
                try {
                    if (w.isDisplayable()) w.dispose();
                } catch (Exception ignored) {}
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
}
