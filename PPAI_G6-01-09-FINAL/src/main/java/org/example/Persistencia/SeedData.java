//“Carga datos iniciales (datos semilla)”
//  Se encarga de meter los datos base que tu sistema necesita para funcionar, al menos en modo prueba.

package org.example.Persistencia;

public class SeedData {
    public static void seedMotivos() {
        // Método estático para cargar motivos “por defecto”.

        MotivoTipoDAO dao = new MotivoTipoDAO();
        // Creamos el DAO para poder insertar en la tabla.

        dao.guardarSiNoExiste("Falta calibracion");
        // Inserta el motivo si no existe.

        dao.guardarSiNoExiste("Sensor dañado");
        // Inserta el motivo si no existe.
    }

    public static void seedEstados() {
        // Carga los estados típicos del CU37 si no existen.

        EstadoDAO dao = new EstadoDAO();
        // DAO para insertar

        dao.guardarSiNoExiste("Completamente Realizada", "...", "Orden de Inspeccion");
        // Estado para órdenes

        dao.guardarSiNoExiste("Cerrada", "...", "Orden de Inspeccion");
        // Estado para órdenes

        dao.guardarSiNoExiste("Fuera de Servicio", "...", "Sismografo");
        // Estado para sismógrafos

        dao.guardarSiNoExiste("Inhabilitado por inspeccion", "...", "Sismografo");
        // Estado para sismógrafos
    }

}
