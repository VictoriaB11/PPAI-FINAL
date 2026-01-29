package org.example.Modelos;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "orden_inspeccion")
public class OrdenDeInspeccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaHoraCierre; // cuando actualiza el estado a cerrada setea su fecha de cierre.
    private LocalDateTime fechaFinalizacion; //para ordenes completamente realizadas

    @Column(name = "numero_orden", nullable = false, unique = true)
    private Integer numeroDeOrden;

    @Column(name = "observacion_cierre")
    private String observacionCierre;

    //Muchas órdenes pueden estar asignadas al mismo empleado.
    @ManyToOne(optional = false)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @ManyToOne
    @JoinColumn(name = "id_estado_orden") // FK a la tabla estado_orden
    private Estado estado;

    /**
     * Muchas órdenes pertenecen a una estación.
     * La estación normalmente existe, por eso sin cascade.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "estacion_id", nullable = false)
    private EstacionSismologica estacionSismologica;

    public OrdenDeInspeccion() {
    }

    public OrdenDeInspeccion(LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraCierre, LocalDateTime fechaHoraFinalizacion, Integer numeroDeOrden, String observacionCierre, Empleado empleado, EstadoSismografo estadoSismografo, EstacionSismologica estacionSismologica) {
        this.fechaHoraCierre = fechaHoraCierre;
        this.fechaFinalizacion = fechaHoraFinalizacion;
        this.numeroDeOrden = numeroDeOrden;
        this.observacionCierre = observacionCierre;
        this.empleado = empleado;
        this.estado = estado;
        this.estacionSismologica = estacionSismologica;
    }

    // --- Getters / setters básicos ---
    public Long getId() { return id; }

    public LocalDateTime getFechaHoraCierre() { return fechaHoraCierre; }

    public void setFechaHoraFinalizacion(LocalDateTime fechaHoraFinalizacion) { this.fechaFinalizacion = fechaHoraFinalizacion; }

    public void setNumeroDeOrden(Integer numeroDeOrden) { this.numeroDeOrden = numeroDeOrden; }

    public String getObservacionCierre() { return observacionCierre; }
    public void setObservacionCierre(String observacionCierre) { this.observacionCierre = observacionCierre; }

    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }

    public Estado getEstado() { return estado; }

    public EstacionSismologica getEstacionSismologica() {
        return estacionSismologica;
    }
    public void setEstacionSismologica(EstacionSismologica estacionSismologica) {
        this.estacionSismologica = estacionSismologica;
    }

    //  PASO 2
    public boolean esEmpleado(Empleado empleadoActual) {
        return this.empleado != null && this.empleado.equals(empleadoActual);
    }

    public boolean esCompletamenteRealizada(){
        return estado != null && estado.esCompletamenteRealizada();
    }


    public String getDatos() {
        Integer numeroOrden = this.getNumeroDeOrden();
        LocalDateTime fechaFin = this.getFechaHoraFinalizacion();
        String nombreEstacion = this.obtenerIdentificador(); // ya delega a estación
        Integer idSismografo = null;
        if (estacionSismologica != null) {
            idSismografo = estacionSismologica.obtenerIdentificadorSismografo(); // flecha del diagrama
        }

        return "Orden N°: " + numeroOrden +
                ", Finalizada: " + fechaFin +
                ", Estación: " + nombreEstacion +
                ", Sismógrafo ID: " + (idSismografo != null ? idSismografo : "-");
    }

    public Integer getNumeroDeOrden() { return numeroDeOrden; }

    public LocalDateTime getFechaHoraFinalizacion() { return fechaFinalizacion; }

    public String obtenerIdentificador() {
        return (estacionSismologica != null) ? estacionSismologica.getNombre() : "-";
    }
    //FIN PASO 2

    // Paso 11: cerrar() Cambia el estado interno de la orden a "Cerrada".
    public void cerrar(Estado estadoCerrada, LocalDateTime fechaHoraActual) {
        setEstado(estadoCerrada);

        // Guarda la fecha y hora actual como la fecha de cierre.
        setFechaHoraCierre(fechaHoraActual);
    }

    //Paso 11 setEstado(): cambia el estado del objeto
    public void setEstado(Estado estado) { this.estado = estado; }

    //Paso 11 setFechaHoraCierre():guarda la fecha y hora de cierre en el objeto
    public void setFechaHoraCierre(LocalDateTime fechaHoraCierre) { this.fechaHoraCierre = fechaHoraCierre; }

    @Override
    public String toString() {
        return "Orden N° " + numeroDeOrden + " - " + fechaFinalizacion.toLocalDate();
    }

}