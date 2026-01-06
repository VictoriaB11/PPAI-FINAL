package org.example.Modelos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map; // <-- necesario para el Map<MotivoTipo, String>

public class Sismografo {

    private LocalDate fechaAdquisicion;
    private Integer identificadorSismografo;
    private Integer nroSerie;
    private List<CambioEstado> historialEstados = new ArrayList<>();
    private CambioEstado estadoActual;
    private EstacionSismologica estacionSismologica;

    public Sismografo() {
    }

    public Sismografo(LocalDate fechaAdquisicion, Integer identificadorSismografo, Integer nroSerie, List<CambioEstado> historialEstados) {
        this.fechaAdquisicion = fechaAdquisicion;
        this.identificadorSismografo = identificadorSismografo;
        this.nroSerie = nroSerie;
        this.historialEstados = historialEstados;
    }

    public EstacionSismologica getEstacionSismologica() {
        return estacionSismologica;
    }

    public void setEstacionSismologica(EstacionSismologica estacionSismologica) {
        this.estacionSismologica = estacionSismologica;
    }

    public LocalDate getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    public void setFechaAdquisicion(LocalDate fechaAdquisicion) {
        this.fechaAdquisicion = fechaAdquisicion;
    }

    public void setIdentificadorSismografo(Integer identificadorSismografo) {
        this.identificadorSismografo = identificadorSismografo;
    }

    public Integer getNroSerie() {
        return nroSerie;
    }

    public void setNroSerie(Integer nroSerie) {
        this.nroSerie = nroSerie;
    }

    public List<CambioEstado> getHistorialEstados() {
        return historialEstados;
    }

    public void setHistorialEstados(List<CambioEstado> historialEstados) {
        this.historialEstados = historialEstados;
    }

    public CambioEstado getEstadoActual() {
        return estadoActual;
    }


    // PASO 2
    public boolean esDeMiEstacion(EstacionSismologica estacion) {
        return this.estacionSismologica != null && this.estacionSismologica.equals(estacion);
    }

    public Integer getIdentificadorSismografo() {
        return identificadorSismografo;
    }
//FIN PASO 2

    /**
     * Pone el sismógrafo en reparación (Fuera de Servicio) creando el CambioEstado
     * y construyendo los MotivoFueraDeServicio a partir del mapa MotivoTipo -> comentario.
     */
    public void ponerEnReparacion(Estado estadoFueraServicio,
                                  Empleado responsable,
                                  Map<MotivoTipo, String> motivosYComentarios) {
        LocalDateTime fechaHoraActual = LocalDateTime.now();

        // Finalizar el cambio de estado actual
        CambioEstado cambioActual = this.buscarUltimoCambioEstado();
        if (cambioActual != null && cambioActual.esEstadoActual()) {
            cambioActual.setFechaHoraFin(fechaHoraActual);
        }

        // Crear nuevo cambio de estado
        CambioEstado nuevoCambio = new CambioEstado();
        nuevoCambio.setEstado(estadoFueraServicio);
        nuevoCambio.setFechaHoraInicio(fechaHoraActual);
        nuevoCambio.setEmpleadoResponsable(responsable);

        // Construir motivos a partir del mapa (usa el metodo nuevo de CambioEstado)
        nuevoCambio.crearMotivosFueraDeServicio(motivosYComentarios);

        // Registrar en historial y actualizar estado actual
        historialEstados.add(nuevoCambio);
        this.setEstadoActual(nuevoCambio);
    }

    public CambioEstado buscarUltimoCambioEstado() {
        for (CambioEstado cambio : historialEstados) {
            if (cambio.esEstadoActual()) {
                return cambio;
            }
        }
        return null;
    }

    public void setEstadoActual(CambioEstado cambio) {
        this.estadoActual = cambio;
    }
}
