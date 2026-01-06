package org.example.Modelos;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class CambioEstado {

    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private List<MotivoFueraDeServicio> motivosFueraDeServicio;;
    private Empleado empleadoResponsable;
    private Estado estado;

    public CambioEstado() {
    }

    public CambioEstado(Estado estado, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, List<MotivoFueraDeServicio> motivosFueraDeServicio, Empleado empleadoResponsable) {
        this.estado = estado;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.motivosFueraDeServicio = motivosFueraDeServicio;
        this.empleadoResponsable = empleadoResponsable;
    }


    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
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

    public Empleado getEmpleadoResponsable() {
        return empleadoResponsable;
    }

    public Estado getEstado() {
        return estado;
    }

    public boolean esEstadoActual() {
        return fechaHoraFin == null;
    }

    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public void setEmpleadoResponsable(Empleado empleadoResponsable) {
        this.empleadoResponsable = empleadoResponsable;
    }

    public void crearMotivosFueraDeServicio(Map<MotivoTipo, String> motivosYComentarios) {
        List<MotivoFueraDeServicio> lista = new ArrayList<>();
        if (motivosYComentarios != null) {
            for (Map.Entry<MotivoTipo, String> entry : motivosYComentarios.entrySet()) {
                MotivoTipo tipo = entry.getKey();
                String comentario = entry.getValue();
                lista.add(new MotivoFueraDeServicio(comentario, tipo));
            }
        }
        this.motivosFueraDeServicio = lista;
    }

}


