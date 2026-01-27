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

    //Metodo 3 del patron
    @Override
    public void ponerEnReparacion(Sismografo sismografo,
                                  LocalDateTime fechaHoraActual,
                                  List<CambioEstado> historialCambioEstado,
                                  List<MotivoFueraDeServicio> motivos,
                                  Empleado RILogueado) {

        // -------------------------------------------------------
        // PASO A: Buscar el cambio de estado actual (SELF)
        // Diagrama: InhabilitadoPorInspeccion -> buscarCambioDeEstadoActual()
        // -------------------------------------------------------
        CambioEstado ultimoCambio = this.buscarCambioDeEstadoActual(historialCambioEstado);

        // -------------------------------------------------------
        // PASO B: Cerrar el estado anterior
        // Diagrama: ultimo:CambioEstado -> setFechaHoraFin(...)
        // -------------------------------------------------------
        if (ultimoCambio != null) {
            ultimoCambio.setFechaHoraFin(fechaHoraActual);
        }

        // -------------------------------------------------------
        // PASO C: Crear el próximo estado (SELF)
        // Diagrama: InhabilitadoPorInspeccion -> crearProximoEstado()
        // -------------------------------------------------------
        Estado proximoEstado = this.crearProximoEstado();

        // -------------------------------------------------------
        // PASO D: Crear el nuevo CambioEstado (new)
        // -------------------------------------------------------
        // Usamos el constructor que definimos en CambioEstado para "nuevos" estados
        CambioEstado nuevoCambio = this.crearCambioEstado(proximoEstado, fechaHoraActual, motivos, RILogueado);
        nuevoCambio.crearMotivosFueraDeServicio(motivos);

        // -------------------------------------------------------
        // PASO E: Actualizar Sismógrafo
        // -------------------------------------------------------
        if (sismografo.getHistorialEstados() != null) {
            sismografo.getHistorialEstados().add(nuevoCambio);
        }
        sismografo.agregarCambioEstado(nuevoCambio);
        sismografo.setEstadoActual(nuevoCambio);
    }

    // -------------------------------------------------------------------------
    // IMPLEMENTACIÓN DEL SELF: buscarCambioDeEstadoActual
    // Recorre la lista y manda el mensaje esEstadoActual() a cada elemento.
    // -------------------------------------------------------------------------

    //Metodo 4 del patron
    public CambioEstado buscarCambioDeEstadoActual(List<CambioEstado> historialCambioEstado) {
        if (historialCambioEstado != null) {
            for (CambioEstado cambio : historialCambioEstado) {
                // Mensaje al CambioEstado: esEstadoActual()
                if (cambio.esEstadoActual()) {
                    return cambio;
                }
            }
        }
        return null;
    }


    //Metodo 7 del patron
    // crearProximoEstado(): según diagrama -> FueraDeServicio
    @Override
    public Estado crearProximoEstado() {
        //Metodo 8 del patron
        return new FueraDeServicio();
    }

    // Metodo 9 del patron
   @Override
   public CambioEstado crearCambioEstado(Estado proximoEstado,
                                          LocalDateTime fechaHoraInicio,
                                          List<MotivoFueraDeServicio> motivos,
                                          Empleado RILogueado) {
        // Metodo 10 del patron
        // Aquí se realiza el 'new' encapsulado
        return new CambioEstado(proximoEstado, fechaHoraInicio, motivos, RILogueado);}

}