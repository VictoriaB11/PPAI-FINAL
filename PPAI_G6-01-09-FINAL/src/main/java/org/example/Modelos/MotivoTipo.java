/**
 * Entidad persistente: representa un tipo de motivo que el usuario puede seleccionar.
 */
package org.example.Modelos;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "motivo_tipo")
public class MotivoTipo {

    /**
     * Clave primaria autogenerada.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Descripción visible en la UI
     */
    @Column(nullable = false, unique = true)
    private String descripcion;

    /**
     * Constructor vacío obligatorio para JPA.
     * (Hibernate lo usa para reconstruir objetos al leer de BD)
     */

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MotivoTipo)) return false;
        MotivoTipo that = (MotivoTipo) o;

        /**
         * equals:
         * - Si ya tienen id (persistido), comparamos por id.
         * Dos entidades son iguales solo si:
         *                       ambas tienen ID y ese ID es el mismo
         * evita bugs típicos cuando el objeto cambia de estado .
         */
        return id != null && Objects.equals(id, that.id);    }

    /**
     * En entidades JPA el hashCode debe ser estable durante todo el ciclo de vida
     * del objeto. Como el ID se asigna recién al persistir, no se utiliza en el
     * cálculo del hashCode para evitar inconsistencias en colecciones y en el
     * manejo interno de Hibernate.
     * Se retorna un valor constante (convención habitual: 31).
     */
    @Override
    public int hashCode() {
        return 31;
    }

    public void setComentario(String comentario) {
    }
}