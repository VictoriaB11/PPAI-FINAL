//“Traduce objetos ↔ filas de tabla”
//DAO = Data Access Object (Objeto de acceso a datos). Se encarga de acceder a los datos en la BD. Solo “guardar” y “leer”.
//      Ejemplo: Clase MotivoTipo → fila en motivo_tipo.
//               Clase Empleado → fila en empleado

package org.example.Persistencia;
import org.example.Modelos.MotivoTipo;
// Importamos la clase del dominio. El DAO devuelve/recibe MotivoTipo reales del sistema.

import java.sql.*;
// Importa Connection, PreparedStatement, ResultSet, SQLException, etc.

import java.util.ArrayList;
// ArrayList para construir listas en memoria con resultados del SELECT.

import java.util.List;
// Interfaz List para devolver listas sin atarte a ArrayList.

public class MotivoTipoDAO {

    public void guardarSiNoExiste(String descripcion) {
        // Guarda un motivo en la BD si todavía no está.
        // Usamos String porque MotivoTipo parece tiene solo "descripcion".

        String sql = "INSERT OR IGNORE INTO motivo_tipo(descripcion) VALUES (?)";
        // SQL para insertar.
        // "OR IGNORE": si viola UNIQUE, no inserta y no explota.
        // "(?)" parámetro, se reemplaza de forma segura con PreparedStatement.

        try (Connection conn = Database.getConnection();
             // Abrimos conexión.

             PreparedStatement ps = conn.prepareStatement(sql)) {
            // PreparedStatement: permite SQL con parámetros y evita inyección + errores de comillas.

            ps.setString(1, descripcion);
            // Reemplaza el primer "?" del SQL por el valor de descripcion.

            ps.executeUpdate();
            // Ejecuta el INSERT. No devuelve filas; modifica la tabla.

        } catch (SQLException e) {
            // Si algo falla en SQL (archivo bloqueado, sintaxis, etc.) cae acá.

            throw new RuntimeException("Error guardando motivo_tipo", e);
            // Propagamos como error fatal con mensaje entendible.
        }
    }

    public List<MotivoTipo> listar() {
        // Lee todos los motivos guardados en la BD y los devuelve como objetos MotivoTipo.

        String sql = "SELECT descripcion FROM motivo_tipo ORDER BY descripcion";
        // SQL para listar motivos.
        // ORDER BY: para que salgan ordenados alfabéticamente.

        List<MotivoTipo> res = new ArrayList<>();
        // Lista donde iremos acumulando los MotivoTipo que devuelve el SELECT.

        try (Connection conn = Database.getConnection();
             // Abrimos conexión.

             PreparedStatement ps = conn.prepareStatement(sql);
             // Preparamos el SELECT.

             ResultSet rs = ps.executeQuery()) {
            // Ejecutamos el SELECT y obtenemos ResultSet (tabla de resultados).
            // executeQuery se usa para SELECT, executeUpdate para INSERT/UPDATE/DELETE.

            while (rs.next()) {
                // rs.next() avanza a la siguiente fila.
                // Devuelve false cuando ya no hay más filas.

                String descripcion = rs.getString("descripcion");
                // Lee la columna "descripcion" de la fila actual.

                MotivoTipo m = new MotivoTipo(descripcion);
                // Reconstruye un objeto del dominio usando el dato de la BD.

                res.add(m);
                // Agrega el objeto a la lista de resultados.
            }

            return res;
            // Devuelve la lista completa.

        } catch (SQLException e) {
            // Manejo de errores SQL.

            throw new RuntimeException("Error listando motivo_tipo", e);
            // Propaga el error con mensaje claro.
        }
    }

    //PARA OBTENER LOS IDs
    public int obtenerIdPorDescripcion(String descripcion) {
        String sql = "SELECT id FROM motivo_tipo WHERE descripcion = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, descripcion);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }

            throw new RuntimeException("No se encontró id de motivo: " + descripcion);

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando id de motivo", e);
        }
    }

}
