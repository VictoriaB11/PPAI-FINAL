package org.example.Modelos;

import java.time.LocalDateTime;
import java.util.List;

public class FueraDeServicio extends Estado {

    public FueraDeServicio() {
        super("FueraDeServicio", "El sismografo está fuera de servicio", "Sismografo");
    }

    @Override
    public CambioEstado crearCambioEstado() {
        CambioEstado nuevoCambio = new CambioEstado();
        nuevoCambio.setEstado(crearProximoEstado());
        return nuevoCambio;
    }

    @Override
    public boolean esFueraDeServicio() {
        return true;
    }

    @Override
    public Estado crearProximoEstado() {
        throw new UnsupportedOperationException(
                "No se puede cambiar automáticamente desde FueraDeServicio"
        );
    }

    // aquí debes implementarlo (probablemente lanzando error o no haciendo nada).
    // Este metodo fue implementado por una regla de java llamada Contrato de Clases Abstractas.
    // todas las clases hijas están obligadas a tener ese metodo escrito en su código.

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
