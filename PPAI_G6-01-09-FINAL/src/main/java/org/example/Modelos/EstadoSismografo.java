package org.example.Modelos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import jakarta.persistence.*;

//EstadoSismografo hereda de Estado.
@Entity
@DiscriminatorValue("estado_sismografo") //etiqueta para saber que clase es
public abstract class EstadoSismografo extends Estado {

    // NOTA: Se eliminó 'idEstado', 'nombre', 'descripcion' y 'ambito' porque ya existen en 'Estado'.

    protected EstadoSismografo() {
        super(); // Llama al constructor vacío de Estado
    }

    protected EstadoSismografo(String nombre, String descripcion, String ambito) {
        super(nombre, descripcion, ambito); // Pasa los valores al padre
    }

    // No necesitamos getNombre(), getDescripcion(), etc., porque se heredan de Estado.
    // Si necesitas acceder al ID, usa getId() (heredado de Estado).

    @Override
    public boolean esAmbitoOrdenDeInspeccion() {
        // Mantenemos tu lógica específica que es más robusta que la del padre (maneja nulls y acentos)
        String a = getAmbito(); // Usamos el getter heredado
        if (a == null) return false;
        a = a.trim();
        return a.equalsIgnoreCase("Orden de Inspeccion")
                || a.equalsIgnoreCase("Orden de Inspección")
                || a.equalsIgnoreCase("OrdenDeInspeccion");
    }

    public boolean esAmbitoSismografo() {
        // Usamos el getter heredado
        return getAmbito() != null && getAmbito().equalsIgnoreCase("Sismografo");
    }

    /* Queries polimórficas: Sobreescribimos los métodos del padre para el patrón State */

    @Override
    public boolean esCerrada() {
        return false;
    }

    @Override
    public boolean esCompletamenteRealizada() {
        return false;
    }

    // Este método no existe en Estado, es propio de Sismografo
    public boolean esFueraDeServicio() {
        return false;
    }

    /* Contratos que deben implementar los estados concretos */
    public abstract EstadoSismografo crearProximoEstado();

    public abstract CambioEstado crearCambioEstado(EstadoSismografo proximoEstadoSismografo,
                                                   LocalDateTime fechaHoraInicio,
                                                   Map<MotivoTipo, String> motivos,
                                                   Empleado RILogueado);

    public abstract void ponerEnReparacion(Sismografo sismografo,
                                           LocalDateTime fechaHoraInicio,
                                           List<CambioEstado> cambiosEstado,
                                           Map<MotivoTipo, String> motivos,
                                           Empleado RILogueado);
}