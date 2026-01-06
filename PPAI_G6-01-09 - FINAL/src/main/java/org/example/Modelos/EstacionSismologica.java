package org.example.Modelos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EstacionSismologica {

    private Integer codEstacion;
    private LocalDate fechaSolicitudCertificacion;
    private boolean certificacionDeAdquisicion;
    private double latitud;
    private double longitud;
    private String nombreEstacionSismologica;
    private Integer nroCertificacionAdquisicion;
    private Sismografo sismografo;

    //PASO 2: Para reflejar el loop del diagrama (puede haber varios sismógrafos)
    private List<Sismografo> sismografos = new ArrayList<>();

    public EstacionSismologica() { }

    public EstacionSismologica(Integer codEstacion, LocalDate fechaSolicitudCertificacion, boolean certificacionDeAdquisicion,
                               double latitud, double longitud, String nombreEstacionSismologica,
                               Integer nroCertificacionAdquisicion, Sismografo sismografo) {
        this.codEstacion = codEstacion;
        this.fechaSolicitudCertificacion = fechaSolicitudCertificacion;
        this.certificacionDeAdquisicion = certificacionDeAdquisicion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.nombreEstacionSismologica = nombreEstacionSismologica;
        this.nroCertificacionAdquisicion = nroCertificacionAdquisicion;
        this.sismografo = sismografo;
        if (sismografo != null) {
            sismografo.setEstacionSismologica(this);
            this.sismografos.add(sismografo);
        }
    }

    public Integer getCodEstacion() { return codEstacion; }
    public void setCodEstacion(Integer codEstacion) { this.codEstacion = codEstacion; }

    public LocalDate getFechaSolicitudCertificacion() { return fechaSolicitudCertificacion; }
    public void setFechaSolicitudCertificacion(LocalDate fechaSolicitudCertificacion) { this.fechaSolicitudCertificacion = fechaSolicitudCertificacion; }

    public boolean isCertificacionDeAdquisicion() { return certificacionDeAdquisicion; }
    public void setCertificacionDeAdquisicion(boolean certificacionDeAdquisicion) { this.certificacionDeAdquisicion = certificacionDeAdquisicion; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public void setNombre(String nombre) { this.nombreEstacionSismologica = nombre; }

    public Integer getNroCertificacionAdquisicion() { return nroCertificacionAdquisicion; }
    public void setNroCertificacionAdquisicion(Integer nroCertificacionAdquisicion) { this.nroCertificacionAdquisicion = nroCertificacionAdquisicion; }

    //Devuelve el sismógrafo ‘principal’ de la estación
    public Sismografo getSismografo() { return sismografo; }

    public void setSismografo(Sismografo sismografo) {
        //Setea el sismógrafo principal de la estación con el que llega por parámetro
        this.sismografo = sismografo;
        if (sismografo != null) {
            //Completa la relación bidireccional: le dice al sismógrafo que su estación es esta.
            //this =  instancia de EstacionSismologica donde estás parado.
            sismografo.setEstacionSismologica(this);
            //Si la lista sismografos aún no lo contiene, lo agrega para mantener consistente la colección.
            if (!this.sismografos.contains(sismografo)) this.sismografos.add(sismografo);
        }
    }

    // PASO 2
    public Integer obtenerIdentificadorSismografo() {
        if (sismografo != null && sismografo.esDeMiEstacion(this)) {
            return sismografo.getIdentificadorSismografo();
        }
        return buscarIdentificadorSismografo();
    }

    // FLECHA: Estación.buscarIdentificadorSismografo()  (hace el loop)
    public Integer buscarIdentificadorSismografo() {
        if (sismografos != null) {
            for (Sismografo s : sismografos) {          // loop Sismógrafo
                if (s.esDeMiEstacion(this)) {           // flecha esDeMiEstacion()
                    return s.getIdentificadorSismografo(); // flecha getIdentificadorSismografo()
                }
            }
        }
        return null; // sin sismógrafo asociado visible para la UI del paso 2
    }

    public String getNombre() {
        return nombreEstacionSismologica;
    }
    //FIN PASO 2
}