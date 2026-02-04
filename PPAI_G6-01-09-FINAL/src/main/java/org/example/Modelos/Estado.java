package org.example.Modelos;

import jakarta.persistence.*;

@Entity
@Table(name = "estado_orden")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //para la herencia, toda la jerarquía se guarda en UNA tabla
public class Estado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private String ambito;

    public Estado() {
    }

    public Estado(String nombre, String descripcion, String ambito) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ambito = ambito;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getAmbito() {
        return ambito;
    }

    public void setAmbito(String ambito) {
        this.ambito = ambito;
    }

    // Paso 11: esAmbitoOrdenDeInspeccion() Devuelve true si el valor de 'ambito' es exactamente "Orden de Inspección"
    public boolean esAmbitoOrdenDeInspeccion() {
        return "Orden de Inspeccion".equalsIgnoreCase(ambito);
    }

    //Paso 11: esCerrada() Devuelve true si el nombre del estado es "Cerrada"
    public boolean esCerrada() {
        return "Cerrada".equalsIgnoreCase(nombre);
    }

    public boolean esCompletamenteRealizada() {
        return "Completamente Realizada".equalsIgnoreCase(nombre);
    }

}