// java
package org.example.Modelos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import jakarta.persistence.*;


/**
 * Estado base para el patrón State.
 * Ajustar tipos de MotivoFueraDeServicio, CambioEstado y Empleado según tu modelo.
 */

@Entity
@Table(name = "estado_sismografo")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //Para que todas las subclases de Estado se guardan en UNA sola tabla
@DiscriminatorColumn(name = "tipo_estado")  //Es una columna extra que Hibernate agrega para saber: “Esta fila corresponde a qué subclase concreta”.
public abstract class EstadoSismografo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEstado;

    @Column(nullable = false)
    protected String nombre;

    protected String descripcion;
    protected String ambito;

    protected EstadoSismografo() {
        // constructor para frameworks/deserialización
    }

    protected EstadoSismografo(String nombre, String descripcion, String ambito) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ambito = ambito;
    }

    public Long getIdEstado() { return idEstado; }
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

    /* Contratos que deben implementar los estados concretos */
    public abstract EstadoSismografo crearProximoEstado();

    public abstract CambioEstado crearCambioEstado(EstadoSismografo proximoEstadoSismografo,
                                                   LocalDateTime fechaHoraInicio,
                                                   Map<MotivoTipo, String> motivos,
                                                   Empleado RILogueado);


    /**
     * Implementación por defecto de ponerEnReparacion:
     * - crea el nuevo CambioEstado delegando a la subclase
     * - delega a Sismografo la responsabilidad de cerrar el anterior y registrar el nuevo
     *
     * Asume la existencia de la firma: Sismografo.setEstadoActual(CambioEstado)
     */
    public abstract void ponerEnReparacion(Sismografo sismografo,
                                  LocalDateTime fechaHoraInicio,
                                  List<CambioEstado> cambiosEstado,
                                  Map<MotivoTipo, String> motivos,
                                  Empleado RILogueado);
}