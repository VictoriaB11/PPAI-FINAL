// java
package org.example.Modelos;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Estado base para el patrón State.
 * Ajustar tipos de MotivoFueraDeServicio, CambioEstado y Empleado según tu modelo.
 */
public abstract class Estado {

    protected String nombre;
    protected String descripcion;
    protected String ambito;

    protected Estado() {
        // constructor para frameworks/deserialización
    }

    protected Estado(String nombre, String descripcion, String ambito) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ambito = ambito;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getAmbito() { return ambito; }

    public boolean esAmbitoOrdenDeInspeccion() {
        if (ambito == null) return false;
        String a = ambito.trim();
        return a.equalsIgnoreCase("Orden de Inspeccion")
                || a.equalsIgnoreCase("Orden de Inspección")
                || a.equalsIgnoreCase("OrdenDeInspeccion");
    }

    public boolean esAmbitoSismografo() {
        return ambito != null && ambito.equalsIgnoreCase("Sismografo");
    }

    /* Queries polimórficas: por defecto false, los estados concretos sobreescriben lo que corresponda */
    public boolean esCerrada() { return false; }
    public boolean esCompletamenteRealizada() { return false; }
    public boolean esFueraDeServicio() { return false; }
    public boolean esInhabilitadoPorInspeccion() { return false; }

    /* Contratos que deben implementar los estados concretos */
    public abstract Estado crearProximoEstado();

    public abstract CambioEstado crearCambioDeEstado(LocalDateTime fechaInicio,
                                                     List<MotivoFueraDeServicio> motivos,
                                                     Empleado responsable);

    /**
     * Implementación por defecto de ponerEnReparacion:
     * - crea el nuevo CambioEstado delegando a la subclase
     * - delega a Sismografo la responsabilidad de cerrar el anterior y registrar el nuevo
     *
     * Asume la existencia de la firma: Sismografo.setEstadoActual(CambioEstado)
     */
    public void ponerEnReparacion(Sismografo sismografo,
                                  LocalDateTime fechaHoraInicio,
                                  List<MotivoFueraDeServicio> motivos,
                                  Empleado responsable) {
        if (sismografo == null || fechaHoraInicio == null) {
            throw new IllegalArgumentException("Sismografo y fechaHoraInicio no pueden ser nulos");
        }

        CambioEstado nuevo = crearCambioDeEstado(fechaHoraInicio, motivos, responsable);
        if (nuevo != null) {
            // delegar en Sismografo el cierre del último cambio y el registro del nuevo
            sismografo.setEstadoActual(nuevo);
        }
    }
}