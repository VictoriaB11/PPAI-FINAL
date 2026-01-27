package org.example.Modelos;

public class Empleado {

    private Integer idEmpleado;
    private String apellido;
    private String nombre;
    private String mail;
    private String telefono;
    private Rol rol;

    public Empleado() {
    }

    public Empleado(Integer idEmpleado, String apellido, String nombre, String mail, String telefono, Rol rol) {
        this.idEmpleado = idEmpleado;
        this.apellido = apellido;
        this.nombre = nombre;
        this.mail = mail;
        this.telefono = telefono;
        this.rol = rol;
    }

    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
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
