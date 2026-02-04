package org.example.Modelos;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
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

    @Column(columnDefinition = "TEXT")
    private LocalDateTime fechaHoraInicio;

    @Column(columnDefinition = "TEXT")
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
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_estado_sismografo")
    private EstadoSismografo estadoSismografo;


    public CambioEstado() {
    }

    public CambioEstado(EstadoSismografo estadoSismografo, LocalDateTime fechaHoraInicio, Empleado RILogueado) {
        this.estadoSismografo = estadoSismografo;
        this.fechaHoraInicio = fechaHoraInicio;
        this.RILogueado = RILogueado;
        this.motivosFueraDeServicio = new ArrayList<>();
    }

    public CambioEstado(EstadoSismografo estadoSismografo, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, List<MotivoFueraDeServicio> motivosFueraDeServicio, Empleado RILogueado) {
        this.estadoSismografo = estadoSismografo;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.motivosFueraDeServicio = motivosFueraDeServicio;
        this.RILogueado = RILogueado;
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

    public void setEstado(EstadoSismografo estadoSismografo) {
        this.estadoSismografo = estadoSismografo;
    }

    public EstadoSismografo getEstado() {
        return estadoSismografo;
    }


    //Metodo 11 del patron
    public void crearMotivosFueraDeServicio(Map<MotivoTipo, String> motivosYComentarios) {
        // Inicializamos la lista si es nula
        if (this.motivosFueraDeServicio == null) {
            this.motivosFueraDeServicio = new ArrayList<>();
        }

        if (motivosYComentarios != null) {
            for (Map.Entry<MotivoTipo, String> entry : motivosYComentarios.entrySet()) {
                MotivoTipo tipo = entry.getKey();
                String comentario = entry.getValue();

                // hacemos el new
                MotivoFueraDeServicio nuevoMotivo = new MotivoFueraDeServicio(comentario, tipo);

                // Asignamos la relación bidireccional (importante para JPA)
                nuevoMotivo.setCambioEstado(this);

                // Agregamos a la lista
                this.motivosFueraDeServicio.add(nuevoMotivo);
            }
        }
    }


}


