package org.example.Modelos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class CambioEstado {

    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private List<MotivoFueraDeServicio> motivosFueraDeServicio;
    private Empleado RILogueado;
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


