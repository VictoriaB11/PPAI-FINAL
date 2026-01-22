//Se encarga de crear lass tablas si todavía no existe.

package org.example.Persistencia;
import java.sql.Connection;
import java.sql.Statement;
// Statement permite ejecutar una sentencia SQL simple (sin parámetros).

public class DbInit {
    public static void init() {
        // Método que se llama al inicio del programa para asegurar que existan las tablas.

        try (Connection conn = Database.getConnection();
             // Abre conexión a la BD usando nuestra clase Database.
             // try-with-resources: cuando termina, cierra automáticamente la conexión.

             Statement st = conn.createStatement()) {
            // Crea un Statement a partir de la conexión para ejecutar SQL.
            st.execute("PRAGMA foreign_keys = ON;"); //Activar clases foraneas


            //MOTIVO TIPO
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS motivo_tipo (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    descripcion TEXT NOT NULL UNIQUE
                );
            """);
            // executeUpdate se usa para SQL que modifica estructura/datos (CREATE/INSERT/UPDATE/DELETE).
            // CREATE TABLE IF NOT EXISTS: crea la tabla solo si no existe.
            // id: clave primaria autoincremental
            // descripcion: texto obligatorio
            // UNIQUE: evita que se repita el mismo motivo si corremos seed varias veces.

                                     //ESTADO
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS estado (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    descripcion TEXT,
                    ambito TEXT NOT NULL,
                    UNIQUE(nombre, ambito)
                );
            """);
            // Crea tabla estado si no existe.
            // nombre: nombre del estado (ej "Cerrada").
            // descripcion: texto libre (puede ser "...").
            // ambito: a qué objeto aplica (ej "Orden de Inspeccion" o "Sismografo").
            // UNIQUE(nombre, ambito): evita duplicados si corrés seed varias veces.


                                     //SISMOGRAFO
                        st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS sismografo (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    estado_actual_cambio_id INTEGER,
                    FOREIGN KEY (estado_actual_cambio_id) REFERENCES cambio_estado(id)
                );
            """);
            // Guarda sismógrafos.
                // estado_actual_cambio_id apunta al último cambio de estado (cambio_estado).

                                     //CAMBIO_ESTADO
                        st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS cambio_estado (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    sismografo_id INTEGER NOT NULL,
                    estado_id INTEGER NOT NULL,
                    fecha_hora TEXT NOT NULL,
                    empleado_nombre TEXT,
                    FOREIGN KEY (sismografo_id) REFERENCES sismografo(id),
                    FOREIGN KEY (estado_id) REFERENCES estado(id)
                );
            """);
            // Guarda cada cambio de estado del sismógrafo (historial).
              // fecha_hora TEXT: lo guardamos como string ISO (ej: 2026-01-08T20:30:00).

                             //CAMBIO_ESTADO_MOTIVO
                        st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS cambio_estado_motivo (
                    cambio_estado_id INTEGER NOT NULL,
                    motivo_tipo_id INTEGER NOT NULL,
                    PRIMARY KEY (cambio_estado_id, motivo_tipo_id),
                    FOREIGN KEY (cambio_estado_id) REFERENCES cambio_estado(id),
                    FOREIGN KEY (motivo_tipo_id) REFERENCES motivo_tipo(id)
                );
            """);
            // Relación muchos-a-muchos:
            //  un cambio_estado puede tener varios motivo_tipo.



        } catch (Exception e) {
            // Capturamos cualquier error para cortar el programa con un mensaje.

            throw new RuntimeException("Error inicializando DB", e);
            // RuntimeException hace que el fallo sea “fatal” y no lo ignores.
            // Incluimos el error original (e) para ver el detalle en consola.
        }
    }
}
