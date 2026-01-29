package org.example.Modelos;

import java.time.LocalDateTime;
import org.example.Modelos.Empleado;
import org.example.Modelos.Usuario;

public class Sesion {

    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private Usuario usuario;

    public Sesion() {
    }

    public Sesion(LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, Usuario usuario) {
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.usuario = usuario;
    }

    public Sesion(Usuario usuario) {
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    //PASO 2
    public Empleado obtenerUsuarioLogueado() {
        return usuario.getEmpleado();
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}
