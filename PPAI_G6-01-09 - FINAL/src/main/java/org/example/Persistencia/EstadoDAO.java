package org.example.Persistencia;
import org.example.Modelos.Estado; // Importa tu clase de dominio Estado
import java.sql.*; // JDBC: Connection, PreparedStatement, ResultSet, SQLException
import java.util.ArrayList; // Para armar listas
import java.util.List; // Para devolver List<Estado>

public class EstadoDAO {
    // DAO para guardar y leer Estados desde la BD.

    public void guardarSiNoExiste(String nombre, String descripcion, String ambito) {
        // Inserta un estado si no existe (gracias al UNIQUE(nombre, ambito)).

        String sql = "INSERT OR IGNORE INTO estado(nombre, descripcion, ambito) VALUES (?, ?, ?)";
        // SQL de insert con parámetros.
        // OR IGNORE evita explotar si ya existe.

        try (Connection conn = Database.getConnection();
             // Abre conexión a la BD.
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Prepara el SQL para setear parámetros.
            ps.setString(1, nombre);
            // Primer ?  -> nombre
            ps.setString(2, descripcion);
            // Segundo ? -> descripcion
            ps.setString(3, ambito);
            // Tercer ?  -> ambito
            ps.executeUpdate();
            // Ejecuta INSERT

        } catch (SQLException e) {
            // Manejo de error SQL
            throw new RuntimeException("Error guardando estado", e);
        }
    }

    public List<Estado> listarTodos() {
        // Devuelve todos los estados de la BD como objetos Estado.

        String sql = "SELECT nombre, descripcion, ambito FROM estado ORDER BY ambito, nombre";
        // Ordena para que sea estable: primero por ámbito, luego por nombre.

        List<Estado> res = new ArrayList<>();
        // Lista resultado

        try (Connection conn = Database.getConnection();
             // Conexión
             PreparedStatement ps = conn.prepareStatement(sql);
             // Prepara SELECT
             ResultSet rs = ps.executeQuery()) {
            // Ejecuta SELECT

            while (rs.next()) {
                // Recorre filas

                String nombre = rs.getString("nombre");
                // Lee columna nombre

                String descripcion = rs.getString("descripcion");
                // Lee columna descripcion

                String ambito = rs.getString("ambito");
                // Lee columna ambito

                Estado e = new Estado(nombre, descripcion, ambito);
                // Reconstruye el objeto del dominio.

                res.add(e);
                // Agrega a la lista
            }

            return res;
            // Devuelve lista completa

        } catch (SQLException e) {
            throw new RuntimeException("Error listando estados", e);
        }
    }
}
