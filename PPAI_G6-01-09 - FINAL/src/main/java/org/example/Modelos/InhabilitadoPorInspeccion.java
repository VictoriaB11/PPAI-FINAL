package org.example.Modelos;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.example.Servicios.EmpleadoService; //VER

/**
 * Estado concreto: InhabilitadoPorInspeccion
 * Métodos en el orden del diagrama: ponerEnReparacion(array), buscarCambioDeEstadoActual,
 * crearProximoEstado, crearCambioDeEstado.
 */
public class InhabilitadoPorInspeccion extends Estado {

    public InhabilitadoPorInspeccion() {
        super("InhabilitadoPorInspeccion", "Inhabilitado por inspección", "Sismografo");
    }

    // 1) Firma del diagrama: arrays + RILogueado
    public void ponerEnReparacion(Sismografo sismografo,
                                  LocalDateTime fechaHoraActual,
                                  CambioEstado[] cambiosDeEstado,
                                  MotivoFueraDeServicio[] motivos,
                                  String RILogueado) {
        List<MotivoFueraDeServicio> listaMotivos = motivos == null ? Collections.emptyList() : Arrays.asList(motivos);

        // Resolver Empleado desde la BD por RI (EmpleadoService debe implementar la consulta real)
        Optional<Empleado> optEmpleado = EmpleadoService.findByRi(RILogueado);
        Empleado responsable = optEmpleado.orElse(null);

        // Delegar en la versión base, que creará y registrará el CambioEstado
        super.ponerEnReparacion(sismografo, fechaHoraActual, listaMotivos, responsable);
    }

    // 2) buscarCambioDeEstadoActual(): devuelve el estado actual (aquí la misma instancia)
    public Estado buscarCambioDeEstadoActual() {
        return this;
    }

    // 3) crearProximoEstado(): según diagrama -> FueraDeServicio
    @Override
    public Estado crearProximoEstado() {
        return new FueraDeServicio();
    }

    // 4) crearCambioDeEstado(...) requerido por la clase base Estado
    @Override
    public CambioEstado crearCambioDeEstado(LocalDateTime fechaYHoraInicio,
                                            List<MotivoFueraDeServicio> motivos,
                                            Empleado responsable) {
        // Crea y retorna la instancia del cambio de estado
        return new CambioEstado(crearProximoEstado(), fechaYHoraInicio, motivos, responsable);
    }

}