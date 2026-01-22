package org.example.Modelos;

public class Rol {

    private String descripcion;
    private String nombre;

    public Rol() {
    }

    public Rol(String descripcion, String nombre) {
        this.descripcion = descripcion;
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

// Paso 13: Compara si el rol que tiene el empleado es responsable de reparacion
    public boolean esResponsableDeReparacion() {
        return this.nombre.equalsIgnoreCase("Responsable de Reparación");
    }
}
