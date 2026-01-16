package org.example.Modelos;

import java.time.LocalDateTime;
//import java.util.Collections;
import java.util.List;

/**
 * Estado concreto: InhabilitadoPorInspeccion
 * Métodos en el orden del diagrama: ponerEnReparacion(array), buscarCambioDeEstadoActual,
 * crearProximoEstado, crearCambioDeEstado.
 */
public class InhabilitadoPorInspeccion extends Estado {

    public InhabilitadoPorInspeccion() {
        super("InhabilitadoPorInspeccion", "Inhabilitado por inspección", "Sismografo");
    }

    @Override
    public void ponerEnReparacion(Sismografo sismografo,
                                  LocalDateTime fechaHoraActual,
                                  List<CambioEstado> cambioEstado,
                                  List<MotivoFueraDeServicio> motivos,
                                  Empleado RILogueado) {

        // 1. Crear el nuevo cambio de estado (usando nuestro propio método factory)
        CambioEstado nuevoCambio = this.crearCambioEstado();

        // 2. Setear los datos que vienen por parámetro
        nuevoCambio.setFechaHoraInicio(fechaHoraActual);
        nuevoCambio.setMotivosFueraDeServicio(motivos);
        nuevoCambio.setEmpleadoResponsable(RILogueado);

        // 3. Cerrar el estado anterior (el actual del sismógrafo)
        CambioEstado estadoActual = sismografo.getEstadoActual();
        if (estadoActual != null) {
            estadoActual.setFechaHoraFin(fechaHoraActual);
        }

        // 4. Registrar el nuevo cambio en el historial y actualizar el actual
        if (sismografo.getHistorialEstados() != null) {
            sismografo.getHistorialEstados().add(nuevoCambio);
        }
        sismografo.setEstadoActual(nuevoCambio);

    }

    // buscarCambioDeEstadoActual(): devuelve el estado actual (aquí la misma instancia)
    public Estado buscarCambioDeEstadoActual() {
        return this;
    }

    // crearProximoEstado(): según diagrama -> FueraDeServicio
    @Override
    public Estado crearProximoEstado() {
        return new FueraDeServicio();
    }

    // crearCambioDeEstado(...) requerido por la clase base Estado
    @Override
    public CambioEstado crearCambioEstado() {
        CambioEstado nuevoCambio = new CambioEstado();
        nuevoCambio.setEstado(crearProximoEstado());
        return nuevoCambio;
    }

}