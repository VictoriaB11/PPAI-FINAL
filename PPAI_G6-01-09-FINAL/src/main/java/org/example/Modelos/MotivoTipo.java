/**
 * Entidad persistente: representa un tipo de motivo que el usuario puede seleccionar.
 */
package org.example.Modelos;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "motivo_tipo")
public class MotivoTipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String descripcion;


    protected MotivoTipo() {
    }

    public MotivoTipo(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
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
}