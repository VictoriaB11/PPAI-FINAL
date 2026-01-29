package org.example.Main;

import org.example.Modelos.*;
import org.example.Vistas.MenuPrincipal;

public class Main{
    public static void main(String[] args) {

        // 1️⃣ Simular usuario logueado
        Rol rol = new Rol();
        rol.setNombre("Responsable de Reparación");

        Empleado empleado = new Empleado();
        empleado.setNombre("Juan");
        empleado.setApellido("Perez");
        empleado.setRol(rol);

        Usuario usuario = new Usuario();
        usuario.setEmpleado(empleado);

        Sesion sesion = new Sesion(usuario);

        // 2️⃣ Abrir menú principal (esto dispara TODO)
        new MenuPrincipal(sesion);
    }
}
