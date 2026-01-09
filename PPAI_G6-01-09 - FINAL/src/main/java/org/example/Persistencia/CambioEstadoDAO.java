//Registra el evento: cambio de estado + motivos.

package org.example.Persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class CambioEstadoDAO {
    /**
     * Inserta un cambio de estado y devuelve su ID.
     * guarda fecha, estado, sismógrafo y empleado
     */
    public int insertarCambioEstado(
            int sismografoId,
            int estadoId,
            LocalDateTime fechaHora,
            String empleadoNombre
    ) {

        String sql = """
            INSERT INTO cambio_estado (sismografo_id, estado_id, fecha_hora, empleado_nombre)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, sismografoId);
            ps.setInt(2, estadoId);
            ps.setString(3, fechaHora.toString()); // ISO-8601
            ps.setString(4, empleadoNombre);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // ID del cambio_estado
            }

            throw new RuntimeException("No se pudo obtener el ID del cambio de estado");

        } catch (SQLException e) {
            throw new RuntimeException("Error insertando cambio de estado", e);
        }
    }

    /**
     * Asocia un motivo a un cambio de estado.
     */
    public void insertarMotivoEnCambio(int cambioEstadoId, int motivoTipoId) {
        String sql = """
            INSERT INTO cambio_estado_motivo (cambio_estado_id, motivo_tipo_id)
            VALUES (?, ?)
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cambioEstadoId);
            ps.setInt(2, motivoTipoId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error insertando motivo en cambio de estado", e);
        }
    }
}
