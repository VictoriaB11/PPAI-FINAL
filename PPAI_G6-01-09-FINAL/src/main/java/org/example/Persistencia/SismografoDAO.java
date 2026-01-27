//Crea el sismógrafo y actualiza su estado actual.

package org.example.Persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SismografoDAO {
    /**
     * Inserta un sismógrafo vacío y devuelve su ID.
     * El estado actual se setea después, cuando exista el cambio_estado
     * crea el registro y devuelve el id
     */
    public int insertarSismografo() {
        String sql = "INSERT INTO sismografo DEFAULT VALUES";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // ID generado por SQLite
            }

            throw new RuntimeException("No se pudo obtener el ID del sismógrafo");

        } catch (SQLException e) {
            throw new RuntimeException("Error insertando sismógrafo", e);
        }
    }

    /**
     * Actualiza el cambio de estado actual del sismógrafo.
     * deja marcado el último cambio
     */
    public void actualizarEstadoActual(int sismografoId, int cambioEstadoId) {
        String sql = "UPDATE sismografo SET estado_actual_cambio_id = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cambioEstadoId);
            ps.setInt(2, sismografoId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando estado actual del sismógrafo", e);
        }
    }
}
