package org.example.Modelos;

public class InhabilitadoPorInspeccion implements Estado {

    @Override
    public boolean esEstadoActual() {
        return true;
    }

    @Override
    public Estado crearProximoEstado() {
        return new FueraDeServicio();
    }

    @Override
    public String getNombre() {
        return "HabilitadoPorInspeccion";
    }
}
