package org.example.Modelos;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "sismografo")
public class Sismografo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_adquisicion")
    private LocalDate fechaAdquisicion;

    /**
     * Identificador funcional del sismógrafo, lo marcamos unique para que no se repita.
     */
    @Column(name = "identificador_sismografo", unique = true)
    private Integer identificadorSismografo;

    @Column(name = "nro_serie")
    private Integer nroSerie;

    /**
     * Historial persistido.
     * mappedBy: el "dueño" de la relación está en CambioEstado.sismografo
     * cascade: si guardo sismografo, se guardan sus cambios de estado.
     * orphanRemoval: si saco un cambio del historial, se borra en DB.
     */
    @OneToMany(mappedBy = "sismografo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CambioEstado> historialEstados = new ArrayList<>();

    /**
     * No lo persistimos: lo calculamos del historial (el que tiene fechaHoraFin = null).
     */
    @Transient
    private CambioEstado ultimoCambioEstado;

    @ManyToOne
    @JoinColumn(name = "estacion_id")
    private EstacionSismologica estacionSismologica;

    public Sismografo() {
    }

    public Sismografo(LocalDate fechaAdquisicion, Integer identificadorSismografo, Integer nroSerie, List<CambioEstado> historialEstados) {
        this.fechaAdquisicion = fechaAdquisicion;
        this.identificadorSismografo = identificadorSismografo;
        this.nroSerie = nroSerie;
    }

    // Metodo 2 del patrón
    public void ponerEnReparacion(LocalDateTime fechaHoraActual,
                                  Map<MotivoTipo, String> motivos,
                                  Empleado RILogueado) {

        // Recalculamos el estado actual desde el historial persistido
        this.ultimoCambioEstado = buscarUltimoCambioEstado();

        // Validamos que tengamos un estado actual cargado
        if (this.ultimoCambioEstado != null && this.ultimoCambioEstado.getEstado() != null) {

            // El sismografo le delega a su espado la logica del cambio de estado
            this.ultimoCambioEstado.getEstado().ponerEnReparacion(
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

    //Metodo 13 del patron (recordar que falta el 12, el new)
    public void agregarCambioEstado(CambioEstado cambio) {
        if (this.historialEstados == null) {
            this.historialEstados = new ArrayList<>();
        }
        // Para PERSISTENCIA:
        // setear el back-reference para JPA:
        // es: además de agregar el cambio a la lista, también decirle al CambioEstado quién es su Sismografo.
        //Porque el dueño de esta relacion ManyToOne en Cambio de estado es la FK de sismogafo
        cambio.setSismografo(this);

        this.historialEstados.add(cambio);

        // Mantener referencia al "actual" en memoria
        this.ultimoCambioEstado = cambio;


    }

    //Metodo 14 del patron
    // Este metodo recibe CambioEstado porque en InhabilitadoPorInspeccion hacemos:
    // sismografo.setEstadoActual(nuevoCambio);
    public void setEstadoActual(CambioEstado ultimoCambioEstado) {
        this.ultimoCambioEstado = ultimoCambioEstado;
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
        return this.ultimoCambioEstado;
    }
    // =========================
    // Getters / Setters
    // =========================

    public Long getId() {
        return id;
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
        return buscarUltimoCambioEstado();
    }

    public EstacionSismologica getEstacionSismologica() {
        return estacionSismologica;
    }

    public void setEstacionSismologica(EstacionSismologica estacionSismologica) {
        this.estacionSismologica = estacionSismologica;
    }

    public boolean esDeMiEstacion(EstacionSismologica estacion) {
        return this.estacionSismologica != null && this.estacionSismologica.equals(estacion);
    }
}