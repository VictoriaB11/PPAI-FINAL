package org.example.Modelos;

public class FueraDeServicio implements Estado {

    @Override
    public boolean esEstadoActual() {
        return true;
    }

    @Override
    public Estado crearProximoEstado() {
        throw new UnsupportedOperationException(
                "No se puede cambiar automáticamente desde FueraDeServicio"
        );
    }

    @Override
    public String getNombre() {
        return "FueraDeServicio";
    }
}
