package org.example.Modelos;
import jakarta.persistence.*;

@Entity
@Table(name = "empleado")
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado")
    private Long idEmpleado;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String mail;

    private String telefono;

    @ManyToOne(optional = false) //Muchos objetos apuntan a uno solo.
    @JoinColumn(name = "rol_id")
    private Rol rol;

    public Empleado() {
    }

    public Empleado( String apellido, String nombre, String mail, String telefono, Rol rol) {
        this.apellido = apellido;
        this.nombre = nombre;
        this.mail = mail;
        this.telefono = telefono;
        this.rol = rol;
    }

    public Long getIdEmpleado() {
        return idEmpleado;
    }


    public String getApellido() {
        return apellido;
    }
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Rol getRol() {
        return rol;
    }
    public void setRol(Rol rol) {
        this.rol = rol;
    }


//Paso 13:
    public boolean esResponsableDeReparacion() {
        return rol != null && rol.esResponsableDeReparacion();
    } // Le preguntamos a la clase rol si tiene el nombre del responsable de reparacion

    public String obtenerMail() {
        return mail;
    } // Paso 13: Nos devuelve el mail del empleado
}
