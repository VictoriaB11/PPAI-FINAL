package org.example.Modelos;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Entity
@Table(name = "cambio_estado")
public class CambioEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;

    @ManyToOne
    @JoinColumn(name = "empleado_id")
    private Empleado RILogueado;

    // Relación: muchos CambioEstado pertenecen a un Sismografo
    @ManyToOne(optional = false)
    @JoinColumn(name = "sismografo_id")
    private Sismografo sismografo;

    public Sismografo getSismografo() {
        return sismografo;
    }

    public void setSismografo(Sismografo sismografo) {
        this.sismografo = sismografo;
    }

    @OneToMany(mappedBy = "cambioEstado", //OneToMany:un cambio de estado tiene varios motivos. mappedBy: define que la clave foránea está del lado del motivo
            cascade = CascadeType.ALL, //Propagación de operaciones, significa: Todo lo que le haga al CambioEstado, se lo hago también a sus motivos.
            orphanRemoval = true) //garantiza que los motivos se eliminen si se quitan del cambio.
    private List<MotivoFueraDeServicio> motivosFueraDeServicio;

    //Muchos CambioEstado apuntan a un mismo Estado
    @ManyToOne(optional = false, //No puede existir un CambioEstado sin Estado, Cada fila de cambio_estado OBLIGATORIAMENTE tiene que tener un estado_id
            cascade = CascadeType.PERSIST) //Si persisto un CambioEstado y su Estado todavía no está en la BD → guardalo automáticamente
    @JoinColumn(name = "estado_id") //La FK que une cambio_estado con estado se llama estado_id
    private Estado estado;


    public CambioEstado() {
    }

    public CambioEstado(Estado estado, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, List<MotivoFueraDeServicio> motivosFueraDeServicio, Empleado RILogueado) {
        this.estado = estado;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.motivosFueraDeServicio = motivosFueraDeServicio;
        this.RILogueado = RILogueado;
    }

    public CambioEstado(Estado estado, LocalDateTime fechaHoraInicio, List<MotivoFueraDeServicio> motivosFueraDeServicio, Empleado RILogueado) {
        this(estado, fechaHoraInicio, null, motivosFueraDeServicio, RILogueado);
    }


    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    //Metodo 5 del patron
    public boolean esEstadoActual() {

        return fechaHoraFin == null;
    }

    //Metodo 6 del patron
    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {

        this.fechaHoraFin = fechaHoraFin;
    }

    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public List<MotivoFueraDeServicio> getMotivosFueraDeServicio() {
        return motivosFueraDeServicio;
    }

    public void setMotivosFueraDeServicio(List<MotivoFueraDeServicio> motivosFueraDeServicio) {
        this.motivosFueraDeServicio = motivosFueraDeServicio;
    }

    public void setEmpleadoResponsable(Empleado RILogueado) {
        this.RILogueado = RILogueado;
    }


    public Empleado getEmpleado() {
        return RILogueado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Estado getEstado() {
        return estado;
    }


    //Metodo 11 del patron
    public void crearMotivosFueraDeServicio(List<MotivoFueraDeServicio> motivos) {
        this.motivosFueraDeServicio = motivos;
    }


}


