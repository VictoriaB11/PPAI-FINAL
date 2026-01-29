package org.example.Modelos;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@DiscriminatorValue("FUERA_DE_SERVICIO")
public class FueraDeServicio extends Estado {

    public FueraDeServicio() {
        super("FueraDeServicio", "El sismografo está fuera de servicio", "Sismografo");
    }

    @Override
    public CambioEstado crearCambioEstado(Estado proximoEstado,
                                          LocalDateTime fechaHoraInicio,
                                          List<MotivoFueraDeServicio> motivos,
                                          Empleado RILogueado) {
        CambioEstado nuevoCambio = new CambioEstado();
        nuevoCambio.setEstado(crearProximoEstado());
        return nuevoCambio;
    }

    @Override
    public boolean esFueraDeServicio() {
        return true;
    }


    // Estos metodos fueron implementados por una regla de java llamada Contrato de Clases Abstractas.
    // todas las clases hijas están obligadas a tener ese metodo escrito en su código.
    @Override
    public Estado crearProximoEstado() {
        throw new RuntimeException(
                "El estado FueraDeServicio no tiene un próximo estado automático."
        );
    }


    @Override
    public void ponerEnReparacion(Sismografo sismografo,
                                  java.time.LocalDateTime fecha,
                                  java.util.List<CambioEstado> cambios,
                                  java.util.List<MotivoFueraDeServicio> motivos,
                                  Empleado responsable) {
        // No se puede poner en reparación algo que ya está fuera de servicio (o depende de tu regla de negocio)
        throw new RuntimeException("El sismógrafo ya se encuentra Fuera de Servicio.");
    }

    @Override
    public String getNombre() {
        return "FueraDeServicio";
    }
}
