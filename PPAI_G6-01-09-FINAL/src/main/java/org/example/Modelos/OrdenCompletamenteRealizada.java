package org.example.Modelos;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@DiscriminatorValue("ORDEN_COMPLETAMENTE_REALIZADA")
public class OrdenCompletamenteRealizada extends Estado {

    public OrdenCompletamenteRealizada() {
        super("CompletamenteRealizada", "Orden completamente realizada", "OrdenDeInspeccion");
    }

    @Override
    public boolean esCompletamenteRealizada() { return true; }

    @Override
    public Estado crearProximoEstado() {
        return new OrdenCerrada();
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
