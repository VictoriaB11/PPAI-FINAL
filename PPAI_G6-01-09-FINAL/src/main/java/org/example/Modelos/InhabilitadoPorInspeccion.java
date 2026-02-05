package org.example.Modelos;

import java.time.LocalDateTime;
//import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.persistence.*;


@Entity
@DiscriminatorValue("InhabilitadaPorInspeccion")
public class InhabilitadoPorInspeccion extends EstadoSismografo {

    public InhabilitadoPorInspeccion() {
        super("InhabilitadoPorInspeccion", "Inhabilitado por inspección", "Sismografo");
    }

    //Metodo 3 del patron
    // Metodo polimórfico: gestiona la transición a FueraDeServicio
    @Override
    public void ponerEnReparacion(Sismografo sismografo,
                                  LocalDateTime fechaHoraActual,
                                  List<CambioEstado> historialCambioEstado,
                                  Map<MotivoTipo, String> motivos,
                                  Empleado RILogueado) {

        // PASO A: Buscar el cambio de estado actual (SELF)
        // Diagrama: InhabilitadoPorInspeccion -> buscarCambioDeEstadoActual()
        CambioEstado ultimoCambio = this.buscarCambioDeEstadoActual(historialCambioEstado);

        // PASO B: Cerrar el estado anterior
        // Diagrama: ultimo:CambioEstado -> setFechaHoraFin(...)
        if (ultimoCambio != null) {
            ultimoCambio.setFechaHoraFin(fechaHoraActual);
        }


        // PASO C: Crear el próximo estado (SELF)
        // Diagrama: InhabilitadoPorInspeccion -> crearProximoEstado()
        EstadoSismografo proximoEstadoSismografo = this.crearProximoEstado();

        // PASO D: Crear el nuevo CambioEstado (new)
        // Usamos el constructor que definimos en CambioEstado para "nuevos" estados
        // y se registran los motivos asociados
        CambioEstado nuevoCambio = this.crearCambioEstado(proximoEstadoSismografo, fechaHoraActual, motivos, RILogueado);
        nuevoCambio.crearMotivosFueraDeServicio(motivos);

        // PASO E: Actualizar Sismógrafo
        // Se registra el nuevo cambio en el historial
        // y se actualiza el estado actual del sismógrafo
        if (sismografo.getHistorialEstados() != null) {
            sismografo.getHistorialEstados().add(nuevoCambio);
        }
        sismografo.agregarCambioEstado(nuevoCambio);
        sismografo.setEstadoActual(nuevoCambio);
    }

    // Metodo 4 del patrón
    // Busca en el historial el cambio de estado actual,
    // delegando en cada CambioEstado la responsabilidad
    // de determinar si sigue vigente mediante esEstadoActual()
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
    public EstadoSismografo crearProximoEstado() {
        //Metodo 8 del patron
        return new FueraDeServicio();
    }

    // Metodo 9 del patron
    // Crea e inicia el nuevo cambioEstado del sismografo
    @Override
    // Primero recibe los parametros
    public CambioEstado crearCambioEstado(EstadoSismografo proximoEstadoSismografo,
                                          LocalDateTime fechaHoraInicio,
                                          Map<MotivoTipo, String> motivos,
                                          Empleado RILogueado) {
        // Metodo 10 del patron
        // el new instancia el CambioEstado y delega la creacion al constructor
        // el return devuelve el cambio recien creado
        return new CambioEstado(proximoEstadoSismografo, fechaHoraInicio, RILogueado);
    }
}