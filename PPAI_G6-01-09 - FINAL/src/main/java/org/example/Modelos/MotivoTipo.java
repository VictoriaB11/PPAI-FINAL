package org.example.Modelos;

import java.util.Objects;

public class MotivoTipo {

    private String descripcion;

    public MotivoTipo(String descripcion) {
        this.descripcion = descripcion;
    }


    //Paso 6 del caso de uso: obtener la descripción del motivo.
    /**
     * Este metodo devuelve el texto descriptivo del motivo, que será mostrado
     * en la interfaz gráfica para que el usuario lo seleccione.
     * Devuelve la descripción textual del motivo.
     */
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MotivoTipo)) return false;
        MotivoTipo that = (MotivoTipo) o;
        return Objects.equals(descripcion, that.descripcion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descripcion);
    }

    public void setComentario(String comentario) {
    }
}