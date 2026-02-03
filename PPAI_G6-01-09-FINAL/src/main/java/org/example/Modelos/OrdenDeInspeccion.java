package org.example.Modelos;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orden_inspeccion")
public class OrdenDeInspeccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_orden")
    private Integer numeroDeOrden;

    private LocalDateTime fechaHoraFinalizacion;
    private LocalDateTime fechaHoraCierre;
    private String observacionCierre;

    @ManyToOne
    @JoinColumn(name = "estacion_id")
    private EstacionSismologica estacionSismologica;

    @ManyToOne
    @JoinColumn(name = "estado_id")
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "empleado_id")
    private Empleado empleado;

    // Constructores, Getters y Setters...
    public OrdenDeInspeccion() {}

    public Long getId() { return id; }
    public Integer getNumeroDeOrden() { return numeroDeOrden; }
    public void setNumeroDeOrden(Integer numeroDeOrden) { this.numeroDeOrden = numeroDeOrden; }
    public LocalDateTime getFechaHoraFinalizacion() { return fechaHoraFinalizacion; }
    public void setFechaHoraFinalizacion(LocalDateTime fechaHoraFinalizacion) { this.fechaHoraFinalizacion = fechaHoraFinalizacion; }
    public EstacionSismologica getEstacionSismologica() { return estacionSismologica; }
    public void setEstacionSismologica(EstacionSismologica estacionSismologica) { this.estacionSismologica = estacionSismologica; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
    public String getObservacionCierre() { return observacionCierre; }
    public void setObservacionCierre(String observacionCierre) { this.observacionCierre = observacionCierre; }
    public void setFechaHoraCierre(LocalDateTime fechaHoraCierre) { this.fechaHoraCierre = fechaHoraCierre; }

    // --- MÉTODOS CLAVE PARA EL FILTRADO ---

    /**
     * CORRECCIÓN IMPORTANTE:
     * Comparamos por ID para asegurar que funcione aunque los objetos vengan de contextos distintos.
     */
    public boolean esEmpleado(Empleado empleadoLogueado) {
        if (this.empleado == null || empleadoLogueado == null) return false;

        // Verificamos que ambos tengan ID (si son nuevos podrían ser null)
        if (this.empleado.getIdEmpleado() == null || empleadoLogueado.getIdEmpleado() == null) return false;

        // Comparamos los IDs (Long)
        return this.empleado.getIdEmpleado().equals(empleadoLogueado.getIdEmpleado());
    }

    public boolean esCompletamenteRealizada() {
        if (this.estado == null) return false;
        // Usamos equalsIgnoreCase para evitar problemas de mayúsculas/minúsculas
        return this.estado.getNombre().trim().equalsIgnoreCase("Completamente Realizada");
    }

    public String getDatos() {
        return "Orden #" + numeroDeOrden + " - Estación: " +
                (estacionSismologica != null ? estacionSismologica.getNombre() : "N/A");
    }

    public void cerrar(Estado nuevoEstado, LocalDateTime fecha) {
        setEstado(nuevoEstado);
        setFechaHoraCierre(fecha);
    }

    @Override
    public String toString() {
        // Esto es lo que se muestra en el ComboBox
        return "Orden N° " + numeroDeOrden + " (" + (estacionSismologica != null ? estacionSismologica.getNombre() : "?") + ")";
    }
}