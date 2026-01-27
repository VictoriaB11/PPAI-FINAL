package org.example.Modelos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        this.estadoActual = buscarUltimoCambioEstado(); //ns si va
    }

    public void ponerEnReparacion(LocalDateTime fechaHoraActual,
                                  List<MotivoFueraDeServicio> motivos,
                                  Empleado RILogueado) {

        // Validamos que tengamos un estado actual cargado
        if (this.estadoActual != null && this.estadoActual.getEstado() != null) {

            // DELEGACIÓN: El sismógrafo le dice a su estado "encárgate tú"
            this.estadoActual.getEstado().ponerEnReparacion(
                    this,               // Le pasamos el contexto (el sismógrafo mismo)
                    fechaHoraActual,
                    this.historialEstados, // Le pasamos el historial
                    motivos,
                    RILogueado
            );
        } else {
            throw new IllegalStateException("El sismógrafo no tiene un estado actual válido para realizar la operación.");
        }
    }

    public void agregarCambioEstado(CambioEstado cambio) {
        if (this.historialEstados == null) {
            this.historialEstados = new ArrayList<>();
        }
        this.historialEstados.add(cambio);

    }

    // Este metodo recibe CambioEstado porque en InhabilitadoPorInspeccion hacemos:
    // sismografo.setEstadoActual(nuevoCambio);
    public void setEstadoActual(CambioEstado cambioEstado) {
        this.estadoActual = cambioEstado;
    }

    //Metodos getters y setters

    public CambioEstado buscarUltimoCambioEstado() {
        // Opción A: Iterar buscando el que es actual
        if (historialEstados != null) {
            for (CambioEstado cambio : historialEstados) {
                if (cambio.esEstadoActual()) {
                    return cambio;
                }
            }
        }
        // Opción B: Si mantenemos la variable 'estadoActual' siempre actualizada,
        // simplemente retornamos esa variable.
        return this.estadoActual;
    }


    public boolean esDeMiEstacion(EstacionSismologica estacion) {
        return this.estacionSismologica != null && this.estacionSismologica.equals(estacion);
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

    public Integer getIdentificadorSismografo() {
        return identificadorSismografo;
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
}