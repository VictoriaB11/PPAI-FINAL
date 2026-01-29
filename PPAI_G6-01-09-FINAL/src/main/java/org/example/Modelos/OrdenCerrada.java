package org.example.Modelos;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@DiscriminatorValue("ORDEN_CERRADA")
public class OrdenCerrada extends Estado {

    public OrdenCerrada() {
        super("Cerrada", "Orden cerrada", "OrdenDeInspeccion");
    }

    @Override
    public boolean esCerrada() { return true; }

    @Override
    public Estado crearProximoEstado() {
        throw new UnsupportedOperationException("No tiene próximo estado.");
    }

    @Override
    public CambioEstado crearCambioEstado(Estado proximoEstado, LocalDateTime fechaHoraInicio,
                                          List<MotivoFueraDeServicio> motivos, Empleado RILogueado) {
        throw new UnsupportedOperationException("No aplica a orden de inspección.");
    }

    @Override
    public void ponerEnReparacion(Sismografo sismografo, LocalDateTime fechaHoraInicio,
                                  List<CambioEstado> cambiosEstado, List<MotivoFueraDeServicio> motivos, Empleado RILogueado) {
        throw new UnsupportedOperationException("No aplica a orden de inspección.");
    }
}
