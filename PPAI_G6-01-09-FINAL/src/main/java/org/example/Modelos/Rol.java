package org.example.Modelos;

import jakarta.persistence.*;

@Entity
@Table(name = "rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column
    private String descripcion;

    public Rol() {
    }

    public Rol(String descripcion, String nombre) {
        this.descripcion = descripcion;
        this.nombre = nombre;
    }

    public Long getIdRol() {
        return idRol;
    }
    public void setIdRol(Long idRol) {
        this.idRol = idRol;
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
        return this.nombre != null && this.nombre.equalsIgnoreCase("Responsable de Reparación");
    }
}
